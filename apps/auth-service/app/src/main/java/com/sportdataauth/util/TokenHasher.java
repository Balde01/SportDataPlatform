package com.sportdataauth.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class TokenHasher {
	
	public String hash(String rawToken) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(rawToken.getBytes("UTF-8"));
			return Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e ){
			throw new IllegalStateException("SHA-256_ALGORITHM_NOT_AVAILABLE", e);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8_ENCODING_NOT_SUPPORTED", e);
		}
	}

}
