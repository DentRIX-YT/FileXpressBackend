package org.example.filexpressbackend.config;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.entity.RefreshToken;
import org.example.filexpressbackend.entity.User;
import org.example.filexpressbackend.repository.UserRepository;
import org.example.filexpressbackend.service.TokenBlacklistService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenBlacklistService tokenBlacklistService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Extract token from request
        String token = request.getHeader(JwtProperties.HEADER_STRING);
        if (token != null && token.startsWith(JwtProperties.TOKEN_PREFIX)) {
            token = token.substring(JwtProperties.TOKEN_PREFIX.length());
        }

        // Blacklist the token
        tokenBlacklistService.addToBlacklist(token);

        // Get username and find the user
        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByUsername(username);

        // Blacklist the refresh token and remove it from the user
        if (user != null) {
            RefreshToken refreshToken = user.getRefreshToken();
            if (refreshToken != null) {
                tokenBlacklistService.addToBlacklist(refreshToken.getToken());
                user.setRefreshToken(null);
                userRepository.save(user);
            }
        }

        // Response status can be handled in the CustomLogoutSuccessHandler
    }
}