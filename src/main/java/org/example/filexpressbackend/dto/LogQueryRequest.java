package org.example.filexpressbackend.dto;

import lombok.Data;

@Data
public class LogQueryRequest {
    private String username;
    private String direction; // sent / received / all
    private int limit;
}
