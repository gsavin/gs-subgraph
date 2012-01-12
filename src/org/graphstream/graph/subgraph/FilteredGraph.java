package org.graphstream.graph.subgraph;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.stream.AttributeSink;
import org.graphstream.stream.ElementSink;
import org.graphstream.stream.GraphParseException;
import org.graphstream.stream.Sink;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSource;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.Layouts;
import org.graphstream.ui.swingViewer.GraphRenderer;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.util.Filter;
import org.graphstream.util.FilteredEdgeIterator;
import org.graphstream.util.FilteredNodeIterator;
import org.graphstream.util.Filters;

public class FilteredGraph extends ElementProxy<Graph> implements Graph {

	Filter<Node> nodeFilter;
	Filter<Edge> edgeFilter;

	final HashMap<String, FilteredNode> filteredNodes;
	final HashMap<String, FilteredEdge> filteredEdges;

	ArrayList<ElementSink> elementSinks;
	ArrayList<AttributeSink> attributeSinks;

	final FilteredSink filteredSink;

	public FilteredGraph(Graph g, Filter<Node> nf, Filter<Edge> ef) {
		super(g);

		filteredNodes = new HashMap<String, FilteredNode>();
		filteredEdges = new HashMap<String, FilteredEdge>();
		filteredSink = new FilteredSink();

		elementSinks = new ArrayList<ElementSink>();
		attributeSinks = new ArrayList<AttributeSink>();

		nodeFilter = Filters.or(nf, Filters.<Node> isContained(filteredNodes
				.values()));
		edgeFilter = Filters.or(ef, Filters.<Edge> isContained(filteredEdges
				.values()));

		g.addSink(filteredSink);

		for (Node n : g.getEachNode()) {
			if (nodeFilter.isAvailable(n))
				include(n);
		}

		for (Edge e : g.getEachEdge()) {
			if (edgeFilter.isAvailable(e))
				include(e);
		}
	}

	public void destroy() {
		element.removeSink(filteredSink);
		filteredNodes.clear();
		filteredEdges.clear();
		elementSinks.clear();
		attributeSinks.clear();
	}
	
	public void include(Node n) {
		filteredNodes.put(n.getId(), new FilteredNode(n, this));
	}
	
	public void include(Edge e) {
		filteredEdges.put(e.getId(), new FilteredEdge(e, this));

		if (filteredNodes.containsKey(e.getNode0().getId()))
			filteredNodes.get(e.getNode0().getId()).register(e);

		if (filteredNodes.containsKey(e.getNode1().getId()))
			filteredNodes.get(e.getNode1().getId()).register(e);
	}

	public Filter<Node> getNodeFilter() {
		return nodeFilter;
	}

	public Filter<Edge> getEdgeFilter() {
		return edgeFilter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public <T extends Edge> T addEdge(String id, String node1, String node2)
			throws IdAlreadyInUseException, ElementNotFoundException,
			EdgeRejectedException {
		Node n1, n2;

		n1 = element.getNode(node1);
		n2 = element.getNode(node2);

		return addEdge(id, n1, n2, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String,
	 * java.lang.String, java.lang.String, boolean)
	 */
	public <T extends Edge> T addEdge(String id, String from, String to,
			boolean directed) throws IdAlreadyInUseException,
			ElementNotFoundException {
		Node n1, n2;

		n1 = element.getNode(from);
		n2 = element.getNode(to);

		return addEdge(id, n1, n2, directed);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String, int, int)
	 */
	public <T extends Edge> T addEdge(String id, int index1, int index2)
			throws IndexOutOfBoundsException, IdAlreadyInUseException,
			EdgeRejectedException {
		Node n1, n2;

		n1 = element.getNode(index1);
		n2 = element.getNode(index2);

		return addEdge(id, n1, n2, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String, int, int,
	 * boolean)
	 */
	public <T extends Edge> T addEdge(String id, int fromIndex, int toIndex,
			boolean directed) throws IndexOutOfBoundsException,
			IdAlreadyInUseException, EdgeRejectedException {
		Node n1, n2;

		n1 = element.getNode(fromIndex);
		n2 = element.getNode(toIndex);

		return addEdge(id, n1, n2, directed);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String,
	 * org.graphstream.graph.Node, org.graphstream.graph.Node)
	 */
	public <T extends Edge> T addEdge(String id, Node node1, Node node2)
			throws IdAlreadyInUseException, EdgeRejectedException {
		return addEdge(id, node1, node2, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String,
	 * org.graphstream.graph.Node, org.graphstream.graph.Node, boolean)
	 */
	@SuppressWarnings("unchecked")
	public <T extends Edge> T addEdge(String id, Node from, Node to,
			boolean directed) throws IdAlreadyInUseException,
			EdgeRejectedException {
		if (from instanceof FilteredNode && filteredNodes.containsValue(from))
			from = ((FilteredNode) from).getOriginalNode();

		if (to instanceof FilteredNode && filteredNodes.containsValue(to))
			to = ((FilteredNode) to).getOriginalNode();

		element.addEdge(id, from, to, directed);
		return (T) filteredEdges.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#addNode(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public <T extends Node> T addNode(String id) throws IdAlreadyInUseException {
		element.addNode(id);
		return (T) filteredNodes.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#attributeSinks()
	 */
	public Iterable<AttributeSink> attributeSinks() {
		return attributeSinks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#clear()
	 */
	public void clear() {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#display()
	 */
	public Viewer display() {
		return display(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#display(boolean)
	 */
	public Viewer display(boolean autoLayout) {
		Viewer viewer = new Viewer(this,
				Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		GraphRenderer renderer = Viewer.newGraphRenderer();
		viewer.addView(Viewer.DEFAULT_VIEW_ID, renderer);
		if (autoLayout) {
			Layout layout = Layouts.newLayoutAlgorithm();
			viewer.enableAutoLayout(layout);
		}
		return viewer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#edgeFactory()
	 */
	public EdgeFactory<? extends Edge> edgeFactory() {
		return element.edgeFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#elementSinks()
	 */
	public Iterable<ElementSink> elementSinks() {
		return elementSinks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#getEachEdge()
	 */
	public <T extends Edge> Iterable<? extends T> getEachEdge() {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return getEdgeIterator();
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#getEachNode()
	 */
	public <T extends Node> Iterable<? extends T> getEachNode() {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return getNodeIterator();
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#getEdge(java.lang.String)
	 */
	public <T extends Edge> T getEdge(String id) {
		T e = element.getEdge(id);

		if (edgeFilter.isAvailable(e))
			return e;

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#getEdge(int)
	 */
	public <T extends Edge> T getEdge(int index)
			throws IndexOutOfBoundsException {
		T e = element.getEdge(index);

		if (edgeFilter.isAvailable(e))
			return e;

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#getEdgeCount()
	 */
	public int getEdgeCount() {
		return filteredEdges.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#getEdgeIterator()
	 */
	public <T extends Edge> Iterator<T> getEdgeIterator() {
		return new FilteredEdgeIterator<T>(element.<T> getEdgeIterator(),
				edgeFilter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#getEdgeSet()
	 */
	public <T extends Edge> Collection<T> getEdgeSet() {
		return new AbstractCollection<T>() {
			public Iterator<T> iterator() {
				return getEdgeIterator();
			}

			public int size() {
				return getNodeCount();
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#getNode(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public <T extends Node> T getNode(String id) {
		return (T) filteredNodes.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#getNode(int)
	 */
	public <T extends Node> T getNode(int index)
			throws IndexOutOfBoundsException {
		Node n = element.getNode(index);
		return getNode(n.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#getNodeCount()
	 */
	public int getNodeCount() {
		return filteredNodes.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#getNodeIterator()
	 */
	public <T extends Node> Iterator<T> getNodeIterator() {
		return new FilteredNodeIterator<T>(element.<T> getNodeIterator(),
				nodeFilter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#getNodeSet()
	 */
	public <T extends Node> Collection<T> getNodeSet() {
		return new AbstractCollection<T>() {
			public Iterator<T> iterator() {
				return getNodeIterator();
			}

			public int size() {
				return getNodeCount();
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#getStep()
	 */
	public double getStep() {
		return element.getStep();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#isAutoCreationEnabled()
	 */
	public boolean isAutoCreationEnabled() {
		return element.isAutoCreationEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#isStrict()
	 */
	public boolean isStrict() {
		return element.isStrict();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#nodeFactory()
	 */
	public NodeFactory<? extends Node> nodeFactory() {
		return element.nodeFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#nullAttributesAreErrors()
	 */
	public boolean nullAttributesAreErrors() {
		return element.nullAttributesAreErrors();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#read(java.lang.String)
	 */
	public void read(String filename) throws IOException, GraphParseException,
			ElementNotFoundException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.graph.Graph#read(org.graphstream.stream.file.FileSource,
	 * java.lang.String)
	 */
	public void read(FileSource input, String filename) throws IOException,
			GraphParseException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#removeEdge(java.lang.String,
	 * java.lang.String)
	 */
	public <T extends Edge> T removeEdge(String from, String to)
			throws ElementNotFoundException {
		Node n1 = getNode(from);
		Node n2 = getNode(to);

		if (n1 == null)
			throw new ElementNotFoundException("Node \"%s\"", from);

		if (n2 == null)
			throw new ElementNotFoundException("Node \"%s\"", to);

		return removeEdge(n1, n2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#removeEdge(java.lang.String)
	 */
	public <T extends Edge> T removeEdge(String id)
			throws ElementNotFoundException {
		Edge e = getEdge(id);

		if (e == null) {
			if (isStrict())
				throw new ElementNotFoundException("Edge \"%s\"", id);
			return null;
		}

		return removeEdge(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#removeEdge(int)
	 */
	public <T extends Edge> T removeEdge(int index)
			throws IndexOutOfBoundsException {
		Edge e = getEdge(index);

		if (e == null) {
			if (isStrict())
				throw new ElementNotFoundException("Edge #%d", index);
			return null;
		}

		return removeEdge(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#removeEdge(int, int)
	 */
	public <T extends Edge> T removeEdge(int fromIndex, int toIndex)
			throws IndexOutOfBoundsException, ElementNotFoundException {
		Node n1 = getNode(fromIndex);
		Node n2 = getNode(toIndex);

		if (n1 == null)
			throw new ElementNotFoundException("Node #%d", fromIndex);

		if (n2 == null)
			throw new ElementNotFoundException("Node #%d", toIndex);

		return removeEdge(n1, n2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#removeEdge(org.graphstream.graph.Node,
	 * org.graphstream.graph.Node)
	 */
	public <T extends Edge> T removeEdge(Node n1, Node n2)
			throws ElementNotFoundException {
		if (!nodeFilter.isAvailable(n1))
			throw new ElementNotFoundException("Node \"%s\"", n1.getId());

		if (!nodeFilter.isAvailable(n2))
			throw new ElementNotFoundException("Node \"%s\"", n2.getId());

		Edge e = n1.getEdgeBetween(n2);
		return removeEdge(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#removeEdge(org.graphstream.graph.Edge)
	 */
	public <T extends Edge> T removeEdge(Edge edge) {
		if (edgeFilter.isAvailable(edge))
			return element.removeEdge(edge);

		if (!edgeFilter.isAvailable(edge) && isStrict())
			throw new ElementNotFoundException("Edge \"%s\"", edge.getId());

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#removeNode(java.lang.String)
	 */
	public <T extends Node> T removeNode(String id)
			throws ElementNotFoundException {
		Node n = getNode(id);

		if (n == null && isStrict())
			throw new ElementNotFoundException("Node \"%s\"", id);

		return removeNode(n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#removeNode(int)
	 */
	public <T extends Node> T removeNode(int index)
			throws IndexOutOfBoundsException {
		Node n = getNode(index);

		if (n == null && isStrict())
			throw new ElementNotFoundException("Node #%d", index);

		return removeNode(n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#removeNode(org.graphstream.graph.Node)
	 */
	@SuppressWarnings("unchecked")
	public <T extends Node> T removeNode(Node node) {
		if (node instanceof FilteredNode && filteredNodes.containsValue(node))
			node = ((FilteredNode) node).getOriginalNode();

		if (!nodeFilter.isAvailable(node) && isStrict())
			return null;

		FilteredNode fn = filteredNodes.get(node.getId());
		Node n = element.removeNode(node);

		if (n != null)
			return (T) fn;

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#setAutoCreate(boolean)
	 */
	public void setAutoCreate(boolean on) {
		element.setAutoCreate(on);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.graph.Graph#setEdgeFactory(org.graphstream.graph.EdgeFactory
	 * )
	 */
	public void setEdgeFactory(EdgeFactory<? extends Edge> ef) {
		element.setEdgeFactory(ef);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.graph.Graph#setNodeFactory(org.graphstream.graph.NodeFactory
	 * )
	 */
	public void setNodeFactory(NodeFactory<? extends Node> nf) {
		element.setNodeFactory(nf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#setNullAttributesAreErrors(boolean)
	 */
	public void setNullAttributesAreErrors(boolean on) {
		element.setNullAttributesAreErrors(on);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#setStrict(boolean)
	 */
	public void setStrict(boolean on) {
		element.setStrict(on);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#stepBegins(double)
	 */
	public void stepBegins(double time) {
		element.stepBegins(time);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.graph.Graph#write(java.lang.String)
	 */
	public void write(String filename) throws IOException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.graph.Graph#write(org.graphstream.stream.file.FileSink,
	 * java.lang.String)
	 */
	public void write(FileSink output, String filename) throws IOException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.Source#addAttributeSink(org.graphstream.stream
	 * .AttributeSink)
	 */
	public void addAttributeSink(AttributeSink sink) {
		attributeSinks.add(sink);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.graphstream.stream.Source#addElementSink(org.graphstream.stream.
	 * ElementSink)
	 */
	public void addElementSink(ElementSink sink) {
		elementSinks.add(sink);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#addSink(org.graphstream.stream.Sink)
	 */
	public void addSink(Sink sink) {
		attributeSinks.add(sink);
		elementSinks.add(sink);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#clearAttributeSinks()
	 */
	public void clearAttributeSinks() {
		attributeSinks.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#clearElementSinks()
	 */
	public void clearElementSinks() {
		elementSinks.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.Source#clearSinks()
	 */
	public void clearSinks() {
		attributeSinks.clear();
		elementSinks.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.Source#removeAttributeSink(org.graphstream.stream
	 * .AttributeSink)
	 */
	public void removeAttributeSink(AttributeSink sink) {
		attributeSinks.remove(sink);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.Source#removeElementSink(org.graphstream.stream
	 * .ElementSink)
	 */
	public void removeElementSink(ElementSink sink) {
		elementSinks.remove(sink);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.Source#removeSink(org.graphstream.stream.Sink)
	 */
	public void removeSink(Sink sink) {
		attributeSinks.remove(sink);
		elementSinks.remove(sink);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
		element.edgeAttributeAdded(sourceId, timeId, edgeId, attribute, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeChanged(java.lang.String
	 * , long, java.lang.String, java.lang.String, java.lang.Object,
	 * java.lang.Object)
	 */
	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		element.edgeAttributeChanged(sourceId, timeId, edgeId, attribute,
				oldValue, newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeRemoved(java.lang.String
	 * , long, java.lang.String, java.lang.String)
	 */
	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
		element.edgeAttributeRemoved(sourceId, timeId, edgeId, attribute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeAdded(java.lang.String
	 * , long, java.lang.String, java.lang.Object)
	 */
	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
		element.graphAttributeAdded(sourceId, timeId, attribute, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeChanged(java.lang.
	 * String, long, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		element.graphAttributeChanged(sourceId, timeId, attribute, oldValue,
				newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeRemoved(java.lang.
	 * String, long, java.lang.String)
	 */
	public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {
		element.graphAttributeRemoved(sourceId, timeId, attribute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		element.nodeAttributeAdded(sourceId, timeId, nodeId, attribute, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeChanged(java.lang.String
	 * , long, java.lang.String, java.lang.String, java.lang.Object,
	 * java.lang.Object)
	 */
	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		element.nodeAttributeChanged(sourceId, timeId, nodeId, attribute,
				oldValue, newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeRemoved(java.lang.String
	 * , long, java.lang.String, java.lang.String)
	 */
	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
		element.nodeAttributeRemoved(sourceId, timeId, nodeId, attribute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeAdded(java.lang.String, long,
	 * java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		element.edgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId,
				directed);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeRemoved(java.lang.String,
	 * long, java.lang.String)
	 */
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		element.edgeRemoved(sourceId, timeId, edgeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#graphCleared(java.lang.String,
	 * long)
	 */
	public void graphCleared(String sourceId, long timeId) {
		element.graphCleared(sourceId, timeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeAdded(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		element.nodeAdded(sourceId, timeId, nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeRemoved(java.lang.String,
	 * long, java.lang.String)
	 */
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		element.nodeRemoved(sourceId, timeId, nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#stepBegins(java.lang.String,
	 * long, double)
	 */
	public void stepBegins(String sourceId, long timeId, double step) {
		element.stepBegins(sourceId, timeId, step);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Node> iterator() {
		return new FilteredNodeIterator<Node>(element.iterator(), nodeFilter);
	}

	class FilteredSink implements Sink {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.ElementSink#edgeAdded(java.lang.String,
		 * long, java.lang.String, java.lang.String, java.lang.String, boolean)
		 */
		public void edgeAdded(String sourceId, long timeId, String edgeId,
				String fromNodeId, String toNodeId, boolean directed) {
			Edge e = element.getEdge(edgeId);

			if (edgeFilter.isAvailable(e)) {
				filteredEdges.put(edgeId, new FilteredEdge(e,
						FilteredGraph.this));

				if (filteredNodes.containsKey(fromNodeId))
					filteredNodes.get(fromNodeId).register(e);

				if (filteredNodes.containsKey(toNodeId))
					filteredNodes.get(toNodeId).register(e);

				for (int i = 0; i < elementSinks.size(); i++)
					elementSinks.get(i).edgeAdded(sourceId, timeId, edgeId,
							fromNodeId, toNodeId, directed);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.ElementSink#edgeRemoved(java.lang.String,
		 * long, java.lang.String)
		 */
		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			Edge e = element.getEdge(edgeId);

			if (edgeFilter.isAvailable(e)) {
				for (int i = 0; i < elementSinks.size(); i++)
					elementSinks.get(i).edgeRemoved(sourceId, timeId, edgeId);

				if (filteredNodes.containsKey(e.getNode0().getId()))
					filteredNodes.get(e.getNode0().getId()).unregister(e);

				if (filteredNodes.containsKey(e.getNode1().getId()))
					filteredNodes.get(e.getNode1().getId()).unregister(e);

				filteredEdges.remove(edgeId);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.ElementSink#graphCleared(java.lang.String,
		 * long)
		 */
		public void graphCleared(String sourceId, long timeId) {
			for (int i = 0; i < elementSinks.size(); i++)
				elementSinks.get(i).graphCleared(sourceId, timeId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.ElementSink#nodeAdded(java.lang.String,
		 * long, java.lang.String)
		 */
		public void nodeAdded(String sourceId, long timeId, String nodeId) {
			Node n = element.getNode(nodeId);

			if (nodeFilter.isAvailable(n)) {
				filteredNodes.put(nodeId, new FilteredNode(n,
						FilteredGraph.this));

				for (int i = 0; i < elementSinks.size(); i++)
					elementSinks.get(i).nodeAdded(sourceId, timeId, nodeId);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.ElementSink#nodeRemoved(java.lang.String,
		 * long, java.lang.String)
		 */
		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			Node n = element.getNode(nodeId);

			if (nodeFilter.isAvailable(n)) {
				for (int i = 0; i < elementSinks.size(); i++)
					elementSinks.get(i).nodeRemoved(sourceId, timeId, nodeId);

				filteredNodes.remove(nodeId);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.stream.ElementSink#stepBegins(java.lang.String,
		 * long, double)
		 */
		public void stepBegins(String sourceId, long timeId, double step) {
			for (int i = 0; i < elementSinks.size(); i++)
				elementSinks.get(i).stepBegins(sourceId, timeId, step);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#edgeAttributeAdded(java.lang
		 * .String, long, java.lang.String, java.lang.String, java.lang.Object)
		 */
		public void edgeAttributeAdded(String sourceId, long timeId,
				String edgeId, String attribute, Object value) {
			Edge e = element.getEdge(edgeId);

			if (edgeFilter.isAvailable(e)) {
				for (int i = 0; i < attributeSinks.size(); i++)
					attributeSinks.get(i).edgeAttributeAdded(sourceId, timeId,
							edgeId, attribute, value);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#edgeAttributeChanged(java.lang
		 * .String, long, java.lang.String, java.lang.String, java.lang.Object,
		 * java.lang.Object)
		 */
		public void edgeAttributeChanged(String sourceId, long timeId,
				String edgeId, String attribute, Object oldValue,
				Object newValue) {
			Edge e = element.getEdge(edgeId);

			if (edgeFilter.isAvailable(e)) {
				for (int i = 0; i < attributeSinks.size(); i++)
					attributeSinks.get(i).edgeAttributeChanged(sourceId,
							timeId, edgeId, attribute, oldValue, newValue);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#edgeAttributeRemoved(java.lang
		 * .String, long, java.lang.String, java.lang.String)
		 */
		public void edgeAttributeRemoved(String sourceId, long timeId,
				String edgeId, String attribute) {
			Edge e = element.getEdge(edgeId);

			if (edgeFilter.isAvailable(e)) {
				for (int i = 0; i < attributeSinks.size(); i++)
					attributeSinks.get(i).edgeAttributeRemoved(sourceId,
							timeId, edgeId, attribute);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#graphAttributeAdded(java.lang
		 * .String, long, java.lang.String, java.lang.Object)
		 */
		public void graphAttributeAdded(String sourceId, long timeId,
				String attribute, Object value) {
			for (int i = 0; i < attributeSinks.size(); i++)
				attributeSinks.get(i).graphAttributeAdded(sourceId, timeId,
						attribute, value);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#graphAttributeChanged(java.lang
		 * .String, long, java.lang.String, java.lang.Object, java.lang.Object)
		 */
		public void graphAttributeChanged(String sourceId, long timeId,
				String attribute, Object oldValue, Object newValue) {
			for (int i = 0; i < attributeSinks.size(); i++)
				attributeSinks.get(i).graphAttributeChanged(sourceId, timeId,
						attribute, oldValue, newValue);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#graphAttributeRemoved(java.lang
		 * .String, long, java.lang.String)
		 */
		public void graphAttributeRemoved(String sourceId, long timeId,
				String attribute) {
			for (int i = 0; i < attributeSinks.size(); i++)
				attributeSinks.get(i).graphAttributeRemoved(sourceId, timeId,
						attribute);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#nodeAttributeAdded(java.lang
		 * .String, long, java.lang.String, java.lang.String, java.lang.Object)
		 */
		public void nodeAttributeAdded(String sourceId, long timeId,
				String nodeId, String attribute, Object value) {
			Node n = element.getNode(nodeId);

			if (nodeFilter.isAvailable(n)) {
				for (int i = 0; i < attributeSinks.size(); i++)
					attributeSinks.get(i).nodeAttributeAdded(sourceId, timeId,
							nodeId, attribute, value);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#nodeAttributeChanged(java.lang
		 * .String, long, java.lang.String, java.lang.String, java.lang.Object,
		 * java.lang.Object)
		 */
		public void nodeAttributeChanged(String sourceId, long timeId,
				String nodeId, String attribute, Object oldValue,
				Object newValue) {
			Node n = element.getNode(nodeId);

			if (nodeFilter.isAvailable(n)) {
				for (int i = 0; i < attributeSinks.size(); i++)
					attributeSinks.get(i).nodeAttributeChanged(sourceId,
							timeId, nodeId, attribute, oldValue, newValue);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#nodeAttributeRemoved(java.lang
		 * .String, long, java.lang.String, java.lang.String)
		 */
		public void nodeAttributeRemoved(String sourceId, long timeId,
				String nodeId, String attribute) {
			Node n = element.getNode(nodeId);

			if (nodeFilter.isAvailable(n)) {
				for (int i = 0; i < attributeSinks.size(); i++)
					attributeSinks.get(i).nodeAttributeRemoved(sourceId,
							timeId, nodeId, attribute);
			}
		}
	}
}
