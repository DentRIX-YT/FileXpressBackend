package org.example.filexpressbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.entity.FilePaths;
import org.example.filexpressbackend.entity.User;
import org.example.filexpressbackend.repository.FilePathsRepository;
import org.example.filexpressbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class WebSocketFileService {

    private final FilePathsRepository filePathsRepository;
    private final UserRepository userRepository;

    public void storeEncryptedFile(String username, String originalFilename,
                                   byte[] encryptedFileData, byte[] encryptedAESKey,
                                   byte[] iv, String fileHash) throws Exception {

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }

        // יצירת קובץ פיזי בשרת (תיקיית files חייבת להיות קיימת)
        String sanitizedFilename = System.currentTimeMillis() + "_" + originalFilename;
        File destination = new File("files/" + sanitizedFilename);
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            fos.write(encryptedFileData);
        }

        // יצירת רשומה במסד הנתונים
        FilePaths entry = new FilePaths();
        entry.setUser(user);
        entry.setOriginalFilename(originalFilename);
        entry.setPath(destination.getAbsolutePath());
        entry.setFileHash(fileHash);
        entry.setEncryptedAESKey(Base64.getEncoder().encodeToString(encryptedAESKey));
        entry.setIv(Base64.getEncoder().encodeToString(iv));
        entry.setUploadedAt(LocalDateTime.now());

        filePathsRepository.save(entry);
    }
}
