package com.sportdataauth.util;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {
	
	private final SecureRandom secureRandom = new SecureRandom();
	public String generateToken() {
		byte[] randomBytes = new byte[32];
		secureRandom.nextBytes(randomBytes);
		// Using URL-safe Base64 encoding without padding to ensure the token can be safely included in URLs and headers
		return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
	}

}
