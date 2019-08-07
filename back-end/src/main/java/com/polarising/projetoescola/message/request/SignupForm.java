package com.polarising.projetoescola.message.request;

import java.util.Set;

import javax.validation.constraints.*;

public class SignupForm {
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    @NotBlank
    private String username;
    
    @NotBlank
    private String personalEmail;
    
    private Set<String> roles;
    
    @NotBlank
    @Size(min = 6, max = 40, message = "Password must have between 6 and 40 characters")
    private String password;

    
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
		return personalEmail;
	}

	public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public Set<String> getRole() {
    	return this.roles;
    }
    
    public void setRole(Set<String> role) {
    	this.roles = role;
    }
}