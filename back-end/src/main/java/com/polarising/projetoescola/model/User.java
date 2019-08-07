package com.polarising.projetoescola.model;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.polarising.projetoescola.StringNormalizer;

public class User {

	private Long id;

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;

	@NotBlank
	private String username;

	@NotBlank
	@Email
	private String email;
	
	@NotBlank
	@Email
	private String personalEmail;

	@NotBlank
	private String password;

	private Set<Role> roles = new HashSet<>();
	
	private Date createdAt;
	
	private String passwordRecoveryCode;
	
	public User() {
	}

	public User(String firstName, String lastName, String username, String password, String personalEmail, String emailDomain) {
		this.firstName = firstName;
		this.lastName = lastName;
		//this.username = StringNormalizer.getInstace().unaccent(firstName + "." + lastName).toLowerCase();
		this.username = StringNormalizer.getInstace().unaccent(username).toLowerCase();
		this.email = username + emailDomain;
		this.personalEmail = personalEmail;
		this.password = password;
		this.passwordRecoveryCode = null;
	}

	public String getPersonalEmail() {
		return personalEmail;
	}

	public void setPersonalEmail(String personalEmail) {
		this.personalEmail = personalEmail;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void addRole(Role role) {
		this.roles.add(role);
	}
	
	public void clearRoles() {
		this.roles.clear();
	}
	
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getPasswordRecoveryCode() {
		return passwordRecoveryCode;
	}

	public void setPasswordRecoveryCode(String passwordRecoveryCode) {
		this.passwordRecoveryCode = passwordRecoveryCode;
	}	
}