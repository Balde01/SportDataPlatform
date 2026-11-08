package com.sportdataauth.repository.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sportdataauth.domain.entity.InvitationToken;
import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.enums.Role;
import com.sportdataauth.domain.enums.TokenPurpose;
import com.sportdataauth.domain.enums.UserStatus;
import com.sportdataauth.domain.exception.InvalidRequestException;
import com.sportdataauth.domain.value.Email;
import com.sportdataauth.repository.InvitationTokenRepository;
import com.sportdataauth.repository.UserRepository;
import com.sportdataauth.util.TransactionRunner;

public class PostgresInvitationTokenRepositoryTest {

    private InvitationTokenRepository invitationTokenRepository;
    private final Instant createdAt = Instant.parse("2026-10-24T18:00:00Z");

    private final Instant expiresAt = Instant.parse("2026-10-31T18:00:00Z");

    private UserRepository userRepository;
    private TransactionRunner tx;
    private User user;
    private final UUID userId = UUID.randomUUID();
    private final Email userEmail = Email.of("disabled@gmail.com");
    private InvitationToken invitationToken;
    private final String hashedToken = "hashedToken";

    @BeforeEach
    public void setUp() {
        tx = new JdbcTransactionRunner();
        userRepository = new PostgresUserRepository(tx);
        user = new User(userId, userEmail, null, Set.of(Role.AGENT), UserStatus.DISABLED, 0, createdAt, null);
        userRepository.insert(user);
        invitationTokenRepository = new PostgresInvitationTokenRepository(tx);
        invitationToken = new InvitationToken(UUID.randomUUID(), userId, hashedToken, TokenPurpose.FIRST_PASSWORD_SET,
                expiresAt, null, createdAt);
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
    public void shouldSaveInvitationToken() {
        invitationTokenRepository.save(invitationToken);
        InvitationToken invitationTokenFound = tx.runInTransaction(() -> {
            Connection conn = JdbcTransactionContext.requireCurrent();

            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM invitation_tokens WHERE id = ?")) {
                stmt.setObject(1, invitationToken.getId());
                try (ResultSet rs = stmt.executeQuery()) {
                    assertTrue(rs.next());
                    UUID userIdFound = rs.getObject("user_id", UUID.class);
                    UUID tokenIdFound = rs.getObject("id", UUID.class);
                    String tokenHashFound = rs.getString("token_hash");
                    TokenPurpose purposeFound = TokenPurpose.valueOf(rs.getString("purpose"));
                    Instant expiresAtFound = rs.getTimestamp("expires_at").toInstant();
                    var usedAtTimestamp = rs.getTimestamp("used_at");
                    Instant usedAtFound = usedAtTimestamp != null ? usedAtTimestamp.toInstant() : null;
                    Instant createdAtFound = rs.getTimestamp("created_at").toInstant();
                    return new InvitationToken(tokenIdFound, userIdFound, tokenHashFound, purposeFound, expiresAtFound,
                            usedAtFound, createdAtFound);
                }

            }
        });

        assertEquals(invitationToken.getId(), invitationTokenFound.getId());
        assertEquals(invitationToken.getUserId(), invitationTokenFound.getUserId());
        assertEquals(invitationToken.getTokenHash(), invitationTokenFound.getTokenHash());
        assertEquals(invitationToken.getPurpose(), invitationTokenFound.getPurpose());
        assertEquals(invitationToken.getExpiresAt(), invitationTokenFound.getExpiresAt());
        assertEquals(invitationToken.getUsedAt(), invitationTokenFound.getUsedAt());
        assertEquals(invitationToken.getCreatedAt(), invitationTokenFound.getCreatedAt());
    }

    @Test
    public void shouldNotSaveInvitationTokenIfUserDoesNotExist() {
        UUID nonExistentUserId = UUID.randomUUID();
        InvitationToken anotherInvitationToken = new InvitationToken(UUID.randomUUID(), nonExistentUserId, hashedToken,
                TokenPurpose.FIRST_PASSWORD_SET, expiresAt, null, createdAt);
        assertThrows(RuntimeException.class, () -> invitationTokenRepository.save(anotherInvitationToken));
    }

    @Test
    public void shouldConsumeValidInvitationToken() {
        invitationTokenRepository.save(invitationToken);
        long secondsToAdd = 3600;
        Instant now = createdAt.plusSeconds(secondsToAdd);
        InvitationToken consumedToken = invitationTokenRepository.consumeValidByTokenHash(hashedToken, now);
        assertEquals(invitationToken.getId(), consumedToken.getId());
        assertEquals(now, consumedToken.getUsedAt());
    }

    @Test
    public void shouldNotConsumeExpiredInvitationToken() {
        invitationTokenRepository.save(invitationToken);
        long secondsToAdd = 1;
        Instant now = expiresAt.plusSeconds(secondsToAdd);
        InvitationToken consumedToken = invitationTokenRepository.consumeValidByTokenHash(hashedToken, now);
        assertNull(consumedToken);
    }

    @Test
    public void shouldNotConsumeNonExistentInvitationToken() {
        long secondsToAdd = 3600;
        Instant now = createdAt.plusSeconds(secondsToAdd);
        InvitationToken consumedToken = invitationTokenRepository.consumeValidByTokenHash("nonExistentHashedToken",
                now);
        assertNull(consumedToken);
    }

    @Test
    public void shouldNotConsumeInvitationTokenWhenExpiresAtEqualsNow() {
        invitationTokenRepository.save(invitationToken);
        Instant now = expiresAt;
        InvitationToken consumedToken = invitationTokenRepository.consumeValidByTokenHash(hashedToken, now);
        assertNull(consumedToken);
    }

    @Test
    public void shouldNotConsumeAlreadyUsedInvitationToken() {
        invitationTokenRepository.save(invitationToken);
        long secondsToAdd = 3600;
        Instant now = createdAt.plusSeconds(secondsToAdd);
        InvitationToken consumedToken = invitationTokenRepository.consumeValidByTokenHash(hashedToken, now);
        assertEquals(invitationToken.getId(), consumedToken.getId());
        InvitationToken secondConsumptionAttempt = invitationTokenRepository.consumeValidByTokenHash(hashedToken, now);
        assertNull(secondConsumptionAttempt);
    }

    @Test
    public void shouldThrowExceptionWhenSavingNullInvitationToken() {
        assertThrows(InvalidRequestException.class, () -> invitationTokenRepository.save(null));
    }

    @Test
    public void shouldThrowExceptionWhenConsumingNullTokenHash() {
        assertThrows(InvalidRequestException.class,
                () -> invitationTokenRepository.consumeValidByTokenHash(null, createdAt));
    }

    @Test
    public void shouldThrowExceptionWhenConsumingNullNow() {
        assertThrows(InvalidRequestException.class,
                () -> invitationTokenRepository.consumeValidByTokenHash(hashedToken, null));
    }

    @Test
    public void shouldThrowExceptionWhenConsumingBlankTokenHash() {
        assertThrows(InvalidRequestException.class,
                () -> invitationTokenRepository.consumeValidByTokenHash("   ", createdAt));
    }
}
