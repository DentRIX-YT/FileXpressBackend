package org.example.filexpressbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.entity.PublicKey;
import org.example.filexpressbackend.entity.User;
import org.example.filexpressbackend.repository.PublicKeyRepository;
import org.example.filexpressbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PublicKeyService {

    private final PublicKeyRepository publicKeyRepository;
    private final UserRepository userRepository;

    public void savePublicKey(String username, String publicKeyValue) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        PublicKey publicKey = new PublicKey();
        publicKey.setPublicKeyValue(publicKeyValue);
        publicKey.setUser(user);

        publicKeyRepository.save(publicKey);
    }

    public Optional<String> getPublicKey(String username) {
        return publicKeyRepository.findByUser_Username(username)
                .map(PublicKey::getPublicKeyValue);
    }
}