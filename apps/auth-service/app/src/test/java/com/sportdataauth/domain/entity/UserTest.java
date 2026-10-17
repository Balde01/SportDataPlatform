package com.sportdataauth.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sportdataauth.domain.enums.Role;
import com.sportdataauth.domain.enums.UserStatus;
import com.sportdataauth.domain.exception.InvalidFailedAttemptsException;
import com.sportdataauth.domain.exception.InvalidRequestException;
import com.sportdataauth.domain.value.Email;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    void shouldIncrementFailedAttempts() {
        User user = createActiveUser(2);

        user.recordFailedLoginAttempt(5);

        assertEquals(3, user.getFailedAttempts());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    void shouldLockUserWhenMaxAttemptsReached() {
        int maxFailedAttempts = 5;
        User user = createActiveUser(0);

        for (int i = 0; i < maxFailedAttempts - 1; i++) {
            user.recordFailedLoginAttempt(maxFailedAttempts);
        }

        assertEquals(4, user.getFailedAttempts());
        assertEquals(UserStatus.ACTIVE, user.getStatus());

        user.recordFailedLoginAttempt(maxFailedAttempts);

        assertEquals(5, user.getFailedAttempts());
        assertEquals(UserStatus.LOCKED, user.getStatus());
    }

    @Test
    void successfulLoginShouldNotChangeUserStatus() {
        Instant loginTime = Instant.parse("2026-10-03T21:00:00Z");

        User user = new User(UUID.randomUUID(), Email.of("test@example.com"), "hashedPassword", Set.of(Role.CLIENT),
                UserStatus.LOCKED, 5, Instant.parse("2026-10-01T20:00:00Z"), null);

        user.recordSuccessfulLogin(loginTime);

        assertEquals(UserStatus.LOCKED, user.getStatus());
        assertEquals(0, user.getFailedAttempts());
        assertEquals(loginTime, user.getLastLoginAt());
    }

    @Test
    void shouldRejectInvalidMaxFailedAttempts() {
        User user = createActiveUser(0);

        assertThrows(InvalidFailedAttemptsException.class, () -> user.recordFailedLoginAttempt(0));
    }

    @Test
    void shouldRejectNullLoginTime() {
        User user = createActiveUser(0);

        assertThrows(InvalidRequestException.class, () -> user.recordSuccessfulLogin(null));
    }

    private User createActiveUser(int failedAttempts) {
        return new User(UUID.randomUUID(), Email.of("test@example.com"), "hashedPassword", Set.of(Role.CLIENT),
                UserStatus.ACTIVE, failedAttempts, Instant.parse("2026-10-01T12:00:00Z"), null);
    }
}
