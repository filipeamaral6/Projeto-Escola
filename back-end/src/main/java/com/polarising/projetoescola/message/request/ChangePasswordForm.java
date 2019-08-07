package com.polarising.projetoescola.message.request;

import javax.validation.constraints.NotBlank;

public class ChangePasswordForm {
	
	@NotBlank
	private String username;
	
	@NotBlank
	private String currentPassword;
	
	@NotBlank
	private String newPassword;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String password) {
		this.currentPassword = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
