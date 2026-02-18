package com.sportdataauth.domain.exception;

public class UserNotEligibleException extends DomainException{

    public UserNotEligibleException() {
        super("USER_EXISTS_NOT_AGENT");
    }

}
