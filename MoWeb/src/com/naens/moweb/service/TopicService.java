package com.naens.moweb.service;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.naens.moweb.dao.TopicDao;
import com.naens.moweb.model.Topic;
import com.naens.moweb.model.User;

public class TopicService {

	private TopicDao topicDao = new TopicDao();

	private EntityManager entityManager = EMF.get().createEntityManager();

	@SuppressWarnings("unchecked")
	public Topic getByName(String topicName, User user) {
		String queryString = "SELECT t FROM Topic t WHERE t.owner=:user and t.name=:name";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("user", user);
		query.setParameter("name", topicName);
		List<Topic> result = query.getResultList();
		return result.size() == 0 ? null : result.get(0);
	}

	public List<Topic> getByUserId(Long userid) {
		List<Topic> topics = topicDao.getAll("id");
		List<Topic> result = new LinkedList <Topic>();
		for (Topic topic : topics) {
			if (topic.getOwner().getId().equals(userid)) {
				result.add(topic);
			}
		}
		return result;
	}

	public int countByUser(User user) {
		String queryString = "SELECT count(*) FROM Topic t WHERE t.owner=:user";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("user", user);
		Long result = (Long) query.getSingleResult();
		return result.intValue();
	}

	@SuppressWarnings("unchecked")
	public List<Topic> getTopicsSortedByPosition(EntityManager em, User user) {
		String queryString = "SELECT t FROM Topic t WHERE t.owner=:user ORDER BY t.position";
		Query query = em.createQuery(queryString);
		query.setParameter("user", user);
		List<Topic> result = query.getResultList();
		return result;
	}

	public List<Topic> getTopicsSortedByPosition(User user) {
		return getTopicsSortedByPosition(entityManager, user);
	}

}
