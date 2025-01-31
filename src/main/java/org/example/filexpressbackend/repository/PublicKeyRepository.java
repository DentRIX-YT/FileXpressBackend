package org.example.filexpressbackend.repository;

import org.example.filexpressbackend.entity.PublicKey;
import org.example.filexpressbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PublicKeyRepository extends JpaRepository<PublicKey, Long> {
    Optional<PublicKey> findByUser(User user); // Find by User entity
    Optional<PublicKey> findByUser_Username(String username); // Find by username through User
}