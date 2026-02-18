package com.sportdataauth.domain.exception;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException(String code) {
        super("INVALID_CREDENTIALS");
    }

}
