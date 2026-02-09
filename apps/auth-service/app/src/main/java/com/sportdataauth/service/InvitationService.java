package com.sportdataauth.service;


import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


import com.sportdataauth.dto.InviteAcceptRequest;
import com.sportdataauth.dto.ProvisionAgentRequest;
import com.sportdataauth.model.InvitationToken;
import com.sportdataauth.model.TokenPurpose;
import com.sportdataauth.model.User;
import com.sportdataauth.model.UserStatus;
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
	
	public void provisionAgent(ProvisionAgentRequest request) throws Exception {
		// Check if user already exists
		if (userRepository.findByEmail(request.getEmail()) == null) {
			// Create User
			UUID userId = java.util.UUID.randomUUID();			
			User newUser = new User(
					userId,
					request.getEmail(),
					null, // passwordHash
					Set.of(com.sportdataauth.model.Role.AGENT), // roles
					UserStatus.DISABLED, // status
					0,    // failedAttempts
					clock.now(),
					null  // lastLoginAt
			);
			userRepository.save(newUser);
		}
		// Create invite token
		String rawToken = createInvite(userRepository.findByEmail(request.getEmail()).getId(), TokenPurpose.FIRST_PASSWORD_SET);
		// Send email with rawToken to user (omitted)


	}
	
	public String createInvite(UUID userId, TokenPurpose purpose) {
		// Create invite token
		String rawToken = tokenGenerator.generateToken();
		String hashedToken = tokenHasher.hash(rawToken);
		LocalDateTime expiresAt = clock.now().plusDays(inviteValidDays);
		LocalDateTime usedAt = null;
		InvitationToken inviteToken = new InvitationToken(
				java.util.UUID.randomUUID(),
				userId,
				hashedToken,
				purpose,
				expiresAt,
				usedAt,
				clock.now()
		);
		tokenRepository.save(inviteToken);
		return rawToken;
	}
	
	public void acceptInvite(InviteAcceptRequest request) {
		String tokenHash = tokenHasher.hash(request.getToken());
		InvitationToken inviteToken = tokenRepository.findValidByTokenHash(tokenHash, clock.now());
		if (inviteToken == null) {
			throw new IllegalArgumentException("Invalid or expired token");
		}
		User user = userRepository.findById(inviteToken.getUserId());
		if (user == null) {
			throw new IllegalStateException("User not found for token");
		}
		// Set new password
		String newHashedPassword = passwordHasher.hash(request.getNewPassword());
		user.setPasswordHash(newHashedPassword);
		user.setStatus(UserStatus.ACTIVE);
		user.setFailedAttempts(0);
		userRepository.save(user);
		// Mark token as used
		tokenRepository.markUsed(inviteToken.getId(), clock.now());
	}

}
