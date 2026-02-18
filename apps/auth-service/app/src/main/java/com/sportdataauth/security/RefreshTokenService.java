package com.sportdataauth.security;

import java.util.UUID;

public interface RefreshTokenService {

	String rotateRefreshToken(UUID userId);
	UUID validateRefreshToken(String refreshToken);
	void revoke(String refreshToken);
	void revokeAll(UUID userId);
}
