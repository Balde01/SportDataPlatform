package com.sportdataauth.service;

import javax.security.sasl.AuthenticationException;

import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.enums.UserStatus;
import com.sportdataauth.domain.value.Email;
import com.sportdataauth.dto.LoginRequest;
import com.sportdataauth.dto.TokenResponse;
import com.sportdataauth.repository.UserRepository;
import com.sportdataauth.security.JwtService;
import com.sportdataauth.security.JwtToken;
import com.sportdataauth.security.PasswordHasher;
import com.sportdataauth.security.RefreshTokenService;
import com.sportdataauth.util.Clock;



public class AuthService {
	private final UserRepository userRepository;
	private final PasswordHasher passwordHasher;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;
	private final Clock clock;
	private final int maxFailedAttempts;

	public AuthService(UserRepository userRepository, PasswordHasher passwordHasher,
					   JwtService jwtService, RefreshTokenService refreshTokenService, Clock clock, int maxFailedAttempts) {
		this.userRepository = userRepository;
		this.passwordHasher = passwordHasher;
		this.jwtService = jwtService;
		this.refreshTokenService = refreshTokenService;
		this.clock = clock;
		this.maxFailedAttempts = maxFailedAttempts;
	}

	public TokenResponse login(LoginRequest loginRequest) throws AuthenticationException {
		Email email = Email.of(loginRequest.getEmail());
		User user = userRepository.findByEmail(email);

		if (user == null){
			throw new AuthenticationException("INVALID_EMAIL_OR_PASSWORD");
		}

		if (user.getStatus() == UserStatus.LOCKED) {
			throw new AuthenticationException("USER_ACCOUNT_LOCKED");
		}

		if (user.getStatus() == UserStatus.DISABLED) {
			throw new AuthenticationException("USER_ACCOUNT_DISABLED");
		}

		if (user.getPasswordHash() == null) {
			throw new AuthenticationException("USER_HAS_NO_PASSWORD");
		}

		boolean passwordMatches = passwordHasher.matches(loginRequest.getPassword(), user.getPasswordHash());
		if (!passwordMatches) {
			user.setFailedAttempts(user.getFailedAttempts() + 1);
			if (user.getFailedAttempts() >= maxFailedAttempts) {
				user.setStatus(UserStatus.LOCKED);
			}
			userRepository.save(user);
			throw new AuthenticationException("INVALID_EMAIL_OR_PASSWORD");
		}
		// Reset failed attempts on successful login
		user.setFailedAttempts(0);
		user.setLastLoginAt(clock.now());
		userRepository.save(user);


		// Generate JWT token
		JwtToken jwtToken = jwtService.generateAccessToken(user);
		// Generate refresh token
		String refreshToken = refreshTokenService.rotateRefreshToken(user.getId());
		return new TokenResponse(jwtToken.getToken(), refreshToken, jwtToken.getExpiresAt());
	}
    
  
  
}

