package org.graphstream.graph.subgraph;

import java.util.Iterator;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Edge;

public class SelectedEdgeIterator
{
	IncludeCondition 			condition;
	Iterator<? extends Edge>	ite;
	Edge						next;
	
	public SelectedEdgeIterator( Graph g, IncludeCondition condition )
	{
		this.ite 		= g.getEdgeIterator();
		this.condition 	= condition;
		
		findNext();
	}
	
	protected void findNext()
	{
		next = null;
		
		while( next == null && ite.hasNext() )
		{
			next = ite.next();
			
			if( ! condition.isInside(next) )
				next = null;
		}
	}
	
	public boolean hasNext()
	{
		return next != null;
	}
	
	public Edge next()
	{
		Edge tmp = next;
		findNext();
		
		return tmp;
	}
	
	public void remove()
	{
		
	}
}
