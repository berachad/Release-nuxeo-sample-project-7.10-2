package org.nuxeo.project.sample.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "User" )
@XmlAccessorType(XmlAccessType.FIELD)
public class User {

	private String username;
	private String password;
	private String lastName;
	private String firstName;
	private String Company;
	private String email;
		
	public User() {
	}

	public User(String username, String password, String company, String email, String firstName, String lastName) {
		super(); 
		this.username = username;
		this.password = password;
		this.Company = company;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setpassword(String password) {
		this.password = password;
	}
	public String getCompany() {
		return Company;
	}
	public void setCompany(String company) {
		Company = company;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	  
}
