package org.graphstream.graph.subgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.graphstream.graph.Element;

public class ElementProxy<E extends Element> implements Element {

	E element;
	
	public ElementProxy(E target) {
		element = target;
	}
	
	public void addAttribute(String attribute, Object... values) {
		element.addAttribute(attribute, values);
	}

	public void addAttributes(Map<String, Object> attributes) {
		element.addAttributes(attributes);
	}

	public void changeAttribute(String attribute, Object... values) {
		element.changeAttribute(attribute, values);
	}

	public void clearAttributes() {
		element.clearAttributes();
	}

	public Object[] getArray(String key) {
		return element.getArray(key);
	}

	public <T> T getAttribute(String key) {
		return element.getAttribute(key);
	}

	public <T> T getAttribute(String key, Class<T> clazz) {
		return element.getAttribute(key, clazz);
	}

	public int getAttributeCount() {
		return element.getAttributeCount();
	}

	public Iterator<String> getAttributeKeyIterator() {
		return element.getAttributeKeyIterator();
	}

	public Iterable<String> getAttributeKeySet() {
		return element.getAttributeKeySet();
	}

	public <T> T getFirstAttributeOf(String... keys) {
		return element.getFirstAttributeOf(keys);
	}

	public <T> T getFirstAttributeOf(Class<T> clazz, String... keys) {
		return element.getFirstAttributeOf(clazz, keys);
	}

	public HashMap<?, ?> getHash(String key) {
		return element.getHash(key);
	}

	public String getId() {
		return element.getId();
	}

	public int getIndex() {
		return element.getIndex();
	}

	public CharSequence getLabel(String key) {
		return element.getLabel(key);
	}

	public double getNumber(String key) {
		return element.getNumber(key);
	}

	public ArrayList<? extends Number> getVector(String key) {
		return element.getVector(key);
	}

	public boolean hasArray(String key) {
		return element.hasArray(key);
	}

	public boolean hasAttribute(String key) {
		return element.hasAttribute(key);
	}

	public boolean hasAttribute(String key, Class<?> clazz) {
		return element.hasAttribute(key, clazz);
	}

	public boolean hasHash(String key) {
		return element.hasHash(key);
	}

	public boolean hasLabel(String key) {
		return element.hasLabel(key);
	}

	public boolean hasNumber(String key) {
		return element.hasNumber(key);
	}

	public boolean hasVector(String key) {
		return element.hasVector(key);
	}

	public void removeAttribute(String attribute) {
		element.removeAttribute(attribute);
	}

	public void setAttribute(String attribute, Object... values) {
		element.setAttribute(attribute, values);
	}

}
