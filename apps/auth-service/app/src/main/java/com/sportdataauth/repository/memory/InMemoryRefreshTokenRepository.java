package com.sportdataauth.repository.memory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.sportdataauth.domain.entity.RefreshToken;
import com.sportdataauth.domain.exception.InvalidRequestException;
import com.sportdataauth.domain.exception.RefreshTokenNotConsumableException;
import com.sportdataauth.repository.RefreshTokenRepository;

public class InMemoryRefreshTokenRepository implements RefreshTokenRepository {

    private final Map<UUID, Set<UUID>> tokenIdsByUserId = new HashMap<>();
    // Single source of truth for refresh token state
    private final Map<UUID, RefreshToken> byIdMap = new HashMap<>();
    private final Map<String, UUID> idByTokenHashMap = new HashMap<>();

    @Override
    public synchronized void save(RefreshToken refreshToken) {
        if (refreshToken == null) {
            throw InvalidRequestException.nullValue("refreshToken");
        }
        if (byIdMap.containsKey(refreshToken.getId())) {
            throw new IllegalArgumentException("REFRESH_TOKEN_ID_ALREADY_EXISTS");
        }
        if (idByTokenHashMap.containsKey(refreshToken.getTokenHash())) {
            throw new IllegalArgumentException("REFRESH_TOKEN_HASH_ALREADY_EXISTS");
        }
        tokenIdsByUserId.computeIfAbsent(refreshToken.getUserId(), userId -> new HashSet<>()).add(refreshToken.getId());

        byIdMap.put(refreshToken.getId(), refreshToken);
        idByTokenHashMap.put(refreshToken.getTokenHash(), refreshToken.getId());
    }

    @Override
    public synchronized Optional<RefreshToken> findByTokenHash(String tokenHash) {
        if (tokenHash == null) {
            throw InvalidRequestException.nullValue("tokenHash");
        }

        UUID tokenId = idByTokenHashMap.get(tokenHash);
        if (tokenId == null) {
            return Optional.empty();
        }
        RefreshToken foundToken = byIdMap.get(tokenId);
        if (foundToken == null) {
            throw new IllegalStateException("REFRESH_TOKEN_STATE_INCONSISTENT");
        }
        return Optional.of(foundToken);
    }

    @Override
    public synchronized RefreshToken consumeValidByTokenHash(String tokenHash, Instant now) {
        if (tokenHash == null) {
            throw InvalidRequestException.nullValue("tokenHash");
        }

        if (now == null) {
            throw InvalidRequestException.nullValue("now");
        }
        UUID tokenId = idByTokenHashMap.get(tokenHash);
        if (tokenId == null) {
            throw new RefreshTokenNotConsumableException();
        }
        RefreshToken refreshTokenToConsume = byIdMap.get(tokenId);
        if (refreshTokenToConsume == null) {
            throw new IllegalStateException("REFRESH_TOKEN_STATE_INCONSISTENT");
        }
        if (refreshTokenToConsume.isExpired(now) || refreshTokenToConsume.isRevoked()) {
            throw new RefreshTokenNotConsumableException();
        }
        RefreshToken newRToken = createRevokedRefreshToken(refreshTokenToConsume, now);
        byIdMap.put(tokenId, newRToken);

        return newRToken;
    }

    @Override
    public synchronized Optional<RefreshToken> revokeByTokenHash(String tokenHash, Instant now) {

        if (tokenHash == null) {
            throw InvalidRequestException.nullValue("tokenHash");
        }

        if (now == null) {
            throw InvalidRequestException.nullValue("now");
        }
        UUID tokenId = idByTokenHashMap.get(tokenHash);
        if (tokenId == null) {
            return Optional.empty();
        }
        RefreshToken foundToken = byIdMap.get(tokenId);
        if (foundToken == null) {
            throw new IllegalStateException("REFRESH_TOKEN_STATE_INCONSISTENT");
        }
        if (foundToken.isRevoked()) {
            return Optional.empty();
        }
        RefreshToken revokedToken = createRevokedRefreshToken(foundToken, now);
        byIdMap.put(tokenId, revokedToken);

        return Optional.of(revokedToken);
    }

    @Override
    public synchronized Optional<RefreshToken> revokeById(UUID tokenId, Instant now) {
        if (tokenId == null) {
            throw InvalidRequestException.nullValue("tokenId");
        }

        if (now == null) {
            throw InvalidRequestException.nullValue("now");
        }
        RefreshToken foundToken = byIdMap.get(tokenId);
        if (foundToken == null) {
            return Optional.empty();
        }
        if (foundToken.isRevoked()) {
            return Optional.empty();
        }
        RefreshToken revokedToken = createRevokedRefreshToken(foundToken, now);
        byIdMap.put(tokenId, revokedToken);

        return Optional.of(revokedToken);
    }

    @Override
    public synchronized int revokeAllByUserId(UUID userId, Instant now) {
        List<RefreshToken> revokedTokens = new ArrayList<>();
        if (userId == null) {
            throw InvalidRequestException.nullValue("userId");
        }

        if (now == null) {
            throw InvalidRequestException.nullValue("now");
        }
        Set<UUID> refreshTokenIds = tokenIdsByUserId.get(userId);
        if (refreshTokenIds == null) {
            return 0;
        }
        // Prepare all revoked token states before mutating the repository
        for (UUID tokenId : refreshTokenIds) {
            RefreshToken foundToken = byIdMap.get(tokenId);
            if (foundToken == null) {
                throw new IllegalStateException("REFRESH_TOKEN_STATE_INCONSISTENT");
            }

            if (!foundToken.isRevoked()) {
                RefreshToken revokedToken = createRevokedRefreshToken(foundToken, now);
                revokedTokens.add(revokedToken);
            }
        }
        // Apply the prepared changes only after validation succeeds
        for (RefreshToken rToken : revokedTokens) {
            byIdMap.put(rToken.getId(), rToken);
        }

        return revokedTokens.size();
    }

    private RefreshToken createRevokedRefreshToken(RefreshToken oldRefreshToken, Instant now) {
        return new RefreshToken(oldRefreshToken.getId(), oldRefreshToken.getUserId(), oldRefreshToken.getTokenHash(),
                oldRefreshToken.getExpiresAt(), now, oldRefreshToken.getCreatedAt());

    }
}
