package org.example.filexpressbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
public class Handshake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderUsername;
    private String handshakeCode;

    @Column(nullable = false)
    private LocalDateTime createdAt; // Store the timestamp

    private boolean isAccepted = false;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // Set creation time when saved
    }


}
