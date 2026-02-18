package com.sportdataauth.domain.exception;

public class EmailNotAllowedException extends DomainException {

    public EmailNotAllowedException() {
        super("EMAIL_NOT_ALLOWED");
    }

}
