package com.sportdataauth.repository;
 
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 
import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.enums.Role;
import com.sportdataauth.domain.enums.UserStatus;
import com.sportdataauth.domain.value.Email;
import com.sportdataauth.util.Clock;
import com.sportdataauth.util.SystemClock;
 
public class InMemoryUserRepositoryTest {
   private UserRepository userRepository;
   private Clock clock;
   private User user;
   private final UUID userId = UUID.randomUUID();
   private final Email userEmail = Email.of("DISABLED@GMAIL.COM");
   
   @BeforeEach
   public void setUp() {
       clock = new SystemClock();
       userRepository = new InMemoryUserRepository();
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
   }    
    @Test
    void shouldSaveAndFindUserByEmail() {
        userRepository.save(user);
        User found = userRepository.findByEmail(userEmail);
        assertEquals(found.getEmail(), userEmail);
    }
 
    @Test
    void shouldNormalizeEmailOnSave() {
        userRepository.save(user);
        User registeredUser = userRepository.findByEmail(userEmail);
        assertNotNull(registeredUser);
        assertEquals(registeredUser.getEmail().value(), "disabled@gmail.com");
    }
 
    @Test
    void shouldReturnNullWhenUserNotFound() {
        Email inexistEmail = Email.of("someone@gmail.com");
        assertNull(userRepository.findByEmail(inexistEmail));
    }

    @Test
    void shouldOverwriteExistingUserOnSave() {
        // Given
        userRepository.save(user);

        // When: we update the user (e.g. activate + set password)
        User updatedUser = new User(
                userId,
                userEmail,
                "hashedPassword",
                Set.of(Role.AGENT),
                UserStatus.ACTIVE,
                0,
                user.getCreatedAt(),
                clock.now()
        );

        userRepository.save(updatedUser);

        // Then
        User found = userRepository.findById(userId);

        assertNotNull(found);
        assertEquals(UserStatus.ACTIVE, found.getStatus());
        assertEquals("hashedPassword", found.getPasswordHash());
    }

    @Test
    void shouldFindUserById() {
        userRepository.save(user);
        User found = userRepository.findById(userId);
        assertNotNull(found);
        assertEquals(userId, found.getId());
    }

    @Test
    void shouldFindUserRegardlessOfEmailCase() {
        userRepository.save(user);
        Email lookup = Email.of("disabled@gmail.com");
        User found = userRepository.findByEmail(lookup);
        assertNotNull(found);
    }
 
}