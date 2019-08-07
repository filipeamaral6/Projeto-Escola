package com.polarising.projetoescola.message.request;

import org.springframework.lang.Nullable;

public class InviteData {
	@Nullable
	private int id;
	
	@Nullable
	private String email;
	
	@Nullable
	private String personalEmail;
	
	@Nullable
	private String firstName;
	
	@Nullable
	private String lastName;
	
	@Nullable
	private String username;
	
	@Nullable
	private String password;
	
	@Nullable
	private String startedAt;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPersonalEmail() {
		return personalEmail;
	}

	public void setPersonalEmail(String email) {
		this.personalEmail = email;
	}

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
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
