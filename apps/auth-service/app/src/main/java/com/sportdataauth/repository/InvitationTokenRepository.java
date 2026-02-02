package com.sportdataauth.repository;


import java.time.LocalDateTime;

import com.sportdataauth.model.InvitationToken;

public interface InvitationTokenRepository {
	InvitationToken findValidByTokenHash(String tokenHash, LocalDateTime now);
	void save(InvitationToken token);
	void markAsUsed(InvitationToken token, LocalDateTime usedAt);
	
}

