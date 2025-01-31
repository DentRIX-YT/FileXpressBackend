package org.example.filexpressbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.dto.PrivateKeyRequest;
import org.example.filexpressbackend.dto.PublicKeyRequest;
import org.example.filexpressbackend.service.PublicKeyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public-key")
public class PublicKeyController {

    private final PublicKeyService publicKeyService;

    @PostMapping
    public ResponseEntity<?> savePublicKey(@RequestBody PublicKeyRequest request) {
        publicKeyService.savePublicKey(request.getUsername(), request.getPublicKeyValue());
        return ResponseEntity.ok("Public key saved successfully.");
    }

    @GetMapping("/{username}")
    public ResponseEntity<Map<String, String>> getPublicKey(@PathVariable String username) {
        return publicKeyService.getPublicKey(username)
                .map(publicKey -> ResponseEntity.ok(Collections.singletonMap("publicKey", publicKey)))
                .orElse(ResponseEntity.status(404).body(Collections.singletonMap("error", "Public key not found.")));
    }
}