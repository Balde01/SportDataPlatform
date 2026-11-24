package com.sportdataauth.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.sportdataauth.domain.entity.RefreshToken;

public interface RefreshTokenRepository {

    void save(RefreshToken refreshToken);

    // Returns empty when no token exists for the provided hash.
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // Atomically consumes a valid token to prevent concurrent reuse during
    // rotation.
    RefreshToken consumeValidByTokenHash(String tokenHash, Instant now);

    // Used for client logout when only the refresh token is known
    Optional<RefreshToken> revokeByTokenHash(String tokenHash, Instant now);

    // Used by administrative flows to revoke a specific session without exposing
    // token hashes.
    Optional<RefreshToken> revokeById(UUID tokenId, Instant now);

    // Returns the number of sessions revoked for the user.
    int revokeAllByUserId(UUID userId, Instant now);

}
