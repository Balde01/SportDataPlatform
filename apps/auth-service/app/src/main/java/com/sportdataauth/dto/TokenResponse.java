package com.sportdataauth.dto;

import java.time.Instant;

public class TokenResponse {
	private String accessToken;
	private String refreshToken;
	public long expiresAtEpochSeconds;
	
	public TokenResponse(String accessToken, String refreshToken, Instant expiresAt) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiresAtEpochSeconds = expiresAt.getEpochSecond();
	}
	
	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public long expiresAtEpochSeconds() {
		return expiresAtEpochSeconds;
	}

}
