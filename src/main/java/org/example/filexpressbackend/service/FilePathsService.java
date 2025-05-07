package org.example.filexpressbackend.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.entity.FilePaths;
import org.example.filexpressbackend.entity.User;
import org.example.filexpressbackend.repository.FilePathsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
