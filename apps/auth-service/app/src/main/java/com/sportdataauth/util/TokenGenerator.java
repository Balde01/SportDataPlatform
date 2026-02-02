package com.sportdataauth.util;

public class TokenGenerator {
	
	public String generateToken() {
		return java.util.UUID.randomUUID().toString();
	}

}
