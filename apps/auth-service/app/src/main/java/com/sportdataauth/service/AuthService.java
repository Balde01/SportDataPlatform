package com.sportdataauth.service;

import javax.security.sasl.AuthenticationException;

import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.enums.UserStatus;
import com.sportdataauth.domain.exception.AccountDisabledException;
import com.sportdataauth.domain.exception.AccountLockedException;
import com.sportdataauth.domain.exception.InvalidCredentialsException;
import com.sportdataauth.domain.exception.InvalidRequestException;
import com.sportdataauth.domain.exception.UserNotEligibleException;
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
		
		if (loginRequest == null) throw InvalidRequestException.nullValue("request");
		if (loginRequest.getPassword() == null) throw InvalidRequestException.nullValue("password");

		Email email = Email.of(loginRequest.getEmail());
		User user = userRepository.findByEmail(email).orElse(null);

		if (user == null){
			throw new InvalidCredentialsException();
		}

		if (user.getStatus() == UserStatus.LOCKED) {
			throw new AccountLockedException();
		}

		if (user.getStatus() == UserStatus.DISABLED) {
			throw new AccountDisabledException();
		}

		if (user.getPasswordHash() == null) {
			throw new UserNotEligibleException("PASSWORD_NOT_SET");
		}

		boolean passwordMatches = passwordHasher.matches(loginRequest.getPassword(), user.getPasswordHash());
		if (!passwordMatches) {
			user.setFailedAttempts(user.getFailedAttempts() + 1);
			if (user.getFailedAttempts() >= maxFailedAttempts) {
				user.setStatus(UserStatus.LOCKED);
			}
			userRepository.save(user);
			throw new InvalidCredentialsException();
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

