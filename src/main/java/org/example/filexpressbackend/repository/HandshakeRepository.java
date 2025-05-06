package org.example.filexpressbackend.repository;

import org.example.filexpressbackend.entity.Handshake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HandshakeRepository extends JpaRepository<Handshake, Long> {
    Optional<Handshake> findBySenderUsername(String senderUsername);

    Optional<Handshake> findByHandshakeCode(String handshakeCode);

    List<Handshake> findByCreatedAtBefore(LocalDateTime time);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM Handshake h " +
            "WHERE ((h.senderUsername = :user1 AND h.receiverUsername = :user2) " +
            "   OR  (h.senderUsername = :user2 AND h.receiverUsername = :user1)) " +
            "AND h.isAccepted = true")
    boolean existsByUsersAndAccepted(@Param("user1") String user1, @Param("user2") String user2);
}
