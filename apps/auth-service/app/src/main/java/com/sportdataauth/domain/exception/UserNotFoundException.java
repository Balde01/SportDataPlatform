package com.sportdataauth.domain.exception;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException() {
        super("USER_NOT_FOUND");
    }

}
