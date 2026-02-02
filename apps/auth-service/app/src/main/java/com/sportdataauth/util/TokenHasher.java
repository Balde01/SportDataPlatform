package com.sportdataauth.util;

import org.mindrot.jbcrypt.BCrypt;

public class TokenHasher {
	
	public String hash(String rawToken) {
		return BCrypt.hashpw(rawToken, BCrypt.gensalt(10));
	}

}
