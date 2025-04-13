package org.example.filexpressbackend.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebRTCConnectionRegistry {
    private final Map<String, String> activeConnections = new ConcurrentHashMap<>();

    public void addConnection(String user1, String user2) {
        activeConnections.put(user1, user2);
        activeConnections.put(user2, user1);
    }

    public String getPeer(String username) {
        return activeConnections.get(username);
    }

    public Map<String, String> getAllConnections() {
        return activeConnections;
    }
}