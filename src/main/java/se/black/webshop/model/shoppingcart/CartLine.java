package se.black.webshop.model.shoppingcart;

import se.black.webshop.model.product.Product;

public class CartLine {

	private final Product product;
	private Integer amount;

	CartLine(Product product, Integer amount) {
		this.product = product;
		this.amount = amount;
	}

	public Product getProduct() {
		return product;
	}

	public Integer getAmount() {
		return amount;
	}
	
	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Long getTotalPrice() {
		return product.getPrice() * amount;
	}

}
