package org.graphstream.graph.subgraph.condition;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.subgraph.IncludeCondition;

public class IDCondition
	implements IncludeCondition
{
	String nodePattern;
	String edgePattern;
	
	public IDCondition( String pattern )
	{
		this( pattern, pattern );
	}
	
	public IDCondition( String nodePattern, String edgePattern )
	{
		this.nodePattern = nodePattern;
		this.edgePattern = edgePattern;
	}
	
	public boolean isInside(Element e)
	{
		if( e instanceof Edge )
			return e.getId().matches(edgePattern);
		
		return e.getId().matches(nodePattern);
	}
}
