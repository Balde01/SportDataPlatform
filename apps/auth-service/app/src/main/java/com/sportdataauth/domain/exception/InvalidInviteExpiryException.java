package com.sportdataauth.domain.exception;

public class InvalidInviteExpiryException extends DomainException{

    public InvalidInviteExpiryException() {
        super("INVALID_INVITE_EXPIRY_DATE");
    }

}
