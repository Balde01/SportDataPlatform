package com.sportdataauth.model;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class User {

    private UUID id;
    private String email;
    private String passwordHash;
    private Set<Role> roles;
    private UserStatus status;
    private int failedAttempts;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    
	public User(UUID id, String email, String passwordHash, Set<Role> roles, UserStatus status, int failedAttempts,
			LocalDateTime createdAt, LocalDateTime lastLoginAt) {
		super();
		this.id = id;
		this.email = email;
		this.passwordHash = passwordHash;
		this.roles = roles;
		this.status = status;
		this.failedAttempts = failedAttempts;
		this.createdAt = createdAt;
		this.lastLoginAt = lastLoginAt;
	}
	
    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

	public UUID getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public int getFailedAttempts() {
		return failedAttempts;
	}

	public void setFailedAttempts(int failedAttempts) {
		this.failedAttempts = failedAttempts;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getLastLoginAt() {
		return lastLoginAt;
	}

	public void setLastLoginAt(LocalDateTime lastLoginAt) {
		this.lastLoginAt = lastLoginAt;
	}
	
	
    
    

}
