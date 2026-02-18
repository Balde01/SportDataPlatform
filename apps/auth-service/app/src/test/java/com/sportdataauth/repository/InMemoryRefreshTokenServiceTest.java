package com.sportdataauth.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.enums.Role;
import com.sportdataauth.domain.enums.UserStatus;
import com.sportdataauth.domain.value.Email;
import com.sportdataauth.dto.LoginRequest;
import com.sportdataauth.dto.RegisterRequest;
import com.sportdataauth.policy.CredentialPolicy;
import com.sportdataauth.policy.DefaultCredentialPolicy;
import com.sportdataauth.security.BcryptPasswordHasher;
import com.sportdataauth.security.InMemoryRefreshTokenService;
import com.sportdataauth.security.JwtService;
import com.sportdataauth.security.PasswordHasher;
import com.sportdataauth.security.RefreshTokenService;
import com.sportdataauth.security.SimpleJwtService;
import com.sportdataauth.service.AuthService;
import com.sportdataauth.service.RegisterService;
import com.sportdataauth.util.Clock;
import com.sportdataauth.util.SystemClock;
import com.sportdataauth.util.TokenGenerator;

public class InMemoryRefreshTokenServiceTest {
    private AuthService authService;
    private RegisterService registerService;
    private PasswordHasher passwordHasher;
    private JwtService jwtService;
    private RefreshTokenService refreshTokenService;
    private TokenGenerator tokenGenerator;
    private UserRepository userRepository;
    private InvitationTokenRepository tokenRepository;
    private CredentialPolicy credentialPolicy;
    private Clock clock;
    private User user;
    private final UUID userId = UUID.randomUUID();
    private final Email userEmail = Email.of("DISABLED@GMAIL.COM");
    private final String password = "Secret123@";
    private final int maxFailedAttempts = 5;
   
    @BeforeEach
    public void setUp() {
        clock = new SystemClock();
        userRepository = new InMemoryUserRepository();
        tokenRepository = new InMemoryInvitationTokenRepository();
        credentialPolicy = new DefaultCredentialPolicy();
        userRepository = new InMemoryUserRepository();
        passwordHasher = new BcryptPasswordHasher();
        tokenGenerator = new TokenGenerator();
        clock = new SystemClock();
        jwtService = new SimpleJwtService(tokenGenerator, clock);
        refreshTokenService = new InMemoryRefreshTokenService();

        authService = new AuthService(
                userRepository,
                passwordHasher,
                jwtService,
                refreshTokenService,
                clock,
                maxFailedAttempts
        );
        registerService = new RegisterService(userRepository, credentialPolicy, passwordHasher, clock);
        registerService.registerClient(new RegisterRequest(userEmail.value(), password));
    } 

    @Test
    void shouldRotateRefreshToken() {
        LoginRequest req = new LoginRequest(userEmail.value(), password);
        assertDoesNotThrow(() -> authService.login(req));
    }

    @Test
    void shouldInvalidateOldTokenAfterRotation() {
        // TODO: implement test
    }

    @Test
    void shouldValidateRefreshToken() {
        // TODO: implement test
    }

    @Test
    void shouldRevokeRefreshToken() {
        // TODO: implement test
    }

    @Test
    void shouldRevokeAllTokensForUser() {
        // TODO: implement test
    }

}

