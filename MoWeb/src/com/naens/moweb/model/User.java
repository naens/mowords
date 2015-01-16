package com.naens.moweb.model;


import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table (name="\"user\"")
public class User {

	@Id
	@GeneratedValue
	private Long id;

	private String firstname;

	private String lastname;

	@OneToOne (cascade = CascadeType.ALL)
	private GoogleProfile googleProfile;

	public User() {

	}

	public User(String firstname, String lastname, Date birthdate, GoogleProfile googleProfile) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.googleProfile = googleProfile;
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

	public GoogleProfile getGoogleProfile() {
		return googleProfile;
	}

	public void setGoogleProfile(GoogleProfile googleProfile) {
		this.googleProfile = googleProfile;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstname=" + firstname + ", lastname=" + lastname + "]";
	}
}