package com.naens.moweb.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table (name="\"user\"")
public class User {

	@Id
	@GeneratedValue
	private Long id;

	private String firstname;

	private String lastname;

	private String email;

	private String gId;

	public User(GoogleProfile googleProfile) {
		this.firstname = googleProfile.getName().getGivenName();
		this.lastname = googleProfile.getName().getFamilyName();
		this.email = googleProfile.getEmail();
		this.gId = googleProfile.getId();

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstname=" + firstname + ", lastname=" + lastname + "]";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getgId() {
		return gId;
	}

	public void setgId(String gId) {
		this.gId = gId;
	}
}