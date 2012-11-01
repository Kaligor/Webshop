package se.black.webshop.model.account;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="customer_account_table")
public class CustomerAccount extends Account {
	
	@Embedded
	private Address address;
	
	// TODO - kanske ska ha ett namn på kunden också? :)
	
	@SuppressWarnings("unused")
	private CustomerAccount(){}
	
	private CustomerAccount(Long id, String username, String password, Address address) {
		super(id, username, password);
		this.address = address;
	}
	
	public CustomerAccount(String username, String password, Address address){
		super(username, password);
		this.address = address;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public CustomerAccount update(String newPassword, Address newAddress) {
		return new CustomerAccount(this.getID(), this.getUsername(), newPassword, newAddress);
	}
	
	
}
