package com.sportdataauth.domain.exception;

public class InvalidRefreshTokenExpiryException extends DomainException {

    public InvalidRefreshTokenExpiryException() {
        super("EXPIRES_AT_MUST_BE_AFTER_CREATED_AT");
    }

}
