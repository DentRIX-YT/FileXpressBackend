package org.example.filexpressbackend.dto;


import lombok.Data;

@Data
public class PrivateKeyRequest {
    private String username;
    private String encryptedPrivateKey;
}
