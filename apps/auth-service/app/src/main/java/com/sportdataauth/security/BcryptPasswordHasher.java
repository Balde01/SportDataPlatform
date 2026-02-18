package com.sportdataauth.security;

import org.mindrot.jbcrypt.BCrypt;

public class BcryptPasswordHasher implements PasswordHasher {

    public BcryptPasswordHasher() {
    }

    @Override
    public String hash(String rawPassword) {
		rawPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt(10));
		return rawPassword;
	}
	
    @Override
	public boolean matches(String rawPassword, String hashedPassword) {
		return BCrypt.checkpw(rawPassword, hashedPassword);
	}

}
