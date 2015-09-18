package com.naens.moweb.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.naens.moweb.model.WordPairType;

@Stateless
public class WordPairTypeDao {
    @PersistenceContext(unitName = "db-validate")
    private EntityManager entityManager;

	public void remove(WordPairType pairType) {
		entityManager.remove(pairType);
		
	}

	public WordPairType getById(Long id) {
		return entityManager.find(WordPairType.class, id);
	}

}
