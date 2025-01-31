package org.example.filexpressbackend.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.config.JwtProperties;
import org.example.filexpressbackend.config.JwtUtil;
import org.example.filexpressbackend.dto.AuthenticationResponse;
import org.example.filexpressbackend.dto.RefreshTokenRequest;
import org.example.filexpressbackend.entity.RefreshToken;
import org.example.filexpressbackend.repository.RefreshTokenRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final CustomUserDetailsService customUserDetailsService;

    @Transactional
    public AuthenticationResponse createRefreshToken(RefreshTokenRequest request) {
        String oldRefreshToken = request.getRefreshToken();
        System.out.println("Received refresh token: " + oldRefreshToken);

        // Verify the validity of the incoming refresh token
        String username = jwtUtil.extractUsername(oldRefreshToken);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        if (!jwtUtil.validateToken(oldRefreshToken, userDetails)) {
            System.out.println("Invalid or expired refresh token");
            throw new RuntimeException("Invalid or expired refresh token");
        }

        // Blacklist old token if valid
        validateAndBlacklistOldToken(oldRefreshToken);

        // Generate new tokens
        String newAccessToken = jwtUtil.generateToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

        System.out.println("Generated new access token: " + newAccessToken);
        System.out.println("Generated new refresh token: " + newRefreshToken);

        // Update or create the refresh token entry in the database
        RefreshToken refreshToken = refreshTokenRepository.findByToken(oldRefreshToken)
                .orElse(new RefreshToken());
        refreshToken.setToken(newRefreshToken);
        refreshToken.setExpiryDate(Instant.now().plusMillis(JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME));
        refreshTokenRepository.save(refreshToken);

        // Return both tokens in the response
        return new AuthenticationResponse(newAccessToken, newRefreshToken, true, userDetails.getAuthorities());
    }

    private void validateAndBlacklistOldToken(String oldToken) {
        if (oldToken != null && !tokenBlacklistService.isBlacklisted(oldToken)) {
            System.out.println("Blacklisting old token: " + oldToken);
            tokenBlacklistService.addToBlacklist(oldToken);
        }
    }
}