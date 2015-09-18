package com.naens.moweb.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.naens.moweb.model.WordSideType;

@Stateless
public class WordSideTypeDao {
    @PersistenceContext(unitName = "db-validate")
    private EntityManager entityManager;

	public WordSideType getById(Long id) {
		return entityManager.find(WordSideType.class, id);
	}

	public void persist(WordSideType sideType) {
		entityManager.persist(sideType);
	}

	public void remove(WordSideType sideType) {
		entityManager.remove(sideType);
	}

}
