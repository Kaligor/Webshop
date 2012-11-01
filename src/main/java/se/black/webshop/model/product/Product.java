package se.black.webshop.model.product;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.*;

import se.black.webshop.model.jpautils.JPAEntity;

@Entity
@Table(name = "product_table")
@NamedQueries({
	@NamedQuery(name = Product.FIND_BY_SKU, query = "select p from Product p where p.sku = :sku"),
	@NamedQuery(name = Product.FIND_ALL, query = "select p from Product p"),
	@NamedQuery(name = Product.FIND_BY_CATEGORY, query = "select p from Product p where p.category = :category"),
	@NamedQuery(name = Product.FIND_BY_ATTRIBUTE_TEMPLATE, query = "SELECT p from Product p INNER JOIN p.attributes a WHERE a.template = :attributeTemplate")
	})
public class Product implements JPAEntity<Long>, Comparable<Product>{
	
	public static final String FIND_BY_CATEGORY = "Product.findByCategory";
	public static final String FIND_BY_ATTRIBUTE_TEMPLATE = "Product.findByAttributeTemplate";
	public static final String FIND_BY_SKU = "Product.findBySku";
	public static final String FIND_ALL = "Product.findAll";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long Id; 
	
	@Column(unique = true)
	private String sku;
	private String name;
	private long price;
	private String description;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Category category;
	
	@ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_attributes_table", joinColumns = @JoinColumn(name = "product_id"))
    @OrderColumn
	private Set<Attribute> attributes;
	
	@SuppressWarnings("unused")
	private Product () {}
	
	Product(String sku, String name, long price, String description, Category category) {
		this.sku = sku;
		this.name = name;
		this.price = price;
		this.description = description;
		this.category = category;
		this.attributes = new TreeSet<Attribute>();
	}
	
	@Override
	public Long getID() {
		return this.Id;
	}

	public String getSku() {
		return sku;
	}

	
	public String getName() {
		return name;
	}

	
	public long getPrice() {
		return price;
	}

	
	public String getDescription() {
		return description;
	}

	
	public Category getCategory() {
		return category;
	}
	
	
	public Attribute getAttribute(String name) {
		for (Attribute a: attributes) {
			if (a.getName().equals(name)) {
				return a;
			}
		}
		return null;
	}
	
	
	public Set<Attribute> getAttributes() {
		return this.attributes;
	}
	
	
	public Product addAttribute(String name, String value, AttributeTemplate template) {
		Product p = new Product(this.sku, name, price, description, category);
		p.attributes = this.attributes;
		p.attributes.add(new Attribute(name, value, template));
		p.Id = this.Id;
		return p;
	}
	
	
	public Product removeAttribute(Attribute attribute) {
		Product p = new Product(this.sku, name, price, description, category);
		for (Attribute a : this.attributes) {
			if (!a.equals(attribute)) {
				p.attributes.add(a);
			}
		}
		p.Id = this.Id;
		return p;
	}
	
	
	public Product update(String name, Long price, String description, Category category) {
		Product p = new Product(this.sku, name, price, description, category);
		p.Id = this.Id;
		return p;
	}
	
	
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("------ PRODUCT ------ ").append("\n");
		stringBuffer.append("Id: ").append(this.Id).append("\n");
		stringBuffer.append("SKU: ").append(this.sku).append("\n");
		stringBuffer.append("Name: ").append(this.name).append("\n");
		stringBuffer.append("Price: ").append(this.price).append("\n");
		stringBuffer.append("Description: ").append(this.description).append("\n");
		stringBuffer.append("Category: ").append(this.category.getName()).append("\n");
		
		for (Attribute attribute : this.attributes) {
			stringBuffer.append("\t").append(attribute.getName()).append(": ").append(attribute.getValue()).append(" template: ").append(attribute.getTemplate().getName()).append("\n");
		}
		
		return stringBuffer.toString();
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result
				+ ((category == null) ? 0 : category.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (price ^ (price >>> 32));
		result = prime * result + ((sku == null) ? 0 : sku.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else {
			// TODO - är det här puckat eller?
			for (Attribute a : this.attributes) {
				if (other.getAttribute(a.getName()) == null || 
						!other.getAttribute(a.getName()).getTemplate().equals(a.getTemplate())) {
					return false;
				}
			}
		}
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (price != other.price)
			return false;
		if (sku == null) {
			if (other.sku != null)
				return false;
		} else if (!sku.equals(other.sku))
			return false;
		return true;
	}

	@Override
	public int compareTo(Product o) {
		return this.sku.compareTo(o.sku);
	}

}
