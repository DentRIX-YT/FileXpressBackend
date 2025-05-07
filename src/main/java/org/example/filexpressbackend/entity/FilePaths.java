package org.example.filexpressbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilePaths {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String path; // הנתיב לקובץ המוצפן בשרת (למשל: /uploads/abc123.enc)

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String fileHash; // SHA-256 hash of encrypted file

    @Column(nullable = false, columnDefinition = "TEXT")
    private String encryptedAESKey; // מוצפן עם המפתח הציבורי של הנמען (ב-base64)

    @Column(nullable = false)
    private String iv; // ה-IV ששימש להצפנה (base64 או HEX)

    @Column(nullable = false)
    private Long size;

    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // הנמען של הקובץ

    @PrePersist
    public void onCreate() {
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }
}
