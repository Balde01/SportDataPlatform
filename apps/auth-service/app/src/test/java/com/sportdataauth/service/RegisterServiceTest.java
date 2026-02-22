package com.sportdataauth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.exception.EmailAlreadyExistsException;
import com.sportdataauth.domain.exception.EmailNotAllowedException;
import com.sportdataauth.domain.exception.WeakPasswordException;
import com.sportdataauth.domain.value.Email;
import com.sportdataauth.dto.RegisterRequest;
import com.sportdataauth.dto.UserResponse;
import com.sportdataauth.policy.CredentialPolicy;
import com.sportdataauth.policy.DefaultCredentialPolicy;
import com.sportdataauth.repository.InMemoryUserRepository;
import com.sportdataauth.repository.UserRepository;
import com.sportdataauth.security.BcryptPasswordHasher;
import com.sportdataauth.security.PasswordHasher;
import com.sportdataauth.util.SystemClock;

public class RegisterServiceTest {

   private RegisterService registerService;
   private UserRepository userRepository;
   private CredentialPolicy credentialPolicy;
   private PasswordHasher passwordHasher;
   private SystemClock clock;

   @BeforeEach
   public void setUp() {
       userRepository = new InMemoryUserRepository();
       credentialPolicy = new DefaultCredentialPolicy();
       passwordHasher = new BcryptPasswordHasher(); 
       clock = new SystemClock();
       registerService = new RegisterService(userRepository, credentialPolicy, passwordHasher, clock);
   }

   @Test
   void shouldRegisterClientSuccessfully() throws Exception {
       RegisterRequest req = new RegisterRequest("test2@email.com", "Secret1@");

       UserResponse created = registerService.registerClient(req);

       assertNotNull(created);
       Email e = Email.of("test2@email.com");
       assertEquals(e.value(), created.getEmail());
      
       assertNotNull(userRepository.findByEmail(e));
   }

   @Test
   void shouldFailWhenEmailInvalid() {
    RegisterRequest req = new RegisterRequest("InvalidEmail.com", "Secret1@");

    assertThrows(EmailNotAllowedException.class, () -> registerService.registerClient(req));

    // Ensure we didn't create a user (simple sanity check)
    assertNull(userRepository.findByEmail(Email.of("InvalidEmail.com")));

   }

   @Test
   void shouldFailWhenPasswordInvalid() {
       RegisterRequest req = new RegisterRequest("test@email.com", "#####");
       assertThrows(WeakPasswordException.class, () -> registerService.registerClient(req));

       RegisterRequest req2 = new RegisterRequest("test@email.com", null);
       assertThrows(WeakPasswordException.class, () -> registerService.registerClient(req2));

       Email e = Email.of("test@email.com");
       assertNull(userRepository.findByEmail(e));
   }

   @Test
   void shouldFailWhenEmailAlreadyExists() {
       RegisterRequest req = new RegisterRequest("test@email.com", "Secret1@");
       registerService.registerClient(req);
       assertThrows(EmailAlreadyExistsException.class, () -> registerService.registerClient(req));
   }

   @Test
   void shouldNormalizeEmailToLowerCaseAndTrim() {
       RegisterRequest req = new RegisterRequest("  TeSt2@Email.com  ", "Secret1@");

        UserResponse created = registerService.registerClient(req);

        assertEquals(Email.of("test2@email.com").value(), created.getEmail());

        Email e1 = Email.of("  TEST2@Email.com  ");
        Email e2 = Email.of("test2@email.com");
        assertNotNull(userRepository.findByEmail(e1));
        assertNotNull(userRepository.findByEmail(e2));
   }

   @Test
   void shouldHashPassword() {
        RegisterRequest req = new RegisterRequest("  TeSt2@Email.com  ", "Secret1@");

        registerService.registerClient(req);

        User user = userRepository.findByEmail(Email.of("test2@email.com"));
        assertNotNull(user.getPasswordHash());
        assertNotEquals("Secret1@", user.getPasswordHash());
        assertEquals(true, passwordHasher.matches("Secret1@", user.getPasswordHash()));

   }
}