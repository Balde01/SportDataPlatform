package com.sportdataauth.repository;


import java.time.LocalDateTime;
import java.util.UUID;

import com.sportdataauth.domain.entity.InvitationToken;

public interface InvitationTokenRepository {
	InvitationToken findValidByTokenHash(String tokenHash, LocalDateTime now);
	void save(InvitationToken token);
	void markUsed(UUID tokenId, LocalDateTime usedAt);
	
}

