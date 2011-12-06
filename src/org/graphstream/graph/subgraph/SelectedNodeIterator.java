package org.graphstream.graph.subgraph;

import java.util.Iterator;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class SelectedNodeIterator
	implements Iterator<Node>
{
	IncludeCondition 			condition;
	Iterator<? extends Node>	ite;
	Node						next;
	
	public SelectedNodeIterator( Graph g, IncludeCondition condition )
	{
		this.ite 		= g.getNodeIterator();
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
	
	public Node next()
	{
		Node tmp = next;
		findNext();
		
		return tmp;
	}
	
	public void remove()
	{
		
	}
}
