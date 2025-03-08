package org.example.filexpressbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.dto.WebRTCMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/webrtc") // Prefix for all routes inside this controller
@RequiredArgsConstructor
public class WebRTCController {
    private final Map<String, String> activeConnections = new ConcurrentHashMap<>();

    @PostMapping("/start-webrtc")
    public ResponseEntity<String> startWebRTC(@RequestBody Map<String, String> payload) {
        String sender = payload.get("sender");
        String receiver = payload.get("receiver");

        if (sender == null || receiver == null) {
            return ResponseEntity.badRequest().body("Missing sender or receiver");
        }

        activeConnections.put(sender, receiver);
        activeConnections.put(receiver, sender);

        System.out.println("✅ WebRTC Connection Established: " + sender + " ↔ " + receiver);
        return ResponseEntity.ok("{\"message\": \"WebRTC Connection Started\"}");
    }

    @GetMapping("/connections")
    public ResponseEntity<Map<String, String>> getConnections() {
        return ResponseEntity.ok(activeConnections);
    }
}