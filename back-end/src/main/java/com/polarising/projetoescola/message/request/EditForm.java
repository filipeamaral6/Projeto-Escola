package com.polarising.projetoescola.message.request;

import java.util.Set;

import javax.validation.constraints.NotBlank;

public class EditForm {
	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;
	
	@NotBlank
	private String username;
	
	private String password;
	
	@NotBlank
	private String email;
	
	@NotBlank
	private String personalEmail;
	
	private String startedAt;

	private Set<String> role;
	
	private String passwordRecoveryCode;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}	

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPersonalEmail() {
		return personalEmail;
	}

	public void setPersonalEmail(String personalEmail) {
		this.personalEmail = personalEmail;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Set<String> getRole() {
		return this.role;
	}

	public void setRole(Set<String> role) {
		this.role = role;
	}

	public String getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(String startedAt) {
		this.startedAt = startedAt;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordRecoveryCode() {
		return passwordRecoveryCode;
	}

	public void setPasswordRecoveryCode(String passwordRecoveryCode) {
		this.passwordRecoveryCode = passwordRecoveryCode;
	}
}
