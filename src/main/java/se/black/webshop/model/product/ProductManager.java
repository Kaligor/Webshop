package se.black.webshop.model.product;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import se.black.webshop.model.exception.DatasourceException;
import se.black.webshop.model.exception.DuplicateEntryException;
import se.black.webshop.model.exception.NoSuchEntryException;

public interface ProductManager {

	Product createProduct(String sku, String name, long price,
			String description, Category category) throws DatasourceException, DuplicateEntryException;

	Product updateProduct(Product product) throws DatasourceException;

	Product getProductBySku(String sku) throws DatasourceException, NoSuchEntryException;

	Collection<Product> getProductsByCategory(Category category) throws DatasourceException;

	Collection<Product> getAllProducts() throws DatasourceException;

	void deleteProduct(Product product) throws DatasourceException;
	
	AttributeTemplate createAttributeTemplate(String name, String suffix, String... legalValues) throws DatasourceException, DuplicateEntryException;

	AttributeTemplate updateAttributeTemplate(AttributeTemplate template) throws DatasourceException;

	Collection<Product> getProductsByAttributeTemplate(
			AttributeTemplate template) throws DatasourceException;

	List<Product> getProductsByAttributeTemplate(AttributeTemplate template,
			Comparator<Product> comparator) throws DatasourceException;


	void deleteAttributeTemplate(AttributeTemplate template)
			throws DatasourceException;

}