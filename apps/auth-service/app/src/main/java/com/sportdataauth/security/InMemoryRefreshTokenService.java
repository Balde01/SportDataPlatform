package com.sportdataauth.security;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sportdataauth.util.TokenGenerator;

public class InMemoryRefreshTokenService implements RefreshTokenService {
    private final TokenGenerator tokenGenerator = new TokenGenerator();
    private final Map<UUID, String> refreshTokenByUserId = new HashMap<>();

    @Override
    public String rotateRefreshToken(UUID userId) {
        String newRefreshToken = tokenGenerator.generateToken();
        refreshTokenByUserId.put(userId, newRefreshToken);
        return newRefreshToken;
    }

    @Override
    public UUID validateRefreshToken(String refreshToken) {
        for (Map.Entry<UUID, String> entry : refreshTokenByUserId.entrySet()) {
            if (entry.getValue().equals(refreshToken)) {
                return entry.getKey();
            }
        }
        return null; // Invalid token
    }

    @Override
    public void revoke(String refreshToken) {
        refreshTokenByUserId.values().remove(refreshToken);
    }

    @Override
    public void revokeAll(UUID userId) {
        refreshTokenByUserId.remove(userId);
    }

}
