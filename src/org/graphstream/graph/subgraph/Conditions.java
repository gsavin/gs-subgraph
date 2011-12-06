/*
 * Copyright 2011 - 2012
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of gs-subgraph, a feature for GraphStream to manipulate
 * subgraph.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
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
