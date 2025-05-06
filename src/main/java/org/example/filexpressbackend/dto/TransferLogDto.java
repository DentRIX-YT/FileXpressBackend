package org.example.filexpressbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransferLogDto {
    private String senderUsername;
    private String receiverUsername;
    private String filename;
    private String method;
    private LocalDateTime timestamp; // ← תאריך ושעה של ההעברה
}
