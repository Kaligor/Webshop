package se.black.webshop.model.account;

import java.util.Collection;

import se.black.webshop.model.exception.DatasourceException;
import se.black.webshop.model.exception.DuplicateEntryException;
import se.black.webshop.model.exception.ManagerException;
import se.black.webshop.model.exception.NoSuchEntryException;

public interface AccountManager{

	// customer methods
	CustomerAccount createCustomer(String username, String password, String street, String city, String zip) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException;

	CustomerAccount getCustomer(String username) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException;

	void updateCustomer(CustomerAccount customer) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException;

	void deleteCustomer(String username) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException;
	
	Collection<CustomerAccount> getAllCustomers() throws DatasourceException, ManagerException;
	
	
	// admin methods
	AdminAccount createAdmin (String username, String password) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException;

	AdminAccount getAdmin(String username) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException;

	void updateAdmin(AdminAccount admin) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException;

	void deleteAdmin(AdminAccount admin) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException;

	Collection<AdminAccount> getAllAdmins() throws DatasourceException, ManagerException;

}
