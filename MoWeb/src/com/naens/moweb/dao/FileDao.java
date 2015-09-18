package com.naens.moweb.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.naens.moweb.model.WordFile;

@Stateless
public class FileDao {

    @PersistenceContext(unitName = "db-validate")
    private EntityManager entityManager;

	public void persist(WordFile file) {
		entityManager.persist(file);
	}

	public WordFile getById(Long id) {
		return entityManager.find(WordFile.class, id);
	}

	public void remove(WordFile file) {
		entityManager.remove(file);
	}
}
