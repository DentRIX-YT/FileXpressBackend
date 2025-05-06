package org.example.filexpressbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.dto.AddLogRequest;
import org.example.filexpressbackend.dto.LogQueryRequest;
import org.example.filexpressbackend.dto.TransferLogDto;
import org.example.filexpressbackend.entity.TransferLog;
import org.example.filexpressbackend.service.TransferLogService;
import org.example.filexpressbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class TransferLogController {

    private final TransferLogService transferLogService;
    private final UserService userService;

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> queryLogs(
            @RequestBody LogQueryRequest request,
            @RequestHeader("Authorization") String tokenHeader) {

        String username = userService.getUsernameFromToken(tokenHeader);
        List<TransferLog> logs;
        switch (request.getDirection().toLowerCase()) {
            case "sent":
                logs = transferLogService.getLogsSentBy(username);
                break;
            case "received":
                logs = transferLogService.getLogsReceivedBy(username);
                break;
            default:
                logs = transferLogService.getAllLogsInvolving(username);
                break;
        }
        // המר ל-DTO
        List<TransferLogDto> dtos = logs.stream()
                .map(transferLogService::toDto)
                .collect(Collectors.toList());


        return ResponseEntity.ok(Map.of(
                "status", "success",
                "logs", dtos
        ));

    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addLog(@RequestBody AddLogRequest request) {
        try {
            transferLogService.saveLogFromDto(request);
            return ResponseEntity.ok(Map.of("status", "log-added"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}
