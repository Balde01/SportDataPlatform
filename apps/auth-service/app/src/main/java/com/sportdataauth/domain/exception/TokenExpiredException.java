package com.sportdataauth.domain.exception;

public class TokenExpiredException extends DomainException {

    public TokenExpiredException() {
        super("TOKEN_EXPIRED");
    }
}
