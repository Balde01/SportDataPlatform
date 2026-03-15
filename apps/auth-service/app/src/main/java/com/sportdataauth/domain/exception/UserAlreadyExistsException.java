package com.sportdataauth.domain.exception;

public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException() {
        super("USER_ALREADY_EXISTS");
    }

}
