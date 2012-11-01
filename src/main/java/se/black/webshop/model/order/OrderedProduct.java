package se.black.webshop.model.order;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import se.black.webshop.model.jpautils.JPAEntity;

@SuppressWarnings("unused")
@Embeddable
//@Table(name="orderedproduct_table")
public class OrderedProduct {

	private String sku;
	private String name;
	private Long price;
	private String description;
	private String categoryName;

	private OrderedProduct(){}
	
	OrderedProduct(String sku, String name, Long price, String description, String categoryName) {
		this.sku = sku;
		this.name = name;
		this.price = price;
		this.description = description;
		this.categoryName = categoryName;
	}

	private void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	private void setDescription(String description) {
		this.description = description;
	}
	
	private void setName(String name) {
		this.name = name;
	}
	
	private void setPrice(Long price) {
		this.price = price;
	}
	
	private void setSku(String sku) {
		this.sku = sku;
	}
	
	public String getSku() {
		return sku;
	}
	
	public String getName() {
		return name;
	}
	
	public Long getPrice() {
		return price;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getCategoryName() {
		return categoryName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((categoryName == null) ? 0 : categoryName.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((sku == null) ? 0 : sku.hashCode());
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
		OrderedProduct other = (OrderedProduct) obj;
		if (categoryName == null) {
			if (other.categoryName != null)
				return false;
		} else if (!categoryName.equals(other.categoryName))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (sku == null) {
			if (other.sku != null)
				return false;
		} else if (!sku.equals(other.sku))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OrderedProduct [sku=" + sku + ", name=" + name + ", price="
				+ price + ", description=" + description + ", categoryName="
				+ categoryName + "]";
	}
}
