package com.sportdataauth.model;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
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
this.id = Objects.requireNonNull(id, "id is required");
       this.userId = Objects.requireNonNull(userId, "userId is required");
       this.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash is required");
       this.purpose = Objects.requireNonNull(purpose, "purpose is required");
       this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt is required");
       this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
       // Invariants simples
       if (!expiresAt.isAfter(createdAt)) {
           throw new IllegalArgumentException("expiresAt must be after createdAt");
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