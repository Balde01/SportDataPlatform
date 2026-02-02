package com.sportdataauth.dto;

import java.time.LocalDateTime;

public class TokenResponse {
	private String acessToken;
	private String refreshToken;
	private LocalDateTime expiresAt;
	
	public TokenResponse(String acessToken, String refreshToken, LocalDateTime expiresAt) {
		this.acessToken = acessToken;
		this.refreshToken = refreshToken;
		this.expiresAt = expiresAt;
	}
	
	public String getAcessToken() {
		return acessToken;
	}
	public void setAcessToken(String acessToken) {
		this.acessToken = acessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

}
