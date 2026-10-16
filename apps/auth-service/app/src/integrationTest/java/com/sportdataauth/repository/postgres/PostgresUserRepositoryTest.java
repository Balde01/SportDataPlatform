package com.sportdataauth.repository.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.enums.Role;
import com.sportdataauth.domain.enums.UserStatus;
import com.sportdataauth.domain.exception.EmailAlreadyExistsException;
import com.sportdataauth.domain.exception.UserNotFoundException;
import com.sportdataauth.domain.value.Email;
import com.sportdataauth.repository.UserRepository;
import com.sportdataauth.util.Clock;
import com.sportdataauth.util.SystemClock;
import com.sportdataauth.util.TransactionRunner;

public class PostgresUserRepositoryTest {

    private UserRepository userRepository;
    private TransactionRunner tx;
    private Clock clock;
    private User user;
    private final UUID userId = UUID.randomUUID();
    private final Email userEmail = Email.of("disabled@gmail.com");

    @BeforeEach
    public void setUp() {
        clock = new SystemClock();
        tx = new JdbcTransactionRunner();
        userRepository = new PostgresUserRepository(tx);
        user = new User(userId, userEmail, null, Set.of(Role.AGENT), UserStatus.DISABLED, 0, clock.now(), null);
    }

    @AfterEach
    public void tearDown() {
        // Clean up the database after each test
        tx.runInTransaction(() -> {
            Connection conn = JdbcTransactionContext.requireCurrent();

            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                stmt.setObject(1, userId);
                stmt.executeUpdate();
            }
            return null;
        });
    }

    @Test
    void shouldInsertAndFindUserByEmail() {
        userRepository.insert(user);
        User found = userRepository.findByEmail(userEmail).orElseThrow();
        assertEquals(userEmail, found.getEmail());
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        Email nonExistentEmail = Email.of("someone@gmail.com");
        assertTrue(userRepository.findByEmail(nonExistentEmail).isEmpty());
    }

    @Test
    void shouldOverwriteExistingUserOnUpdate() {
        // Given
        userRepository.insert(user);

        // When: we update the user (e.g. activate + set password)
        User updatedUser = new User(userId, userEmail, "hashedPassword", Set.of(Role.AGENT), UserStatus.ACTIVE, 0,
                user.getCreatedAt(), clock.now());

        userRepository.update(updatedUser);
        // Then
        User found = userRepository.findById(userId).orElseThrow();

        assertEquals(UserStatus.ACTIVE, found.getStatus());
        assertEquals("hashedPassword", found.getPasswordHash());
    }

    @Test
    void shouldFindUserById() {
        userRepository.insert(user);
        User found = userRepository.findById(userId).orElseThrow();
        assertEquals(userId, found.getId());
    }

    @Test
    void shouldThrowWhenInsertingUserWithExistingEmail() {
        userRepository.insert(user);
        User anotherUser = new User(UUID.randomUUID(), userEmail, null, Set.of(Role.CLIENT), UserStatus.DISABLED, 0,
                clock.now(), null);
        assertThrows(EmailAlreadyExistsException.class, () -> userRepository.insert(anotherUser));
    }

    @Test
    void shouldThrowWhenUpdateMissingUser() {
        User nonExisting = new User(UUID.randomUUID(), Email.of("nonexisting@gmail.com"), "hashedPassword",
                Set.of(Role.CLIENT), UserStatus.ACTIVE, 0, clock.now(), null);
        assertThrows(UserNotFoundException.class, () -> userRepository.update(nonExisting));
    }

    @Test
    void shouldReturnTrueIfEmailExists() {
        userRepository.insert(user);
        assertTrue(userRepository.existsByEmail(userEmail));
    }

    @Test
    void shouldReturnFalseIfEmailDoesNotExist() {
        assertFalse(userRepository.existsByEmail(Email.of("nonexisting@gmail.com")));
    }

    @Test
    void shouldReturnTrueIfIdExists() {
        userRepository.insert(user);
        assertTrue(userRepository.existsById(userId));
    }

    @Test
    void shouldReturnFalseIfIdDoesNotExist() {
        assertFalse(userRepository.existsById(UUID.randomUUID()));
    }

    @Test
    void shouldPersistAndFindMultipleRoles() {
        User multiRoleUser = new User(userId, userEmail, "hashedPassword", Set.of(Role.AGENT, Role.CLIENT),
                UserStatus.ACTIVE, 0, clock.now(), null);

        userRepository.insert(multiRoleUser);

        User found = userRepository.findById(userId).orElseThrow();

        assertEquals(Set.of(Role.AGENT, Role.CLIENT), found.getRoles());
    }
}
