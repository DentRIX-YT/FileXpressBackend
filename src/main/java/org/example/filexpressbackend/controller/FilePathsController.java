package org.example.filexpressbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.entity.FilePaths;
import org.example.filexpressbackend.entity.User;
import org.example.filexpressbackend.repository.UserRepository;
import org.example.filexpressbackend.service.FilePathsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FilePathsController {

    private final FilePathsService filePathsService;
    private final UserRepository userRepository;

    @GetMapping("/{username}")
    public ResponseEntity<List<FilePaths>> getFilesForUser(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<FilePaths> files = filePathsService.getFilesForUser(user);
        return ResponseEntity.ok(files);
    }


}
