package org.graphstream.graph.filtered.test;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.graphstream.graph.Element;
import org.graphstream.graph.filtered.FilteredElement;
import org.graphstream.graph.implementations.AbstractElement;
import org.junit.Test;

public class TestElementProxy {

	protected static String getRandomString(int size) {
		final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-%=+/:";
		StringBuilder sb = new StringBuilder();
		Random r = new Random();

		while (size-- > 0)
			sb.append(chars.charAt(r.nextInt(chars.length())));

		return sb.toString();
	}

	@Test
	public void testId() {
		String id = getRandomString(20);
		Element e1 = new TestElement(id);
		Element e2 = new FilteredElement<Element>(e1);

		assertEquals(e1.getId(), id);
		assertEquals(e2.getId(), id);
	}

	@Test
	public void testAttributeProxyToElement() {
		Element e1 = new TestElement("testElement");
		Element e2 = new FilteredElement<Element>(e1);

		String[] keys = { "attr1", "attr2", "attr3" };
		Object[] values = { getRandomString(30), 10, 123.0 };

		for (int i = 0; i < keys.length; i++)
			e2.setAttribute(keys[i], values[i]);

		for (int i = 0; i < keys.length; i++)
			assertEquals(e1.getAttribute(keys[i]), values[i]);
	}

	@Test
	public void testAttributeElementToProxy() {
		Element e1 = new TestElement("testElement");
		Element e2 = new FilteredElement<Element>(e1);

		String[] keys = { "attr1", "attr2", "attr3" };
		Object[] values = { getRandomString(30), 10, 123.0 };

		for (int i = 0; i < keys.length; i++)
			e1.setAttribute(keys[i], values[i]);

		for (int i = 0; i < keys.length; i++)
			assertEquals(e2.getAttribute(keys[i]), values[i]);
	}

	protected static class TestElement extends AbstractElement {
		long eventId = 0;

		public TestElement(String id) {
			super(id);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.graph.implementations.AbstractElement#attributeChanged
		 * (java.lang.String, long, java.lang.String,
		 * org.graphstream.graph.implementations
		 * .AbstractElement.AttributeChangeEvent, java.lang.Object,
		 * java.lang.Object)
		 */
		protected void attributeChanged(String sourceId, long timeId,
				String attribute, AttributeChangeEvent event, Object oldValue,
				Object newValue) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.graph.implementations.AbstractElement#myGraphId()
		 */
		protected String myGraphId() {
			return "test";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.graph.implementations.AbstractElement#newEvent()
		 */
		protected long newEvent() {
			return eventId++;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.graphstream.graph.implementations.AbstractElement#
		 * nullAttributesAreErrors()
		 */
		protected boolean nullAttributesAreErrors() {
			return false;
		}
	}
}
