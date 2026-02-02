package com.sportdataauth.dto;

public class ProvisionAgentRequest {
	
    private String email;
    
    public ProvisionAgentRequest(String email) {
		this.email = email;
	}
    
    
    public String getEmail() {
		return email;
	}
    
    public void setEmail(String email) {
    	this.email = email;
    }

}
