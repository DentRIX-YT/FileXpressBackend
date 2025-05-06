package org.example.filexpressbackend.dto;

import lombok.Data;
import org.example.filexpressbackend.enums.TransferMethod;

@Data
public class AddLogRequest {
    private String senderUsername;
    private String receiverUsername;
    private String filename;
    private TransferMethod method;
    private boolean storedForLater;
}
