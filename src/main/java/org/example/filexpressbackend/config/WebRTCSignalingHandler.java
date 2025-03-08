package org.example.filexpressbackend.config;

import org.example.filexpressbackend.dto.WebRTCMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class WebRTCSignalingHandler {
    private final Map<String, String> activeConnections = new ConcurrentHashMap<>();

    @MessageMapping("/signal")
    @SendTo("/topic/signaling")
    public WebRTCMessage handleSignal(WebRTCMessage message) {
        String peer = activeConnections.get(message.getFrom());
        if (peer != null) {
            message.setTo(peer);
            System.out.println("📡 Redirecting WebRTC Signal from " + message.getFrom() + " to " + peer);
        }
        return message;
    }
}