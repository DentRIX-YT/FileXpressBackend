package org.example.filexpressbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.dto.PrivateKeyRequest;
import org.example.filexpressbackend.service.PrivateKeyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/private-key")
@RequiredArgsConstructor
public class PrivateKeyController {
    private final PrivateKeyService privateKeyService;

    @PostMapping
    public ResponseEntity<?> savePrivateKey(@RequestBody PrivateKeyRequest request) {
        privateKeyService.savePrivateKey(request.getUsername(), request.getEncryptedPrivateKey());
        return ResponseEntity.ok("Private key saved successfully.");
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getPrivateKey(@PathVariable String username) {
        String encryptedKey = privateKeyService.getPrivateKey(username);
        if (encryptedKey == null) {
            return ResponseEntity.status(404).body("Private key not found.");
        }
        return ResponseEntity.ok(Collections.singletonMap("encryptedPrivateKey", encryptedKey));
    }
}
