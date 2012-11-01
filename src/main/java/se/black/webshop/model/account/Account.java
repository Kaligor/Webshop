package se.black.webshop.model.account;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import se.black.webshop.model.jpautils.JPAEntity;

@MappedSuperclass
abstract class Account implements JPAEntity<Long>{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(unique =true)
	private String username;
	private String password;
	
	protected Account() {}
	
	public Account(String username, String password) {
		this.username = username.toLowerCase();
		this.password = BCrypt.hashpw(password, BCrypt.gensalt());
	}
	
	protected Account(Long id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = BCrypt.hashpw(password, BCrypt.gensalt());
	}
	
	public String getUsername() {
		return username;
	}
	
	public boolean validatePassword(String plainTextPassword) {
		return BCrypt.checkpw(plainTextPassword, password);
	}
	
	@Override
	public Long getID() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
