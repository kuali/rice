/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.workflow.tools.xml;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentType {

	private String name;
	private DocumentType parent;
	List<RouteNode> nodes = new ArrayList<RouteNode>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DocumentType getParent() {
		return parent;
	}

	public void setParent(DocumentType parent) {
		this.parent = parent;
	}

	public List<RouteNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<RouteNode> nodes) {
		this.nodes = nodes;
	}

	public RouteNode getNodeByName(String nodeName) {
		for (RouteNode node : nodes) {
			if (node.getName().equals(nodeName)) {
				return node;
			}
		}
		if (parent != null) {
			return parent.getNodeByName(nodeName);
		}
		return null;
	}

}
