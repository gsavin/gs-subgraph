package org.graphstream.graph.filtered.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.filtered.FilteredEdge;
import org.graphstream.graph.filtered.FilteredGraph;
import org.graphstream.graph.filtered.FilteredNode;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.util.Filter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class TestFilteredGraph {

	Graph baseGraph;

	@Before
	public void loadGraph() throws IOException {
		FileSourceDGS dgs = new FileSourceDGS();

		baseGraph = new AdjacencyListGraph("g");

		dgs.addSink(baseGraph);
		dgs.readAll(getClass().getResource("data/test.dgs"));
		dgs.removeSink(baseGraph);
	}

	@Test
	public void checkParts() {
		assertNotNull(baseGraph);

		String[] nodes = { "A0", "A1", "A2", "B0", "B1", "B2", "C0", "C1", "C2" };
		String[] edges = { "A01", "A02", "A12", "B01", "B02", "B12", "C01",
				"C02", "C12", "AB", "AC", "BC" };

		assertEquals(0x9, baseGraph.getNodeCount());
		assertEquals(0xC, baseGraph.getEdgeCount());

		for (String id : nodes)
			assertNotNull(baseGraph.getNode(id));

		for (String id : edges)
			assertNotNull(baseGraph.getEdge(id));

		checkReachable(baseGraph, baseGraph.getNode(0), nodes);

		checkPart("A");
		checkPart("B");
		checkPart("C");
	}

	protected void checkPart(final String type) {
		Filter<Node> nodeFilter = new Filter<Node>() {
			public boolean isAvailable(Node n) {
				return n.hasAttribute("type")
						&& n.getLabel("type").equals(type);
			}
		};

		Filter<Edge> edgeFilter = new Filter<Edge>() {
			public boolean isAvailable(Edge e) {
				return e.hasAttribute("type")
						&& e.getLabel("type").equals(type);
			}
		};

		FilteredGraph g = new FilteredGraph(baseGraph, nodeFilter, edgeFilter);
		Node n0, n1, n2;
		Edge e01, e02, e12;

		n0 = g.getNode(type + "0");
		n1 = g.getNode(type + "1");
		n2 = g.getNode(type + "2");

		e01 = g.getEdge(type + "01");
		e02 = g.getEdge(type + "02");
		e12 = g.getEdge(type + "12");

		assertTrue(g.getNodeCount() == 3);
		assertTrue(g.getEdgeCount() == 3);

		assertNotNull(n0);
		assertNotNull(n1);
		assertNotNull(n2);

		assertEquals(FilteredNode.class, n0.getClass());
		assertEquals(FilteredNode.class, n1.getClass());
		assertEquals(FilteredNode.class, n2.getClass());

		assertEquals(2, n0.getDegree());
		assertEquals(2, n1.getDegree());
		assertEquals(2, n2.getDegree());

		assertNotNull(e01);
		assertNotNull(e02);
		assertNotNull(e12);

		assertEquals(FilteredEdge.class, e01.getClass());
		assertEquals(FilteredEdge.class, e02.getClass());
		assertEquals(FilteredEdge.class, e12.getClass());

		assertEquals(2, e01.getNode0().getDegree());
		assertEquals(2, e01.getNode1().getDegree());
		assertEquals(2, e02.getNode0().getDegree());
		assertEquals(2, e02.getNode1().getDegree());
		assertEquals(2, e12.getNode0().getDegree());
		assertEquals(2, e12.getNode1().getDegree());

		checkReachable(g, n0, n0.getId(), n1.getId(), n2.getId());
	}

	protected void checkReachable(Graph g, Node from, String... nodes) {
		HashSet<String> reached = new HashSet<String>();
		LinkedList<Node> toVisit = new LinkedList<Node>();

		toVisit.add(from);

		while (toVisit.size() > 0) {
			Node n = toVisit.poll();
			reached.add(n.getId());

			Iterator<Node> it = n.getNeighborNodeIterator();

			while (it.hasNext()) {
				Node neigh = it.next();
				if (!reached.contains(neigh.getId())
						&& !toVisit.contains(neigh))
					toVisit.addLast(neigh);
			}
		}

		assertEquals(reached, new HashSet<String>(Arrays.asList(nodes)));
	}
}
