package com.naens.moweb.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.naens.moweb.model.PairTypeSideType;

@Stateless
public class PtstDao {

	@PersistenceContext(unitName = "db-validate")
	private EntityManager entityManager;

	public void persist(PairTypeSideType ptst) {
		entityManager.persist(ptst);
	}

	public void remove(PairTypeSideType ptst) {
		entityManager.remove(ptst);
	}
}
