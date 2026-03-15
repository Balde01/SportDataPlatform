package com.sportdataauth.service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.enums.Role;
import com.sportdataauth.domain.enums.TokenPurpose;
import com.sportdataauth.domain.enums.UserStatus;
import com.sportdataauth.domain.exception.InvitationTokenNotFoundException;
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
import com.sportdataauth.security.PasswordHasher;
import com.sportdataauth.util.NoopTransactionRunner;
import com.sportdataauth.util.SystemClock;
import com.sportdataauth.util.TokenGenerator;
import com.sportdataauth.util.TokenHasher;
import com.sportdataauth.util.TransactionRunner;

public class InvitationServiceTest {
   private InvitationService invitationService;

   private TokenHasher tokenHasher;
   private InvitationTokenRepository tokenRepository;
   private CredentialPolicy credentialPolicy;
   private UserRepository userRepository;
   private PasswordHasher passwordHasher;
   private TokenGenerator tokenGenerator;
   private SystemClock clock;
   private TransactionRunner tx;
   private User user;

   private final UUID userId = UUID.randomUUID();
   private final Email userEmail = Email.of("disabled@gmail.com");
   private final String password = "Secret123@";
   private final int inviteValidDays = 7;

   @BeforeEach
   public void setUp() {
      userRepository = new InMemoryUserRepository();
      passwordHasher = new BcryptPasswordHasher();
      tokenGenerator = new TokenGenerator();
      clock = new SystemClock();
      credentialPolicy = new DefaultCredentialPolicy();
      tokenHasher = new TokenHasher();
      tokenRepository = new InMemoryInvitationTokenRepository();
      tx = new NoopTransactionRunner();

      invitationService = new InvitationService(
         userRepository,
         tokenRepository,
         tokenGenerator,
         tokenHasher,
         passwordHasher,
         credentialPolicy,
         clock,
         inviteValidDays,
         tx
      );

      user = new User(
         userId,
         userEmail,
         null,
         Set.of(Role.AGENT),
         UserStatus.DISABLED,
         0,
         clock.now(),
         null
      );
      userRepository.insert(user);
   }   

   @Test
   void shouldProvisionAgentAsDisabled() {
       ProvisionAgentRequest request = new ProvisionAgentRequest(userEmail.value());
       assertDoesNotThrow(()->invitationService.provisionAgent(request));
      }

   @Test
   void shouldCreateInvitationToken() {
       TokenPurpose purpose = TokenPurpose.FIRST_PASSWORD_SET; 
       assertDoesNotThrow(()->invitationService.createInvite(userId, purpose));
      }

   @Test
   void shouldAcceptInviteSuccessfully() {
       TokenPurpose purpose = TokenPurpose.FIRST_PASSWORD_SET; 
       String token = invitationService.createInvite(userId, purpose);
       InviteAcceptRequest request = new InviteAcceptRequest(token, password);
       assertDoesNotThrow(()->invitationService.acceptInvite(request));
       
      }

   @Test
   void shouldFailWhenTokenExpired() {
      String token = invitationService.createInvite(userId, TokenPurpose.FIRST_PASSWORD_SET);

      clock.setFixedTime(clock.now().plus(Duration.ofDays(inviteValidDays + 1)));

      InviteAcceptRequest req = new InviteAcceptRequest(token, password);
      assertThrows(InvitationTokenNotFoundException.class, () -> invitationService.acceptInvite(req));
      }


   @Test
   void shouldFailWhenTokenAlreadyUsed() {
      String token = invitationService.createInvite(userId, TokenPurpose.FIRST_PASSWORD_SET);
      invitationService.acceptInvite(new InviteAcceptRequest(token, password));
      assertThrows(InvitationTokenNotFoundException.class,
         () -> invitationService.acceptInvite(new InviteAcceptRequest(token, password)));
   }

   @Test
   void shouldActivateUserAfterAcceptingInvite() {
         String token = invitationService.createInvite(userId, TokenPurpose.FIRST_PASSWORD_SET);
         assertNotNull(token);
         assertTrue(!token.isBlank());
         invitationService.acceptInvite(new InviteAcceptRequest(token, password));

         User updated = userRepository.findById(userId).orElse(null);
         assertEquals(UserStatus.ACTIVE, updated.getStatus());
         assertNotNull(updated.getPasswordHash());
         assertTrue(passwordHasher.matches(password, updated.getPasswordHash()));
      }

}

