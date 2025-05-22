package org.example.filexpressbackend.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.entity.FilePaths;
import org.example.filexpressbackend.service.FilePathsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Component
@RequiredArgsConstructor
public class WebSocketDownloadHandler implements WebSocketHandler {
    private final FilePathsService filePathsService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Download session opened: " + session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        if (message instanceof TextMessage textMessage) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(textMessage.getPayload());

            if (!root.has("type") || !"downloadRequest".equals(root.get("type").asText())) {
                session.sendMessage(new TextMessage("ERROR: Invalid message type"));
                return;
            }

            if (!root.has("fileID") || root.get("fileID").isNull()) {
                session.sendMessage(new TextMessage("ERROR: Missing fileID in request"));
                session.close(CloseStatus.BAD_DATA);
                System.err.println("Missing fileID in download request");
                return;
            }

            Long fileId = root.get("fileID").asLong();
            handleFileDownload(session, fileId);
        }
    }

    private void handleFileDownload(WebSocketSession session, Long fileId) {
        try {
            FilePaths file = filePathsService.getFileById(fileId);
            if (file == null) {
                session.sendMessage(new TextMessage("ERROR: File not found"));
                return;
            }

            Path path = Paths.get(file.getPath());
            if (!Files.exists(path)) {
                session.sendMessage(new TextMessage("ERROR: File not found on disk"));
                return;
            }

            ObjectNode metadataMsg = new ObjectMapper().createObjectNode();
            metadataMsg.put("type", "downloadMetadata");
            ObjectNode meta = metadataMsg.putObject("metadata");
            meta.put("filename", file.getOriginalFilename());
            meta.put("iv", file.getIv());
            meta.put("encryptedAESKey", file.getEncryptedAESKey());
            meta.put("sha256", file.getFileHash());
            meta.put("totalSize", file.getSize());

            session.sendMessage(new TextMessage(metadataMsg.toString()));
            session.sendMessage(new BinaryMessage(Files.readAllBytes(path)));
            session.sendMessage(new TextMessage("{\"type\":\"EOF\"}"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}