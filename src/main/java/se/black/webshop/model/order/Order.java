package se.black.webshop.model.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import se.black.webshop.model.jpautils.JPAEntity;
import se.black.webshop.model.product.Product;

@SuppressWarnings("unused")
@Entity
@Table(name = "order_table")
@NamedQueries(value = { @NamedQuery(name = Order.FIND_ALL, query = "SELECT o FROM Order o"),
		@NamedQuery(name = Order.FIND_BY_ORDERNO, query = "SELECT o FROM Order o WHERE o.orderNo = " + Order.PARAM_ORDERNO),
		@NamedQuery(name = Order.DELETE, query = "DELETE FROM Order o WHERE o.orderNo = ?1"),
		@NamedQuery(name = Order.FIND_BY_USERNAME, query = "SELECT o FROM Order o WHERE o.customer.username = " + Order.PARAM_USERNAME) })
public class Order implements JPAEntity<Long> {

	public static final String FIND_ALL = "Order.findAll";
	public static final String FIND_BY_ORDERNO = "Order.findByOrderNo";
	public static final String FIND_BY_PRODUCT_SKU = "Order.findByProductSku";
	public static final String FIND_BY_USERNAME = "Order.findByUsername";
	public static final String DELETE = "Order.deleteOrder";
	public static final String PARAM_ORDERNO = ":orderNo";
	public static final String PARAM_PRODUCT_SKU = ":sku";
	public static final String PARAM_USERNAME = ":username";


	@Id
	@GeneratedValue
	private Long ID;
	@Column(unique = true)
	private Long orderNo;
	private Long orderDate;
	@Embedded
	private OrderedCustomer customer;

	@ElementCollection(fetch = FetchType.EAGER)
//	@JoinTable(name = "order_orderlines")
	@CollectionTable(name="order_orderlines", joinColumns = @JoinColumn(name="order_id"))
	@OrderColumn
	private Collection<OrderLine> orderlines;

	private Order() {
	}

	private Order(Long ID, Long orderNo, Long orderDate, OrderedCustomer orderingCustomer, Collection<OrderLine> orderlines) {
		this.ID = ID;
		this.orderNo = orderNo;
		this.orderDate = orderDate;
		this.customer = orderingCustomer;
		this.orderlines = orderlines;
	}

	Order(Long orderNo, OrderedCustomer orderingCustomer, Collection<OrderLine> orderlines) {
		this.orderNo = orderNo;
		this.orderDate = System.currentTimeMillis();
		this.customer = orderingCustomer;
		this.orderlines = orderlines;
	}

	@Override
	public Long getID() {
		return ID;
	}

	public Long getOrderNo() {
		return orderNo;
	}

	public Date getOrderDate() {
		return new Date(orderDate);
	}

	public Collection<OrderLine> getOrderLines() {
		return orderlines;
	}

	public Long getTotalPrice() {
		Long price = 0L;
		for (OrderLine line : orderlines) {
			price += line.getTotalPrice();
		}

		return price;
	}

	public OrderedCustomer getCustomer() {
		return customer;
	}

	public DeliveryAddress getDeliveryAddress() {
		return customer.getDeliveryAddress();
	}

	public Order update(OrderedCustomer orderingCustomer, Collection<OrderLine> orderlines) {
		return new Order(ID, orderNo, orderDate, orderingCustomer, orderlines);
	}
	
	public Order updateAddress(String street, String zip, String city){
		OrderedCustomer orderedCustomer = new OrderedCustomer(this.customer.getUsername(), this.customer.getDeliveryAddress().update(street, zip, city));
		return new Order(ID, orderNo, orderDate, orderedCustomer, orderlines);
	}
	
	public OrderLine getOrderlineWithProduct(Product product){
		for(OrderLine line : orderlines){
			if(line.getProduct().getSku().equals(product.getSku())){
				return line;
			}
		}
		return null;
	}
	
	public Order setQuantityonOrderline(OrderLine orderline, Integer newQuantity){
		orderlines.remove(orderline);
		orderline = orderline.update(orderline.getProduct(), newQuantity);
		orderlines.add(orderline);
		return update(customer, orderlines);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + ((orderDate == null) ? 0 : orderDate.hashCode());
		result = prime * result + ((orderNo == null) ? 0 : orderNo.hashCode());
		result = prime * result + ((orderlines == null) ? 0 : orderlines.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(obj instanceof Order){
			Order other = (Order) obj;
			if(this.orderDate.equals(other.orderDate) == false){
				return false;
			}
			
			if(this.orderNo.equals(other.orderNo) == false){
				return false;
			}
			
			if(orderlines.size() == other.orderlines.size()){
				List<OrderLine> thisOrderlines = new ArrayList<OrderLine>(orderlines);
				Collections.sort(thisOrderlines);
				List<OrderLine> otherOrderlines = new ArrayList<OrderLine>(other.orderlines);
				Collections.sort(otherOrderlines);

				for(int i = 0; i < thisOrderlines.size(); i++){
					if(! thisOrderlines.get(i).equals(otherOrderlines.get(i))){
						return false;
					}
				}
			} else{
				return false;
			}		
			return true;	
		}
		return false;
	}

	@Override
	public String toString() {
		return "Order [orderNo=" + orderNo + ", orderDate=" + orderDate + ", orderingCustomer=" + customer + ", orderlines=" + orderlines + "]";
	}
}
