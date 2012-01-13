package org.graphstream.graph.filtered;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

public class FilteredEdge extends ElementProxy<Edge> implements Edge,
		FilteredElement<Edge> {

	FilteredGraph graph;

	public FilteredEdge(Edge e, FilteredGraph g) {
		super(e);
		graph = g;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.filtered.FilteredElement#getFilteredElement()
	 */
	public Edge getFilteredElement() {
		return element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Edge#getNode0()
	 */
	public <T extends Node> T getNode0() {
		return graph.getNode(element.getNode0().getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Edge#getNode1()
	 */
	public <T extends Node> T getNode1() {
		return graph.getNode(element.getNode1().getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Edge#getOpposite(org.graphstream.graph.Node)
	 */
	public <T extends Node> T getOpposite(Node node) {
		return graph.getNode(element.getOpposite(node).getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Edge#getSourceNode()
	 */
	public <T extends Node> T getSourceNode() {
		return graph.getNode(element.getSourceNode().getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Edge#getTargetNode()
	 */
	public <T extends Node> T getTargetNode() {
		return graph.getNode(element.getTargetNode().getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Edge#isDirected()
	 */
	public boolean isDirected() {
		return element.isDirected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Edge#isLoop()
	 */
	public boolean isLoop() {
		return element.isLoop();
	}

}
