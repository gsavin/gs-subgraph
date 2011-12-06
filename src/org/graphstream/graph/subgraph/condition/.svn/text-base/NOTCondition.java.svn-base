package org.graphstream.graph.subgraph.condition;

import org.graphstream.graph.Element;
import org.graphstream.graph.subgraph.IncludeCondition;

public class NOTCondition
	implements IncludeCondition
{
	IncludeCondition condition;
	
	public NOTCondition( IncludeCondition condition )
	{
		this.condition = condition;
	}
	
	public boolean isInside(Element e)
	{
		return ! condition.isInside(e);
	}
}
