package org.example.filexpressbackend.config;

import org.example.filexpressbackend.dto.WebRTCMessage;
import org.example.filexpressbackend.service.WebRTCConnectionRegistry;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebRTCSignalingHandler {
    private final WebRTCConnectionRegistry connectionRegistry;

    public WebRTCSignalingHandler(WebRTCConnectionRegistry connectionRegistry) {
        this.connectionRegistry = connectionRegistry;
    }

    @MessageMapping("/signal")
    @SendTo("/topic/signaling")
    public WebRTCMessage handleSignal(WebRTCMessage message) {
        String peer = connectionRegistry.getPeer(message.getFrom());
        if (peer != null) {
            message.setTo(peer);
            System.out.println("📡 Redirecting WebRTC Signal from " + message.getFrom() + " to " + peer);
        }
        return message;
    }
}