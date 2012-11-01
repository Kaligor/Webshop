package se.black.webshop.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.black.webshop.model.account.AccountManager;
import se.black.webshop.model.account.CustomerAccount;
import se.black.webshop.model.account.JPAAccountManager;
import se.black.webshop.model.exception.DatasourceException;
import se.black.webshop.model.exception.DuplicateEntryException;
import se.black.webshop.model.exception.ManagerException;
import se.black.webshop.model.exception.NoSuchEntryException;
import se.black.webshop.model.order.JPAOrderManager;
import se.black.webshop.model.order.Order;
import se.black.webshop.model.order.OrderLine;
import se.black.webshop.model.order.OrderManager;
import se.black.webshop.model.product.Category;
import se.black.webshop.model.product.JPAProductManager;
import se.black.webshop.model.product.Product;
import se.black.webshop.model.shoppingcart.ShoppingCart;

public class OrderTest {

	private OrderManager ordermanager;
	private ShoppingCart cart;
	private AccountManager accountmanager;
	private JPAProductManager productmanager;
	
	@Before
	public void setup(){
		ordermanager = new JPAOrderManager();
		cart = new ShoppingCart();
		accountmanager = new JPAAccountManager();
		productmanager = new JPAProductManager();
	}
	
	@Test
	public void canCreateOrder() throws ManagerException {
		accountmanager.createCustomer("Me", "secret", "here", "big", "222");
		CustomerAccount customer = accountmanager.getCustomer("Me");
		
		productmanager.createProduct("123ABC", "A product", 100L, "This is a product description", Category.ROOT.createSubcategory("AnotherCategory"));
		Product product = productmanager.getProductBySku("123ABC");
		
		cart.addProduct(product, 3);
		
		Order order = ordermanager.createOrder(cart, customer);
		
		assertNotNull(order);
	}
	
	@Test
	public void canGetAllOrdersForCustomer() throws ManagerException {
		accountmanager.createCustomer("You", "secret", "here", "big", "222");
		
		CustomerAccount customer = accountmanager.getCustomer("You");
		
		productmanager.createProduct("ABC1234", "A product", 100L, "This is a product description", Category.ROOT);
		productmanager.createProduct("ABC124", "Another product", 100L, "This is a product description", Category.ROOT);
		productmanager.createProduct("ABC125", "Yet another product", 100L, "This is a product description", Category.ROOT);
		
		Product product = productmanager.getProductBySku("ABC1234");
		Product productTwo = productmanager.getProductBySku("ABC124");
		Product productThree = productmanager.getProductBySku("ABC125");
		
		cart.addProduct(product, 3);
		cart.addProduct(product, 3);
		cart.addProduct(productTwo, 5);
		cart.addProduct(productThree, 1);
		
		Order order = ordermanager.createOrder(cart, customer);
		
		Collection<Order> ordersByUsername = ordermanager.getOrdersByUsername(customer.getUsername());
		
		List<Order> ordersFromStorageList = new ArrayList<Order>(ordersByUsername);
		
		assertEquals(order, ordersFromStorageList.get(0));
	}
	
	@Test
	public void canGetOneOrder() throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException {
		accountmanager.createCustomer("SomeoneElse", "secret", "here", "big", "222");
		
		CustomerAccount customer = accountmanager.getCustomer("SomeoneElse");
		
		productmanager.createProduct("BBC123", "A product", 100L, "This is a product description", Category.ROOT);
		productmanager.createProduct("BBC124", "Another product", 100L, "This is a product description", Category.ROOT);
		productmanager.createProduct("BBC125", "Yet another product", 100L, "This is a product description", Category.ROOT);
		
		Product product = productmanager.getProductBySku("BBC123");
		Product productTwo = productmanager.getProductBySku("BBC124");
		Product productThree = productmanager.getProductBySku("BBC125");
		
		cart.addProduct(product, 3);
		cart.addProduct(product, 3);
		cart.addProduct(productTwo, 5);
		cart.addProduct(productThree, 1);
		
		Order order = ordermanager.createOrder(cart, customer);
		Order orderFromStorage = ordermanager.getOrderByOrderNo(order.getOrderNo());
		
		assertEquals(order, orderFromStorage);
		
	}
	
	@Test
	public void canUpdateOrder() throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException {
		accountmanager.createCustomer("NotMe", "secret", "here", "big", "222");
		
		CustomerAccount customer = accountmanager.getCustomer("NotMe");
		
		productmanager.createProduct("CBC123", "A product", 100L, "This is a product description", Category.ROOT);
		
		Product product = productmanager.getProductBySku("CBC123");
		
		cart.addProduct(product, 3);
		
		Order order = ordermanager.createOrder(cart, customer);
		order = ordermanager.getOrderByOrderNo(order.getOrderNo());
		
		OrderLine orderline = order.getOrderlineWithProduct(product);
		order = order.setQuantityonOrderline(orderline, 10);
		
		ordermanager.updateOrder(order);
		order = ordermanager.getOrderByOrderNo(order.getOrderNo());
		
		assertEquals(new Integer(10), new ArrayList<OrderLine>(order.getOrderLines()).get(0).getAmount());
		
		
	}
	
	@Test(expected=NoSuchEntryException.class)
	public void canDeleteOrder() throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException {
		accountmanager.createCustomer("MyCousine", "secret", "here", "big", "222");
		
		CustomerAccount customer = accountmanager.getCustomer("MyCousine");
		
		productmanager.createProduct("DBC123", "A product", 100L, "This is a product description", Category.ROOT);
		
		Product product = productmanager.getProductBySku("DBC123");
		
		cart.addProduct(product, 3);
		
		Order order = ordermanager.createOrder(cart, customer);
		order = ordermanager.getOrderByOrderNo(order.getOrderNo());
		 
		ordermanager.deleteOrder(order);
		ordermanager.getOrderByOrderNo(order.getOrderNo());
	
	}

}
