package org.example.filexpressbackend.repository;

import org.example.filexpressbackend.entity.Handshake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HandshakeRepository extends JpaRepository<Handshake, Long> {
    Optional<Handshake> findBySenderUsername(String senderUsername);

    Optional<Handshake> findByHandshakeCode(String handshakeCode);

    List<Handshake> findByCreatedAtBefore(LocalDateTime time);
}
