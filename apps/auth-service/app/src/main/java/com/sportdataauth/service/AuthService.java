package com.sportdataauth.service;

import javax.security.sasl.AuthenticationException;
import com.sportdataauth.dto.LoginRequest;
import com.sportdataauth.dto.TokenResponse;
import com.sportdataauth.model.User;
import com.sportdataauth.model.UserStatus;
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
		User user = userRepository.findByEmail(loginRequest.getEmail());

		if (user == null){
			throw new AuthenticationException("Invalid email or password");
		}

		if (user.getStatus() == UserStatus.LOCKED) {
			throw new AuthenticationException("User account is locked");
		}

		if (user.getStatus() == UserStatus.DISABLED) {
			throw new AuthenticationException("User account is disabled");
		}

		if (user.getPasswordHash() == null) {
			throw new AuthenticationException("User has not set a password");
		}

		boolean passwordMatches = passwordHasher.matches(loginRequest.getPassword(), user.getPasswordHash());
		if (!passwordMatches) {
			user.setFailedAttempts(user.getFailedAttempts() + 1);
			if (user.getFailedAttempts() >= maxFailedAttempts) {
				user.setStatus(UserStatus.LOCKED);
			}
			userRepository.save(user);
			throw new AuthenticationException("Invalid email or password");
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

