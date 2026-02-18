package com.sportdataauth.dto;

import java.time.LocalDateTime;

public class TokenResponse {
	private String accessToken;
	private String refreshToken;
	private LocalDateTime expiresAt;
	
	public TokenResponse(String accessToken, String refreshToken, LocalDateTime expiresAt) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiresAt = expiresAt;
	}
	
	public String getAcessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

}
