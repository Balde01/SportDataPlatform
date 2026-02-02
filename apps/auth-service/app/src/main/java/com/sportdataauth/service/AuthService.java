package com.sportdataauth.service;

import javax.security.sasl.AuthenticationException;

import org.mindrot.jbcrypt.BCrypt;

import com.sportdataauth.dto.LoginRequest;
import com.sportdataauth.dto.TokenResponse;
import com.sportdataauth.model.User;
import com.sportdataauth.model.UserStatus;
import com.sportdataauth.repository.UserRepository;
import com.sportdataauth.security.JwtService;
import com.sportdataauth.security.PasswordHasher;
import com.sportdataauth.security.RefreshTokenService;
import com.sportdataauth.util.Clock;



public class AuthService {
		private final UserRepository userRepository;
	private final PasswordHasher passwordHasher;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;
	private final Clock clock;

	public AuthService(UserRepository userRepository, PasswordHasher passwordHasher,
					   JwtService jwtService, RefreshTokenService refreshTokenService, Clock clock) {
		this.userRepository = userRepository;
		this.passwordHasher = passwordHasher;
		this.jwtService = jwtService;
		this.refreshTokenService = refreshTokenService;
		this.clock = clock;
	}

	public TokenResponse login(LoginRequest loginRequest) throws AuthenticationException {
		User user = userRepository.findByEmail(loginRequest.getEmail());
		if (user == null || !passwordHasher.matches(loginRequest.getPassword(), user.getPasswordHash())) {
			throw new AuthenticationException("Invalid email or password");
		}

		if (user.getStatus() == UserStatus.LOCKED) {
			throw new AuthenticationException("User account is locked");
		}

		if (user.getStatus() == UserStatus.DISABLED) {
			throw new AuthenticationException("User account is disabled");
		}

		// Generate JWT token
		

		return null;
	}
    
  
  
}

