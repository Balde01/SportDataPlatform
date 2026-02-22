package com.sportdataauth.security.exception;

public class TokenHashingException extends RuntimeException {

    public TokenHashingException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenHashingException(String message) {
        super(message);
    }
}