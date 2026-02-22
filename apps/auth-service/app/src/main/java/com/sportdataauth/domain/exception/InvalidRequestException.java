package com.sportdataauth.domain.exception;

public class InvalidRequestException extends DomainException {

    public InvalidRequestException(String code) {
        super(code);
    }

    public static InvalidRequestException nullValue(String field) {
        return new InvalidRequestException("NULL_"+ field.toUpperCase());
    }
    public static InvalidRequestException invalidValue(String field) {
        return new InvalidRequestException("INVALID_"+ field.toUpperCase());
    }
}
