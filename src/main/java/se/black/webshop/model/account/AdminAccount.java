package se.black.webshop.model.account;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="admin_account_table")
public class AdminAccount extends Account {
	
	private AdminAccount(){}
	
	private AdminAccount(Long id, String username, String password) {
		super(id, username, password);
	}
	
	public AdminAccount(String username, String password){
		super(username, password);
	}

	
	public AdminAccount update(String newPassword) {
		return new AdminAccount(this.getID(), this.getUsername(), newPassword);		
	}
	
}
