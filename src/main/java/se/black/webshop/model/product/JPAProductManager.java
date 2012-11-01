package se.black.webshop.model.product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import se.black.webshop.model.exception.DatasourceException;
import se.black.webshop.model.exception.DuplicateEntryException;
import se.black.webshop.model.exception.NoSuchEntryException;
import se.black.webshop.model.jpautils.Parameter;



import static se.black.webshop.model.jpautils.JPA.*;

public class JPAProductManager implements ProductManager {
	

	@Override
	public Product createProduct(String sku, String name, long price, String description, Category category) throws DatasourceException, DuplicateEntryException {
		Product product = new Product(sku, name, price, description, category);
		
		if (!executeNamedQuery(Product.FIND_BY_SKU, Product.class, Arrays.asList(new Parameter<String, String>("sku", sku))).isEmpty()) {
			throw new DuplicateEntryException("a product already exists with sku " + sku);
		}
		
		try {
			product = saveOrUpdate(product);
		}  catch (Exception e) {
			throw new DatasourceException("Error when persisting product", e);
		}
		return product;
	}
	

	@Override
	public Product updateProduct(Product product) throws DatasourceException {
		
		try {
			product = saveOrUpdate(product);
		} catch (Exception e) {
			throw new DatasourceException("Error when trying to update product", e);
		}
		
		return product;
	}
	

	@Override
	public Product getProductBySku(String sku) throws DatasourceException, NoSuchEntryException {
		
		Collection<Product> returnProduct = new ArrayList<Product>();
		
		try {
			returnProduct = executeNamedQuery(Product.FIND_BY_SKU, Product.class, Arrays.asList(new Parameter<String, String>("sku", sku)));
		} catch (Exception e) {
			throw new DatasourceException("Error when trying to fetch product", e);
		}
		
		if (!returnProduct.iterator().hasNext()) {
			throw new NoSuchEntryException("No product in storage with sku " + sku);
		}
		 
		 return returnProduct.iterator().next();
	}
	
	@Override
	public Collection<Product> getProductsByCategory(Category category) throws DatasourceException {
		try {
			return executeNamedQuery(Product.FIND_BY_CATEGORY, Product.class, Arrays.asList(new Parameter<String, Category>("category", category)));
		} catch (Exception e) {
			throw new DatasourceException("Error when fetching products", e);
		}
	}
	
	@Override
	public Collection<Product> getProductsByAttributeTemplate(AttributeTemplate template) throws DatasourceException {
		
		try {
			return executeNamedQuery(Product.FIND_BY_ATTRIBUTE_TEMPLATE, Product.class, Arrays.asList(new Parameter<String, AttributeTemplate>("attributeTemplate", template)));
		} catch (Exception e) {
			throw new DatasourceException("Error when fetching products", e);
		}
	}
	
	@Override
	public List<Product> getProductsByAttributeTemplate(AttributeTemplate template, Comparator<Product> comparator) throws DatasourceException {
		List<Product> products = new LinkedList<Product>(getProductsByAttributeTemplate(template));
		Collections.sort(products, comparator);
		return products;
	}
	
	@Override
	public Collection<Product> getAllProducts() throws DatasourceException {
		try {
			return executeNamedQuery(Product.FIND_ALL, Product.class, new LinkedList<Parameter<String, String>>());
		} catch (Exception e) {
			throw new DatasourceException("Error when fetching products", e);
		}
	}
	
	@Override
	public void deleteProduct(Product product) throws DatasourceException {
		
		try {
			// workaround på grund av att jpa inte cascadar Delete:s till ElementCollections-s
			product.getAttributes().clear();
			updateProduct(product);
			
			delete("delete from Product p where p.sku = ?1", Arrays.asList(new Parameter<Integer, String>(1, product.getSku())));
		} catch (Exception e) {
			throw new DatasourceException("Error when deleting products", e);
		}
	}
	
	@Override
	public AttributeTemplate createAttributeTemplate(String name, String suffix, String... legalValues) throws DatasourceException, DuplicateEntryException {
		
		if (!executeNamedQuery(AttributeTemplate.FIND_BY_NAME, AttributeTemplate.class, Arrays.asList(new Parameter<String, String>("name", name))).isEmpty()) {
			throw new DuplicateEntryException("A attribute template already exists with name " + name);
		}
		
		AttributeTemplate template = new AttributeTemplate(name, suffix);
		template.getLegalValues().addAll(Arrays.asList(legalValues));
		
		try {
			return saveOrUpdate(template);
		} catch (Exception e) {
			throw new DatasourceException("Error when creating AttributeTemplate", e);
		}
	}
	
	@Override
	public AttributeTemplate updateAttributeTemplate(AttributeTemplate template) throws DatasourceException {
		
		try {
			return saveOrUpdate(template);
		} catch (Exception e) {
			throw new DatasourceException("Error when updating attribute template", e);
		}
	}
	
	@Override
	public void deleteAttributeTemplate(AttributeTemplate template) throws DatasourceException {

		// workaround på grund av att jpa inte cascadar Delete:s till ElementCollections-s
		template.getLegalValues().clear();
		updateAttributeTemplate(template);
		
		List<Product> productsWithAttribute = (List<Product>) executeNamedQuery(Product.FIND_BY_ATTRIBUTE_TEMPLATE, Product.class, Arrays.asList(new Parameter<String, AttributeTemplate>("attributeTemplate", template)));
		
		for (Product product : productsWithAttribute) {
			Product productWithRemovedAttribute = null;
			boolean iHasDeletedAnAttribute = false;
			Iterator<Attribute> iterator = product.getAttributes().iterator();
			while (iterator.hasNext()) {
				Attribute currentAttribute = iterator.next();
				if (currentAttribute.getTemplate().equals(template)) {
					
					productWithRemovedAttribute = product.removeAttribute(currentAttribute);
					iHasDeletedAnAttribute = true;
					
				}
			}
			if (iHasDeletedAnAttribute) {
				updateProduct(productWithRemovedAttribute);
			}
		}

		
		delete("DELETE FROM AttributeTemplate a WHERE a.name = ?1", Arrays.asList(new Parameter<Integer, String>(1, template.getName())));
		
	}
	
	

}
