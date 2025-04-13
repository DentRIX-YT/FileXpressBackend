package org.example.filexpressbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.service.WebRTCConnectionRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webrtc")
@RequiredArgsConstructor
public class WebRTCController {
    private final WebRTCConnectionRegistry connectionRegistry;

    @PostMapping("/start-webrtc")
    public ResponseEntity<String> startWebRTC(@RequestBody Map<String, String> payload) {
        String sender = payload.get("sender");
        String receiver = payload.get("receiver");

        if (sender == null || receiver == null) {
            return ResponseEntity.badRequest().body("Missing sender or receiver");
        }

        connectionRegistry.addConnection(sender, receiver);
        System.out.println("✅ WebRTC Connection Established: " + sender + " ↔ " + receiver);
        return ResponseEntity.ok("{\"message\": \"WebRTC Connection Started\"}");
    }

    @GetMapping("/connections")
    public ResponseEntity<Map<String, String>> getConnections() {
        return ResponseEntity.ok(connectionRegistry.getAllConnections());
    }
}