package com.sportdataauth.service;
import java.util.Set;
import java.util.UUID;


import com.sportdataauth.dto.RegisterRequest;
import com.sportdataauth.dto.UserResponse;
import com.sportdataauth.model.Role;
import com.sportdataauth.model.User;
import com.sportdataauth.model.UserStatus;
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
    
    public UserResponse registerClient(RegisterRequest registerRequest) throws Exception {
		// Validate input
		boolean isValidEmailResponse =validator.isValidEmail(registerRequest.getEmail());
		boolean isValidPasswordResponse = validator.isValidPassword(registerRequest.getPassword());
		
		if (!isValidEmailResponse) {
			throw new Exception("Invalid email format");
		}
		
		if (!isValidPasswordResponse) {
			throw new Exception("Invalid password format");
		}
		
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
				Set.of(Role.CLIENT), // roles
				UserStatus.ACTIVE, // status
				0,    // failedAttempts
				clock.now(),
				null  // lastLoginAt
		);

		
		// Save user to repository
		userRepository.save(newUser);
		return new UserResponse(newUser.getId(), newUser.getEmail(), newUser.getRoles(), newUser.getStatus(), newUser.getCreatedAt());
	}
}
