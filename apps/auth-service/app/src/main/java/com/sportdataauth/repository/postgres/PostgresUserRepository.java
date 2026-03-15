package com.sportdataauth.repository.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.enums.Role;
import com.sportdataauth.domain.enums.UserStatus;
import com.sportdataauth.domain.exception.EmailAlreadyExistsException;
import com.sportdataauth.domain.exception.InvalidRequestException;
import com.sportdataauth.domain.exception.UserNotFoundException;
import com.sportdataauth.domain.value.Email;
import com.sportdataauth.repository.UserRepository;

public class PostgresUserRepository implements UserRepository {

    @Override
    public void insert(User user) {
        if (user == null) {
            throw InvalidRequestException.nullValue("user");
        }
        JdbcTx.inTx(conn -> {
            insertUser(conn, user);
            insertRoles(conn, user.getId(), user.getRoles());
                return null;
        });
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        if (email == null) {
            throw InvalidRequestException.nullValue("email");
        }
        try (Connection conn = JdbcConnectionFactory.open()) {
            Optional<UserRow> rowOpt = selectUserByEmail(conn, email.value());
            if (rowOpt.isEmpty()) return Optional.empty();

            UserRow row = rowOpt.get();
            Set<Role> roles = selectRoles(conn, row.id);

            return Optional.of(toDomain(row, roles));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by email", e);
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        if (id == null) {
            throw InvalidRequestException.nullValue("id");
        }
        try (Connection conn = JdbcConnectionFactory.open()) {
            Optional<UserRow> rowOpt = selectUserById(conn, id);
            if (rowOpt.isEmpty()) return Optional.empty();

            UserRow row = rowOpt.get();
            Set<Role> roles = selectRoles(conn, row.id);

            return Optional.of(toDomain(row, roles));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by ID", e);
        }
    }
    @Override
    public void update(User user) {
        if (user == null) {
            throw InvalidRequestException.nullValue("user");
        }
        JdbcTx.inTx(conn -> {
            updateUser(conn, user);
            replaceRoles(conn, user.getId(), user.getRoles());
            return null;
        });
    }

    @Override
    public boolean existsById(UUID id) {
        if (id == null) {
            throw InvalidRequestException.nullValue("id");
        }
        String sql = "SELECT 1 FROM users WHERE id = ?";
        try (Connection conn = JdbcConnectionFactory.open();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check existence by id", e);
        }
    }

    @Override
    public boolean existsByEmail(Email email) {
        if (email == null) {
            throw InvalidRequestException.nullValue("email");
        }
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = JdbcConnectionFactory.open();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.value());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check existence by email", e);
        }
    }
    // ---------- SQL helpers ----------

    private void insertUser(Connection conn, User user) throws SQLException {
        String sql = """
            INSERT INTO users (id, email, password_hash, status, failed_attempts, created_at, updated_at, last_login_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, user.getId());
            ps.setString(2, user.getEmail().value());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getStatus().name());
            ps.setInt(5, user.getFailedAttempts());

            Timestamp created = Timestamp.from(user.getCreatedAt());
            ps.setTimestamp(6, created);
            ps.setTimestamp(7, created); // updated_at = created_at for now (domain doesn't track updatedAt yet)

            Instant lastLogin = user.getLastLoginAt();
            if (lastLogin == null) ps.setNull(8, Types.TIMESTAMP);
            else ps.setTimestamp(8, Timestamp.from(lastLogin));

            ps.executeUpdate();
        } catch (SQLException e) {
            // Postgres unique violation SQLSTATE = 23505
            if ("23505".equals(e.getSQLState())) {
                throw new EmailAlreadyExistsException();
            }
            throw e;
        }
    }
    private void updateUser(Connection conn, User user) throws SQLException {
        String sql = """
            UPDATE users
            SET password_hash = ?, status = ?, failed_attempts = ?, updated_at = ?, last_login_at = ?
            WHERE id = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getPasswordHash());
            ps.setString(2, user.getStatus().name());
            ps.setInt(3, user.getFailedAttempts());
            ps.setTimestamp(4, Timestamp.from(Instant.now())); // updated_at
            Instant lastLogin = user.getLastLoginAt();
            if (lastLogin == null) ps.setNull(5, Types.TIMESTAMP);
            else ps.setTimestamp(5, Timestamp.from(lastLogin));
            ps.setObject(6, user.getId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new UserNotFoundException();
            }
        }
    }
    private void replaceRoles(Connection conn, UUID userId, Set<Role> roles) throws SQLException {
        String deleteSql = "DELETE FROM user_roles WHERE user_id = ?";
        try (PreparedStatement delete = conn.prepareStatement(deleteSql)) {
            delete.setObject(1, userId);
            delete.executeUpdate();
        }

        insertRoles(conn, userId, roles);
    }

    private void insertRoles(Connection conn, UUID userId, Set<Role> roles) throws SQLException {
        String ins = "INSERT INTO user_roles (user_id, role) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(ins)) {
            for (Role r : roles) {
                ps.setObject(1, userId);
                ps.setString(2, r.name());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private Optional<UserRow> selectUserByEmail(Connection conn, String email) throws SQLException {
        String sql = """
            SELECT id, email, password_hash, status, failed_attempts, created_at, last_login_at
            FROM users
            WHERE email = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapRow(rs));
            }
        }
    }

    private Optional<UserRow> selectUserById(Connection conn, UUID id) throws SQLException {
        String sql = """
            SELECT id, email, password_hash, status, failed_attempts, created_at, last_login_at
            FROM users
            WHERE id = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapRow(rs));
            }
        }
    }

    private Set<Role> selectRoles(Connection conn, UUID userId) throws SQLException {
        String sql = "SELECT role FROM user_roles WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                Set<Role> roles = EnumSet.noneOf(Role.class);
                while (rs.next()) {
                    roles.add(Role.valueOf(rs.getString("role")));
                }
                return roles;
            }
        }
    }

    private UserRow mapRow(ResultSet rs) throws SQLException {
        UUID id = rs.getObject("id", UUID.class);
        Email email = Email.of(rs.getString("email"));
        String passwordHash = rs.getString("password_hash");
        UserStatus status = UserStatus.valueOf(rs.getString("status"));
        int failedAttempts = rs.getInt("failed_attempts");
        Instant createdAt = rs.getTimestamp("created_at").toInstant();

        Timestamp lastLoginTs = rs.getTimestamp("last_login_at");
        Instant lastLoginAt = (lastLoginTs == null) ? null : lastLoginTs.toInstant();

        return new UserRow(id, email, passwordHash, status, failedAttempts, createdAt, lastLoginAt);
    }

    private User toDomain(UserRow row, Set<Role> roles) {
        return new User(
                row.id,
                row.email,
                row.passwordHash,
                roles,
                row.status,
                row.failedAttempts,
                row.createdAt,
                row.lastLoginAt
        );
    }

    private record UserRow(
            UUID id,
            Email email,
            String passwordHash,
            UserStatus status,
            int failedAttempts, 
            Instant createdAt,
            Instant lastLoginAt
    ) {}




}