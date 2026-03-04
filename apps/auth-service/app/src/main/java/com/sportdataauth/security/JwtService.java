package com.sportdataauth.security;

import com.sportdataauth.domain.entity.User;

public interface JwtService {
	JwtToken generateAccessToken(User user);
	boolean validateAccessToken(String token);
}
