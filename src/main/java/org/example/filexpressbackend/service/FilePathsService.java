package org.example.filexpressbackend.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.entity.FilePaths;
import org.example.filexpressbackend.entity.User;
import org.example.filexpressbackend.repository.FilePathsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilePathsService {

    private final FilePathsRepository filePathsRepository;

    public FilePaths save(FilePaths filePaths) {
        return filePathsRepository.save(filePaths);
    }

    public List<FilePaths> getFilesForUser(User user) {
        return filePathsRepository.findByUser(user);
    }

    public FilePaths getFileById(Long id) {
        return filePathsRepository.findById(id).orElse(null);
    }

    public boolean deleteFileById(Long id) {
        Optional<FilePaths> optionalFile = filePathsRepository.findById(id);
        if (optionalFile.isEmpty()) return false;

        FilePaths file = optionalFile.get();
        try {
            Path path = Paths.get(file.getPath());
            Files.deleteIfExists(path); // מחיקת הקובץ הפיזי
            filePathsRepository.deleteById(id); // מחיקת הרשומה מה-DB
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
