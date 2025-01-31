package org.example.filexpressbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.config.JwtUtil;
import org.example.filexpressbackend.dto.AuthenticationRequest;
import org.example.filexpressbackend.dto.AuthenticationResponse;
import org.example.filexpressbackend.repository.PrivateKeyRepository;
import org.example.filexpressbackend.repository.RefreshTokenRepository;
import org.example.filexpressbackend.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final PrivateKeyRepository privateKeyRepository;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        // load the user details from the database using the username by calling the loadUserByUsername() method
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        try{
            // check if the password matches the password in the database
            if (!passwordEncoder.matches(authenticationRequest.getPassword(), userDetails.getPassword())) {
                throw new AuthenticationServiceException("Invalid credentials");
            }

            // generate the JWT token
            String jwtToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            boolean ifPrivateKeyExists = privateKeyRepository.findByUser_Username(authenticationRequest.getUsername()).isPresent();

            // get the user's roles
            Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();

            // return the AuthenticationResponse object
            return new AuthenticationResponse(jwtToken, refreshToken, ifPrivateKeyExists, roles);
        }
        catch (Exception e){
            throw new AuthenticationServiceException("Invalid credentials");
        }



    }

}