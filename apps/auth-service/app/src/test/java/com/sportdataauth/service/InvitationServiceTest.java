package com.sportdataauth.service;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.enums.Role;
import com.sportdataauth.domain.enums.TokenPurpose;
import com.sportdataauth.domain.enums.UserStatus;
import com.sportdataauth.domain.value.Email;
import com.sportdataauth.dto.InviteAcceptRequest;
import com.sportdataauth.dto.ProvisionAgentRequest;
import com.sportdataauth.policy.CredentialPolicy;
import com.sportdataauth.policy.DefaultCredentialPolicy;
import com.sportdataauth.repository.InMemoryInvitationTokenRepository;
import com.sportdataauth.repository.InMemoryUserRepository;
import com.sportdataauth.repository.InvitationTokenRepository;
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
import com.sportdataauth.util.TokenHasher;

public class InvitationServiceTest {
   private AuthService authService;
   private RegisterService registerService;
   private InvitationService invitationService;

   private TokenHasher tokenHasher;
   private InvitationTokenRepository tokenRepository;
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
       tokenHasher = new TokenHasher();
       tokenRepository = new InMemoryInvitationTokenRepository();
       invitationService = new InvitationService(
                userRepository,
                tokenRepository, 
                tokenGenerator,
                tokenHasher, 
                passwordHasher, 
                credentialPolicy, 
                clock, 
                maxFailedAttempts);
       authService = new AuthService(
               userRepository,
               passwordHasher,
               jwtService,
               refreshTokenService,
               clock,
               maxFailedAttempts
       );

       registerService = new RegisterService(userRepository, credentialPolicy, passwordHasher, clock);
   }   

    @Test
    void shouldProvisionAgentAsDisabled() {
       ProvisionAgentRequest request = new ProvisionAgentRequest(email);
       assertDoesNotThrow(()->invitationService.provisionAgent(request));
    }

    @Test
    void shouldCreateInvitationToken() {
       UUID userId = UUID.randomUUID();
       TokenPurpose purpose = TokenPurpose.PASSWORD_RESET; 
       assertDoesNotThrow(()->invitationService.createInvite(userId, purpose));
    }

    @Test
    void shouldAcceptInviteSuccessfully() {
       UUID id = UUID.randomUUID();
       Email userEmail = Email.of("disabled@gmail.com");


       User user = new User(
               id,
               userEmail,
               null,
               Set.of(Role.CLIENT),
               UserStatus.DISABLED,
               0,
               clock.now(),
               null
       );
       userRepository.save(user);
       TokenPurpose purpose = TokenPurpose.PASSWORD_RESET; 
       String token = invitationService.createInvite(id, purpose);
       InviteAcceptRequest request = new InviteAcceptRequest(token, password);
       assertDoesNotThrow(()->invitationService.acceptInvite(request));
       
    }

    @Test
    void shouldFailWhenTokenExpired() {
        
    }

    @Test
    void shouldFailWhenTokenAlreadyUsed() {
        
    }

    @Test
    void shouldActivateUserAfterAcceptingInvite() {
        
    }

}

