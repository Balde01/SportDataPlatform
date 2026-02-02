package com.sportdataauth.model;

import java.time.LocalDateTime;
import java.util.UUID;



public class InvitationToken {
	private UUID id;
	private UUID userId;
	private String tokenHash;
	private TokenPurpose purpose;
	private LocalDateTime expiresAt;
	private LocalDateTime usedAt;
	private LocalDateTime createdAt;
	private LocalDateTime clock;
	
	public InvitationToken(UUID id, UUID userId, String tokenHash, TokenPurpose purpose, LocalDateTime expiresAt,
			LocalDateTime usedAt, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.userId = userId;
		this.tokenHash = tokenHash;
		this.purpose = purpose;
		this.expiresAt = expiresAt;
		this.usedAt = usedAt;
		this.createdAt = createdAt;
	}
	
	public boolean isExpired(LocalDateTime now) {
		return expiresAt.isBefore(now);
	}
	
	public boolean isUsed() {
		return usedAt != null;
	}
	public UUID getId() {
		return id;
	}
	public UUID getUserId() {
		return userId;
	}
	public void setUsedAt(LocalDateTime usedAt) {
		this.usedAt = usedAt;
	}

}
