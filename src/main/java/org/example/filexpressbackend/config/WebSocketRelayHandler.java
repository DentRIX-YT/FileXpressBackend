package org.example.filexpressbackend.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketRelayHandler implements WebSocketHandler {

    private final Map<String, WebSocketSession> recipientSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("📡 Relay WebSocket connected: " + session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        if (message instanceof TextMessage textMessage) {
            JsonNode json = objectMapper.readTree(textMessage.getPayload());
            String type = json.get("type").asText();

            if ("registerReceiver".equals(type)) {
                String receiverUsername = json.get("username").asText();
                recipientSessions.put(receiverUsername, session);
                System.out.println("Registered receiver: " + receiverUsername);
            }

        } else if (message instanceof BinaryMessage binaryMessage) {
            String target = (String) session.getAttributes().get("relayTarget");

            if (target != null && recipientSessions.containsKey(target)) {
                recipientSessions.get(target).sendMessage(binaryMessage);
            } else {
                System.out.println("No target registered for relay");
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("Relay WebSocket error: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        recipientSessions.values().removeIf(s -> s.getId().equals(session.getId()));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
