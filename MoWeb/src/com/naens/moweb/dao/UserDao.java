package com.naens.moweb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.naens.moweb.model.User;

@Stateless
public class UserDao {

    @PersistenceContext(unitName = "db-validate")
    private EntityManager entityManager;

	public void persist(User user) {
		entityManager.persist(user);
	}

	public User getById(Long id) {
		return entityManager.find(User.class, id);
	}

	public User getByEmail(String email) {
		String queryString = "SELECT u FROM User u WHERE u.email=:email";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("email", email);
		@SuppressWarnings("unchecked")
		List<User> result = query.getResultList();
		return result.size() == 0 ? null : result.get(0);
	}
}
