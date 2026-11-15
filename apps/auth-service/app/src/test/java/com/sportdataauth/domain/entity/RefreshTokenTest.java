package com.sportdataauth.domain.entity;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.sportdataauth.domain.exception.InvalidRefreshTokenExpiryException;

public class RefreshTokenTest {

    private final Instant expiresAt = Instant.parse("2026-11-24T18:00:00Z");
    private final Instant createdAt = Instant.parse("2026-10-24T18:00:00Z");
    private final UUID userId = UUID.randomUUID();
    private final String tokenHash = "validTokenHash";
    private final UUID tokenId = UUID.randomUUID();

    @Test
    void shouldCreateRefreshToken() {
        RefreshToken refreshToken = createValidRefreshToken();
        assertEquals(tokenId, refreshToken.getId());
        assertEquals(userId, refreshToken.getUserId());
        assertEquals(tokenHash, refreshToken.getTokenHash());
        assertEquals(expiresAt, refreshToken.getExpiresAt());
        assertEquals(createdAt, refreshToken.getCreatedAt());
        assertNull(refreshToken.getRevokedAt());
    }

    @Test
    void shouldNotCreateRefreshTokenWhenExpiresAtIsBeforeCreatedAt() {
        Instant invalidExpiresAt = createdAt.minusSeconds(1); // expiresAt is before createdAt
        assertThrows(InvalidRefreshTokenExpiryException.class, () -> {
            new RefreshToken(tokenId, userId, tokenHash, invalidExpiresAt, null, createdAt);
        });
    }

    @Test
    void shouldNotCreateRefreshTokenWhenExpiresAtIsEqualToCreatedAt() {
        Instant invalidExpiresAt = createdAt; // expiresAt is equal to createdAt
        assertThrows(InvalidRefreshTokenExpiryException.class, () -> {
            new RefreshToken(tokenId, userId, tokenHash, invalidExpiresAt, null, createdAt);
        });
    }

    @Test
    void shouldNotCreateRefreshTokenWithNullExpiresAt() {
        assertThrows(NullPointerException.class, () -> {
            new RefreshToken(tokenId, userId, tokenHash, null, // null expiresAt
                    null, createdAt);
        });
    }

    @Test
    void shouldNotCreateRefreshTokenWithBlankTokenHash() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RefreshToken(tokenId, userId, "   ", // blank token hash
                    expiresAt, null, createdAt);
        });
    }

    @Test
    void shouldNotCreateRefreshTokenWithRevokedBeforeCreated() {
        long timeOffset = 1000L; // 1 second
        Instant revokedAt = createdAt.minusMillis(timeOffset); // revokedAt is before createdAt
        assertThrows(IllegalArgumentException.class, () -> {
            new RefreshToken(tokenId, userId, tokenHash, expiresAt, revokedAt, // revoked before created
                    createdAt);
        });
    }

    @Test
    void shouldNotCreateRefreshTokenWithNullCreatedAt() {
        assertThrows(NullPointerException.class, () -> {
            new RefreshToken(tokenId, userId, tokenHash, expiresAt, null, null // null createdAt
            );
        });
    }

    @Test
    void shouldCreateRefreshTokenWithRevokedEqualToCreated() {
        Instant revokedAt = createdAt; // revokedAt is equal to createdAt
        RefreshToken refreshToken = new RefreshToken(tokenId, userId, tokenHash, expiresAt, revokedAt, // revoked equal
                                                                                                       // to created
                createdAt);
        assertEquals(revokedAt, refreshToken.getRevokedAt());
    }

    @Test
    void shouldCreateRefreshTokenWithRevokedAfterCreated() {
        long timeOffset = 1000L; // 1 second
        Instant revokedAt = createdAt.plusMillis(timeOffset); // revokedAt is after createdAt
        RefreshToken refreshToken = new RefreshToken(tokenId, userId, tokenHash, expiresAt, revokedAt, // revoked after
                                                                                                       // created
                createdAt);
        assertEquals(revokedAt, refreshToken.getRevokedAt());
    }

    @Test
    void shouldNotCreateRefreshTokenWithNullId() {
        assertThrows(NullPointerException.class, () -> {
            new RefreshToken(null, // null id
                    userId, tokenHash, expiresAt, null, createdAt);
        });
    }

    @Test
    void shouldNotCreateRefreshTokenWithNullUserId() {
        assertThrows(NullPointerException.class, () -> {
            new RefreshToken(tokenId, null, // null userId
                    tokenHash, expiresAt, null, createdAt);
        });
    }

    @Test
    void shouldNotCreateRefreshTokenWithNullTokenHash() {
        assertThrows(NullPointerException.class, () -> {
            new RefreshToken(tokenId, userId, null, // null tokenHash
                    expiresAt, null, createdAt);
        });
    }

    @Test
    void should_isExpired_ReturnTrueWhenNowIsAfterExpiresAt() {
        RefreshToken refreshToken = createValidRefreshToken();
        Instant now = expiresAt.plusSeconds(1); // now is after expiresAt
        assertTrue(refreshToken.isExpired(now));
    }

    @Test
    void should_isExpired_ReturnFalseWhenNowIsBeforeExpiresAt() {
        RefreshToken refreshToken = createValidRefreshToken();
        Instant now = expiresAt.minusSeconds(1); // now is before expiresAt
        assertFalse(refreshToken.isExpired(now));
    }

    @Test
    void should_isExpired_ReturnTrueWhenNowIsEqualToExpiresAt() {
        RefreshToken refreshToken = createValidRefreshToken();
        Instant now = expiresAt; // now is equal to expiresAt
        assertTrue(refreshToken.isExpired(now));
    }

    @Test
    void should_isExpired_ThrowExceptionWhenNowIsNull() {
        RefreshToken refreshToken = createValidRefreshToken();
        assertThrows(IllegalArgumentException.class, () -> {
            refreshToken.isExpired(null); // now is null
        });
    }

    @Test
    void should_isRevoked_ReturnFalseWhenRevokedAtIsNull() {
        RefreshToken refreshToken = createValidRefreshToken();
        assertFalse(refreshToken.isRevoked());
    }

    @Test
    void should_isRevoked_ReturnTrueWhenRevokedAtIsNotNull() {
        long timeOffset = 1000L; // 1 second
        Instant revokedAt = createdAt.plusMillis(timeOffset); // revokedAt is after createdAt
        RefreshToken refreshToken = new RefreshToken(tokenId, userId, tokenHash, expiresAt, revokedAt, // revoked after
                                                                                                       // created
                createdAt);
        assertTrue(refreshToken.isRevoked());
    }

    private RefreshToken createValidRefreshToken() {
        return new RefreshToken(tokenId, userId, tokenHash, expiresAt, null, // not revoked
                createdAt);
    }

}
