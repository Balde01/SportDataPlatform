package com.sportdataauth.security;

import com.sportdataauth.model.User;

public interface JwtService {
	JwtToken generateAccessToken(User user);
	boolean validateAccessToken(String token);
}
