package com.sportdataauth.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import com.sportdataauth.security.exception.TokenHashingException;

public class TokenHasher {
	
	public String hash(String rawToken) {
		if (rawToken == null) {
			throw new TokenHashingException("RAW_TOKEN_NULL");
		}
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(rawToken.getBytes("UTF-8"));
			return Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e ){
			throw new TokenHashingException("SHA-256_ALGORITHM_NOT_AVAILABLE", e);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new TokenHashingException("UTF-8_ENCODING_NOT_SUPPORTED", e);
		}
	}

}
