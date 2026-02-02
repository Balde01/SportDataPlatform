package com.sportdataauth.dto;

public class InviteAcceptRequest {
	private String token;
	private String newPassword;
	
	public InviteAcceptRequest(String token, String newPassword) {
		this.token = token;
		this.newPassword = newPassword;
	}
	
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
