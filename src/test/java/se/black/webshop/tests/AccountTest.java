package se.black.webshop.tests;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.black.webshop.model.account.AccountManager;
import se.black.webshop.model.account.Address;
import se.black.webshop.model.account.AdminAccount;
import se.black.webshop.model.account.CustomerAccount;
import se.black.webshop.model.account.JPAAccountManager;
import se.black.webshop.model.exception.DatasourceException;
import se.black.webshop.model.exception.DuplicateEntryException;
import se.black.webshop.model.exception.ManagerException;
import se.black.webshop.model.exception.NoSuchEntryException;
import se.black.webshop.model.jpautils.JPA;
import se.black.webshop.model.jpautils.Parameter;


public class AccountTest {
	
	private AccountManager manager;
	
	@Before
	public void setup() throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException {
		
		//TODO: hur tommer man en table m JPA queries?
		JPA.delete("DELETE FROM AdminAccount a  WHERE a.id >=0", new ArrayList<Parameter<Integer, String>>());
		JPA.delete("DELETE FROM CustomerAccount a  WHERE a.id >=0", new ArrayList<Parameter<Integer, String>>());
		
		manager = new JPAAccountManager();
		manager.createAdmin("test", "123");
		manager.createCustomer("user242", "242", "Jernvegsgatan 2", "Uppsala","12453");
		
	}

	@Test
	public void testThatAdminAccountCanBeCreatedAndRetrieved() throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException{
		AdminAccount account = manager.createAdmin("adminOne", "secret");
		AdminAccount retrievedAccount = manager.getAdmin("adminOne");
		
		assertEquals(retrievedAccount.getUsername(), "adminone");
		assertTrue(retrievedAccount.validatePassword("secret"));
		
		assertEquals(account, retrievedAccount);
		assertNotSame(account, retrievedAccount);
		assertEquals(account.hashCode(), retrievedAccount.hashCode());
	}
	
	@Test
	public void testThatAdminAccountCanBeUpdated() throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException{
		
		AdminAccount account = manager.getAdmin("test");
		account = account.update("newPassword");
		
		manager.updateAdmin(account);
		
		account = manager.getAdmin("test");
		assertFalse(account.validatePassword("123"));
		assertTrue(account.validatePassword("newPassword"));
		
	}
	
	@Test(expected = NoSuchEntryException.class)
	public void testThatAdminAccountCanBeDeleted() throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException{
		AdminAccount test;
		try {
			test = manager.getAdmin("test");
		} catch (NoSuchEntryException e) {
			throw new ManagerException("test doesn't exist");
		}
		
		manager.deleteAdmin(test);
		manager.getAdmin("test");
		
	}
	
	
	@Test
	public void testThatCustomerAccountCanBeCreatedAndRetrieved() throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException{
		CustomerAccount customer = manager.createCustomer("EliasKron", "123", "Stationsv 3", "Kalarne", "84142");
		
		CustomerAccount retrievedAccount = manager.getCustomer("EliasKron");
		
		assertEquals(customer, retrievedAccount);
		
	}
	
	@Test
	public void testThatCustomerAccountCanBeUpdated() throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException{
		CustomerAccount account = manager.getCustomer("user242");
		account = account.update("newPassword", new Address("rollerbladesgatan", "visby", "12345"));
		manager.updateCustomer(account);
		
		CustomerAccount fromStorage = manager.getCustomer("user242");
		
		assertEquals(account, fromStorage);
		assertNotSame(account, fromStorage);
		
	}
	
	@Test(expected = NoSuchEntryException.class)
	public void testThatCustomerAccountCanBeDeleted() throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException {		
		try {
			manager.getCustomer("user242");
		} catch (NoSuchEntryException e) {
			throw new ManagerException("user242 doesn't exist");
		}
		
		manager.deleteCustomer("user242");
		manager.getCustomer("user242");
	}
	
	@Test
	public void testThatAllAccountsCanBeRetrived() throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException {
		
		List<CustomerAccount> createdCustomers = new ArrayList<CustomerAccount>();
		List<AdminAccount> createdAdmins = new ArrayList<AdminAccount>();
		
		for (int i = 0; i < 5; i++) {
			String randomUsername = new Double(Math.random()).toString();
			CustomerAccount newCustomerAccount = manager.createCustomer(randomUsername, "secret", "Tellusborgsvägen", "Hägersten", "12640");
			AdminAccount newAdminAccount = manager.createAdmin(randomUsername, "secret");
			createdCustomers.add(newCustomerAccount);
			createdAdmins.add(newAdminAccount);
		}
		
		Collection<CustomerAccount> allCustomers = manager.getAllCustomers();
		Collection<AdminAccount> allAdmins = manager.getAllAdmins();
		
		assertEquals(true, allCustomers.containsAll(createdCustomers));
		assertEquals(true, allAdmins.containsAll(createdAdmins));
		
	}
	
	@Test
	public void canTryToCreateTwoInstancesAndThenDelete() throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException {
		manager.createCustomer("anca01", "secret", "lingongatan", "piteå", "12345");
		
		try {
			manager.createCustomer("anca01", "secret", "lingongatan", "piteå", "12345");
		} catch (DuplicateEntryException e) {
//			 
		}
		
		manager.deleteCustomer("anca01");
		
	}
}
