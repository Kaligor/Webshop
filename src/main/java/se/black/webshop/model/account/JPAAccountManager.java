package se.black.webshop.model.account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.JDBCConnectionException;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import se.black.webshop.model.exception.DatasourceException;
import se.black.webshop.model.exception.DuplicateEntryException;
import se.black.webshop.model.exception.ManagerException;
import se.black.webshop.model.exception.NoSuchEntryException;
import se.black.webshop.model.jpautils.JPA;
import se.black.webshop.model.jpautils.Parameter;

public class JPAAccountManager implements AccountManager {

	public JPAAccountManager() {
	}

	@Override
	public CustomerAccount createCustomer(String username, String password, String street, String city, String zip) throws ManagerException{
		Address address = new Address(street, city, zip);
		CustomerAccount account = new CustomerAccount(username, password, address);
		
		Collection<CustomerAccount> existingAccount = JPA.getMany("SELECT a FROM CustomerAccount a WHERE a.username = ?1", CustomerAccount.class, Arrays.asList(new Parameter<Integer, String>(1,username)));
		if (existingAccount.size() != 0) {
			throw new DuplicateEntryException("A customer already exists with username " + username);
		}
		
		
		try{
			JPA.saveOrUpdate(account);
		} catch(Exception e){
			throw handle(e, username);
		}
		
		return getCustomer(username);
	}

	
	@Override
	public CustomerAccount getCustomer(String username) throws ManagerException {
		CustomerAccount account = null;
		List<Parameter<Integer, String>> parameters = new ArrayList<Parameter<Integer, String>>();
		parameters.add(new Parameter<Integer, String>(1, username));
		try{
			account = JPA.getOne("select a from CustomerAccount a where a.username = ?1", CustomerAccount.class, parameters);
		}catch(Exception e){
			throw handle(e, username);
		}
		if(account == null){
			throw new NoSuchEntryException("no customer with username in storage: "+username);
		} 
		return account;
	}
	
	
	@Override
	public Collection<CustomerAccount> getAllCustomers() throws DatasourceException, ManagerException {
		try {
			return JPA.getMany("SELECT a from CustomerAccount a", CustomerAccount.class, new ArrayList<Parameter<Integer, String>>());
		} catch (Exception e) {
			throw handle(e);
		}
	}

	
	@Override
	public void updateCustomer(CustomerAccount customer) throws ManagerException {
		
		try{
			JPA.saveOrUpdate(customer);			
		}catch(Exception e){
			throw handle(e, customer.getUsername());
		}

	}

	@Override
	public void deleteCustomer(String username) throws ManagerException {
		int deletedRows = 0;
		List<Parameter<Integer, String>> parameters = new ArrayList<Parameter<Integer, String>>();
		parameters.add(new Parameter<Integer, String>(1, username));
		try{
			deletedRows = JPA.delete("DELETE FROM CustomerAccount a WHERE a.username = ?1", parameters);			
		} catch (Exception e){
			throw handle(e, username);
		}	
		if(deletedRows == 0){
			throw new NoSuchEntryException("Could not delete entry. No entry with username in storage: " + username);
		}
	}

	
	@Override
	public AdminAccount createAdmin(String username, String password) throws ManagerException {
		AdminAccount account = new AdminAccount(username, password);
		try{
			JPA.saveOrUpdate(account);
		} catch (Exception e){
			throw handle(e, username);
		}
		return getAdmin(username);
		
	}

	
	@Override
	public AdminAccount getAdmin(String username) throws ManagerException{
		AdminAccount account = null;
		List<Parameter<Integer, String>> parameters = new ArrayList<Parameter<Integer, String>>();
		parameters.add(new Parameter<Integer, String>(1, username));
		try{
			account = JPA.getOne("select a from AdminAccount a where a.username = ?1", AdminAccount.class, parameters);
		} catch(Exception e){
			throw handle(e, username);
		}
		if(account == null){
			throw new NoSuchEntryException("no customer with username in storage: "+username);
		}
		return account;
	}
	
	@Override
	public Collection<AdminAccount> getAllAdmins() throws DatasourceException, ManagerException {
		try {
			return JPA.getMany("SELECT a from AdminAccount a", AdminAccount.class, new ArrayList<Parameter<Integer, String>>());
		} catch (Exception e) {
			throw handle(e);
		}
	}

	@Override
	public void updateAdmin(AdminAccount admin) throws ManagerException{

		try{
			JPA.saveOrUpdate(admin);			
		}catch(Exception e){
			throw handle(e, admin.getUsername());
		}
		
	}

	@Override
	public void deleteAdmin(AdminAccount admin) throws ManagerException{
		int deletedRows = 0;
		String username = admin.getUsername();
		List<Parameter<Integer, String>> parameters = new ArrayList<Parameter<Integer, String>>();
		parameters.add(new Parameter<Integer, String>(1, username));
		try{
			deletedRows = JPA.delete("DELETE FROM AdminAccount a WHERE a.username = ?1", parameters);
		}catch(Exception e){
			throw handle(e, username);
		}
		if(deletedRows == 0){
			throw new NoSuchEntryException("Could not delete entry. No entry with username in storage: " + username);
		}
		
	}
	
	
	
	
	private ManagerException handle(Exception e){
		return handle(e, "");
	}
	
	private ManagerException handle(Exception e, String data) {
		
		if(e.getCause() instanceof EntityExistsException) {
		return new DuplicateEntryException("Account with name already exists: "+data, e);
		}
		
		if(e.getCause() instanceof ConstraintViolationException) {
		return new DuplicateEntryException("Account with name already exists: "+data, e);
		} 
		
		if(e.getCause() instanceof IllegalArgumentException) {
			return new NoSuchEntryException("Could not handle account. Account does not exist.", e);
		} 

		if(e.getCause() instanceof JDBCConnectionException) {
			return new DatasourceException("Could not connect to the storage.", e);
		}
		
		if (e instanceof PersistenceException) {
			return new ManagerException("Unknown error occured. Account could not be handled.", e);
		}
		
		if(e.getCause() instanceof MySQLIntegrityConstraintViolationException || e instanceof MySQLIntegrityConstraintViolationException){
			
		}
		
		return new ManagerException("An even more unknown issue occured. Ask someone else to solve it, Im out of here!");
		
	}

}