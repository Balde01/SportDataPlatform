package com.sportdataauth.domain.exception;

public class WeakPasswordException extends DomainException{

    public WeakPasswordException() {
        super("WEAK_PASSWORD");
    }

}
