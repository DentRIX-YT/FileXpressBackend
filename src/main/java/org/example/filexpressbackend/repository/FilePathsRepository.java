package org.example.filexpressbackend.repository;

import org.example.filexpressbackend.entity.FilePaths;
import org.example.filexpressbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilePathsRepository extends JpaRepository<FilePaths, Long> {
    List<FilePaths> findByUser(User user);
}
