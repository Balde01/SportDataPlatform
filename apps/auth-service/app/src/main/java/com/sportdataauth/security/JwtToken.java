package com.sportdataauth.security;

import java.time.Instant;

public class JwtToken {

    private final String token;

    private final Instant expiresAt;

    public JwtToken(String token, Instant expiresAt) {

        this.token = token;

        this.expiresAt = expiresAt;

    }

    public String getToken() {
        return token;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

}