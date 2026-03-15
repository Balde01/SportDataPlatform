package com.sportdataauth.repository;
 
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 
import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.enums.Role;
import com.sportdataauth.domain.enums.UserStatus;
import com.sportdataauth.domain.exception.EmailAlreadyExistsException;
import com.sportdataauth.domain.exception.UserNotFoundException;
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
    void shouldInsertAndFindUserByEmail() {
        userRepository.insert(user);
        User found = userRepository.findByEmail(userEmail)
                                   .orElse(null);
        assertEquals(found.getEmail(), userEmail);
    }
 
    @Test
    void shouldNormalizeEmailOnInsert() {
        userRepository.insert(user);
        User registeredUser = userRepository.findByEmail(userEmail)
                                            .orElse(null);
        assertNotNull(registeredUser);
        assertEquals(registeredUser.getEmail().value(), "disabled@gmail.com");
    }
 
    @Test
    void shouldReturnOptionalEmptyWhenUserNotFound() {
        Email inexistEmail = Email.of("someone@gmail.com");
        assertEquals(Optional.empty(), userRepository.findByEmail(inexistEmail));
    }

    @Test
    void shouldOverwriteExistingUserOnUpdate() {
        // Given
        userRepository.insert(user);

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

        userRepository.update(updatedUser);
        // Then
        User found = userRepository.findById(userId).orElse(null);

        assertNotNull(found);
        assertEquals(UserStatus.ACTIVE, found.getStatus());
        assertEquals("hashedPassword", found.getPasswordHash());
    }

    @Test
    void shouldFindUserById() {
        userRepository.insert(user);
        User found = userRepository.findById(userId).orElse(null);
        assertNotNull(found);
        assertEquals(userId, found.getId());
    }

    @Test
    void shouldFindUserRegardlessOfEmailCase() {
        userRepository.insert(user);
        Email lookup = Email.of("disabled@gmail.com");
        User found = userRepository.findByEmail(lookup)
                                   .orElse(null);
        assertNotNull(found);
    }

    @Test
    void shouldThrowWhenInsertingUserWithExistingEmail() {
        userRepository.insert(user);
        User anotherUser = new User(
            UUID.randomUUID(),
            userEmail,
            null,
             Set.of(Role.CLIENT),
             UserStatus.ACTIVE,
             0,
             clock.now(),
             null
        );
        assertThrows(EmailAlreadyExistsException.class, () -> userRepository.insert(anotherUser));
    }
    
    @Test
    void shouldThrowWhenUpdateMissingUser() {
        User nonExisting = new User(
            UUID.randomUUID(),
            Email.of("nonexisting@gmail.com"),
            null,
            Set.of(Role.CLIENT),
            UserStatus.ACTIVE,
            0,
            clock.now(),
            null
        );
        assertThrows(UserNotFoundException.class, () -> userRepository.update(nonExisting));
    }

    @Test
    void shouldReturnTrueIfEmailExists() {
        userRepository.insert(user);
        assertEquals(true, userRepository.existsByEmail(userEmail));
    }

    @Test
    void shouldReturnFalseIfEmailDoesNotExist() {
        assertEquals(false, userRepository.existsByEmail(Email.of("nonexisting@gmail.com")));
    }

    @Test
    void shouldReturnTrueIfIdExists() {
        userRepository.insert(user);
        assertEquals(true, userRepository.existsById(userId));
    }

    @Test
    void shouldReturnFalseIfIdDoesNotExist() {
        assertEquals(false, userRepository.existsById(UUID.randomUUID()));
    }

}