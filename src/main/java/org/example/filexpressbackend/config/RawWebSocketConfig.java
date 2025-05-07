package org.example.filexpressbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@Order(1)
@RequiredArgsConstructor
public class RawWebSocketConfig implements WebSocketConfigurer {

    private final WebSocketFileHandler fileUploadHandler;
    private final WebSocketDownloadHandler webSocketDownloadHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        System.out.println("✅ Registering WebSocketHandler for /file-upload");
        registry
                .addHandler(fileUploadHandler, "/file-upload")
                .setAllowedOrigins("http://localhost:3000");
        registry.addHandler(webSocketDownloadHandler, "/file-download")
                .setAllowedOrigins("http://localhost:3000");


        ;
    }
}