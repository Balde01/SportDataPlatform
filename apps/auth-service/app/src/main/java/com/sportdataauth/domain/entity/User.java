package com.sportdataauth.domain.entity;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.sportdataauth.domain.enums.Role;
import com.sportdataauth.domain.enums.UserStatus;
import com.sportdataauth.domain.exception.InvalidFailedAttemptsException;
import com.sportdataauth.domain.value.Email;
public class User {
   private final UUID id;
   private final Email email;
   private String passwordHash;
   private Set<Role> roles;
   private UserStatus status;
   private int failedAttempts;
   private final Instant createdAt;
   private Instant lastLoginAt;
   public User(UUID id,
               Email email,
               String passwordHash,
               Set<Role> roles,
               UserStatus status,
               int failedAttempts,
               Instant createdAt,
               Instant lastLoginAt) {
       this.id = Objects.requireNonNull(id, "ID_IS_REQUIRED");
       this.email = Objects.requireNonNull(email, "EMAIL_IS_REQUIRED");
       this.roles = Objects.requireNonNull(roles, "ROLES_IS_REQUIRED");
       this.status = Objects.requireNonNull(status, "STATUS_IS_REQUIRED");
       if (failedAttempts < 0) throw new InvalidFailedAttemptsException();
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
   public Email getEmail() { return email; }
   public String getPasswordHash() { return passwordHash; }
   public Set<Role> getRoles() { return roles; }
   public UserStatus getStatus() { return status; }
   public int getFailedAttempts() { return failedAttempts; }
   public Instant getCreatedAt() { return createdAt; }
   public Instant getLastLoginAt() { return lastLoginAt; }
   public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
   public void setRoles(Set<Role> roles) { this.roles = Objects.requireNonNull(roles); }
   public void setStatus(UserStatus status) { this.status = Objects.requireNonNull(status); }
   public void setFailedAttempts(int failedAttempts) {
       if (failedAttempts < 0) throw new InvalidFailedAttemptsException();
       this.failedAttempts = failedAttempts;
   }
   public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }
}