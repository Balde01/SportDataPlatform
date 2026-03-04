package com.sportdataauth.domain.exception;

public class InvitationTokenNotFoundException extends DomainException {

    public InvitationTokenNotFoundException() {
        super("INVITE_TOKEN_NOT_FOUND");
    }

}
