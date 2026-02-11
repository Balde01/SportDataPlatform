package com.sportdataauth.model;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
public class User {
   private final UUID id;
   private final String email;
   private String passwordHash;
   private Set<Role> roles;
   private UserStatus status;
   private int failedAttempts;
   private final LocalDateTime createdAt;
   private LocalDateTime lastLoginAt;
   public User(UUID id,
               String email,
               String passwordHash,
               Set<Role> roles,
               UserStatus status,
               int failedAttempts,
               LocalDateTime createdAt,
               LocalDateTime lastLoginAt) {
       this.id = Objects.requireNonNull(id, "ID_IS_REQUIRED");
       this.email = requireNonBlank(email, "EMAIL_IS_REQUIRED");
       this.roles = Objects.requireNonNull(roles, "ROLES_IS_REQUIRED");
       this.status = Objects.requireNonNull(status, "STATUS_IS_REQUIRED");
       if (failedAttempts < 0) throw new IllegalArgumentException("FAILED_ATTEMPTS_MUST_BE_GREATER_OR_EQUAL_TO_ZERO");
       this.passwordHash = passwordHash;
       this.failedAttempts = failedAttempts;
       this.createdAt = Objects.requireNonNull(createdAt, "CREATED_AT_IS_REQUIRED");
       this.lastLoginAt = lastLoginAt;
   }
   public boolean hasRole(Role role) {
       return roles.contains(role);
   }
   public boolean isActive() {
       return status == UserStatus.ACTIVE;
   }
   public UUID getId() { return id; }
   public String getEmail() { return email; }
   public String getPasswordHash() { return passwordHash; }
   public Set<Role> getRoles() { return roles; }
   public UserStatus getStatus() { return status; }
   public int getFailedAttempts() { return failedAttempts; }
   public LocalDateTime getCreatedAt() { return createdAt; }
   public LocalDateTime getLastLoginAt() { return lastLoginAt; }
   public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
   public void setRoles(Set<Role> roles) { this.roles = Objects.requireNonNull(roles); }
   public void setStatus(UserStatus status) { this.status = Objects.requireNonNull(status); }
   public void setFailedAttempts(int failedAttempts) {
       if (failedAttempts < 0) throw new IllegalArgumentException("FAILED_ATTEMPTS_MUST_BE_GREATER_OR_EQUAL_TO_ZERO");
       this.failedAttempts = failedAttempts;
   }
   public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
   private static String requireNonBlank(String value, String message) {
       if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
       return value;
   }
}