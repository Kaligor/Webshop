package se.black.webshop.model.order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.JDBCConnectionException;

import se.black.webshop.model.exception.DatasourceException;
import se.black.webshop.model.exception.DuplicateEntryException;
import se.black.webshop.model.exception.ManagerException;
import se.black.webshop.model.exception.NoSuchEntryException;
import se.black.webshop.model.jpautils.JPA;
import se.black.webshop.model.jpautils.Parameter;


import static se.black.webshop.model.jpautils.JPA.*;

public class JPAOrderManager extends AbstractOrderManager{

//	public final String queryByOrderNo = "SELECT o FROM Order o WHERE o.orderNo = ?1";
//	public final String queryByUsername = "SELECT o FROM Order o WHERE o.customer.username = ?1";
//	public final String queryBySKU = "SELECT o FROM Order o WHERE o.orderlines.product.sku = ?1";
	
	@Override
	protected Order create(Order order) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException {
		try{
			return saveOrUpdate(order);
		} catch (Exception e) {
			throw handle(e);
		}
	}

	@Override
	protected Order getByOrderNo(Long orderNo) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException {
		List<Parameter<String, Long>> parameters = new ArrayList<Parameter<String, Long>>();
		parameters.add(new Parameter<String, Long>("orderNo", orderNo));
		
		Collection<Order> resultList = null;
		try{
			resultList = executeNamedQuery(Order.FIND_BY_ORDERNO, Order.class, parameters);
		} catch (Exception e) {
			throw handle(e);
		}
		
		if(resultList.size() > 1){
			throw new DuplicateEntryException("Multiple orders was targeted with the provided order no. A single order could not be returned.");
		} else if(resultList.isEmpty()){
			throw new NoSuchEntryException("No order was targeted with the provided order no.");
		} else {
			return resultList.iterator().next();
		}
		
	}
	@Override
	protected Collection<Order> getAll() {
		return executeNamedQuery(Order.FIND_ALL, Order.class, new ArrayList<Parameter<String, String>>());
	}

	//TODO: TA in en customer, om inga ordrar hittades, returnera tom lista
	@Override
	protected Collection<Order> getByUsername(String username) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException {
		List<Parameter<String, String>> parameters = new ArrayList<Parameter<String, String>>();
		parameters.add(new Parameter<String, String>("username", username));
		
		Collection<Order> resultList = null;
		try{
			resultList = executeNamedQuery(Order.FIND_BY_USERNAME, Order.class, parameters);
		} catch (Exception e) {
			throw handle(e);
		}
		if(resultList.isEmpty()){
			throw new NoSuchEntryException("No order was targeted with the provided order no.");
		} else {
			return resultList;
		}
	}

	@Override
	protected Collection<Order> getByProductSku(String sku) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException {
		List<Parameter<String, String>> parameters = new ArrayList<Parameter<String, String>>();
		parameters.add(new Parameter<String, String>("sku", sku));
		
		Collection<Order> resultList = null;
		try{
			resultList = executeNamedQuery(Order.FIND_BY_PRODUCT_SKU, Order.class, parameters);
		} catch (Exception e) {
			throw handle(e);
		}
		
		if(resultList.isEmpty()){
			throw new NoSuchEntryException("No order was targeted with the provided order no.");
		} else {
			return resultList;
		}
	}

	@Override
	protected void update(Order order) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException{
		try{
			saveOrUpdate(order);
		} catch (Exception e) {
			throw handle(e);
		}
	}

	@Override
	protected void delete(Order order) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException{
		List<Parameter<Integer, Long>> parameters = new ArrayList<Parameter<Integer,Long>>();
		parameters.add(new Parameter<Integer, Long>(1, order.getOrderNo()));
		try{
			order = order.update(order.getCustomer(), new ArrayList<OrderLine>());
			update(order);
			JPA.delete("DELETE FROM Order o WHERE o.orderNo = ?1", parameters);
		} catch (Exception e) {
			throw handle(e);
		}
	}
	
	private ManagerException handle(Exception e) {
		if(e.getCause() instanceof EntityExistsException) {
			return new DuplicateEntryException("Could not create order. Perhaps order no is duplicated?", e);
		} 
		
		if(e.getCause() instanceof ConstraintViolationException) {
			return new DuplicateEntryException("A constraint is violated when trying to store the order. Order was not created.", e);
		} 
		
		if(e.getCause() instanceof IllegalArgumentException) {
			return new NoSuchEntryException("Could not handle order. Order does not exist.", e);
		} 
		
		if(e.getCause() instanceof JDBCConnectionException) {
			return new DatasourceException("Could not connect to the storage.", e);
		} 
		
		if(e instanceof PersistenceException) {
			return new ManagerException("Unknown error occured. Order could not be handle.", e);
		} 
		e.printStackTrace();
		return new ManagerException("An even more unknown issue occured. Ask someone else to solve it, Im out of here!");
	}

	@Override
	protected Long getUniqueOrderNo() {
		Collection<Order> orders = new ArrayList<Order>();
		Long orderNo = null;
		do{
			orderNo = (long) ((Math.random() * 1000000000) +1);
			orders = getMany("SELECT o FROM Order o WHERE o.orderNo = ?1", Order.class, Arrays.asList(new Parameter<Integer, Long>(1, orderNo)));
		} while ( ! orders.isEmpty());
		return orderNo;
	}
}
