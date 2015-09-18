package com.naens.moweb.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.naens.moweb.model.Results;

@Stateless
public class ResultsDao {

    @PersistenceContext(unitName = "db-validate")
    private EntityManager entityManager;

	public void persist(Results r) {
		entityManager.persist(r);
	}

	public Results getById(Long id) {
		return entityManager.find(Results.class, id);
	}

	public void remove(Results r) {
		entityManager.remove(r);
	}
}
