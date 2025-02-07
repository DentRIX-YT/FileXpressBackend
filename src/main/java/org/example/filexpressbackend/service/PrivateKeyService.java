package org.example.filexpressbackend.service;


import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.entity.PrivateKey;
import org.example.filexpressbackend.entity.User;
import org.example.filexpressbackend.repository.PrivateKeyRepository;
import org.example.filexpressbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

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

    // Function to Derive AES Key from Passphrase and Salt
    private SecretKeySpec deriveAESKey(String passphrase, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, 100000, 256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    public String decryptPrivateKey(String encryptedData, String passphrase) {
        try {
            //Split the data: salt, IV, encrypted key
            String[] parts = encryptedData.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid encrypted data format");
            }

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] iv = Base64.getDecoder().decode(parts[1]); // IV must be 12 bytes for AES-GCM
            byte[] cipherText = Base64.getDecoder().decode(parts[2]);

            // Generate AES Key from passphrase
            SecretKeySpec secretKey = deriveAESKey(passphrase, salt);

            // Decrypt using AES-GCM
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv); // ✅ Use GCMParameterSpec instead of IvParameterSpec
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

            byte[] decryptedBytes = cipher.doFinal(cipherText);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Decryption failed (wrong passphrase)
        }
    }

}
