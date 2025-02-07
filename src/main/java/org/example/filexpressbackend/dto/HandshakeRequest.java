package org.example.filexpressbackend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
public class HandshakeRequest {
    private String senderUsername;
    private String handshakeCode;
}
