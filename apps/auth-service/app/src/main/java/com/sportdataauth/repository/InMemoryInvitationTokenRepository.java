package com.sportdataauth.repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sportdataauth.model.InvitationToken;
import com.sportdataauth.util.SystemClock;

public class InMemoryInvitationTokenRepository implements InvitationTokenRepository {

    private final Map<UUID, InvitationToken> byIdMap = new HashMap<>();
    private final Map<String, UUID> idByTokenHashMap = new HashMap<>();

    @Override
    public InvitationToken findValidByTokenHash(String tokenHash, LocalDateTime now) {
        UUID id = idByTokenHashMap.get(tokenHash);
        if (id == null) {
            return null;
        }
        InvitationToken token = byIdMap.get(id);
        if (token == null) {
            return null;
        }
        if (token.isUsed() || token.isExpired(now)) {
            return null;
        }
        return token;
    }

    @Override
    public void save(InvitationToken token) {
        byIdMap.put(token.getId(), token);
        idByTokenHashMap.put(token.getTokenHash(), token.getId());
    }

    @Override
    public void markUsed(UUID tokenId, LocalDateTime usedAt) {
        byIdMap.computeIfPresent(tokenId, (id, token) -> {
            if (token.isUsed()) {
                throw new IllegalStateException("TOKEN_ALREADY_USED");
            }
            else if (token.isExpired(usedAt)) {
                throw new IllegalStateException("TOKEN_ALREADY_EXPIRED");
            }
            return new InvitationToken(
                    token.getId(),
                    token.getUserId(),
                    token.getTokenHash(),
                    token.getPurpose(),
                    token.getExpiresAt(),
                    usedAt,
                    token.getCreatedAt()
            );
        });
    }

}
