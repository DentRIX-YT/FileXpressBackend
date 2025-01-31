package org.example.filexpressbackend.service;


import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.entity.PrivateKey;
import org.example.filexpressbackend.entity.User;
import org.example.filexpressbackend.repository.PrivateKeyRepository;
import org.example.filexpressbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrivateKeyService {

    private final PrivateKeyRepository privateKeyRepository;
    private final UserRepository userRepository;

    public void savePrivateKey(String username, String encryptedPrivateKey) {
        // Find the user by username
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Create and set up the PrivateKey entity
        PrivateKey privateKey = new PrivateKey();
        privateKey.setEncryptedPrivateKey(encryptedPrivateKey);
        privateKey.setUser(user); // Set the user

        // Save the PrivateKey entity
        privateKeyRepository.save(privateKey);
    }

    public String getPrivateKey(String username) {
        return privateKeyRepository.findByUser_Username(username)
                .map(PrivateKey::getEncryptedPrivateKey)
                .orElse(null);
    }
}
