/*
 * Copyright 2007-2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.engine.node;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.rule.xmlrouting.XPathHelper;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * A simple class for performing operations on RouteNode.  In particular, this class provides some
 * convenience methods for processing custom RouteNode XML content fragments. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RouteNodeUtils {

	/**
	 * Searches a RouteNode's "contentFragment" (it's XML definition) for an XML element with
	 * the given name and returns it's value.
	 * 
	 * <p>For example, in a node with the following definition:
	 *
	 * <pre><routeNode name="...">
	 *   ...
	 *   <myCustomProperty>propertyValue</myCustomProperty>
	 * </routeNode></pre>
	 * 
	 * <p>An invocation of getValueOfCustomProperty(routeNode, "myCustomProperty") would return
	 * "propertyValue".
	 * 
	 * @param routeNode RouteNode to examine
	 * @param propertyName name of the XML element to search for
	 * 
	 * @return the value of the XML element, or null if it could not be located
	 */
	public static String getValueOfCustomProperty(RouteNode routeNode, String propertyName) {
		String contentFragment = routeNode.getContentFragment();
		String elementValue = null;
		if (!StringUtils.isBlank(contentFragment)) {
			try {
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document document = db.parse(new InputSource(new StringReader(contentFragment)));	
				elementValue = XPathHelper.newXPath().evaluate("//" + propertyName, document);
			} catch (Exception e) {
				throw new RiceRuntimeException("Error when attempting to parse Document Type content fragment for property name: " + propertyName, e);
			}
		}
		return elementValue;
	}
	
	public static List getFlattenedNodeInstances(DocumentRouteHeaderValue document, boolean includeProcesses) {
        List nodeInstances = new ArrayList();
        Set visitedNodeInstanceIds = new HashSet();
        for (Iterator iterator = document.getInitialRouteNodeInstances().iterator(); iterator.hasNext();) {
            RouteNodeInstance initialNodeInstance = (RouteNodeInstance) iterator.next();
            flattenNodeInstanceGraph(nodeInstances, visitedNodeInstanceIds, initialNodeInstance, includeProcesses);    
        }
        return nodeInstances;
    }
    
    private static void flattenNodeInstanceGraph(List nodeInstances, Set visitedNodeInstanceIds, RouteNodeInstance nodeInstance, boolean includeProcesses) {
        if (visitedNodeInstanceIds.contains(nodeInstance.getRouteNodeInstanceId())) {
            return;
        }
        if (includeProcesses && nodeInstance.getProcess() != null) {
            flattenNodeInstanceGraph(nodeInstances, visitedNodeInstanceIds, nodeInstance.getProcess(), includeProcesses);
        }
        visitedNodeInstanceIds.add(nodeInstance.getRouteNodeInstanceId());
        nodeInstances.add(nodeInstance);
        for (Iterator iterator = nodeInstance.getNextNodeInstances().iterator(); iterator.hasNext();) {
            RouteNodeInstance nextNodeInstance = (RouteNodeInstance) iterator.next();
            flattenNodeInstanceGraph(nodeInstances, visitedNodeInstanceIds, nextNodeInstance, includeProcesses);
        }
    }
    
    public static List getFlattenedNodes(DocumentType documentType, boolean climbHierarchy) {
        List nodes = new ArrayList();
        if (!documentType.isRouteInherited() || climbHierarchy) {
            for (Iterator iterator = documentType.getProcesses().iterator(); iterator.hasNext();) {
                Process process = (Process) iterator.next();
                nodes.addAll(getFlattenedNodes(process));
            }
        }
        Collections.sort(nodes, new RouteNodeSorter());
        return nodes;
    }
    
    public static List getFlattenedNodes(Process process) {
        Map nodesMap = new HashMap();
        if (process.getInitialRouteNode() != null) {
            flattenNodeGraph(nodesMap, process.getInitialRouteNode());
            List nodes = new ArrayList(nodesMap.values());
            Collections.sort(nodes, new RouteNodeSorter());
            return nodes;
        } else {
            List nodes = new ArrayList();
            nodes.add(new RouteNode());
            return nodes;
        }

    }
    
    /**
     * Recursively walks the node graph and builds up the map.  Uses a map because we will
     * end up walking through duplicates, as is the case with Join nodes.
     */
    private static void flattenNodeGraph(Map nodes, RouteNode node) {
        if (node != null) {
            if (nodes.containsKey(node.getRouteNodeName())) {
                return;
            }
            nodes.put(node.getRouteNodeName(), node);
            for (Iterator iterator = node.getNextNodes().iterator(); iterator.hasNext();) {
                RouteNode nextNode = (RouteNode) iterator.next();
                flattenNodeGraph(nodes, nextNode);
            }
        } else {
            return;
        }
    }
    
    /**
     * Sorts by RouteNodeId or the order the nodes will be evaluated in *roughly*.  This is 
     * for display purposes when rendering a flattened list of nodes.
     * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    private static class RouteNodeSorter implements Comparator {
        public int compare(Object arg0, Object arg1) {
            RouteNode rn1 = (RouteNode)arg0;
            RouteNode rn2 = (RouteNode)arg1;
            return rn1.getRouteNodeId().compareTo(rn2.getRouteNodeId());
        }
    }
    
    public static List getActiveNodeInstances(DocumentRouteHeaderValue document) {
        List flattenedNodeInstances = getFlattenedNodeInstances(document, true);
        List activeNodeInstances = new ArrayList();
        for (Iterator iterator = flattenedNodeInstances.iterator(); iterator.hasNext();) {
            RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
            if (nodeInstance.isActive()) {
                activeNodeInstances.add(nodeInstance);
            }
        }
        return activeNodeInstances;
    }
    
    public static RouteNodeInstance findRouteNodeInstanceById(Long nodeInstanceId, DocumentRouteHeaderValue document) {
    	List flattenedNodeInstances = getFlattenedNodeInstances(document, true);
    	RouteNodeInstance niRet = null;
        for (Iterator iterator = flattenedNodeInstances.iterator(); iterator.hasNext();) {
        	RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
            if(nodeInstanceId == nodeInstance.getRouteNodeInstanceId()){
            	niRet = nodeInstance;
            	break;
            }
        }
        return niRet;
    }
    
    
	
}
