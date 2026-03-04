package com.sportdataauth.domain.exception;

public abstract class DomainException extends RuntimeException {
    
    private final String code;

    protected DomainException(String code) {
        super(code);
        this.code = code;
    }

    public String getCode() {
        return code;
    } 

}
