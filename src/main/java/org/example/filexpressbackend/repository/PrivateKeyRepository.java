package org.example.filexpressbackend.repository;

import org.example.filexpressbackend.entity.PrivateKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivateKeyRepository extends JpaRepository<PrivateKey, Long> {
    Optional<PrivateKey> findByUser_Username(String username);
}
