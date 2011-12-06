package org.graphstream.graph.subgraph.condition;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.subgraph.IncludeCondition;

public class InterEdgeCondition
	implements IncludeCondition
{
	IncludeCondition condition;
	
	public InterEdgeCondition( IncludeCondition ic )
	{
		this.condition = ic;
	}
	
	public boolean isInside( Element e )
	{
		if( e instanceof Edge )
		{
			return condition.isInside( ((Edge) e).getSourceNode() ) &&
				condition.isInside( ((Edge) e).getTargetNode() );
		}
		
		return false;
	}
}
