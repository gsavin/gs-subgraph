package org.graphstream.graph.filtered;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.graphstream.graph.Element;

public class FilteredElement<E extends Element> implements Element {

	protected final E element;
	private int index;

	public FilteredElement(E target) {
		element = target;
		index = target.getIndex();
	}

	/**
	 * Get the element that is filtered by this object.
	 * 
	 * @return filtered element
	 */
	public E getFilteredElement() {
		return element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#getIndex()
	 */
	public int getIndex() {
		return index;
	}

	void setIndex(int index) {
		this.index = index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#addAttribute(java.lang.String,
	 * java.lang.Object[])
	 */
	public void addAttribute(String attribute, Object... values) {
		element.addAttribute(attribute, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#addAttributes(java.util.Map)
	 */
	public void addAttributes(Map<String, Object> attributes) {
		element.addAttributes(attributes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#changeAttribute(java.lang.String,
	 * java.lang.Object[])
	 */
	public void changeAttribute(String attribute, Object... values) {
		element.changeAttribute(attribute, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#clearAttributes()
	 */
	public void clearAttributes() {
		element.clearAttributes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#getArray(java.lang.String)
	 */
	public Object[] getArray(String key) {
		return element.getArray(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#getAttribute(java.lang.String)
	 */
	public <T> T getAttribute(String key) {
		return element.getAttribute(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#getAttribute(java.lang.String,
	 * java.lang.Class)
	 */
	public <T> T getAttribute(String key, Class<T> clazz) {
		return element.getAttribute(key, clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#getAttributeCount()
	 */
	public int getAttributeCount() {
		return element.getAttributeCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#getAttributeKeyIterator()
	 */
	public Iterator<String> getAttributeKeyIterator() {
		return element.getAttributeKeyIterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#getAttributeKeySet()
	 */
	public Iterable<String> getAttributeKeySet() {
		return element.getAttributeKeySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.graph.Element#getFirstAttributeOf(java.lang.String[])
	 */
	public <T> T getFirstAttributeOf(String... keys) {
		return element.getFirstAttributeOf(keys);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#getFirstAttributeOf(java.lang.Class,
	 * java.lang.String[])
	 */
	public <T> T getFirstAttributeOf(Class<T> clazz, String... keys) {
		return element.getFirstAttributeOf(clazz, keys);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#getHash(java.lang.String)
	 */
	public HashMap<?, ?> getHash(String key) {
		return element.getHash(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#getId()
	 */
	public String getId() {
		return element.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#getLabel(java.lang.String)
	 */
	public CharSequence getLabel(String key) {
		return element.getLabel(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#getNumber(java.lang.String)
	 */
	public double getNumber(String key) {
		return element.getNumber(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#getVector(java.lang.String)
	 */
	public ArrayList<? extends Number> getVector(String key) {
		return element.getVector(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#hasArray(java.lang.String)
	 */
	public boolean hasArray(String key) {
		return element.hasArray(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#hasAttribute(java.lang.String)
	 */
	public boolean hasAttribute(String key) {
		return element.hasAttribute(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#hasAttribute(java.lang.String,
	 * java.lang.Class)
	 */
	public boolean hasAttribute(String key, Class<?> clazz) {
		return element.hasAttribute(key, clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#hasHash(java.lang.String)
	 */
	public boolean hasHash(String key) {
		return element.hasHash(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#hasLabel(java.lang.String)
	 */
	public boolean hasLabel(String key) {
		return element.hasLabel(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#hasNumber(java.lang.String)
	 */
	public boolean hasNumber(String key) {
		return element.hasNumber(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#hasVector(java.lang.String)
	 */
	public boolean hasVector(String key) {
		return element.hasVector(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String attribute) {
		element.removeAttribute(attribute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Element#setAttribute(java.lang.String,
	 * java.lang.Object[])
	 */
	public void setAttribute(String attribute, Object... values) {
		element.setAttribute(attribute, values);
	}

}
