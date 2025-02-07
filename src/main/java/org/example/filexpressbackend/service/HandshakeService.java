package org.example.filexpressbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.entity.Handshake;

import org.example.filexpressbackend.entity.User;
import org.example.filexpressbackend.repository.HandshakeRepository;
import org.example.filexpressbackend.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HandshakeService {

    private final HandshakeRepository handshakeRepository;
    private final UserRepository userRepository;
    private final PrivateKeyService privateKeyService;

    //add handshake to the database temporarily
    public Handshake addHandshake(String senderUsername, String handshakeCode) {
        Handshake handshake = new Handshake();
        handshake.setHandshakeCode(handshakeCode);
        handshake.setSenderUsername(senderUsername);
        try {
            return handshakeRepository.save(handshake);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeHandshake(String senderUsername) {
        Optional<Handshake> handshake = handshakeRepository.findBySenderUsername(senderUsername);
        handshake.ifPresent(handshakeRepository::delete);
    }

    // Validate the handshake and decrypt the receiver's private key
    public String validateHandshake(String receiverUsername, String handshakeCode) {
        String expectedCode = handshakeRepository.findByHandshakeCode(handshakeCode)
                .map(Handshake::getHandshakeCode)
                .orElse(null);
        if (expectedCode == null || !expectedCode.equals(handshakeCode)) {
            return null; // Invalid handshake code
        }

        User user = userRepository.findByUsername(receiverUsername);
        if(user == null){
            return null;
        }

        return handshakeRepository.findByHandshakeCode(handshakeCode)
                .map(Handshake::getSenderUsername)
                .orElse(null);


    }

    // Cleanup task runs every 1 minute and removes handshakes older than 5 minutes
    @Scheduled(fixedRate = 60000) // 60,000ms = 1 min
    @Transactional
    public void cleanupExpiredHandshakes() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(5);
        List<Handshake> expiredHandshakes = handshakeRepository.findByCreatedAtBefore(expirationTime);

        if (!expiredHandshakes.isEmpty()) {
            handshakeRepository.deleteAll(expiredHandshakes);
            System.out.println("Removed " + expiredHandshakes.size() + " expired handshake codes.");
        }
    }

    private boolean isValidRSAPrivateKey(String privateKeyStr) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey != null; // If no exception, it's valid
        } catch (Exception e) {
            return false; // Not a valid RSA private key → Passphrase is incorrect
        }
    }

    //validaiting if the passphrase is dcrypting the private key correctly (if not its mean the passphrase is wrong)
    public boolean validatePassphrase(String passphrase, String username) {
        // Retrieve the encrypted private key from storage
        String encryptedPrivateKey = privateKeyService.getPrivateKey(username);

        if (encryptedPrivateKey == null || encryptedPrivateKey.isEmpty()) {
            return false; // No private key found for the user
        }

        try {
            // Attempt to decrypt the private key
            String decryptedPrivateKey = privateKeyService.decryptPrivateKey(encryptedPrivateKey, passphrase);

            // Validate if the decrypted key is a real RSA private key
            return isValidRSAPrivateKey(decryptedPrivateKey);

        } catch (Exception e) {
            return false; // Decryption failed → Incorrect passphrase
        }
    }
}
