package com.sportdataauth.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import com.sportdataauth.dto.RegisterRequest;
import com.sportdataauth.model.User;
import com.sportdataauth.repository.UserRepository;
import com.sportdataauth.security.PasswordHasher;
import com.sportdataauth.util.Clock;
import com.sportdataauth.util.Validator;

public class RegisterService {

    private final UserRepository userRepository;
    private final Validator validator;
    private final PasswordHasher passwordHasher;
    private final Clock clock;
    
    public RegisterService(UserRepository userRepository, Validator validator,
						   PasswordHasher passwordHasher, Clock clock) {
		this.userRepository = userRepository;
		this.validator = validator;
		this.passwordHasher = passwordHasher;
		this.clock = clock;
	}
    
    public void register(RegisterRequest registerRequest) throws Exception {
		// Validate input
		validator.isValidEmail(registerRequest.getEmail());
		validator.isValidPassword(registerRequest.getPassword());
		
		// Check if user already exists
		if (userRepository.findByEmail(registerRequest.getEmail()) != null) {
			throw new Exception("User with this email already exists");
		}
		
		// Hash password
		String hashedPassword = passwordHasher.hash(registerRequest.getPassword());
		
		// Generate user ID
		UUID userId = java.util.UUID.randomUUID();
		
		// Create new user
		User newUser = new User(
				userId,
				registerRequest.getEmail(),
				hashedPassword,
				null, // roles
				null, // status
				0,    // failedAttempts
				clock.now(),
				null  // lastLoginAt
		);

		
		// Save user to repository
		userRepository.save(newUser);
	}
}
