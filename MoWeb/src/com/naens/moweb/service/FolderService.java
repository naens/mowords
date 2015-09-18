package com.naens.moweb.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.naens.moweb.model.Topic;
import com.naens.moweb.model.WordFolder;
import com.naens.moweb.model.WordPairType;

@Stateless
public class FolderService {

    @PersistenceContext(unitName = "db-validate")
    private EntityManager em;

	@SuppressWarnings("unchecked")
	public WordFolder getByName(String folderName, Topic topic) {
		try {
			String queryString = "SELECT f FROM WordFolder f WHERE f.topic=:topic AND f.name=:name";
			Query query = em.createQuery(queryString);
			query.setParameter("topic", topic);
			query.setParameter("name", folderName);
			List<WordFolder> results = query.getResultList();
			return results.size() > 0 ? results.get(0) : null;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}


	@SuppressWarnings("unchecked")
	public List<WordFolder> getByPairTypeSortedByNumber(Topic topic) {
		try {
			String queryString = "SELECT f FROM WordFolder f WHERE f.topic=:topic AND f.pairType IS NULL ORDER BY f.orderNumber";
			Query query = em.createQuery(queryString);
			query.setParameter("topic", topic);
			return query.getResultList();
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}	

	@SuppressWarnings("unchecked")
	public List<WordFolder> getByPairTypeSortedByNumber(WordPairType pairType) {
		try {
			String queryString = "SELECT f FROM WordFolder f WHERE f.pairType=:pt ORDER BY f.orderNumber";
			Query query = em.createQuery(queryString);
			query.setParameter("pt", pairType);
			List<WordFolder> result = query.getResultList();
			return result;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	public int countFoldersByPairType(Topic topic, WordPairType pairType) {
		if (pairType == null) {
			String queryString = "SELECT count(f) FROM WordFolder f WHERE f.topic=:topic AND f.pairType IS NULL";
			Query query = em.createQuery(queryString);
			query.setParameter("topic", topic);
			return ((Long) query.getSingleResult()).intValue();
		}
		String queryString = "SELECT count(f) FROM WordFolder f WHERE f.topic=:topic AND f.pairType=:pt";
		Query query = em.createQuery(queryString);
		query.setParameter("topic", topic);
		query.setParameter("pt", pairType);
		return ((Long) query.getSingleResult()).intValue();
	}

//	public void updateNumbers(WordFolder[] folders) {
//		em.getTransaction().begin();
//		for (WordFolder wordFolder : folders) {
//			Query query = em.createQuery("UPDATE WordFolder wf SET orderNumber = :orderNumber WHERE wf.id = :id");
//			query.setParameter("orderNumber", wordFolder.getOrderNumber());
//			query.setParameter("id", wordFolder.getId());
//			int updateCount = query.executeUpdate();
//			System.out.println("updated:"+updateCount);
//		}
//		em.getTransaction().commit();
//	}

//	public void rename(WordFolder folder) {
//		em.getTransaction().begin();
//		Query query = em.createQuery("UPDATE WordFolder wf SET name = :name WHERE wf.id = :id");
//		query.setParameter("name", folder.getName());
//		query.setParameter("id", folder.getId());
//		int updateCount = query.executeUpdate();
//		System.out.println("rename-updated:"+updateCount);
//		em.getTransaction().commit();
//	}

}
