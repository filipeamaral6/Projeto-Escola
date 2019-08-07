package com.polarising.projetoescola.message.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateEmployeeForm {
	@NotBlank
	@Size(max = 50)
	private String firstName;

	@NotBlank
	@Size(max = 50)
	private String lastName;
	
	@NotBlank
	@Size(min=3, max = 100)
	private String username;
	
	@NotBlank
	private String personalEmail;

	private String startedAt;

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

	public String getUsername() {
		return this.username;
	}
	
	public String getPersonalEmail() {
		return this.personalEmail;
	}

	public String getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(String startedAt) {
		this.startedAt = startedAt;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPersonalEmail(String personalEmail) {
		this.personalEmail = personalEmail;
	}
}
