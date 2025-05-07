package org.example.filexpressbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.dto.FilePathsDTO;
import org.example.filexpressbackend.entity.FilePaths;
import org.example.filexpressbackend.entity.User;
import org.example.filexpressbackend.repository.UserRepository;
import org.example.filexpressbackend.service.FilePathsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FilePathsController {

    private final FilePathsService filePathsService;
    private final UserRepository userRepository;



    @GetMapping("/{username}")
    public ResponseEntity<List<FilePathsDTO>> getFilesForUser(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<FilePathsDTO> dtos = filePathsService.getFilesForUser(user)
                .stream()
                .map(file -> {
                    FilePathsDTO dto = new FilePathsDTO();
                    dto.setId(file.getId());
                    dto.setOriginalFilename(file.getOriginalFilename());
                    dto.setPath(file.getPath());
                    dto.setSize(file.getSize());
                    dto.setUploadedAt(file.getUploadedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        boolean deleted = filePathsService.deleteFileById(id);

        if (deleted) {
            return ResponseEntity.ok(Collections.singletonMap("status", "success"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found or failed to delete.");
        }
    }





}
