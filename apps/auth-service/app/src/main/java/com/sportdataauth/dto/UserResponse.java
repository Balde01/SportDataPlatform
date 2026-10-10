package com.sportdataauth.dto;

import com.sportdataauth.domain.enums.Role;
import com.sportdataauth.domain.enums.UserStatus;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class UserResponse {
    private UUID id;
    private String email;
    private Set<Role> roles;
    private UserStatus status;
    private Instant createdAt;

    public UserResponse(UUID id, String email, Set<Role> roles, UserStatus status, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.roles = roles;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
