package org.graphstream.graph.subgraph;

import org.graphstream.graph.subgraph.condition.ANDCondition;
import org.graphstream.graph.subgraph.condition.AttributeCondition;
import org.graphstream.graph.subgraph.condition.FalseCondition;
import org.graphstream.graph.subgraph.condition.ORCondition;
import org.graphstream.graph.subgraph.condition.TrueCondition;
import org.graphstream.graph.subgraph.condition.XORCondition;

public class Conditions {
	private static final IncludeCondition FALSE = new FalseCondition();
	private static final IncludeCondition TRUE = new TrueCondition();
	
	public static final IncludeCondition none() {
		return FALSE;
	}
	
	public static final IncludeCondition all() {
		return TRUE;
	}
	
	public static final IncludeCondition and(IncludeCondition ... conditions) {
		return new ANDCondition(conditions);
	}
	
	public static final IncludeCondition or(IncludeCondition ... conditions) {
		return new ORCondition(conditions);
	}
	
	public static final IncludeCondition xor(IncludeCondition ... conditions) {
		return new XORCondition(conditions);
	}
	
	public static final IncludeCondition attr(String key, Object value) {
		return new AttributeCondition(key,value==null?null:value.toString());
	}
}
