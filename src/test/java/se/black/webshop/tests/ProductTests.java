package se.black.webshop.tests;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import se.black.webshop.model.exception.DatasourceException;
import se.black.webshop.model.exception.DuplicateEntryException;
import se.black.webshop.model.exception.NoSuchEntryException;
import se.black.webshop.model.product.AttributeTemplate;
import se.black.webshop.model.product.Category;
import se.black.webshop.model.product.JPAProductManager;
import se.black.webshop.model.product.Product;
import se.black.webshop.model.product.ProductManager;

public class ProductTests {
	
	private ProductManager productManager;

	
	@Before
	public void setup() {
		productManager = new JPAProductManager();
	}

	@Test
	public void canFindParent() throws DuplicateEntryException, DatasourceException {
		
		Category one = Category.ROOT.createSubcategory("one");
		Category two = one.createSubcategory("two");
		Category three = two.createSubcategory("three");

		Category branchTwoLevel1 = Category.ROOT.createSubcategory("branchTwoLevelOne");
		Category branchTwoLevel2 = branchTwoLevel1.createSubcategory("branchTwoLevelTwo");
		
		assertEquals(one, two.getParent());
		assertEquals(two, three.getParent());
		assertEquals(branchTwoLevel1, branchTwoLevel2.getParent());
		
		two.clearChildren();
		one.clearChildren();
		branchTwoLevel1.clearChildren();
		Category.ROOT.clearChildren();
		assertEquals(0, Category.ROOT.getChildren().size());
	}
	
	@Test
	public void canDoEqualsOnCategoryBasedOnPositionInTree() throws NoSuchEntryException, DuplicateEntryException, DatasourceException {
		Category skor = Category.ROOT.createSubcategory("skor");
		Category herrskor = skor.createSubcategory("herrskor");
		
		assertEquals(herrskor, Category.getCategory("/skor/herrskor/"));
		
		skor.clearChildren();
		Category.ROOT.clearChildren();
		assertEquals(0, Category.ROOT.getChildren().size());
		
	}
	
	@Test(expected=NoSuchEntryException.class)
	public void canRemoveSingleChildCategory() throws DuplicateEntryException, NoSuchEntryException, DatasourceException{
		Category gosedjur = Category.ROOT.createSubcategory("Gosedjur");
		Category nallar = gosedjur.createSubcategory("Nallar");
		gosedjur.createSubcategory("Kaniner");
		gosedjur.removeChild(nallar);
		String nallarnasPath = nallar.getPath();
		
		gosedjur.clearChildren();
		Category.ROOT.clearChildren();
		
		Category.getCategory(nallarnasPath);
	}
	
	@Test
	public void canGetRoot() throws NoSuchEntryException {
		Category shouldBeRoot = Category.getCategory("/");
		assertSame(Category.ROOT, shouldBeRoot);
	}
	
	@Test
	public void cannotRemoveACategoryWithProductsOrSubcategories() throws DuplicateEntryException, DatasourceException {
		
		Category gosedjur = Category.ROOT.createSubcategory("gosedjur");
		
		Product product = productManager.createProduct("ABC123", "paddington", 2000, "A cute bear", gosedjur);
		
		try {
			Category.ROOT.removeChild(gosedjur);
		} catch (Exception e) {
			if (!(e instanceof DuplicateEntryException)) {
				fail("Another exception than DuplicateEntryException was thrown when category contains product");
			}
		}
		
		gosedjur.createSubcategory("nallebjörnar");
		
		try {
			Category.ROOT.removeChild(gosedjur);
		} catch (Exception e) {
			if (!(e instanceof DuplicateEntryException)) {
				fail("Another exception than DuplicateEntryException was thrown when category contains subcategories and product");
			}
		}
		
		product = product.update(product.getName(), product.getPrice(), product.getDescription(), Category.ROOT);
		productManager.updateProduct(product);
		
		
		try {
			Category.ROOT.removeChild(gosedjur);
		} catch (Exception e) {
			if (!(e instanceof DuplicateEntryException)) {
				fail("Another exception than DuplicateEntryException was thrown when category contains subcategories");
			}
		}
		
		gosedjur.clearChildren();
		Category.ROOT.clearChildren();
		productManager.deleteProduct(product);
		
	}
	
	@Test(expected=DuplicateEntryException.class)
	public void cannotAddTwoSubcategoriesWithTheSameName() throws DuplicateEntryException, DatasourceException {
		try {
		Category.ROOT.createSubcategory("name");
		Category.ROOT.createSubcategory("name");
		} finally {
			Category.ROOT.clearChildren();
		}
	}

	
	@Test
	public void canPersistAndFetchAndUpdateAndDeleteProduct() throws DatasourceException, NoSuchEntryException, DuplicateEntryException {
		Product p = productManager.createProduct("ABC", "New Cool Product", 2000, "Very Cool", Category.ROOT);
		
		AttributeTemplate template = productManager.createAttributeTemplate("a-test-template", "");
		p = p.update("Inte så jättecool produkt längre", 12345L, "Not so cool anymore", Category.ROOT);
		p = p.addAttribute("test", "test", template);
		
		productManager.updateProduct(p);
		Product fetchedProduct = productManager.getProductBySku(p.getSku());
		
		assertEquals(p, fetchedProduct);
		assertEquals(p.getAttribute("test").getTemplate(), template);
		
		Collection<Product> allProducts = productManager.getAllProducts();
		Collection<Product> rootProducts = productManager.getProductsByCategory(Category.ROOT);
		
		assertTrue(allProducts.size() > 0);
		assertTrue(rootProducts.contains(p));
		
		productManager.deleteProduct(p);
		productManager.deleteAttributeTemplate(template);

		boolean exceptionThrown = false;
		try {
			productManager.getProductBySku(p.getSku());
		} catch (NoSuchEntryException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
	}
	
	@Test
	public void canThrowHandyExceptions() throws DatasourceException {
		
		Product p1 = null;
		
		try {
			p1 = productManager.createProduct("ABC", "New Cool Product", 2000, "Very Cool", Category.ROOT);
			productManager.createProduct("ABC", "New Cool Product", 2000, "Very Cool", Category.ROOT);
		} catch (Exception e) {
			if (!(e instanceof DuplicateEntryException)) {
				fail("Exception not instance of DuplicateEntryException. It is a " + e.getMessage());
			}
		}
		
		try {
			productManager.getProductBySku("BAD-CODE");
		} catch (Exception e) {
			if (!(e instanceof NoSuchEntryException)) {
				fail("Exception not instance of NoSuchEntryException. It is a " + e.getClass());
			}
		}
		
		productManager.deleteProduct(p1);
	}
	
	@Test
	public void canCreateAndSortAttributes() throws DatasourceException, DuplicateEntryException, NoSuchEntryException {
		Product p1 = productManager.createProduct("TESTING-STUFF-1", "New Cool Product", 2000, "Very Cool", Category.ROOT);
		Product p2 = productManager.createProduct("TESTING-STUFF-2", "New Cool Product", 2000, "Very Cool", Category.ROOT);
		Product p3 = productManager.createProduct("TESTING-STUFF-3", "New Cool Product", 2000, "Very Cool", Category.ROOT);
		
		AttributeTemplate template = productManager.createAttributeTemplate("Test", "tst", "800x600", "900x800", "1024x1600");
		
		try {
			template = productManager.createAttributeTemplate("Test", "tst", "800x600", "900x800", "1024x1600");
		} catch (DuplicateEntryException e) {
			// Do nothing, all is well
		} catch (Exception e) {
			fail("createAttributeTemplate threw something other than DuplicateEntryException");
		}
		
		List<Product> products = new LinkedList<Product>();
		
		p1.addAttribute("Test", "1024x1600", template);
		p2.addAttribute("Test", "900x800", template);
		p3.addAttribute("Test", "800x600", template);
		
		productManager.updateProduct(p1);
		productManager.updateProduct(p2);
		productManager.updateProduct(p3);
		
		products.add(p1);
		products.add(p2);
		products.add(p3);
		
		Comparator<Product> c = new Comparator<Product>() {

			@Override
			public int compare(Product p1, Product p2) {
				return p1.getAttribute("Test").compareTo(p2.getAttribute("Test"));
			}
		};	
		
		Collections.sort(products, c);
		
		assertEquals(p3, products.get(0));
		
		// brute-force-swap:)
		template.getLegalValues().add(0, template.getLegalValues().get(2));
		template.getLegalValues().remove(3);
		productManager.updateAttributeTemplate(template);
		
		Collections.sort(products, c);
		assertEquals(p1, products.get(0));
		
		assertTrue(productManager.getProductsByAttributeTemplate(template).containsAll(products));
		
		assertEquals(p1, productManager.getProductsByAttributeTemplate(template, c).get(0));
		
		assertEquals(p1.getAttribute("Test"), productManager.getProductBySku(p1.getSku()).getAttribute("Test"));
		
		assertEquals(p1.getAttribute("Test").getTemplate(), productManager.getProductBySku(p1.getSku()).getAttribute("Test").getTemplate());
		
		p1 = p1.removeAttribute(p1.getAttribute("Test"));
		assertEquals(0, p1.getAttributes().size());
		
		productManager.updateProduct(p1);

		assertEquals(0, productManager.getProductBySku(p1.getSku()).getAttributes().size());
		
		productManager.deleteProduct(p1);
		productManager.deleteProduct(p2);
		productManager.deleteProduct(p3);
		
		productManager.deleteAttributeTemplate(template);
		
	}
	
	
	@Test
	public void canDeleteAttributeTemplateAndAttributesAreAlsoDeletedFromRelatedProducts() throws DatasourceException, DuplicateEntryException, NoSuchEntryException {
		
		Product product1 = productManager.createProduct("Testing attributes", "New Cool Product", 2000, "Very Cool", Category.ROOT);
		Product product2 = productManager.createProduct("Testing attributes again", "New Cool Product", 2000, "Very Cool", Category.ROOT);
		
		AttributeTemplate template = productManager.createAttributeTemplate("Testing-template", "tzzt", "one", "two", "three");
		
		product1.addAttribute("Test", "one", template);
		product2.addAttribute("Test", "two", template);
		
		product1 = productManager.updateProduct(product1);
		product2 = productManager.updateProduct(product2);
		
		productManager.deleteAttributeTemplate(template);
		
		product1 = productManager.getProductBySku(product1.getSku());
		product2 = productManager.getProductBySku(product2.getSku());
		
		assertEquals(0, product1.getAttributes().size());
		assertEquals(0, product2.getAttributes().size());
		
		productManager.deleteProduct(product1);
		productManager.deleteProduct(product2);
		
	}
	
	
	
	
	
}
