package com.sportdataauth.service;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import com.sportdataauth.domain.entity.InvitationToken;
import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.enums.Role;
import com.sportdataauth.domain.enums.TokenPurpose;
import com.sportdataauth.domain.enums.UserStatus;
import com.sportdataauth.domain.exception.InvalidRequestException;
import com.sportdataauth.domain.exception.InvitationTokenNotFoundException;
import com.sportdataauth.domain.exception.UserNotEligibleException;
import com.sportdataauth.domain.exception.UserNotFoundException;
import com.sportdataauth.domain.exception.WeakPasswordException;
import com.sportdataauth.domain.value.Email;
import com.sportdataauth.dto.InviteAcceptRequest;
import com.sportdataauth.dto.ProvisionAgentRequest;
import com.sportdataauth.policy.CredentialPolicy;
import com.sportdataauth.repository.InvitationTokenRepository;
import com.sportdataauth.repository.UserRepository;
import com.sportdataauth.security.PasswordHasher;
import com.sportdataauth.util.Clock;
import com.sportdataauth.util.TokenGenerator;
import com.sportdataauth.util.TokenHasher;
import com.sportdataauth.util.TransactionRunner;

public class InvitationService {

   private final UserRepository userRepository;
   private final InvitationTokenRepository tokenRepository;
   private final TokenGenerator tokenGenerator;
   private final TokenHasher tokenHasher;
   private final PasswordHasher passwordHasher;
   private final CredentialPolicy credentialPolicy;
   private final Clock clock;
   private final int inviteValidDays;
   private final TransactionRunner tx;

   public InvitationService(UserRepository userRepository,
                            InvitationTokenRepository tokenRepository,
                            TokenGenerator tokenGenerator,
                            TokenHasher tokenHasher,
                            PasswordHasher passwordHasher,
                            CredentialPolicy credentialPolicy,
                            Clock clock,
                            int inviteValidDays,
                            TransactionRunner tx) {
       this.userRepository = userRepository;
       this.tokenRepository = tokenRepository;
       this.tokenGenerator = tokenGenerator;
       this.tokenHasher = tokenHasher;
       this.passwordHasher = passwordHasher;
       this.credentialPolicy = credentialPolicy;
       this.clock = clock;
       this.inviteValidDays = inviteValidDays;
       this.tx = tx;
   }

   /**
    * Provision an AGENT account (if missing) and issue an invitation token.
    * Returns the raw token (to be emailed / shown once).
    */
   public String provisionAgent(ProvisionAgentRequest request) {
       if (request == null || request.getEmail() == null) {
           throw InvalidRequestException.nullValue("request");
       }

	   Email email = Email.of(request.getEmail());

       User user = userRepository.findByEmail(email);

       if (user == null) {
           UUID userId = UUID.randomUUID();
           User newUser = new User(
                   userId,
                   email,
                   null,                   // passwordHash
                   Set.of(Role.AGENT),     // roles
                   UserStatus.DISABLED,    // status
                   0,                      // failedAttempts
                   clock.now(),            // createdAt
                   null                    // lastLoginAt
           );
           userRepository.save(newUser);
           user = newUser;
       } else if (!user.hasRole(Role.AGENT)) {
           // Avoid turning a CLIENT into AGENT silently in MVP
           throw new UserNotEligibleException("USER_EXISTS_NOT_AGENT");
       }

       return createInvite(user.getId(), TokenPurpose.FIRST_PASSWORD_SET);
   }

   public String createInvite(UUID userId, TokenPurpose purpose) {
       if (userId == null || purpose == null) {
           throw InvalidRequestException.invalidValue("inviteParams");
       } 
       User user = userRepository.findById(userId);
       if (user == null){
        throw new UserNotFoundException();
       }
       

       String rawToken = tokenGenerator.generateToken();
       String hashedToken = tokenHasher.hash(rawToken);

       Instant now = clock.now();

       InvitationToken inviteToken = new InvitationToken(
               UUID.randomUUID(),
               userId,
               hashedToken,
               purpose,
               now.plus(Duration.ofDays(inviteValidDays)),
               null,   // usedAt
               now     // createdAt
       );

       tokenRepository.save(inviteToken);
       return rawToken;
   }

   public void acceptInvite(InviteAcceptRequest request) {
       tx.runInTransaction(() ->{
            if (request == null) {
                throw InvalidRequestException.nullValue("request");
            }
            if (request.getToken() == null) {
                throw InvalidRequestException.nullValue("token");
            }
            if (request.getNewPassword() == null) {
                throw InvalidRequestException.nullValue("newPassword");
            }

            Instant now = clock.now();

            String tokenHash = tokenHasher.hash(request.getToken());
            InvitationToken inviteToken = tokenRepository.consumeValidByTokenHash(tokenHash, now);

            if (inviteToken == null) {
                throw new InvitationTokenNotFoundException();
            }

            User user = userRepository.findById(inviteToken.getUserId());
            if (user == null) {
                throw new UserNotFoundException();
            }

            // For FIRST_PASSWORD_SET we expect the account to be disabled until activation
            if (inviteToken.getPurpose() == TokenPurpose.FIRST_PASSWORD_SET
                    && user.getStatus() != UserStatus.DISABLED) {
                throw new UserNotEligibleException("USER_NOT_DISABLED_FOR_FIRST_PASSWORD_SET");
            }

            // In a more complex scenario we might have different flows based on TokenPurpose (e.g. password reset vs account activation)
            String newPassword = request.getNewPassword();
            if (!credentialPolicy.isPasswordStrong(newPassword)) {
                throw new WeakPasswordException();
            }
            String newHashedPassword = passwordHasher.hash(newPassword);

            user.setPasswordHash(newHashedPassword);
            user.setStatus(UserStatus.ACTIVE);
            user.setFailedAttempts(0);
            userRepository.save(user);
        });
   }
}