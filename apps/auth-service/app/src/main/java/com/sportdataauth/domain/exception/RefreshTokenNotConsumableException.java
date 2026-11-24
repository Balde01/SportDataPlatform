package com.sportdataauth.domain.exception;

public class RefreshTokenNotConsumableException extends DomainException {

    public RefreshTokenNotConsumableException() {
        super("REFRESH_TOKEN_NOT_CONSUMABLE_EXCEPTION");
    }

}
