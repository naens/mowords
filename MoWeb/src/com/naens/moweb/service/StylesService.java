package com.naens.moweb.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.naens.moweb.model.PairTypeSideType;
import com.naens.moweb.model.Topic;
import com.naens.moweb.model.WordPairType;
import com.naens.moweb.model.WordSideType;

public class StylesService {

	private EntityManager entityManager = EMF.get().createEntityManager();

	public WordSideType getSideTypeByName(String sideTypeName, Topic topic) {
		String queryString = "SELECT st FROM WordSideType st WHERE st.name=:name AND st.topic=:topic";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("name", sideTypeName);
		query.setParameter("topic", topic);
		try {
			WordSideType sts = (WordSideType) query.getSingleResult();
			return sts;
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List <WordPairType> getPairTypes(Topic topic) {
		try{
			String queryString = "SELECT DISTINCT f.pairType FROM WordFolder f WHERE f.topic=:topic";
			Query query = entityManager.createQuery(queryString);
			query.setParameter("topic", topic);
			List <WordPairType> sts = query.getResultList();
			return sts;
		} catch (Throwable x) {
			x.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<WordSideType> getSideTypes(Topic topic) {
		try{
			String queryString = "SELECT DISTINCT st FROM WordSideType st WHERE st.topic=:topic ORDER BY st.listPosition";
			Query query = entityManager.createQuery(queryString);
			query.setParameter("topic", topic);
			List<WordSideType> result = query.getResultList();
			return result;
		} catch (Throwable x) {
			x.printStackTrace();
			return null;
		}
	}

	public boolean isSideTypeinPTST(WordSideType sideType) {
		String queryString = "SELECT count(*) FROM PairTypeSideType ptst WHERE ptst.sideType=:st";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("st", sideType);
		return (Long) query.getSingleResult() > 0;
	}

	@SuppressWarnings("unchecked")
	public List<PairTypeSideType> getPtstsSortedByNumber(EntityManager entityManager2, WordPairType pairType) {
		String queryString = "FROM PairTypeSideType ptst WHERE ptst.pairType=:pt ORDER BY ptst.number";
		Query query = entityManager2.createQuery(queryString);
		query.setParameter("pt", pairType);
		List<PairTypeSideType> result = query.getResultList();
		return result;
	}

	public int countByTopic(Topic topic) {
		String queryString = "SELECT count(*) FROM WordSideType st WHERE st.topic=:topic";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("topic", topic);
		Long result = (Long) query.getSingleResult();
		return result.intValue();
	}

}
