package com.naens.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.naens.moweb.model.GoogleProfile;
import com.naens.moweb.model.User;
import com.naens.moweb.service.EMF;

public class InsertUser {

	public static void main(String[] args) {
		String email = "email.email@email.email";
		User user = new User();
		GoogleProfile googleProfile = new GoogleProfile();
		googleProfile.setEmail(email);
		user.setGoogleProfile(googleProfile);

		EntityManager em = EMF.get().createEntityManager();
		EntityTransaction et = em.getTransaction();
		et.begin();
		try {
			em.persist(user);
		} finally {
			et.commit();
			em.close();
		}
	}

}
