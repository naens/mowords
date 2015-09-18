package com.naens.moweb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.naens.moweb.model.WordFolder;

@Stateless
public class FolderDao {
	@PersistenceContext(unitName = "db-validate")
	private EntityManager entityManager;

	public WordFolder merge(WordFolder folder) {
		return entityManager.merge(folder);

	}

	public WordFolder getById(Long folderId) {
		return entityManager.find(WordFolder.class, folderId);
	}

	public void persist(WordFolder folder) {
		entityManager.persist(folder);
	}

	public void remove(WordFolder folder) {
		entityManager.remove(folder);
	}

	public void persist(List<WordFolder> folders) {

		entityManager.getTransaction().begin();

		for (WordFolder wordFolder : folders) {
			entityManager.persist(wordFolder);
			entityManager.flush();
			entityManager.clear();
		}

		entityManager.getTransaction().commit();

	}

}
