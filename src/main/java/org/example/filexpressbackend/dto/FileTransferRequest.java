package org.example.filexpressbackend.dto;

import lombok.Data;

@Data
public class FileTransferRequest {
    private String sender;
    private String receiver;
    private String filename;
    private String sha256;
    private String iv;
    private String encryptedAESKey;
}
