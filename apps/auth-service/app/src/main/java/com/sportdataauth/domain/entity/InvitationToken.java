package com.sportdataauth.domain.entity;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.sportdataauth.domain.enums.TokenPurpose;
import com.sportdataauth.domain.exception.InvalidInviteExpiryException;
public class InvitationToken {
   private final UUID id;
   private final UUID userId;
   private final String tokenHash;
   private final TokenPurpose purpose;
   private final LocalDateTime expiresAt;
   private final LocalDateTime createdAt;
   private LocalDateTime usedAt; // modifié via Repository (markUsed)
   public InvitationToken(UUID id,
                          UUID userId,
                          String tokenHash,
                          TokenPurpose purpose,
                          LocalDateTime expiresAt,
                          LocalDateTime usedAt,
                          LocalDateTime createdAt) {
       this.id = Objects.requireNonNull(id, "ID_IS_REQUIRED");
       this.userId = Objects.requireNonNull(userId, "USER_ID_IS_REQUIRED");
       this.tokenHash = Objects.requireNonNull(tokenHash, "TOKEN_HASH_IS_REQUIRED");
       this.purpose = Objects.requireNonNull(purpose, "PURPOSE_IS_REQUIRED");
       this.expiresAt = Objects.requireNonNull(expiresAt, "EXPIRES_AT_IS_REQUIRED");
       this.createdAt = Objects.requireNonNull(createdAt, "CREATED_AT_IS_REQUIRED");
       // Invariants simples
       if (!expiresAt.isAfter(createdAt)) {
           throw new InvalidInviteExpiryException();
       }
       this.usedAt = usedAt;
   }
   public boolean isExpired(LocalDateTime now) {
       return expiresAt.isBefore(now);
   }
   public boolean isUsed() {
       return usedAt != null;
   }
   public UUID getId() { return id; }
   public UUID getUserId() { return userId; }
   public String getTokenHash() { return tokenHash; }
   public TokenPurpose getPurpose() { return purpose; }
   public LocalDateTime getExpiresAt() { return expiresAt; }
   public LocalDateTime getUsedAt() { return usedAt; }
   public LocalDateTime getCreatedAt() { return createdAt; }
}