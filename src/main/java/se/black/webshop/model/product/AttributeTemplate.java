package se.black.webshop.model.product;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import se.black.webshop.model.jpautils.JPAEntity;

@Entity
@Table(name = "attribute_template_table")
@NamedQueries({
	@NamedQuery(name = AttributeTemplate.FIND_BY_NAME, query = "select a from AttributeTemplate a where a.name = :name"),
	})
public class AttributeTemplate implements JPAEntity<Long> {
	
	public static final String FIND_BY_NAME = "AttributeTemplate.findByName";
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(unique = true)
	private String name;
	private String suffix;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = "ordr")
	@JoinTable(name = "legal_values_table")
	private List<String> legalValues;
	
	@SuppressWarnings("unused")
	private AttributeTemplate() {}
	
	public AttributeTemplate(String name, String suffix) {
		this.name = name;
		this.suffix = suffix;
		this.legalValues = new LinkedList<String>();
	}
	
	@Override
	public Long getID() {
		return this.id;
	}

	public String getName() {
		return name;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public List<String> getLegalValues() {
		return legalValues;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((legalValues == null) ? 0 : legalValues.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
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
		AttributeTemplate other = (AttributeTemplate) obj;
		if (legalValues == null) {
			if (other.legalValues != null)
				return false;
		} else if (!legalValues.containsAll(other.legalValues))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (suffix == null) {
			if (other.suffix != null)
				return false;
		} else if (!suffix.equals(other.suffix))
			return false;
		return true;
	}



	
	
}
