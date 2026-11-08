package com.sportdataauth.domain.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.sportdataauth.domain.exception.InvalidRefreshTokenExpiryException;

public class RefreshToken {

    private final UUID id;
    private final UUID userId;
    private final String tokenHash;
    private final Instant expiresAt;
    private final Instant revokedAt;
    private final Instant createdAt;

    public RefreshToken(UUID id, UUID userId, String tokenHash, Instant expiresAt, Instant revokedAt,
            Instant createdAt) {
        this.id = Objects.requireNonNull(id, "ID_IS_REQUIRED");
        this.userId = Objects.requireNonNull(userId, "USER_ID_IS_REQUIRED");
        this.tokenHash = Objects.requireNonNull(tokenHash, "TOKEN_HASH_IS_REQUIRED");
        if (tokenHash.isBlank()) {
            throw new IllegalArgumentException("TOKEN_HASH_IS_REQUIRED");
        }
        
        this.expiresAt = Objects.requireNonNull(expiresAt, "EXPIRES_AT_IS_REQUIRED");
        this.revokedAt = revokedAt;
        this.createdAt = Objects.requireNonNull(createdAt, "CREATED_AT_IS_REQUIRED");
        if (!expiresAt.isAfter(createdAt)) {
            throw new InvalidRefreshTokenExpiryException();
        }

        if (revokedAt != null && revokedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("REVOKED_AT_MUST_NOT_BE_BEFORE_CREATED_AT");
        }
        
    }

    public boolean isExpired(Instant now) {
        if (now == null) {
            throw new IllegalArgumentException("NOW_IS_REQUIRED");
        }
        return !expiresAt.isAfter(now); // expired if expiresAt <= now
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}
