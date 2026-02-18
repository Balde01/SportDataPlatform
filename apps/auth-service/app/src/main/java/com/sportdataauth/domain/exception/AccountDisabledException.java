package com.sportdataauth.domain.exception;

public class AccountDisabledException extends DomainException {

    public AccountDisabledException() {
        super("ACCOUNT_DISABLED");
    }

}
