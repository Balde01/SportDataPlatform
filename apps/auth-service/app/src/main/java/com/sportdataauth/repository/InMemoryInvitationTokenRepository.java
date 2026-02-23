package com.sportdataauth.repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sportdataauth.domain.entity.InvitationToken;

public class InMemoryInvitationTokenRepository implements InvitationTokenRepository {

   private final Map<UUID, InvitationToken> byIdMap = new HashMap<>();
   private final Map<String, UUID> idByTokenHashMap = new HashMap<>();

   @Override
   public void save(InvitationToken token) {
       byIdMap.put(token.getId(), token);
       idByTokenHashMap.put(token.getTokenHash(), token.getId());
   }

   @Override
   public synchronized InvitationToken consumeValidByTokenHash(String tokenHash, Instant now) {
       UUID id = idByTokenHashMap.get(tokenHash);
       if (id == null) return null;

       InvitationToken token = byIdMap.get(id);
       if (token == null) return null;

       if (token.isUsed() || token.isExpired(now)) return null;

       InvitationToken used = new InvitationToken(
               token.getId(),
               token.getUserId(),
               token.getTokenHash(),
               token.getPurpose(),
               token.getExpiresAt(),
               now,              // usedAt
               token.getCreatedAt()
       );

       byIdMap.put(id, used);
       return used;
   }
}