package org.example.filexpressbackend.dto;

import lombok.Data;

@Data
public class HandshakeValidationRequest {
    private String receiverUsername;
    private String providedHandshakeCode;
    private String passphrase;
}
