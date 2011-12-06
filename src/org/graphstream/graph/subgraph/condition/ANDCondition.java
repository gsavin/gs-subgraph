package org.graphstream.graph.subgraph.condition;

import org.graphstream.graph.Element;
import org.graphstream.graph.subgraph.IncludeCondition;

public class ANDCondition implements IncludeCondition
{
	IncludeCondition [] conditions;
	
	public ANDCondition( IncludeCondition ... conditions )
	{
		this.conditions = conditions;
	}
	
	public boolean isInside(Element e)
	{
		if( conditions == null )
			return false;
		
		boolean b = true;
		
		for( int i = 0; i < conditions.length; i++ )
			b = b && conditions [i].isInside(e);
		
		return b;
	}
}
