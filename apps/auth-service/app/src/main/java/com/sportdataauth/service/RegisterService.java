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
    
    public UserResponse registerClient(RegisterRequest req) {
		String email = req.getEmail().trim().toLowerCase();
		String password = req.getPassword();
		if (!validator.isValidEmail(email)) {
			throw new IllegalArgumentException("INVALID_EMAIL");
		}
		if (!validator.isValidPassword(password)) {
			throw new IllegalArgumentException("WEAK_PASSWORD");
		}
		if (userRepository.findByEmail(email) != null) {
			throw new IllegalStateException("EMAIL_ALREADY_EXISTS");
		}
		String hashedPassword = passwordHasher.hash(password);
		User newUser = new User(
			UUID.randomUUID(),
			email,
			hashedPassword,
			Set.of(Role.CLIENT),
			UserStatus.ACTIVE,
			0,
			clock.now(),
			null
		);
		userRepository.save(newUser);
		return new UserResponse(
			newUser.getId(),
			newUser.getEmail(),
			newUser.getRoles(),
			newUser.getStatus(),
			newUser.getCreatedAt()
		);
	}
}
