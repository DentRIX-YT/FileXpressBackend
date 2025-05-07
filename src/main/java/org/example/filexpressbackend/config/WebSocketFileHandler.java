package org.example.filexpressbackend.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.example.filexpressbackend.entity.FilePaths;
import org.example.filexpressbackend.entity.User;
import org.example.filexpressbackend.repository.FilePathsRepository;
import org.example.filexpressbackend.repository.UserRepository;
import org.example.filexpressbackend.service.FilePathsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import jakarta.annotation.PostConstruct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WebSocketFileHandler implements WebSocketHandler {

    private final FilePathsRepository filePathsRepository;
    private final FilePathsService filePathsService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, ByteArrayOutputStream> sessionBuffers = new ConcurrentHashMap<>();
    private final Map<String, FileMetadata> sessionMetadata = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        System.out.println("WebSocket file handler initialized");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        session.setBinaryMessageSizeLimit(10 * 1024 * 1024); // או כמה שאתה צריך
        sessionBuffers.put(session.getId(), new ByteArrayOutputStream());
        System.out.println("✅ WebSocket connection established with session ID: " + session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        if (message instanceof TextMessage textMessage) {
            String payload = textMessage.getPayload();
            JsonNode root = objectMapper.readTree(payload);
            String type = root.has("type") ? root.get("type").asText() : "";
            System.out.println(payload);

            if ("metadata".equals(type)) {
                FileMetadata metadata = objectMapper.treeToValue(root.get("metadata"), FileMetadata.class);
                sessionMetadata.put(session.getId(), metadata);
                System.out.println("Received metadata");
                session.sendMessage(new TextMessage("ACK_METADATA"));
            } else if ("EOF".equals(type)) {
                saveToDiskAndDatabase(session);
                System.out.println("Received EOF");
            } else if ("downloadRequest".equals(type)) {
                Long fileID = root.get("fileID").asLong();
                handleFileDownload(session, fileID);
            }
        } else if (message instanceof BinaryMessage binaryMessage) {
//            System.out.println("Received binary message");
            sessionBuffers.get(session.getId()).write(binaryMessage.getPayload().array());
        }
        else{
            System.out.println("Received unknown message type: " + message.getClass());
        }
    }

    private void handleFileDownload(WebSocketSession session, Long requestFileID) {
        try {
            // חיפוש הקובץ במסד נתונים
            FilePaths fileRecord = filePathsService.getFileById(requestFileID);
            if (fileRecord == null) {
                session.sendMessage(new TextMessage("ERROR: File not found"));
                return;
            }

            Path filePath = Paths.get(fileRecord.getPath());
            if (!Files.exists(filePath)) {
                session.sendMessage(new TextMessage("ERROR: File missing on server"));
                return;
            }

            // שליחת metadata לפני התוכן הבינארי
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode responseMetadata = mapper.createObjectNode();
            responseMetadata.put("type", "downloadMetadata");
            ObjectNode meta = responseMetadata.putObject("metadata");
            meta.put("filename", fileRecord.getOriginalFilename());
            meta.put("iv", fileRecord.getIv());
            meta.put("encryptedAESKey", fileRecord.getEncryptedAESKey());
            meta.put("sha256", fileRecord.getFileHash());
            meta.put("totalSize", Files.size(filePath));

            session.sendMessage(new TextMessage(responseMetadata.toString()));

            // שליחת תוכן הקובץ כ־Binary
            byte[] fileBytes = Files.readAllBytes(filePath);
            session.sendMessage(new BinaryMessage(fileBytes));

            session.sendMessage(new TextMessage("{\"type\":\"EOF\"}"));
        } catch (Exception e) {
            e.printStackTrace();
            try {
                session.sendMessage(new TextMessage("ERROR: " + e.getMessage()));
            } catch (IOException ignored) {
            }
        }
    }

    private void saveToDiskAndDatabase(WebSocketSession session) {
        try {
            FileMetadata meta = sessionMetadata.get(session.getId());
            if (meta == null) throw new RuntimeException("No metadata found for session.");

            byte[] encryptedFile = sessionBuffers.get(session.getId()).toByteArray();

            String uniqueFileName = System.currentTimeMillis() + "_" + meta.filename;
            Path uploadDir = Paths.get("uploads");
            Files.createDirectories(uploadDir);
            Path filePath = uploadDir.resolve(uniqueFileName);
            Files.write(filePath, encryptedFile);

            System.out.println(meta.encryptedAESKey);
            System.out.println(meta.iv);
            System.out.println(meta.sha256);
            System.out.println(meta.filename);
            System.out.println(filePath.toString());

            User receiver = userRepository.findByUsername(meta.receiver);
            if (receiver == null) throw new RuntimeException("Receiver not found");

            FilePaths fileRecord = new FilePaths();
            fileRecord.setUser(receiver);
            fileRecord.setPath(filePath.toString());
            fileRecord.setOriginalFilename(meta.filename);
            fileRecord.setFileHash(meta.sha256);
            fileRecord.setUploadedAt(LocalDateTime.now());
            fileRecord.setEncryptedAESKey(meta.encryptedAESKey);
            fileRecord.setIv(meta.iv);

            filePathsRepository.save(fileRecord);

            session.sendMessage(new TextMessage("Upload complete"));
        } catch (Exception e) {
            e.printStackTrace();
            try {
                session.sendMessage(new TextMessage("Upload failed: " + e.getMessage()));
            } catch (IOException ignored) {
            }
        } finally {
            sessionBuffers.remove(session.getId());
            sessionMetadata.remove(session.getId());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("WebSocket error: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        sessionBuffers.remove(session.getId());
        sessionMetadata.remove(session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    @RequiredArgsConstructor
    @ToString
    // Inner class for metadata
    private static class FileMetadata {
        public String sender;
        public String receiver;
        public String filename;
        public String sha256;
        public String iv;
        public String encryptedAESKey;
    }
}
