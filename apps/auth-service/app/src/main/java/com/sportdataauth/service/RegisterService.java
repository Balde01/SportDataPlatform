package com.sportdataauth.service;
import java.util.Set;
import java.util.UUID;

import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.enums.Role;
import com.sportdataauth.domain.enums.UserStatus;
import com.sportdataauth.domain.value.Email;
import com.sportdataauth.dto.RegisterRequest;
import com.sportdataauth.dto.UserResponse;
import com.sportdataauth.policy.CredentialPolicy;
import com.sportdataauth.repository.UserRepository;
import com.sportdataauth.security.PasswordHasher;
import com.sportdataauth.util.Clock;

public class RegisterService {

    private final UserRepository userRepository;
    private final CredentialPolicy credentialPolicy;
    private final PasswordHasher passwordHasher;
    private final Clock clock;
    
    public RegisterService(UserRepository userRepository, CredentialPolicy credentialPolicy,
						   PasswordHasher passwordHasher, Clock clock) {
		this.userRepository = userRepository;
		this.credentialPolicy = credentialPolicy;
		this.passwordHasher = passwordHasher;
		this.clock = clock;
	}
    
    public UserResponse registerClient(RegisterRequest req) {
		Email email = Email.of(req.getEmail());
		String password = req.getPassword();
		if (!credentialPolicy.isEmailAllowed(email.value())) {
			throw new IllegalArgumentException("INVALID_EMAIL");
		}
		if (!credentialPolicy.isPasswordStrong(password)) {
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
			newUser.getEmail().toString(),
			newUser.getRoles(),
			newUser.getStatus(),
			newUser.getCreatedAt()
		);
	}
}
