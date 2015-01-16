package com.naens.test;

import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class StartHibernate {

	public static void main(String[] args) {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("db-validate");
		EntityManager em = factory.createEntityManager();
		if (em.isOpen()) {
			System.out.println("open");

			Scanner in = new Scanner(System.in);
			String username = in.nextLine();
			System.out.println("You entered : " + username);
			in.close();

			em.close();
			factory.close();
		}
	}
}
