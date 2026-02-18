package com.sportdataauth.security;

import java.time.LocalDateTime;

public class JwtToken {

    private final String token;

    private final LocalDateTime expiresAt;

    public JwtToken(String token, LocalDateTime expiresAt) {

        this.token = token;

        this.expiresAt = expiresAt;

    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

}