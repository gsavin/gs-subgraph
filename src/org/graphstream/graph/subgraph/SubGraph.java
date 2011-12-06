package org.graphstream.graph.subgraph;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Element;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.implementations.AbstractElement;
import org.graphstream.graph.subgraph.condition.AttributeCondition;
import org.graphstream.graph.subgraph.condition.FalseCondition;
import org.graphstream.graph.subgraph.condition.ORCondition;
import org.graphstream.graph.subgraph.condition.InterEdgeCondition;
import org.graphstream.stream.AttributeSink;
import org.graphstream.stream.ElementSink;
import org.graphstream.stream.GraphParseException;
import org.graphstream.stream.Pipe;
import org.graphstream.stream.Sink;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.stream.SourceBase;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.sync.SinkTime;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.swingViewer.GraphRenderer;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.basicRenderer.SwingBasicGraphRenderer;

public class SubGraph extends AbstractElement implements Graph {
	public static SubGraph selectFromAttribute(Graph g, String attribute) {
		return selectFromAttribute(g, attribute, null, true, false);
	}

	public static SubGraph selectFromAttribute(Graph g, String attribute,
			boolean includeInterEdges, boolean staticInclusion) {
		return selectFromAttribute(g, attribute, null, includeInterEdges,
				staticInclusion);
	}

	public static SubGraph selectFromAttribute(Graph g, String attribute,
			String value) {
		return selectFromAttribute(g, attribute, value, true, false);
	}

	/**
	 * Select a part of a graph according to element attributes.
	 * 
	 * @param g
	 *            the original graph
	 * @param attribute
	 *            attribute we want to select from
	 * @param value
	 *            expected value of the attribute, if null just check is
	 *            attribute exists
	 * @param includeInterEdges
	 *            if true, include edges with included extremities
	 * @param staticInclusion
	 *            if true, do not update inclusion
	 * @return a subgraph
	 */
	public static SubGraph selectFromAttribute(Graph g, String attribute,
			String value, boolean includeInterEdges, boolean staticInclusion) {
		IncludeCondition condition = new AttributeCondition(attribute, value);

		if (includeInterEdges)
			condition = new ORCondition(condition, new InterEdgeCondition(
					condition));

		return new SubGraph(String.format("subgraph-%s-attribute-%s-%s", g
				.getId(), attribute, value), g, condition, staticInclusion);
	}

	class SubGraphEdgeIterator<T extends Edge> implements Iterator<T> {
		Iterator<String> ite;

		public SubGraphEdgeIterator() {
			this.ite = edges.iterator();
		}

		public boolean hasNext() {
			return ite.hasNext();
		}

		public T next() {
			String edgeId = ite.next();

			if (edgeId != null)
				return fullGraph.getEdge(edgeId);

			return null;
		}

		public void remove() {

		}
	}

	class SubGraphEdgeIterable<T extends Edge> implements Iterable<T> {
		public Iterator<T> iterator() {
			return new SubGraphEdgeIterator<T>();
		}
	}

	class SubGraphNodeIterator<T extends Node> implements Iterator<T> {
		Iterator<String> ite;

		public SubGraphNodeIterator() {
			this.ite = nodes.iterator();
		}

		public boolean hasNext() {
			return ite.hasNext();
		}

		public T next() {
			String nodeId = ite.next();

			if (nodeId != null)
				return fullGraph.getNode(nodeId);

			return null;
		}

		public void remove() {

		}
	}

	class SubGraphNodeIterable<T extends Node> implements Iterable<T> {
		public Iterator<T> iterator() {
			return new SubGraphNodeIterator<T>();
		}
	}

	protected final Graph fullGraph;
	protected IncludeCondition condition;
	protected GraphListeners listeners;
	protected FullGraphListener fullGraphListener;
	protected final HashSet<String> nodes;
	protected final HashSet<String> edges;
	protected boolean staticInclusion;

	public SubGraph(String id, Graph fullGraph, IncludeCondition condition,
			boolean staticInclusion) {
		super(id);

		this.fullGraph = fullGraph;
		this.condition = condition;
		this.staticInclusion = staticInclusion;

		this.listeners = new GraphListeners();
		this.fullGraphListener = new FullGraphListener();

		this.nodes = new HashSet<String>();
		this.edges = new HashSet<String>();

		for (Node n : fullGraph)
			if (condition.isInside(n))
				nodes.add(n.getId());

		for (Edge e : fullGraph.getEdgeSet())
			if (condition.isInside(e))
				edges.add(e.getId());

		if (!staticInclusion)
			fullGraph.addSink(fullGraphListener);
		else
			this.condition = new FalseCondition();
	}

	public void include(Element e) {

		if (e instanceof Node)
			nodes.add(e.getId());
		else if (e instanceof Edge)
			edges.add(e.getId());

	}

	public void remove(Element e) {

		if (e instanceof Node)
			nodes.remove(e.getId());
		else if (e instanceof Edge)
			edges.remove(e.getId());

	}

	public void empty() {
		nodes.clear();
		edges.clear();
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public <T extends Edge> T addEdge(String id, String node1, String node2)
			throws IdAlreadyInUseException, ElementNotFoundException {
		return addEdge(id, node1, node2, false);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public <T extends Edge> T addEdge(String id, String from, String to,
			boolean directed) throws IdAlreadyInUseException,
			ElementNotFoundException {
		return addEdge(id, getNode(from), getNode(to), directed);
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String, int, int)
	 */
	public <T extends Edge> T addEdge(String id, int index1, int index2)
			throws IndexOutOfBoundsException, IdAlreadyInUseException,
			EdgeRejectedException {
		return addEdge(id, index1, index2);
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String, int, int, boolean)
	 */
	public <T extends Edge> T addEdge(String id, int fromIndex, int toIndex,
			boolean directed) throws IndexOutOfBoundsException,
			IdAlreadyInUseException, EdgeRejectedException {
		return addEdge(id, getNode(fromIndex), getNode(toIndex), directed);
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String, org.graphstream.graph.Node, org.graphstream.graph.Node)
	 */
	public <T extends Edge> T addEdge(String id, Node node1, Node node2)
			throws IdAlreadyInUseException, EdgeRejectedException {
		return addEdge(id, node1, node2, false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String, org.graphstream.graph.Node, org.graphstream.graph.Node, boolean)
	 */
	public <T extends Edge> T addEdge(String id, Node from, Node to,
			boolean directed) throws IdAlreadyInUseException,
			EdgeRejectedException {
		T e = fullGraph.addEdge(id, from, to, directed);

		if (condition.isInside(e)) {
			edges.add(id);
			listeners.sendEdgeAdded(getId(), id, from.getId(), to.getId(),
					directed);
		}

		return e;
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public <T extends Node> T addNode(String id) throws IdAlreadyInUseException {
		T n = fullGraph.addNode(id);

		if (condition.isInside(n)) {
			nodes.add(id);
			listeners.sendNodeAdded(getId(), id);

			for (Edge e : n)
				addEdge(e.getId(), e.getSourceNode().getId(), e.getTargetNode()
						.getId(), e.isDirected());
		}

		return n;
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public Iterable<AttributeSink> attributeSinks() {
		return listeners.attributeSinks();
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void clear() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public Viewer display() {
		return display(true);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public Viewer display(boolean autoLayout) {
		Viewer viewer = new Viewer(this,
				Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		GraphRenderer renderer = newGraphRenderer();

		viewer.addView(String.format("defaultView_%d",
				(long) (Math.random() * 10000)), renderer);

		if (autoLayout) {
			Layout layout = newLayoutAlgorithm();
			viewer.enableAutoLayout(layout);
		}

		return viewer;
	}

	protected static Layout newLayoutAlgorithm() {
		String layoutClassName = System.getProperty("gs.ui.layout");

		if (layoutClassName == null)
			return new org.graphstream.ui.layout.springbox.SpringBox(false);

		try {
			Class<?> c = Class.forName(layoutClassName);
			Object object = c.newInstance();

			if (object instanceof Layout) {
				return (Layout) object;
			} else {
				System.err.printf("class '%s' is not a 'GraphRenderer'%n",
						object);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err
					.printf("Cannot create layout, 'GraphRenderer' class not found : "
							+ e.getMessage());
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.err.printf("Cannot create layout, class '" + layoutClassName
					+ "' error : " + e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.err.printf("Cannot create layout, class '" + layoutClassName
					+ "' illegal access : " + e.getMessage());
		}

		return new org.graphstream.ui.layout.springbox.SpringBox(false);
	}

	protected static GraphRenderer newGraphRenderer() {
		String rendererClassName = System.getProperty("gs.ui.renderer");

		if (rendererClassName == null)
			return new SwingBasicGraphRenderer();

		try {
			Class<?> c = Class.forName(rendererClassName);
			Object object = c.newInstance();

			if (object instanceof GraphRenderer) {
				return (GraphRenderer) object;
			} else {
				System.err.printf("class '%s' is not a 'GraphRenderer'%n",
						object);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err
					.printf("Cannot create graph renderer, 'GraphRenderer' class not found : "
							+ e.getMessage());
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.err.printf("Cannot create graph renderer, class '"
					+ rendererClassName + "' error : " + e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.err.printf("Cannot create graph renderer, class '"
					+ rendererClassName + "' illegal access : "
					+ e.getMessage());
		}

		return new SwingBasicGraphRenderer();
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public EdgeFactory<? extends Edge> edgeFactory() {
		return fullGraph.edgeFactory();
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public <T extends Edge> Collection<T> getEdgeSet() {
		return null;
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public Iterable<ElementSink> elementSinks() {
		return listeners.elementSinks();
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public <T extends Edge> T getEdge(String id) {
		T e = fullGraph.getEdge(id);

		if (e == null)
			return null;

		return condition.isInside(e) ? e : null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.graph.Graph#getEdge(int)
	 */
	public <T extends Edge> T getEdge(int index)
			throws IndexOutOfBoundsException {
		T e = fullGraph.getEdge(index);

		if (e == null)
			return null;

		return condition.isInside(e) ? e : null;
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public int getEdgeCount() {
		return edges.size();
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public <T extends Edge> Iterator<T> getEdgeIterator() {
		return new SubGraphEdgeIterator<T>();
	}

	public <T extends Edge> Iterable<T> getEachEdge() {
		return new SubGraphEdgeIterable<T>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.graph.Graph#getNode(int)
	 */
	public <T extends Node> T getNode(int index)
			throws IndexOutOfBoundsException {
		T n = fullGraph.getNode(index);

		if (n == null)
			return null;

		return condition.isInside(n) ? n : null;
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public <T extends Node> T getNode(String id) {
		T n = fullGraph.getNode(id);

		if (n == null)
			return null;

		return condition.isInside(n) ? n : null;
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public int getNodeCount() {
		return nodes.size();
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public <T extends Node> Iterator<T> getNodeIterator() {
		return new SubGraphNodeIterator<T>();
	}

	public <T extends Node> Iterable<T> getEachNode() {
		return new SubGraphNodeIterable<T>();
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public double getStep() {
		return fullGraph.getStep();
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public boolean isAutoCreationEnabled() {
		return fullGraph.isAutoCreationEnabled();
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public boolean isStrict() {
		return fullGraph.isStrict();
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public NodeFactory<? extends Node> nodeFactory() {
		return fullGraph.nodeFactory();
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public <T extends Node> Collection<T> getNodeSet() {
		return null;
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void read(String filename) throws IOException, GraphParseException,
			ElementNotFoundException {
		fullGraph.read(filename);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void read(FileSource input, String filename) throws IOException,
			GraphParseException {
		fullGraph.read(input, filename);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public <T extends Edge> T removeEdge(String from, String to)
			throws ElementNotFoundException {
		Node fromNode = getNode(from);
		Node toNode = getNode(to);

		if (fromNode == null || toNode == null)
			return null;

		Edge e = fromNode.getEdgeToward(to);

		if (e != null && condition.isInside(e)) {
			edges.remove(e.getId());
			listeners.sendEdgeRemoved(getId(), e.getId());
			return fullGraph.removeEdge(from, to);
		}

		return null;
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public <T extends Edge> T removeEdge(String id)
			throws ElementNotFoundException {
		return removeEdge(getEdge(id));
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.graph.Graph#removeEdge(int)
	 */
	public <T extends Edge> T removeEdge(int index)
			throws IndexOutOfBoundsException {
		return removeEdge(getEdge(index));
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.graph.Graph#removeEdge(int, int)
	 */
	public <T extends Edge> T removeEdge(int fromIndex, int toIndex)
			throws IndexOutOfBoundsException, ElementNotFoundException {
		return removeEdge(getNode(fromIndex), getNode(toIndex));
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.graph.Graph#removeEdge(org.graphstream.graph.Node, org.graphstream.graph.Node)
	 */
	public <T extends Edge> T removeEdge(Node node1, Node node2)
			throws ElementNotFoundException {
		return removeEdge(node1.getEdgeBetween(node2));
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.graph.Graph#removeEdge(org.graphstream.graph.Edge)
	 */
	public <T extends Edge> T removeEdge(Edge edge) {
		if (edge != null) {
			edges.remove(edge.getId());
			listeners.sendEdgeRemoved(getId(), edge.getId());
			return fullGraph.removeEdge(edge);
		}

		return null;
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public <T extends Node> T removeNode(String id)
			throws ElementNotFoundException {
		Node n = getNode(id);
		return removeNode(n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#removeNode(int)
	 */
	public <T extends Node> T removeNode(int index)
			throws IndexOutOfBoundsException {
		return removeNode(getNode(index));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#removeNode(org.graphstream.graph.Node)
	 */
	public <T extends Node> T removeNode(Node node) {

		if (node != null) {
			for (int i = 0; i < node.getDegree(); i++)
				edges.remove(node.getEdge(i).getId());

			nodes.remove(node.getId());
			listeners.sendNodeRemoved(getId(), node.getId());
			return fullGraph.removeNode(node);
		}

		return null;
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void setAutoCreate(boolean on) {
		fullGraph.setAutoCreate(on);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void setEdgeFactory(EdgeFactory<? extends Edge> ef) {
		fullGraph.setEdgeFactory(ef);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void setNodeFactory(NodeFactory<? extends Node> nf) {
		fullGraph.setNodeFactory(nf);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void setStrict(boolean on) {
		fullGraph.setStrict(on);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void stepBegins(double time) {
		fullGraph.stepBegins(time);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void write(String filename) throws IOException {
		// TODO
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void write(FileSink output, String filename) throws IOException {
		// TODO
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void addAttributeSink(AttributeSink sink) {
		listeners.addAttributeSink(sink);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void addElementSink(ElementSink sink) {
		listeners.addElementSink(sink);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void addSink(Sink sink) {
		listeners.addSink(sink);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void clearAttributeSinks() {
		listeners.clearAttributeSinks();
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void clearElementSinks() {
		listeners.clearElementSinks();
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void clearSinks() {
		listeners.clearSinks();
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void removeAttributeSink(AttributeSink sink) {
		listeners.removeAttributeSink(sink);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void removeElementSink(ElementSink sink) {
		listeners.removeElementSink(sink);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void removeSink(Sink sink) {
		listeners.removeSink(sink);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
		listeners
				.edgeAttributeAdded(sourceId, timeId, edgeId, attribute, value);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		listeners.edgeAttributeChanged(sourceId, timeId, edgeId, attribute,
				oldValue, newValue);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
		listeners.edgeAttributeRemoved(sourceId, timeId, edgeId, attribute);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
		listeners.graphAttributeAdded(sourceId, timeId, attribute, value);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		listeners.graphAttributeChanged(sourceId, timeId, attribute, oldValue,
				newValue);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {
		listeners.graphAttributeRemoved(sourceId, timeId, attribute);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		listeners
				.nodeAttributeAdded(sourceId, timeId, nodeId, attribute, value);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		listeners.nodeAttributeChanged(sourceId, timeId, nodeId, attribute,
				oldValue, newValue);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
		listeners.nodeAttributeRemoved(sourceId, timeId, nodeId, attribute);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		listeners.edgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId,
				directed);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		listeners.edgeRemoved(sourceId, timeId, edgeId);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void graphCleared(String sourceId, long timeId) {
		listeners.graphCleared(sourceId, timeId);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		listeners.nodeAdded(sourceId, timeId, nodeId);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		listeners.nodeRemoved(sourceId, timeId, nodeId);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public void stepBegins(String sourceId, long timeId, double step) {
		listeners.stepBegins(sourceId, timeId, step);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	protected void attributeChanged(String sourceId, long timeId,
			String attribute, AttributeChangeEvent event, Object oldValue,
			Object newValue) {
		listeners.sendAttributeChangedEvent(sourceId, timeId, getId(),
				SourceBase.ElementType.GRAPH, attribute, event, oldValue,
				newValue);
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	public Iterator<Node> iterator() {
		return new SubGraphNodeIterator<Node>();
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	protected String myGraphId() {
		return getId();
	}

	/**
	 * @see org.graphstream.graph.Graph
	 */
	protected long newEvent() {
		// TODO Auto-generated method stub
		return 0;
	}

	class FullGraphListener extends SinkAdapter {
		protected void checkNodeInclusion(String nodeId) {
			Node n = fullGraph.getNode(nodeId);

			if (n != null) {
				if (condition.isInside(n) && !nodes.contains(nodeId)) {
					nodes.add(nodeId);
					listeners.sendNodeAdded(getId(), nodeId);

					for (int i = 0; i < n.getDegree(); i++)
						checkEdgeInclusion(n.getEdge(i).getId());
				} else if (!condition.isInside(n) && nodes.contains(nodeId)) {
					for (int i = 0; i < n.getDegree(); i++)
						edges.remove(n.getEdge(i).getId());

					listeners.sendNodeRemoved(getId(), nodeId);
					nodes.remove(nodeId);
				}
			}
		}

		protected void checkEdgeInclusion(String edgeId) {
			Edge e = fullGraph.getEdge(edgeId);

			if (e != null) {
				if (condition.isInside(e) && !edges.contains(edgeId)) {
					edges.add(edgeId);
					listeners
							.sendEdgeAdded(getId(), edgeId, e.getSourceNode()
									.getId(), e.getTargetNode().getId(), e
									.isDirected());
				} else if (!condition.isInside(e) && edges.contains(edgeId)) {
					listeners.sendEdgeRemoved(getId(), edgeId);
					edges.remove(edgeId);
				}
			}
		}

		public void nodeAdded(String sourceId, long timeId, String nodeId) {
			Node n = fullGraph.getNode(nodeId);

			if (n != null && condition.isInside(n)) {
				nodes.add(nodeId);
				listeners.sendNodeAdded(sourceId, timeId, nodeId);
			}
		}

		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			if (nodes.contains(nodeId)) {
				Node n = fullGraph.getNode(nodeId);

				for (int i = 0; i < n.getDegree(); i++)
					edges.remove(n.getEdge(i).getId());

				nodes.remove(nodeId);
				listeners.sendNodeRemoved(sourceId, timeId, nodeId);
			}
		}

		public void edgeAdded(String sourceId, long timeId, String edgeId,
				String sourceNodeId, String targetNodeId, boolean directed) {
			Edge e = fullGraph.getEdge(edgeId);

			if (e != null && condition.isInside(e)) {
				edges.add(edgeId);
				listeners.sendEdgeAdded(sourceId, timeId, edgeId, sourceNodeId,
						targetNodeId, directed);
			}
		}

		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			if (edges.contains(edgeId)) {
				edges.remove(edgeId);
				listeners.sendEdgeRemoved(sourceId, timeId, edgeId);
			}
		}

		public void nodeAttributeAdded(String sourceId, long timeId,
				String nodeId, String attributeId, Object value) {
			if (nodes.contains(nodeId)) {
				listeners.sendAttributeChangedEvent(sourceId, timeId, nodeId,
						SourceBase.ElementType.NODE, attributeId,
						AbstractElement.AttributeChangeEvent.ADD, null, value);
			}

			checkNodeInclusion(nodeId);
		}

		public void nodeAttributeChanged(String sourceId, long timeId,
				String nodeId, String attributeId, Object oldValue, Object value) {
			if (nodes.contains(nodeId)) {
				listeners.sendAttributeChangedEvent(sourceId, timeId, nodeId,
						SourceBase.ElementType.NODE, attributeId,
						AbstractElement.AttributeChangeEvent.CHANGE, oldValue,
						value);
			}

			checkNodeInclusion(nodeId);
		}

		public void nodeAttributeRemoved(String sourceId, long timeId,
				String nodeId, String attributeId) {
			if (nodes.contains(nodeId)) {
				listeners
						.sendAttributeChangedEvent(sourceId, timeId, nodeId,
								SourceBase.ElementType.NODE, attributeId,
								AbstractElement.AttributeChangeEvent.REMOVE,
								null, null);
			}

			checkNodeInclusion(nodeId);
		}

		public void edgeAttributeAdded(String sourceId, long timeId,
				String edgeId, String attributeId, Object value) {
			if (edges.contains(edgeId)) {
				listeners.sendAttributeChangedEvent(sourceId, timeId, edgeId,
						SourceBase.ElementType.EDGE, attributeId,
						AbstractElement.AttributeChangeEvent.ADD, null, value);
			}

			checkEdgeInclusion(edgeId);
		}

		public void edgeAttributeChanged(String sourceId, long timeId,
				String edgeId, String attributeId, Object oldValue, Object value) {
			if (edges.contains(edgeId)) {
				listeners.sendAttributeChangedEvent(sourceId, timeId, edgeId,
						SourceBase.ElementType.EDGE, attributeId,
						AbstractElement.AttributeChangeEvent.CHANGE, oldValue,
						value);
			}

			checkEdgeInclusion(edgeId);
		}

		public void edgeAttributeRemoved(String sourceId, long timeId,
				String edgeId, String attributeId) {
			if (edges.contains(edgeId)) {
				listeners
						.sendAttributeChangedEvent(sourceId, timeId, edgeId,
								SourceBase.ElementType.EDGE, attributeId,
								AbstractElement.AttributeChangeEvent.REMOVE,
								null, null);
			}

			checkEdgeInclusion(edgeId);
		}
	}

	// Handling the listeners -- We use the IO2 InputBase for this.

	class GraphListeners extends SourceBase implements Pipe {
		SinkTime sinkTime;

		public GraphListeners() {
			super(getId());

			sinkTime = new SinkTime();
			sourceTime.setSinkTime(sinkTime);
		}

		public long newEvent() {
			return sourceTime.newEvent();
		}

		public void edgeAttributeAdded(String sourceId, long timeId,
				String edgeId, String attribute, Object value) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				if (edges.contains(edgeId))
					fullGraph.edgeAttributeAdded(sourceId, timeId, edgeId,
							attribute, value);
			}
		}

		public void edgeAttributeChanged(String sourceId, long timeId,
				String edgeId, String attribute, Object oldValue,
				Object newValue) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				if (edges.contains(edgeId))
					fullGraph.edgeAttributeChanged(sourceId, timeId, edgeId,
							attribute, oldValue, newValue);
			}
		}

		public void edgeAttributeRemoved(String sourceId, long timeId,
				String edgeId, String attribute) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				if (edges.contains(edgeId))
					fullGraph.edgeAttributeRemoved(sourceId, timeId, edgeId,
							attribute);
			}
		}

		public void graphAttributeAdded(String sourceId, long timeId,
				String attribute, Object value) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				addAttribute_(sourceId, timeId, attribute, value);
			}
		}

		public void graphAttributeChanged(String sourceId, long timeId,
				String attribute, Object oldValue, Object newValue) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				changeAttribute_(sourceId, timeId, attribute, newValue);
			}
		}

		public void graphAttributeRemoved(String sourceId, long timeId,
				String attribute) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				removeAttribute_(sourceId, timeId, attribute);
			}
		}

		public void nodeAttributeAdded(String sourceId, long timeId,
				String nodeId, String attribute, Object value) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				if (nodes.contains(nodeId))
					fullGraph.nodeAttributeAdded(sourceId, timeId, nodeId,
							attribute, value);
			}
		}

		public void nodeAttributeChanged(String sourceId, long timeId,
				String nodeId, String attribute, Object oldValue,
				Object newValue) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				if (nodes.contains(nodeId))
					fullGraph.nodeAttributeChanged(sourceId, timeId, nodeId,
							attribute, oldValue, newValue);
			}
		}

		public void nodeAttributeRemoved(String sourceId, long timeId,
				String nodeId, String attribute) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				if (nodes.contains(nodeId))
					fullGraph.nodeAttributeRemoved(sourceId, timeId, nodeId,
							attribute);
			}
		}

		public void edgeAdded(String sourceId, long timeId, String edgeId,
				String fromNodeId, String toNodeId, boolean directed) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				fullGraph.edgeAdded(sourceId, timeId, edgeId, fromNodeId,
						toNodeId, directed);
			}
		}

		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				if (edges.contains(edgeId))
					fullGraph.edgeRemoved(sourceId, timeId, edgeId);
			}
		}

		public void graphCleared(String sourceId, long timeId) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				// TODO
			}
		}

		public void nodeAdded(String sourceId, long timeId, String nodeId) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				fullGraph.addNode(nodeId);
			}
		}

		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				if (nodes.contains(nodeId))
					fullGraph.nodeRemoved(sourceId, timeId, nodeId);
			}
		}

		public void stepBegins(String sourceId, long timeId, double step) {
			if (sinkTime.isNewEvent(sourceId, timeId)) {
				fullGraph.stepBegins(sourceId, timeId, step);
			}
		}
	}

	public void setNullAttributesAreErrors(boolean on) {
		fullGraph.setNullAttributesAreErrors(on);
	}

	public boolean nullAttributesAreErrors() {
		return fullGraph.nullAttributesAreErrors();
	}
}
