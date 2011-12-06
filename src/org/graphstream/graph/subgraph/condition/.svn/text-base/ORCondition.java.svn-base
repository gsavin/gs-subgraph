package org.graphstream.graph.subgraph.condition;

import org.graphstream.graph.Element;
import org.graphstream.graph.subgraph.IncludeCondition;

public class ORCondition
	implements IncludeCondition
{
	IncludeCondition [] conditions;
	
	public ORCondition( IncludeCondition ... conditions )
	{
		this.conditions = conditions;
	}
	
	public boolean isInside(Element e)
	{
		if( conditions == null )
			return false;
		
		for( int i = 0; i < conditions.length; i++ )
			if( conditions [i].isInside(e) ) return true;
		
		return false;
	}
}
