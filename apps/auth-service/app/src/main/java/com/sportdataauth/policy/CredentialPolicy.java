package com.sportdataauth.policy;

public interface  CredentialPolicy {

    public boolean isEmailAllowed(String email);
    
    public boolean isPasswordStrong(String password);
}

