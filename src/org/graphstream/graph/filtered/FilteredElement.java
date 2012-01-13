package org.graphstream.graph.filtered;

import org.graphstream.graph.Element;

public interface FilteredElement<T extends Element> {
	T getFilteredElement();
}
