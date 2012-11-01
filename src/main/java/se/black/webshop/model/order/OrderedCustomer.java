package se.black.webshop.model.order;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@SuppressWarnings("unused")
@Embeddable
public class OrderedCustomer {
	
	private String username;
	@Embedded
	private DeliveryAddress deliveryAddress;
	
	private OrderedCustomer(){}
	
	OrderedCustomer(String username, DeliveryAddress deliveryAddress) {
		this.username = username;
		this.deliveryAddress = deliveryAddress;
	}

	public String getUsername() {
		return username;
	}
	
	public DeliveryAddress getDeliveryAddress(){
		return deliveryAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((deliveryAddress == null) ? 0 : deliveryAddress.hashCode());
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
		OrderedCustomer other = (OrderedCustomer) obj;
		if (deliveryAddress == null) {
			if (other.deliveryAddress != null)
				return false;
		} else if (!deliveryAddress.equals(other.deliveryAddress))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OrderingCustomer [username=" + username + ", deliveryAddress="
				+ deliveryAddress + "]";
	}
}
