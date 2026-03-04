package com.sportdataauth.domain.exception;

public class AccountLockedException extends DomainException{

    public AccountLockedException() {
        super("ACCOUNT_LOCKED");
    }

}
