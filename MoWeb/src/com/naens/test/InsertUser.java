package com.naens.test;

import javax.ejb.EJB;

import com.naens.moweb.dao.UserDao;
import com.naens.moweb.model.GoogleProfile;
import com.naens.moweb.model.User;

public class InsertUser {

	@EJB
	private static UserDao userDao;

	public static void main(String[] args) {
		String email = "email.email@email.email";
		GoogleProfile googleProfile = new GoogleProfile();
		googleProfile.setEmail(email);
		User user = new User(googleProfile);
		userDao.persist(user);
	}

}
