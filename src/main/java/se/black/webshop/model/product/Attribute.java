package se.black.webshop.model.product;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class Attribute implements Comparable<Attribute> {
	
	private String name;
	private String value;
	
	@ManyToOne
	private AttributeTemplate template;
	
	private Attribute() {}

	Attribute(String name, String value, AttributeTemplate template) {
		this.name = name;

		if (template.getLegalValues().isEmpty() || template.getLegalValues().contains(value)) {
			this.value = value;
		} else {
			throw new IllegalArgumentException(value + " is not a legal value in template " + template);
		}
		
		this.template = template;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public AttributeTemplate getTemplate() {
		return template;
	}
	
	@Override
	public String toString() {
		String s = " --- ATTRIBUTE ---\n";
		s += "name: " + this.name + "\n";
		s += "value: " + this.value + "\n";
		s += "template: " + this.template.getName() + "\n";
		return s;
	}

	@Override
	public int compareTo(Attribute o) {
		if (this.template.equals(o.template) && this.template.getLegalValues().size() > 0) {
			return (this.getTemplate().getLegalValues().indexOf(this.value) - o.getTemplate().getLegalValues().indexOf(o.value));
		} else {
			return this.value.compareTo(o.value);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((template == null) ? 0 : template.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Attribute other = (Attribute) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (template == null) {
			if (other.template != null)
				return false;
		} else if (!template.equals(other.template))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	
}
