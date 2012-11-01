package se.black.webshop.tests;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.black.webshop.model.account.AccountManager;
import se.black.webshop.model.account.CustomerAccount;
import se.black.webshop.model.account.JPAAccountManager;
import se.black.webshop.model.exception.DuplicateEntryException;
import se.black.webshop.model.exception.ManagerException;
import se.black.webshop.model.exception.NoSuchEntryException;
import se.black.webshop.model.order.DeliveryAddress;
import se.black.webshop.model.order.JPAOrderManager;
import se.black.webshop.model.order.Order;
import se.black.webshop.model.order.OrderLine;
import se.black.webshop.model.order.OrderManager;
import se.black.webshop.model.order.OrderedCustomer;
import se.black.webshop.model.order.OrderedProduct;
import se.black.webshop.model.product.Category;
import se.black.webshop.model.product.JPAProductManager;
import se.black.webshop.model.product.Product;
import se.black.webshop.model.shoppingcart.CartLine;
import se.black.webshop.model.shoppingcart.ShoppingCart;

public class BasicFunctionalityTest {

	private JPAProductManager productManager;
	private OrderManager orderManager;
	private AccountManager accountManager;
	private ShoppingCart shoppingCart;
	
	@Before
	public void setup(){
		productManager = new JPAProductManager();
		orderManager = new JPAOrderManager();
		accountManager = new JPAAccountManager();
		shoppingCart = new ShoppingCart();
	}
	
	@Test
	public void canCreateProduct() {
		try{
			Category parent = Category.ROOT;
			Category category = Category.ROOT.createSubcategory("My First Category");
			Product product = productManager.createProduct("sku-no-1-2", "name", 150L, "A product", category);
			String sku = product.getSku();
			String name = product.getName();
			Long price = product.getPrice();
			String description = product.getDescription();
			Category category2 = product.getCategory();
		} catch(DuplicateEntryException e){
			//TODO: handle
		} catch(ManagerException e){
			//TODO: handle database-issues
		}
	}
	
	@Test
	public void canGetProduct(){
		try{
			Category parent = Category.ROOT;
			Category category = Category.ROOT.createSubcategory("blabla-name");
			Product product = productManager.createProduct("sku-hfdsjkgfs", "name", 150L, "description", category);
			Product productFromStorage = productManager.getProductBySku("sku");
			
			//TODO: other get-methods that return List<Product> or Map<K, Product>
			
			assertEquals(product, productFromStorage);
		} catch(NoSuchEntryException e){
			//TODO: handle
		} catch(ManagerException e){
			//TODO: handle database-issues
		}
	}
	
	@Test
	public void canUpdateProduct(){
		try{
			Category parent = Category.ROOT;
			Category category = Category.ROOT.createSubcategory("sum-other-name");
			Product product = productManager.createProduct("sku-fhjdsjkhfds", "name", 150L, "description", category);
			Product productFromStorage = productManager.getProductBySku("sku");
			
			String sku = productFromStorage.getSku();
			String name = productFromStorage.getName();
			Long price = productFromStorage.getPrice();
			String description = productFromStorage.getDescription();
			Category category2 = productFromStorage.getCategory();
			
			Product updatedProduct = productFromStorage.update(name, price, description, category);
			productManager.updateProduct(updatedProduct);
		} catch(NoSuchEntryException e){
			//TODO: handle
		} catch(ManagerException e){
			//TODO: handle database-issues
		}
	}
	
	@Test
	public void canDeleteProduct(){
		try{
			Category parent = Category.ROOT;
			Category category = Category.ROOT.createSubcategory("another-name");
			Product product = productManager.createProduct("sku-hfjkdshkdd", "name", 150L, "description", category);
			Product productFromStorage = productManager.getProductBySku("sku");
			
			productManager.deleteProduct(productFromStorage);
			
		} catch(DuplicateEntryException e){
			//TODO: handle
		} catch(NoSuchEntryException e){
			//TODO: handle
		} catch(ManagerException e){
			//TODO: handle database-issues
		}
	}
	
	@Test
	public void canHandleProductInShoppingCart(){
		try{
			Category parent = Category.ROOT;
			Category category = Category.ROOT.createSubcategory("yet-another-name");
			Product product = productManager.createProduct("sku-nmvc,jdfh", "name", 150L, "description", category);
			Product productFromStorage = productManager.getProductBySku("sku");
			
			shoppingCart.addProduct(product, 1);
			assertEquals(shoppingCart.getTotalPrice(), (Long)product.getPrice());
			
			shoppingCart.removeProduct(productFromStorage.getSku());
			shoppingCart.setAmount(product.getSku(), 2);
			Collection<CartLine> cartlines = shoppingCart.getCartLines();
			shoppingCart.clear();
			
			//TODO: complete this test.
			
		} catch(DuplicateEntryException e){
			//TODO: handle
		} catch(NoSuchEntryException e){
			//TODO: handle
		} catch(ManagerException e){
			//TODO: handle database-issues
		}
	}
	
//	ORDER-TESTS
	
	@Test
	public void canCreateOrder() throws NoSuchEntryException, ManagerException{
		accountManager.createCustomer("black", "secret", "storgatan", "sthlm", "12640");
		String username = "black";
		CustomerAccount customerAccount = accountManager.getCustomer(username);
		
		Product p = productManager.createProduct("abc123", "A Product", 10000, "Great product! Buy Now", Category.ROOT);
		
		shoppingCart.addProduct(p, 5);
		Order order = orderManager.createOrder(shoppingCart, customerAccount);
		
		Long orderNo = order.getOrderNo();
		Date orderDate = order.getOrderDate();
		Collection<OrderLine> orderlines = order.getOrderLines();
		Long orderprice = order.getTotalPrice();
		
		OrderedCustomer orderingcustomer = order.getCustomer();
		username = orderingcustomer.getUsername();
		DeliveryAddress deliveryaddress = order.getDeliveryAddress();
		String street = deliveryaddress.getStreet();
		String zip = deliveryaddress.getZip();
		String city = deliveryaddress.getCity();
		
		OrderLine orderline = ((List<OrderLine>)orderlines).get(0);
		OrderedProduct orderedProduct = orderline.getProduct();
		Long price = orderline.getTotalPrice();
		int pcs = orderline.getAmount();
	}
	
	@Test
	public void canGetOrder(){
		
	}

}
