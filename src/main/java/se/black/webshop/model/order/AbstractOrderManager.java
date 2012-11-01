package se.black.webshop.model.order;

import java.util.ArrayList;
import java.util.Collection;

import se.black.webshop.model.account.Address;
import se.black.webshop.model.account.CustomerAccount;
import se.black.webshop.model.exception.DatasourceException;
import se.black.webshop.model.exception.DuplicateEntryException;
import se.black.webshop.model.exception.ManagerException;
import se.black.webshop.model.exception.NoSuchEntryException;
import se.black.webshop.model.product.Product;
import se.black.webshop.model.shoppingcart.CartLine;
import se.black.webshop.model.shoppingcart.ShoppingCart;

 abstract class AbstractOrderManager implements OrderManager{
	
	@Override
	public Order createOrder(ShoppingCart shoppingCart, CustomerAccount customerAccount) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException {
		Collection<OrderLine> orderlines = new ArrayList<OrderLine>();
		Address address = customerAccount.getAddress();
		DeliveryAddress deliveryAddress = new DeliveryAddress(address.getStreet(), address.getZip(), address.getCity());
		OrderedCustomer orderingCustomer = new OrderedCustomer(customerAccount.getUsername(), deliveryAddress);

		for(CartLine cartline : shoppingCart.getCartLines()){
			Product product = cartline.getProduct();
			OrderedProduct orderedProduct = new OrderedProduct(product.getSku(), product.getName(), product.getPrice(), product.getDescription(), product.getCategory().getName());
			orderlines.add(new OrderLine(orderedProduct, cartline.getAmount()));
		}
		
		Long orderNo = getUniqueOrderNo();
		
		Order order = create(new Order(orderNo, orderingCustomer, orderlines));
		return order;
	}
	

	@Override
	public Order getOrderByOrderNo(Long orderNo) throws DatasourceException, ManagerException {
		// TODO Auto-generated method stub
		return getByOrderNo(orderNo);
	}
	
	@Override
	public Collection<Order> getAllOrders() {
		return getAll();
	}

	@Override
	public Collection<Order> getOrdersByUsername(String username) throws DuplicateEntryException, DatasourceException, ManagerException {
		// TODO Auto-generated method stub
		return getByUsername(username);
	}

	@Override
	public Collection<Order> getOrdersByProductSku(String sku) throws DuplicateEntryException, DatasourceException, ManagerException {
		// TODO Auto-generated method stub
		return getByProductSku(sku);
	}

	@Override
	public void updateOrder(Order order) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException {
		// TODO Auto-generated method stub
		update(order);
	}
	
	@Override
	public void deleteOrder(Order order) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException {
		// TODO Auto-generated method stub
		delete(order);
	}
	
	protected abstract Long getUniqueOrderNo();
	protected abstract Order create(Order order) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException;
	protected abstract Order getByOrderNo(Long orderNo) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException;
	protected abstract Collection<Order> getAll();
	protected abstract Collection<Order> getByUsername(String username) throws NoSuchEntryException, DuplicateEntryException, DatasourceException, ManagerException;
	protected abstract Collection<Order> getByProductSku(String sku) throws NoSuchEntryException, DuplicateEntryException, DatasourceException, ManagerException;
	protected abstract void update(Order order) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException;
	protected abstract void delete(Order order) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException;
	
}
