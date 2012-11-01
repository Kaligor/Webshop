package se.black.webshop.model.product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

import se.black.webshop.model.exception.DatasourceException;
import se.black.webshop.model.exception.DuplicateEntryException;
import se.black.webshop.model.exception.NoSuchEntryException;
import se.black.webshop.model.jpautils.JPA;
import se.black.webshop.model.jpautils.JPAEntity;
import se.black.webshop.model.jpautils.Parameter;



import static se.black.webshop.model.jpautils.JPA.*;


@Entity
@Table(name = "category_table")
@NamedQueries({
	@NamedQuery(name = Category.FIND_BY_NAME, query = "SELECT c FROM Category c WHERE c.name = :name"),
	@NamedQuery(name = Category.FIND_BY_PATH, query = "SELECT c FROM Category c WHERE c.path = :path"),
	@NamedQuery(name = Category.DELETE_BY_PATH, query = "DELETE FROM Category c WHERE c.path = ?1")
	})
public class Category implements JPAEntity<Long> {
	
	public static final String FIND_BY_NAME = "Category.findByName";
	public static final String FIND_BY_PATH = "Category.findByPath";
	public static final String DELETE_BY_PATH = "Category.deleteByPath";
	
	private static final ProductManager productManager = new JPAProductManager();
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;
	private String name;
	
	@Column(unique = true)
	private String path;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "category_children_table")
	private List<Category> children;
	
	public static final Category ROOT;
	
	
	static {
		ROOT = getOrCreateRoot();
	}

	private static final Category getOrCreateRoot() {
		
		if (executeNamedQuery(FIND_BY_NAME, Category.class, Arrays.asList(new Parameter<String, String>("name", "ROOT"))).size() == 0) {
			Category root = new Category("ROOT");
			saveOrUpdate(root);
		}
		
		return executeNamedQuery(FIND_BY_NAME, Category.class, Arrays.asList(new Parameter<String, String>("name", "ROOT"))).iterator().next();
		
	}

	
	@SuppressWarnings("unused")
	private Category() {}
	
	private Category(String name) {
		this.name = name;
		this.path = "/";
		this.children = new LinkedList<Category>();
	}
	
	private Category(String name, Category parent) {
		if (parent == null) {
			throw new IllegalArgumentException("parent is null");
		} else if (name == "ROOT") {
			throw new IllegalArgumentException("ROOT is an illegal name");
		}
		
		this.name = name;
		this.path = parent.path + name + "/";
		this.children = new LinkedList<Category>();
		parent.children.add(this);
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	
	public List<Category> getChildren() {
		return children;
	}
	
	public Category getChild(String name) {
		for (Category child : this.children) {
			if (child.getName().equals(name)) {
				return child;
			}
		}
		return null;
	}
	
	public Category getCategoryById(Long id) throws NoSuchEntryException{
		return getCategoryByIdRecursive(Category.ROOT, id);
	}
	
	private Category getCategoryByIdRecursive(Category category, Long id) throws NoSuchEntryException{
		if(category.getID() == id){
			return category;
		} else{
			for(Category catChild : category.getChildren()){
				return getCategoryByIdRecursive(catChild, id);
			}
		}
		throw new NoSuchEntryException("No Category with id on model:" +id);
	}
	
	public Category createSubcategory(String name) throws DuplicateEntryException {
		
		for (Category category : this.children) {
			if (category.getName().equals(name)) {
				throw new DuplicateEntryException("A subcategory already exists with name " + name);
			}
		}
 		
		Category newChild = new Category(name, this);
		Category parentProxy = null;
		parentProxy = saveOrUpdate(this);
		
		for(Category child : parentProxy.children){
			if(child.name.equals(name)){
				newChild.Id = child.Id;
			}
		}
		
		return newChild;
	}

	public void removeChild(Category category) throws DuplicateEntryException, DatasourceException {
		
		if (category.getChildren().size() != 0 || productManager.getProductsByCategory(category).size() != 0) {
			throw new DuplicateEntryException("Category has subcategories, or products exists in the category at " + category.path);
		}
		
		if(this.children.contains(category)){
			this.children.remove(category);
			saveOrUpdate(this);
			JPA.delete("DELETE FROM Category c WHERE c.path = ?1", Arrays.asList(new Parameter<Integer, String>(1, category.path)));
		}
	}
	
	public void clearChildren() throws DatasourceException, DuplicateEntryException {
		
		List<Category> childrenToKill = new ArrayList<Category>();
		childrenToKill.addAll(children);
		for (Category child: this.children) {
			if (child.getChildren().size() != 0 || productManager.getProductsByCategory(child).size() != 0) {
				throw new DuplicateEntryException("Category has subcategories, or products exists in the category at " + child.path);
			}
		}
		
		this.children.clear();
		saveOrUpdate(this);
		
		for (Category child : childrenToKill) {
			JPA.delete("DELETE FROM Category c WHERE c.path = ?1", Arrays.asList(new Parameter<Integer, String>(1, child.path)));
		}
	}
	
	public Category getParent() {
		return findParentRecusive(this, ROOT);
	}
	
	private Category findParentRecusive(Category whoIsMyParent, Category startingPoint) {
		
		if (startingPoint.children.contains(whoIsMyParent)) {
			return startingPoint;
			
		} else {
			
			Category cat = null;
			for (Category subcategory : startingPoint.children) {
				if (findParentRecusive(whoIsMyParent, subcategory) != null) {
					cat = findParentRecusive(whoIsMyParent, subcategory);
				}
			}
			return cat;
		}
	}

	
	@Override
	public String toString() {
		return toStringRecursive(0);
	}
	
	private String toStringRecursive(int level) {
		String indentation = "";
		for (int i = 0; i < level; i++) {
			indentation += "    ";
		}
		String s = indentation + "---- category ----\n";
		s += indentation + "name; " + this.name + "\n";
		s += indentation + "id: " + this.Id + "\n";
		s += indentation + "path: " + this.path + "\n";
		for (Category category: this.children) {
			s += category.toStringRecursive(level+1);
		}
		return s;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Category)) {
			return false;
		}
		Category other = (Category) obj;
		return this.path.equals(other.path);
	}

	
	@Override
	public int hashCode() {
		int result = 5346;
		int prime = 13;
		result = prime*result + this.name.hashCode();
		result = prime*result + this.path.hashCode();
		return result;
	}

	
	@Override
	public Long getID() {
		return this.Id;
	}
	
	
	public static Category getCategory(String path) throws NoSuchEntryException {
		
		if (path.equals("/")) {
			return Category.ROOT;
		}

		path = path.substring(1, path.length());
		String[] categoryStrings = path.split("/");
		
		Category cat = Category.ROOT;
		
		for (int i = 0; i < categoryStrings.length; i++) {
			cat = findSubcategory(cat.getChildren(), categoryStrings[i]);
			if (cat == null) {
				throw new NoSuchEntryException("No Category exists with path " + path);
			}
		}
		return cat;
	}
	
	private static Category findSubcategory(List<Category> categories, String name) {
		for (Category category : categories) {
			
			if (category.getName().equals(name)) {
				return category;
			}
		}
		return null;
	}
	
}
