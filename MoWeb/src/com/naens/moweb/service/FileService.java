package com.naens.moweb.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.naens.moweb.model.WordFile;
import com.naens.moweb.model.WordFolder;

public class FileService {

	private EntityManager em = EMF.get().createEntityManager();

	@SuppressWarnings("unchecked")
	public WordFile getByName(WordFolder folder, String fileName) {
		String queryString = "SELECT f FROM WordFile f WHERE f.folder=:folder AND f.name=:name";
		Query query = em.createQuery(queryString);
		query.setParameter("folder", folder);
		query.setParameter("name", fileName);
		List <WordFile> resultList = query.getResultList();
		return resultList.size() > 0 ? resultList.get(0) : null;
	}

	public int countFilesInFolder(WordFolder folder) {
		String queryString = "SELECT count(*) FROM WordFile f WHERE f.folder=:folder";
		Query query = em.createQuery(queryString);
		query.setParameter("folder", folder);
		return ((Long) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<WordFile> getFilesOrdered(WordFolder folder) {
		String queryString = "SELECT f FROM WordFile f WHERE f.folder=:folder ORDER BY f.orderNumber";
		Query query = em.createQuery(queryString);
		query.setParameter("folder", folder);
		return query.getResultList();
	}

}
