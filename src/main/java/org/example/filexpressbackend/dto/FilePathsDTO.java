package org.example.filexpressbackend.dto;

import lombok.Data;

@Data
public class FilePathsDTO {
    Long id;
    String originalFilename;
    String path;
    Long size;
    String uploadedAt;
}
