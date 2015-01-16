package com.naens.moweb.service;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class EMF {
	private static final EntityManagerFactory emfInstance = Persistence.createEntityManagerFactory("db-validate");

	private EMF() {
	}

	public static EntityManagerFactory get() {
		return emfInstance;
	}
}
