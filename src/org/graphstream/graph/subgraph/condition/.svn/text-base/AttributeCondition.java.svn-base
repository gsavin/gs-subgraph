package org.graphstream.graph.subgraph.condition;

import org.graphstream.graph.Element;
import org.graphstream.graph.subgraph.IncludeCondition;

public class AttributeCondition
	implements IncludeCondition
{
	String attribute;
	String expectedValue;
	
	public AttributeCondition( String attribute, String expectedValue )
	{
		this.attribute = attribute;
		this.expectedValue = expectedValue;
	}
	
	public boolean isInside(Element e)
	{
		if( ! e.hasAttribute(attribute) )
			return false;
		
		Object o = e.getAttribute(attribute);
		
		if( o == null )
		{
			return expectedValue == null;
		}
		else
		{
			return expectedValue == null ||
				o.toString().equals(expectedValue);
		}
	}
}
