package se.black.webshop.model.jpautils;

public class Parameter<K extends Object, V> {

	
	public final K position;
	public final V value;

	public Parameter(K position, V value) {
		this.position = position;
		this.value = value;
	}
}
