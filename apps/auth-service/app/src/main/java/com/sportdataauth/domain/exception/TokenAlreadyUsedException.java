package com.sportdataauth.domain.exception;

public class TokenAlreadyUsedException extends DomainException{

    public TokenAlreadyUsedException() {
        super("TOKEN_ALREADY_USED");
    }

}
