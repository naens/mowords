package com.naens.moweb.dao;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.naens.moweb.service.EMF;

// Spring: This class is a "Repository" (or "DAO"), with transactions (rollback if exceptions, etc.)
// N.B.: So the code here should no more explicitly handle Java persistence entity managers, DB transactions, etc.
@SuppressWarnings({"rawtypes","unchecked"})
public abstract class BaseDao<E> {
//	EntityManagerFactory factory = Persistence.createEntityManagerFactory("adsdfaefew");
	private EntityManager entityManager = EMF.get().createEntityManager();
	////////// Attributes (Overridden): //////////
	
	
	////////// Attributes: //////////
	
	// Java persistence entity manager (managed 'auto-magically' via Spring's OpenEntityManagerInViewFilter)
//	@PersistenceContext
//	protected EntityManager entityManager = em;
//	protected EntityManager entityManager = null;

	// Name of the model class corresponding to the current DAO class
	protected String modelClassName = null;
	
	////////// Constructors (Overridden): //////////

	////////// Constructors: //////////

	public BaseDao() {
		// Try to determine the corresponding model class (from the generics class <E>)
		Class clazz = returnedClass();
		if (clazz != null) {
			modelClassName = clazz.getName();
		} else {
			// Else, as a last resort, assume the corresponding model class base name is the same as this DAO class
			// without "Dao" at its end (e.g. 'TopicDao' -> 'Topic').
			// If not the case for a specific model class, then override the default constructor
			// of the corresponding DAO class to define the correct model class name in 'modelClassName'.
			modelClassName = this.getClass().getSimpleName().replaceFirst("[Dd][Aa][Oo]$", "");
			// if the corresponding model class (generics class E) cannot be determined, throw exception instead of simply keeping 'null'?
		}
	}
	
	
	////////// Getters/Setters (Overridden): //////////
	
	
	////////// Getters/Setters: //////////
	
	/**
	 * @return the modelClassName
	 */
	public String getModelClassName() {
		return this.modelClassName;
	}
	
	
	////////// Methods (Standard, Overridden): //////////
	
	////////// Methods (Overridden): //////////
	
	
	////////// Methods (Contrib): //////////

	/**
	 * Return the actual runtime (model) class referenced by the generics argument <E>
	 * @see http://www.artima.com/weblogs/viewpost.jsp?thread=208860
	 */
	public Class returnedClass() {
		List<Class<?>> list = Generics.getTypeArguments(BaseDao.class, getClass());
		if (list == null) return null;
		return list.get(0);
	}

	
	////////// Methods: //////////
	
	/**
	 * Get an item with the given ID from the DB via JPA (typically corresponding DB table's primary key)
	 * <p>e.g.: Level level = levelDao.getById(levelId)
	 */
	public E getById(Long id) {
		if (id == null) {
			return null;
		}
		try {
//			EntityTransaction tx = entityManager.getTransaction();
//			tx.begin();
			E result = (E) entityManager.find(Class.forName(modelClassName), id);
//			tx.commit();
			return result;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Get a list of all such items from the DB via JPA
	 * <p>e.g.: List&lt;Question&gt; questions = questionDao.getAll()
	 */
	public List<E> getAll() {
//		entityManager = factory.createEntityManager();
//		EntityTransaction tx = entityManager.getTransaction();
//		tx.begin();
		List<E> result = entityManager.createQuery("from " + modelClassName).getResultList();
//		tx.commit();
//		entityManager.close();
//		entityManager = null;
		return result;
	}

	public List<E> getAll(String order) {
//		entityManager = factory.createEntityManager();
//		EntityTransaction tx = entityManager.getTransaction();
//		tx.begin();
		List<E> result = entityManager.createQuery("from " + modelClassName + " order by " + order).getResultList();
//		tx.commit();
//		entityManager.close();
//		entityManager = null;
		return result;
	}

	/**
	 * Get the count of all such items in the DB via JPA
	 * <p>e.g.: long numberOfQuestions = questionDao.getCountAll()
	 */
	public long getCountAll() {
		return (Long) entityManager.createQuery("select count(x) from " + getModelClassName() + " x").getSingleResult();
	}

	/**
	 * Get all such items from the DB within a given range via JPA
	 * <p>e.g.: List&lt;Question&gt; rangeOfQuestions = questionDao.getAllInRange(100, 25)
	 */
	public List<E> getAllInRange(long firstResult, long maxResults) {
		// Limit the number of items to get from DB to be more efficient (like a SQL 'limit' clause)
		if (firstResult < 0) firstResult = 0;
		if (maxResults <= 0) maxResults = 1;
		return entityManager.createQuery("from " + getModelClassName() + " order by id asc")
		.setFirstResult((int) firstResult)
		.setMaxResults((int) maxResults)
		.getResultList();
	}
	
	/**
	 * Save (persist) an item into the DB via JPA
	 * <p>e.g.: topicDao.save(topic)
	 */
	public void persist(E... items) {
		if (items == null) {
			return;
		}
//		entityManager = factory.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		tx.begin();
		for (E item : items) {
			System.out.println("DAO: " + this.getClass().getName() + ".save(): trying to persist item: " + item);
			if (item != null) {
				entityManager.persist(item);
			}
		}
		tx.commit();
//		entityManager.close();
//		entityManager = null;
	}

	public void merge(E... items) {
		if (items == null) {
			return;
		}
//		entityManager = factory.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		tx.begin();
		for (E item : items) {
//			System.out.println("DAO: " + this.getClass().getName() + ".save(): trying to persist item: " + item);
			if (item != null) entityManager.merge(item);
		}
		tx.commit();
//		entityManager.close();
//		entityManager = null;
	}

	/**
	 * Delete (remove) an item from the DB via JPA
	 * <p>e.g.: topicDao.remove(topic)
	 */
	public void remove(E item, Object pk) {
		if (item == null) {
			return;
		}

//		entityManager = factory.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		tx.begin();
		if (item != null) {
			if (entityManager.contains(item)) {
				entityManager.remove(item);
		    } else {
		        Object ee = entityManager.getReference(item.getClass(), pk);
		        entityManager.remove(ee);
		    }
//			entityManager.remove(item);
//			entityManager.remove(entityManager.contains(item) ? item : entityManager.merge(item));
		}
		tx.commit();
//		entityManager.close();
//		entityManager = null;
	}

	/**
	 * Get a list of items from the DB via JPA using a given JPA-QL query string with optional named parameters ":arg1", ":arg2",... 
	 * <p>e.g.: ClientDao.getListViaQueryAndArgs("select distinct cl from Client cl join cl.accounts ac where ac.bank = :arg1", bank)
	 * 
	 * @param qlString a Java Persistence query language query string with named parameters ":arg1", ":arg2",... 
	 * @param args the object instances object1, object2, ... to bind to these parameters ":arg1", ":arg2",... in the query string
	 * @return the query results as a List
	 */
	public List<E> getListViaQueryAndArgs(String qlString, Object... args) {
		Query query = entityManager.createQuery(qlString);
		if ((args != null) && (args.length > 0)) {
			int i = 1;
			for (Object obj : args) {
				query.setParameter("arg" + i++, obj);
			}
		}
		return query.getResultList();
	}

	/**
	 * Get a list of items from the DB via JPA using a given JPA-QL query string with optional named parameters ":arg1", ":arg2",...
	 * <p>e.g.: ClientDao.getItemViaQueryAndArgs("select distinct cl from Client cl join cl.accounts ac where ac.bank = :arg1", bank)
	 * 
	 * @param qlString a Java Persistence query language query string with named parameters ":arg1", ":arg2",... 
	 * @param args the object instances object1, object2, ... to bind to these parameters ":arg1", ":arg2",... in the query string
	 * @return the query result as an item of type E (i.e. the first one found, or 'null' if none found)
	 */
	public E getItemViaQueryAndArgs(String qlString, Object... args) {
		List<E> list = getListViaQueryAndArgs(qlString, args);
		if ((list == null) || (list.size() <= 0)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * Get the first item returned from the DB via JPA using a JPA-QL SELECT clause with optional WHERE parameters
	 * <p>e.g.: PlayerDao.getListViaArgs("order by login", "login", login, "password", password)
	 * 
	 * @param qlLastArg a Java Persistence query language query sub-string to append to the SELECT query string (e.g. "ORDER BY ...")
	 * @param args the object instance names and values objectName1, object1, objectName2, object2, ... to bind to this SELECT query string with a "WHERE {objectName1} = {object1} AND {objectName2} = {object2} AND ..." clause
	 * @return the query results as a List
	 */
	public List<E> getListViaArgs(String qlLastArg, Object... args) {
		String queryString = "from " + modelClassName + " ";
		List<Object[]> argQueryParams = null;
		if ((args != null) && (args.length > 0)) {
			argQueryParams = new ArrayList<Object[]>();
			String queryWhereClause = null;
			for (int i = 0; i < args.length ; i += 2) {
				String argQueryParam = "arg" + i;
				String argObjName = (String) args[i];
				Object argObj = args[i + 1];
				argQueryParams.add(new Object[] { argQueryParam, argObj });
				if (queryWhereClause == null) {
					queryWhereClause = " where ";
				} else {
					queryWhereClause += " and ";
				}
				queryWhereClause += " " + argObjName + " = :" + argQueryParam + " ";
			}
			queryString += queryWhereClause;
		}
		if (qlLastArg != null) queryString += " " + qlLastArg;
		Query query = entityManager.createQuery(queryString);
		if ((argQueryParams != null) && (argQueryParams.size() > 0)) {
			for (Object[] obj : argQueryParams) {
				query.setParameter((String) obj[0], obj[1]);
			}
		}
		return query.getResultList();
	}
	
	/**
	 * Get the first item returned from the DB via JPA using a JPA-QL SELECT clause with optional WHERE parameters
	 * <p>e.g.: TopicDao.getItemViaArgs("", "name", name)
	 * 
	 * @param qlLastArg a Java Persistence query language query sub-string to append to the SELECT query string (e.g. "ORDER BY ...")
	 * @param args the object instance names and values objectName1, object1, objectName2, object2, ... to bind to this SELECT query string with a "WHERE {objectName1} = {object1} AND {objectName2} = {object2} AND ..." clause
	 * @return the query result as an item of type E (i.e. the first one found, or 'null' if none found)
	 */
	public E getItemViaArgs(String qlLastArg, Object... args) {
		List<E> list = getListViaArgs(qlLastArg, args);
		if ((list == null) || (list.size() <= 0)) {
			return null;
		}
		return list.get(0);
	}
	
}
