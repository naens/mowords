package com.naens.moweb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.naens.moweb.model.Topic;

@Stateless
public class TopicDao {

    @PersistenceContext(unitName = "db-validate")
    private EntityManager entityManager;

	public List<Topic> getAll(String string) {
		@SuppressWarnings("unchecked")
		List<Topic> result = entityManager.createQuery("from Topic").getResultList();
		return result;
	}

	public Topic getById(Long topicId) {
		return entityManager.find(Topic.class, topicId);
	}

	public void remove(Topic topic, Long id) {
		entityManager.remove(topic);
	}

	public void persist(Topic topic) {
		entityManager.persist(topic);
	}

}
