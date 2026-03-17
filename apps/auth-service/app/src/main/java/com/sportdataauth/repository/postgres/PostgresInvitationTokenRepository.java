package com.sportdataauth.repository.postgres;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

import com.sportdataauth.domain.entity.InvitationToken;
import com.sportdataauth.domain.enums.TokenPurpose;
import com.sportdataauth.domain.exception.InvalidRequestException;
import com.sportdataauth.repository.InvitationTokenRepository;
import com.sportdataauth.util.TransactionRunner;

public class PostgresInvitationTokenRepository implements InvitationTokenRepository {
    private final TransactionRunner tx;

    public PostgresInvitationTokenRepository(TransactionRunner tx) {
        this.tx = tx;
    }

    private static final String SQL_CONSUME_VALID_BY_HASH = """
        UPDATE invitation_tokens
        SET used_at = ?
        WHERE token_hash = ? AND used_at IS NULL AND expires_at > ?
        RETURNING id, user_id, token_hash, purpose, expires_at, used_at, created_at
    """;

    private static final String SQL_INSERT = """
        INSERT INTO invitation_tokens (id, user_id, token_hash, purpose, expires_at, created_at)
        VALUES (?, ?, ?, ?, ?, ?)
    """;
    @Override
    public InvitationToken consumeValidByTokenHash(String tokenHash, Instant now) {
        if (tokenHash == null) {
            throw InvalidRequestException.nullValue("tokenHash");
        }
        if (tokenHash.isBlank()) {
            throw InvalidRequestException.invalidValue("tokenHash");
        }
        if(now == null) {
            throw InvalidRequestException.nullValue("now");
        }
            return tx.runInTransaction(() ->  {
                Connection conn = JdbcTransactionContext.requireCurrent();
                try (var stmt = conn.prepareStatement(SQL_CONSUME_VALID_BY_HASH)) {
                    stmt.setTimestamp(1, java.sql.Timestamp.from(now));
                    stmt.setString(2, tokenHash);
                    stmt.setTimestamp(3, java.sql.Timestamp.from(now));
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return mapResultSetToInvitationToken(rs);
                        } else {
                            return null; // No valid token found
                        }
                    }
                }
            });
    }

    @Override
    public void save(InvitationToken token) {
        if (token == null) {
            throw InvalidRequestException.nullValue("invitationToken");
        }
        withConnection(conn -> {
            try (var stmt = conn.prepareStatement(SQL_INSERT)) {
                stmt.setObject(1, token.getId());
                stmt.setObject(2, token.getUserId());
                stmt.setString(3, token.getTokenHash());
                stmt.setString(4, token.getPurpose().name());
                stmt.setTimestamp(5, java.sql.Timestamp.from(token.getExpiresAt()));
                stmt.setTimestamp(6, java.sql.Timestamp.from(token.getCreatedAt()));

                stmt.executeUpdate();
            }
            return null;
        });
    }

    /*=========== SQL helpers =========== */
    @FunctionalInterface
    private interface SqlWork<T> {
        T run(Connection conn) throws Exception;
    }
    private <T> T withConnection(SqlWork<T> work) {
        Connection current = JdbcTransactionContext.getCurrent();
        if (current != null) {
            try {
                return work.run(current);
            } catch (Exception e) {
                throw new RuntimeException("SQL work failed", e);
            }
        }

        try (Connection conn = JdbcConnectionFactory.open()) {
            return work.run(conn);
        } catch (Exception e) {
            throw new RuntimeException("Database operation failed", e);
        }
    }

    private InvitationToken mapResultSetToInvitationToken(ResultSet rs) throws SQLException {
        var usedAtTimestamp = rs.getTimestamp("used_at");
        Instant usedAt = usedAtTimestamp != null ? usedAtTimestamp.toInstant() : null;
        return new InvitationToken(
            rs.getObject("id",UUID.class),
            rs.getObject("user_id", UUID.class),
            rs.getString("token_hash"),                                
            TokenPurpose.valueOf(rs.getString("purpose")),
            rs.getTimestamp("expires_at").toInstant(),
            usedAt,
            rs.getTimestamp("created_at").toInstant()
        );
    }







}
