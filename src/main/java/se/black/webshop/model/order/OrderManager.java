package se.black.webshop.model.order;

import java.util.Collection;

import se.black.webshop.model.account.CustomerAccount;
import se.black.webshop.model.exception.DatasourceException;
import se.black.webshop.model.exception.DuplicateEntryException;
import se.black.webshop.model.exception.ManagerException;
import se.black.webshop.model.exception.NoSuchEntryException;
import se.black.webshop.model.shoppingcart.ShoppingCart;

public interface OrderManager {

	Order createOrder(ShoppingCart shoppingCart, CustomerAccount customerAccount) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException;
	Order getOrderByOrderNo(Long orderNo) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException;
	Collection<Order> getAllOrders();
	Collection<Order> getOrdersByUsername(String username) throws NoSuchEntryException, DuplicateEntryException, DatasourceException, ManagerException;
	Collection<Order> getOrdersByProductSku(String sku) throws NoSuchEntryException, DuplicateEntryException, DatasourceException, ManagerException;
	void updateOrder(Order order) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException;
	void deleteOrder(Order order) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException;
}
