package com.sportdataauth.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.sportdataauth.model.Role;
import com.sportdataauth.model.UserStatus;

public class UserResponse {
	private UUID id;
	private String email;
	private Set<Role> roles;
	private UserStatus status;
	private LocalDateTime createdAt;
	
	public UserResponse(UUID id, String email, Set<Role> roles, UserStatus status, LocalDateTime createdAt) {
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
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

}
