package org.example.filexpressbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.dto.HandshakeRequest;
import org.example.filexpressbackend.dto.HandshakeValidationRequest;
import org.example.filexpressbackend.repository.PrivateKeyRepository;
import org.example.filexpressbackend.service.HandshakeService;
import org.example.filexpressbackend.service.PrivateKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/handshake")
@RequiredArgsConstructor
public class HandshakeController {

    private final PrivateKeyService privateKeyService;
    private final HandshakeService handshakeService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateHandshake(@RequestBody HandshakeRequest request) {
        String handshakeCode = handshakeService.addHandshake(request.getSenderUsername(), request.getHandshakeCode()).getHandshakeCode();
        return ResponseEntity.ok(Map.of("handshakeCode", handshakeCode));
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, String>> validateHandshake(@RequestBody HandshakeValidationRequest request) {
        boolean isPassphraseValid = handshakeService.validatePassphrase(request.getPassphrase(), request.getReceiverUsername());
        if (!isPassphraseValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "failed-passphrase"));
        }

        String senderUsername = handshakeService.validateHandshake(request.getReceiverUsername(), request.getProvidedHandshakeCode());
        if (senderUsername != null) {
            handshakeService.markHandshakeAsAccepted(senderUsername);
            handshakeService.setHandshakeReceiver(request.getReceiverUsername(), request.getProvidedHandshakeCode());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "encryptedPrivateKey", privateKeyService.getPrivateKey(request.getReceiverUsername()),
                    "senderUsername", senderUsername
            ));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "failed"));
    }

    @GetMapping("/status/{senderUsername}")
    public ResponseEntity<Map<String, String>> checkHandshakeStatus(@PathVariable String senderUsername) {
        boolean isCompleted = handshakeService.isHandshakeComplete(senderUsername);
        if (isCompleted) {
//            handshakeService.removeHandshake(senderUsername); //not removing the handshake for now
            return ResponseEntity.ok(Map.of("status", "completed", "receiverUsername" , handshakeService.getReceiverFromSender(senderUsername), "receiverPublicKey", handshakeService.getReceiverPublicKey(senderUsername)));
        }
        return ResponseEntity.ok(Map.of("status", "pending"));
    }

    @DeleteMapping("/remove/{senderUsername}")
    public ResponseEntity<Map<String, String>> removeHandshake(@PathVariable String senderUsername) {
        handshakeService.removeHandshake(senderUsername);
        return ResponseEntity.ok(Map.of("status", "removed"));
    }
}