package com.sportdataauth.service;

import java.util.UUID;

import com.sportdataauth.dto.InviteAcceptRequest;
import com.sportdataauth.dto.ProvisionAgentRequest;
import com.sportdataauth.model.TokenPurpose;
import com.sportdataauth.repository.InvitationTokenRepository;
import com.sportdataauth.repository.UserRepository;
import com.sportdataauth.security.PasswordHasher;
import com.sportdataauth.util.Clock;
import com.sportdataauth.util.TokenGenerator;
import com.sportdataauth.util.TokenHasher;

public class InvitationService {
	private final UserRepository userRepository;
	private final InvitationTokenRepository tokenRepository;
	private final TokenGenerator tokenGenerator;
	private final TokenHasher tokenHasher;
	private final PasswordHasher passwordHasher;
	private final Clock clock;
	private int inviteValidDays;
	
	public InvitationService(UserRepository userRepository, InvitationTokenRepository tokenRepository,
			TokenGenerator tokenGenerator, TokenHasher tokenHasher, PasswordHasher passwordHasher, Clock clock,
			int inviteValidDays) {
		this.userRepository = userRepository;
		this.tokenRepository = tokenRepository;
		this.tokenGenerator = tokenGenerator;
		this.tokenHasher = tokenHasher;
		this.passwordHasher = passwordHasher;
		this.clock = clock;
		this.inviteValidDays = inviteValidDays;
	}
	
	public void provisionAgent(ProvisionAgentRequest request) {
		// Implementation goes here
	}
	
	public String createInvite(UUID userId, TokenPurpose purpose) {
		// Implementation goes here
		return null;
	}
	
	public void acceptInvite(InviteAcceptRequest request) {
		// Implementation goes here
	}

}
