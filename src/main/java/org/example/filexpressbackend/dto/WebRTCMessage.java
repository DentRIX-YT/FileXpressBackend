package org.example.filexpressbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebRTCMessage {
    private String type;
    private String sender;
    private String receiver;
    private String sdp;
    private String from;
    private String to;

    // Constructor for sender/receiver only
    public WebRTCMessage(String sender, String receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }
}
