package org.example.filexpressbackend.dto;

import lombok.Data;

@Data
public class PublicKeyRequest {
    private String username;
    private String publicKeyValue;
}
