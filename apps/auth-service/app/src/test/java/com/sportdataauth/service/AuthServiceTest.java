package com.sportdataauth.service;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.enums.Role;
import com.sportdataauth.domain.enums.UserStatus;
import com.sportdataauth.domain.exception.AccountDisabledException;
import com.sportdataauth.domain.exception.AccountLockedException;
import com.sportdataauth.domain.exception.InvalidCredentialsException;
import com.sportdataauth.domain.value.Email;
import com.sportdataauth.dto.LoginRequest;
import com.sportdataauth.dto.RegisterRequest;
import com.sportdataauth.policy.CredentialPolicy;
import com.sportdataauth.policy.DefaultCredentialPolicy;
import com.sportdataauth.repository.InMemoryUserRepository;
import com.sportdataauth.repository.UserRepository;
import com.sportdataauth.security.BcryptPasswordHasher;
import com.sportdataauth.security.InMemoryRefreshTokenService;
import com.sportdataauth.security.JwtService;
import com.sportdataauth.security.PasswordHasher;
import com.sportdataauth.security.RefreshTokenService;
import com.sportdataauth.security.SimpleJwtService;
import com.sportdataauth.util.Clock;
import com.sportdataauth.util.SystemClock;
import com.sportdataauth.util.TokenGenerator;

public class AuthServiceTest {

   private AuthService authService;
   private RegisterService registerService;

   private CredentialPolicy credentialPolicy;
   private UserRepository userRepository;
   private PasswordHasher passwordHasher;
   private JwtService jwtService;
   private RefreshTokenService refreshTokenService;
   private TokenGenerator tokenGenerator;
   private Clock clock;

   private final String email = "test@gmail.com";
   private final String password = "Secret123@";
   private final int maxFailedAttempts = 5;

   @BeforeEach
   public void setUp() {
       userRepository = new InMemoryUserRepository();
       passwordHasher = new BcryptPasswordHasher();
       tokenGenerator = new TokenGenerator();
       clock = new SystemClock();
       jwtService = new SimpleJwtService(tokenGenerator, clock);
       refreshTokenService = new InMemoryRefreshTokenService();
       credentialPolicy = new DefaultCredentialPolicy();

       authService = new AuthService(
               userRepository,
               passwordHasher,
               jwtService,
               refreshTokenService,
               clock,
               maxFailedAttempts
       );

       registerService = new RegisterService(userRepository, credentialPolicy, passwordHasher, clock);
       registerService.registerClient(new RegisterRequest(email, password));
   }

   @Test
   void shouldLoginSuccessfully() {
       LoginRequest req = new LoginRequest(email, password);
       assertDoesNotThrow(() -> authService.login(req));
   }

   @Test
   void shouldFailWhenPasswordIncorrect() {
       LoginRequest req = new LoginRequest(email, password + "Fake");
       assertThrows(InvalidCredentialsException.class, () -> authService.login(req));
   }

   @Test
   void shouldIncrementFailedAttempts() {
       Email e = Email.of(email);
       int before = userRepository.findByEmail(e).orElse(null).getFailedAttempts();

       LoginRequest req = new LoginRequest(email, password + "Fake");
       assertThrows(InvalidCredentialsException.class, () -> authService.login(req));

       int after = userRepository.findByEmail(e).orElse(null).getFailedAttempts();
       assertEquals(before + 1, after);
   }

   @Test
   void shouldLockUserAfterMaxAttempts() {
       for (int i = 0; i < maxFailedAttempts; i++) {
           LoginRequest req = new LoginRequest(email, password + "Fake");
           assertThrows(InvalidCredentialsException.class, () -> authService.login(req));
       }

       UserStatus status = userRepository.findByEmail(Email.of(email)).orElse(null).getStatus();
       assertEquals(UserStatus.LOCKED, status);
   }

   @Test
   void shouldResetFailedAttemptsOnSuccess() {
       for (int i = 0; i < maxFailedAttempts - 1; i++) {
           LoginRequest req = new LoginRequest(email, password + "Fake");
           assertThrows(InvalidCredentialsException.class, () -> authService.login(req));
       }

       assertDoesNotThrow(() -> authService.login(new LoginRequest(email, password)));

       int failedAttempts = userRepository.findByEmail(Email.of(email)).orElse(null).getFailedAttempts();
       assertEquals(0, failedAttempts);
   }

   @Test
   void shouldNotLoginWhenUserLocked() {
       for (int i = 0; i < maxFailedAttempts; i++) {
           LoginRequest req = new LoginRequest(email, password + "Fake");
           assertThrows(InvalidCredentialsException.class, () -> authService.login(req));
       }

       assertThrows(AccountLockedException.class, () -> authService.login(new LoginRequest(email, password)));
   }

   @Test
   void shouldNotLoginWhenUserDisabled() {
       UUID id = UUID.randomUUID();
       Email userEmail = Email.of("disabled@gmail.com");

       String hash = passwordHasher.hash(password);
       User user = new User(
               id,
               userEmail,
               hash,
               Set.of(Role.CLIENT),
               UserStatus.DISABLED,
               0,
               clock.now(),
               null
       );
       userRepository.save(user);

       assertThrows(AccountDisabledException.class,
               () -> authService.login(new LoginRequest("disabled@gmail.com", password)));
   }
}