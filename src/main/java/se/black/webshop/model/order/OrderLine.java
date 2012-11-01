package se.black.webshop.model.order;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;


import se.black.webshop.model.jpautils.JPAEntity;

@SuppressWarnings("unused")
@Embeddable
@Table(name="orderline_table")
public class OrderLine implements Comparable<OrderLine>{

	@Embedded
	private OrderedProduct product;
	private Integer amount;

	private OrderLine(){}
	
	OrderLine(OrderedProduct orderedProduct, Integer amount) {
		this.product = orderedProduct;
		this.amount = amount;
	}
	
	public OrderedProduct getProduct() {
		return product;
	}

	public Long getTotalPrice() {
		return (long) (product.getPrice() * amount);
	
	}

	public Integer getAmount() {
		return amount;
	}
	
	public OrderLine update(OrderedProduct orderedProduct, Integer amount){
		return new OrderLine(orderedProduct, amount);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result
				+ ((product == null) ? 0 : product.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof OrderLine){
			OrderLine other = (OrderLine) obj;
			return this.product.equals(other.product) && this.amount.equals(other.amount);
		}
		return false;
	}

	@Override
	public String toString() {
		return "OrderLine [orderedProduct=" + product + ", amount="
				+ amount + "]";
	}

	@Override
	public int compareTo(OrderLine other) {
		return this.product.getName().compareTo(other.product.getName());
	}
}
