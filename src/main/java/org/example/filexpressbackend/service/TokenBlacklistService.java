package org.example.filexpressbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.config.JwtUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final JwtUtil jwtUtil;
    private final ConcurrentHashMap<String, Instant> blacklist = new ConcurrentHashMap<>();

    public void addToBlacklist(String token) {
        Instant expirationTime = jwtUtil.extractExpiration(token).toInstant();
        blacklist.put(token, expirationTime);
        removeExpiredTokens();
    }

    private void removeExpiredTokens() {
        Iterator<ConcurrentHashMap.Entry<String, Instant>> iterator =
                blacklist.entrySet().iterator();
        while (iterator.hasNext()) {
            ConcurrentHashMap.Entry<String, Instant> entry = iterator.next();
            if (Instant.now().isAfter(entry.getValue())) {
                iterator.remove();
            }
        }
    }

    public boolean isBlacklisted(String token) {
        Instant expiration = blacklist.get(token);
        if (expiration == null || Instant.now().isAfter(expiration)) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }
}
