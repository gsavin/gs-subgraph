package org.graphstream.graph.subgraph.condition;

import org.graphstream.graph.Element;
import org.graphstream.graph.subgraph.IncludeCondition;

public class FalseCondition
	implements IncludeCondition
{
	public boolean isInside(Element e)
	{
		return false;
	}
}
