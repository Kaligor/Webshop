package se.black.webshop.model.shoppingcart;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import se.black.webshop.model.product.Product;

public class ShoppingCart {

	private Map<String, CartLine> cartlines;
	
	public ShoppingCart() {
		this.cartlines = new HashMap<String, CartLine>();
	}
	
	public Collection<CartLine> getCartLines() {
		return cartlines.values();
	}
	
	public void addProduct(Product product, Integer amount){
		cartlines.put(product.getSku(), new CartLine(product, amount));
	}
	
	public CartLine getCartLine(String productSku){
		return cartlines.get(productSku);
	}
	
	public void removeProduct(String productSku){
		cartlines.remove(productSku);
	}

	public Long getTotalPrice() {
		Long totalPrice = 0L;
		for(CartLine line : cartlines.values()){
			totalPrice += line.getTotalPrice();
		}
		return totalPrice;
	}

	public void setAmount(String sku, int amount) {
		cartlines.get(sku).setAmount(amount);
	}

	public void clear() {
		cartlines.clear();
	}
}
