package com.polarising.projetoescola.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.polarising.projetoescola.StringNormalizer;


public class Employee {

	private Long id;

	@NotBlank
	@Size(max = 50)
	private String firstName;

	@NotBlank
	@Size(max = 50)
	private String lastName;
	
	private String fullName;

	@NotBlank
	@Size(min = 3, max = 50)
	private String username = firstName + " " + lastName;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;
	
	@NotBlank
	@Size(max = 50)
	@Email
	private String personalEmail;
	
	private String createdAt;
	
	private String startedAt;
	
	@NotBlank
	private boolean intranet = false;

	@NotBlank
	private int slack = 0;

	@NotBlank
	private boolean office365 = false;
	
	@NotBlank
	private String status;

	public Employee() {
	}
	
	public Employee(String firstName, String lastName, String username, String personalEmail, String startedAt, String emailDomain) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.fullName = firstName + " " + lastName;
		this.username = StringNormalizer.getInstace().unaccent(username).toLowerCase();
		//this.email = username + "@academiapolarising.onmicrosoft.com";
		this.email = username + emailDomain;
		this.personalEmail = personalEmail;
		this.startedAt = startedAt;
		this.status = EmployeeStatus.ACTIVE.toString();
	}

	public Long getId() {
		return id;
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
	
	public String getFullName() {
		this.fullName = firstName + " " + lastName;
		return this.fullName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public boolean isIntranet() {
		return intranet;
	}

	public void setIntranet(boolean intranet) {
		this.intranet = intranet;
	}

	public int getSlack() {
		return slack;
	}

	public void setSlack(int slack) {
		this.slack = slack;
	}

	public boolean isOffice365() {
		return office365;
	}

	public void setOffice365(boolean office365) {
		this.office365 = office365;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(String startedAt) {
		this.startedAt = startedAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
