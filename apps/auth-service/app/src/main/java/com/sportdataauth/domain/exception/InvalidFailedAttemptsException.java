package com.sportdataauth.domain.exception;

public class InvalidFailedAttemptsException extends DomainException {

    public InvalidFailedAttemptsException() {
        super("FAILED_ATTEMPTS_MUST_BE_GREATER_OR_EQUAL_TO_ZERO");
    }

}
