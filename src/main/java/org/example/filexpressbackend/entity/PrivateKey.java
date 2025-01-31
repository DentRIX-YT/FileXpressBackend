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
public class PrivateKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String encryptedPrivateKey;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user; // One-to-one relationship with User
}
