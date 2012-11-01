package se.black.webshop.model.jpautils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class JPA {
	private static EntityManagerFactory factory = Persistence.createEntityManagerFactory("another_webshop");
	
	/**
	 * Persists a new entity or merge an updated, already existing, entity with the data in the database.
	 * Returns the stored entity.  
	 * @param entity The entity that is going to be stored.
	 * @return the stored entity.
	 * @throws EntityExistsException if the entity has null as Id, and yet already exists in the database.
	 * @throws IllegalArgumentException if instance is not an entity or is a removed entity.
	 */
	public static <T extends JPAEntity<K>, K extends Object> T saveOrUpdate(T entity) {
		EntityManager manager = open();
		try {
			manager.getTransaction().begin();
			if(entity.getID() == null){
				manager.persist(entity);
			} else {
				entity = manager.merge(entity);
			}
			manager.getTransaction().commit();
		} catch (Exception e) {
			 
		} finally {
			close(manager);
		}
		return entity;
	}
	
	/**
	 * Returns a single object as a result of the querystring combined with the parameters and of the class provided.
	 * @param queryString the JP QL string to be executed, starting with SELECT.
	 * @param classOfT the class of the object expected to be returned.
	 * @param parameters a List of parameters containing position and value.
	 * @return a single entity or null if not found.
	 * @throws NonUniqueResultException if more than one entity was the result of the query.
	 * @throws IllegalStateException if the querystring is an Update or Delete-string. 
	 */
	public static <T extends JPAEntity<K>, K extends Object, V> T getOne(String queryString, Class<T> classOfT, List<Parameter<Integer, V>> parameters){
		EntityManager manager = open();
		try {
			T entity = null;
			TypedQuery<T> query = manager.createQuery(queryString, classOfT);
			setParameters(query, parameters);
			try{
				entity = query.getSingleResult();
			} catch (NoResultException e) {
				return null;
			}
			return entity;
		} finally {
			close(manager);
		}
	}
	
	/**
	 * Returns a Collection of objects as a result of the querystring compined with the parameters and of the class provided.
	 * @param queryString the JP QL string to be executed, starting with SELECT.
	 * @param classOfT the class of the object expected to be returned in the collection.
	 * @param parameters a List of the parameters containing the position and value.
	 * @return a Collection of objects
	 * @throws Alot of exceptions, check the TypedQuery<T>.getResultList() for more details.
	 */
	public static <T extends JPAEntity<K>, K extends Object, V> Collection<T> getMany(String queryString, Class<T> classOfT, List<Parameter<Integer, V>> parameters){
		EntityManager manager = open();
		try{
			TypedQuery<T> query = manager.createQuery(queryString, classOfT);
			setParameters(query, parameters);
			Collection<T> resultList = query.getResultList();
			return resultList;
		} finally {
			close(manager);
		}
	}
	
	public static <T extends JPAEntity<K>, K extends Object, V> Collection<T> executeNamedQuery(String queryName, Class<T> classOfT, List<Parameter<String, V>> parameters){
		EntityManager manager = open();
		try{
			TypedQuery<T> query = manager.createNamedQuery(queryName, classOfT);
			setNamedParameters(query, parameters);
			Collection<T> resultList = query.getResultList();
			return resultList;
		}catch (PersistenceException e) {
			return new ArrayList<T>();
		} finally {
			close(manager);
		}
	}
	
	
	/**
	 * Deletes objects matching the querystring combined with the parameters.
	 * @param queryString the JP QL string to be executed, starting with DELETE.
	 * @param parameters a List of parameters containing the position and value
	 * @return the number of rows affected by the delete. 
	 */
	public static <T extends JPAEntity<V>, K extends Object, V> int delete(String queryString, List<Parameter<Integer, V>> parameters){
		EntityManager manager = open();
		try{
			manager.getTransaction().begin();
			Query query = manager.createQuery(queryString);
			setParameters(query, parameters);
			int deletedRows = query.executeUpdate();
			manager.getTransaction().commit();
			return deletedRows;
		}
		finally{
			close(manager);
		}
	}
	
	
	private static <V> Query setParameters(Query query, List<Parameter<Integer, V>> parameters){
		for(Parameter<Integer, V> p : parameters){
			query.setParameter(p.position, p.value);
		}
		return query;
	}
	
	
	private static <V> Query setNamedParameters(Query query, List<Parameter<String, V>> parameters){
		for(Parameter<String, V> p : parameters){
			query.setParameter(p.position, p.value);
		}
		return query;
	}
	
	
	private static EntityManager open(){
		EntityManager manager = null;
		if(factory == null || (! factory.isOpen())){
			factory = Persistence.createEntityManagerFactory("another_webshop");
		}
		
		if(manager == null || (! manager.isOpen())){
			manager = factory.createEntityManager();
		}
		return manager;
	}
	
	
	private static void close(EntityManager manager){
		manager.close();
	}
	
	public static void closeFactory(){
		factory.close();
	}
	
//	public static void main(String[] args) {
//		EntityManager manager = open();
//		manager.getTransaction().begin();
//		manager.createQuery("DELETE FROM Product p").executeUpdate();
//		manager.createQuery("DELETE FROM Category c").executeUpdate();
//		manager.createQuery("DELETE FROM AdminAccount a").executeUpdate();
//		manager.createQuery("DELETE FROM CustomerAccount cu").executeUpdate();
//		manager.createQuery("DELETE FROM Order o").executeUpdate();
//		manager.getTransaction().commit();
//		close(manager);
//	}
}
