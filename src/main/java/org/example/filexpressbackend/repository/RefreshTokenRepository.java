package org.example.filexpressbackend.repository;


import org.example.filexpressbackend.entity.RefreshToken;
import org.example.filexpressbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Find a refresh token by the associated user
    Optional<RefreshToken> findByUser(User user);

    // Find a refresh token by the token value
    Optional<RefreshToken> findByToken(String token);

    // Delete a refresh token by the user (useful for logging out all sessions of a user)
    void deleteByUser(User user);



}