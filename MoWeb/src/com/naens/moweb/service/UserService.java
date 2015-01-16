package com.naens.moweb.service;

import java.util.List;

import com.naens.moweb.dao.UserDao;
import com.naens.moweb.model.GoogleProfile;
import com.naens.moweb.model.User;

public class UserService {

    private static UserDao userDAO = new UserDao();

	public User getByGoogleEmail (String googleEmail) {

		List <User> users = userDAO.getAll();
		for (User user : users) {
			GoogleProfile googleProfile = user.getGoogleProfile();
			if (googleProfile !=null && googleProfile.getEmail().equals(googleEmail)) {
				return user;
			}
		}
		return null;
	}
}
