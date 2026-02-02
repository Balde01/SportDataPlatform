package com.sportdataauth.security;

import com.sportdataauth.model.User;

public class JwtService {
	
	public String generateToken(User user) {
		// Implementation for generating JWT token
		return " token string ";
		}
	
	public String generateRefreshToken(User user) {
		// Implementation for generating JWT refresh
		return " refresh token string ";
	}
	
	public boolean validateAccessToken(String token) {
		// Implementation for validating JWT token
		return true;
	}

}
