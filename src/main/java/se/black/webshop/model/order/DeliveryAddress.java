package se.black.webshop.model.order;

import javax.persistence.Embeddable;

@SuppressWarnings("unused")
@Embeddable
public class DeliveryAddress {

	private String street;
	private String zip;
	private String city;

	private DeliveryAddress(){}
	
	DeliveryAddress(String street, String zip, String city) {
		this.street = street;
		this.zip = zip;
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public String getZip() {
		return zip;
	}

	public String getCity() {
		return city;
	}
	
	public DeliveryAddress update(String street, String zip, String city){
		return new DeliveryAddress(street, zip, city);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((street == null) ? 0 : street.hashCode());
		result = prime * result + ((zip == null) ? 0 : zip.hashCode());
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
		DeliveryAddress other = (DeliveryAddress) obj;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (street == null) {
			if (other.street != null)
				return false;
		} else if (!street.equals(other.street))
			return false;
		if (zip == null) {
			if (other.zip != null)
				return false;
		} else if (!zip.equals(other.zip))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DeliveryAddress [street=" + street + ", zip=" + zip + ", city="
				+ city + "]";
	}
}
