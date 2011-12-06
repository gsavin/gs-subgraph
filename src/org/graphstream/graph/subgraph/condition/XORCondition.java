package org.graphstream.graph.subgraph.condition;

import org.graphstream.graph.Element;
import org.graphstream.graph.subgraph.IncludeCondition;

public class XORCondition
	implements IncludeCondition
{
	IncludeCondition [] conditions;
	
	public XORCondition( IncludeCondition ... conditions )
	{
		this.conditions = conditions;
	}
	
	public boolean isInside(Element e)
	{
		if( conditions == null )
			return false;
		
		boolean b = false;
		
		for( int i = 0; i < conditions.length; i++ )
		{
			if( conditions [i].isInside(e) )
			{
				if( b ) return false;
				else b = true;
			}
		}
		
		return b;
	}
}
