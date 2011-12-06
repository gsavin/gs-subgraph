package org.graphstream.graph.subgraph;

import org.graphstream.graph.Element;

public interface IncludeCondition
{
	boolean isInside( Element e );
}
