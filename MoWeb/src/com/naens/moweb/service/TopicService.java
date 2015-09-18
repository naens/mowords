package com.naens.moweb.service;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.naens.moweb.dao.TopicDao;
import com.naens.moweb.model.Topic;
import com.naens.moweb.model.User;

@Stateless
public class TopicService {

    @PersistenceContext(unitName = "db-validate")
    private EntityManager entityManager;

	@EJB
	private TopicDao topicDao;

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
		String queryString = "SELECT COUNT (t) FROM Topic t WHERE t.owner=:user";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("user", user);
		Long result = (Long) query.getSingleResult();
		return result.intValue();
	}

	@SuppressWarnings("unchecked")
	public List<Topic> getTopicsSortedByPosition(User user) {
		String queryString = "SELECT t FROM Topic t WHERE t.owner=:user ORDER BY t.position";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("user", user);
		List<Topic> result = query.getResultList();
		return result;
	}

}
