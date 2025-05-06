package org.example.filexpressbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.dto.AddLogRequest;
import org.example.filexpressbackend.dto.TransferLogDto;
import org.example.filexpressbackend.entity.TransferLog;
import org.example.filexpressbackend.entity.User;
import org.example.filexpressbackend.enums.TransferMethod;
import org.example.filexpressbackend.repository.TransferLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferLogService {

    private final TransferLogRepository transferLogRepository;
    private final UserService userService;

    public List<TransferLog> getLogsSentBy(String senderUsername) {
        User sender = userService.getUserEntityByUsername(senderUsername);
        return transferLogRepository.findBySender(sender);
    }

    public List<TransferLog> getLogsReceivedBy(String receiverUsername) {
        User receiver = userService.getUserEntityByUsername(receiverUsername);
        return transferLogRepository.findByReceiver(receiver);
    }

    public List<TransferLog> getAllLogsInvolving(String username) {
        User user = userService.getUserEntityByUsername(username);
        return transferLogRepository.findBySenderOrReceiver(user, user);
    }

    public void saveLogFromDto(AddLogRequest request) {
        User sender = userService.getUserEntityByUsername(request.getSenderUsername());
        User receiver = userService.getUserEntityByUsername(request.getReceiverUsername());

        TransferLog log = new TransferLog();
        log.setSender(sender);
        log.setReceiver(receiver);
        log.setFilename(request.getFilename());
        log.setMethod(request.getMethod());
        log.setStoredForLater(request.isStoredForLater());

        transferLogRepository.save(log);
    }

    public TransferLogDto toDto(TransferLog log) {
        TransferLogDto dto = new TransferLogDto();
        dto.setSenderUsername(log.getSender().getUsername());
        dto.setReceiverUsername(log.getReceiver().getUsername());
        dto.setFilename(log.getFilename());
        dto.setTimestamp(log.getTimestamp());

        // פירוש ברור לשיטת ההעברה
        String readableMethod;
        switch (log.getMethod()) {
            case CLIENT_TO_CLIENT:
                readableMethod = "Direct (Client to Client)";
                break;
            case CLIENT_TO_BOTH:
                readableMethod = "Upload (Sender to Server)";
                break;
            case SERVER_RELAY:
                readableMethod = "Download (Server to Recipient)";
                break;
            default:
                readableMethod = "Unknown";
                break;
        }

        dto.setMethod(readableMethod);
        return dto;
    }


}
