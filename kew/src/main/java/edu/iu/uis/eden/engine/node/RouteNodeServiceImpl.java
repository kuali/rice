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
package edu.iu.uis.eden.engine.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.ComparatorUtils;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.engine.RouteHelper;
import edu.iu.uis.eden.engine.node.dao.RouteNodeDAO;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.util.Utilities;


public class RouteNodeServiceImpl implements RouteNodeService {

	protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());
	
	public static final String REVOKED_NODE_INSTANCES_STATE_KEY = "NodeInstances.Revoked";

	private static final Comparator NODE_INSTANCE_FORWARD_SORT = new NodeInstanceIdSorter();
	private static final Comparator NODE_INSTANCE_BACKWARD_SORT = 
		ComparatorUtils.reversedComparator(NODE_INSTANCE_FORWARD_SORT);
    private RouteHelper helper = new RouteHelper();
	private RouteNodeDAO routeNodeDAO;
	
    public void save(RouteNode node) {
    	routeNodeDAO.save(node);
    }
    
    public void save(RouteNodeInstance nodeInstance) {
    	routeNodeDAO.save(nodeInstance);
    }
    
    public void save(NodeState nodeState) {
        routeNodeDAO.save(nodeState);
    }
    
    public void save(Branch branch) {
        routeNodeDAO.save(branch);
    }

    public RouteNode findRouteNodeById(Long nodeId) {
    	return routeNodeDAO.findRouteNodeById(nodeId);
    }
    
    public RouteNodeInstance findRouteNodeInstanceById(Long nodeInstanceId) {
    	return routeNodeDAO.findRouteNodeInstanceById(nodeInstanceId);
    }

    public List getCurrentNodeInstances(Long documentId) {
        List currentNodeInstances = getActiveNodeInstances(documentId);
        if (currentNodeInstances.isEmpty()) {
            currentNodeInstances = getTerminalNodeInstances(documentId);
        }
        return currentNodeInstances;
    }
    
    public List getActiveNodeInstances(Long documentId) {
    	return routeNodeDAO.getActiveNodeInstances(documentId);
    }
    
    public List getActiveNodeInstances(DocumentRouteHeaderValue document) {
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
    
    public List getTerminalNodeInstances(Long documentId) {
        return routeNodeDAO.getTerminalNodeInstances(documentId);
    }
    
    public List getInitialNodeInstances(Long documentId) {
    	return routeNodeDAO.getInitialNodeInstances(documentId);
    }
    
    public NodeState findNodeState(Long nodeInstanceId, String key) {
        return routeNodeDAO.findNodeState(nodeInstanceId, key);
    }
    
    public RouteNode findRouteNodeByName(Long documentTypeId, String name) {
        return routeNodeDAO.findRouteNodeByName(documentTypeId, name);
    }
    
    public List findFinalApprovalRouteNodes(Long documentTypeId) {
        DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findById(documentTypeId);
        documentType = documentType.getRouteDefiningDocumentType();
        return routeNodeDAO.findFinalApprovalRouteNodes(documentType.getDocumentTypeId());
    }
    
    public List findNextRouteNodesInPath(RouteNodeInstance nodeInstance, String nodeName) {
        List nodesInPath = new ArrayList();
        for (Iterator iterator = nodeInstance.getRouteNode().getNextNodes().iterator(); iterator.hasNext();) {
            RouteNode nextNode = (RouteNode) iterator.next();
            nodesInPath.addAll(findNextRouteNodesInPath(nodeName, nextNode, new HashSet()));
        }
        return nodesInPath;
    }
    
    private List findNextRouteNodesInPath(String nodeName, RouteNode node, Set inspected) {
        List nextNodesInPath = new ArrayList();
        if (inspected.contains(node.getRouteNodeId())) {
            return nextNodesInPath;
        }
        inspected.add(node.getRouteNodeId());
        if (node.getRouteNodeName().equals(nodeName)) {
            nextNodesInPath.add(node);
        } else {
            if (helper.isSubProcessNode(node)) {
                Process subProcess = node.getDocumentType().getNamedProcess(node.getRouteNodeName());
                RouteNode subNode = subProcess.getInitialRouteNode();
                nextNodesInPath.addAll(findNextRouteNodesInPath(nodeName, subNode, inspected));
            }
            for (Iterator iterator = node.getNextNodes().iterator(); iterator.hasNext();) {
                RouteNode nextNode = (RouteNode) iterator.next();
                nextNodesInPath.addAll(findNextRouteNodesInPath(nodeName, nextNode, inspected));
            }
        }
        return nextNodesInPath;
    }
    
    public boolean isNodeInPath(DocumentRouteHeaderValue document, String nodeName) {
        boolean isInPath = false;
        Collection activeNodes = getActiveNodeInstances(document.getRouteHeaderId());
        for (Iterator iterator = activeNodes.iterator(); iterator.hasNext();) {
            RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
            List nextNodesInPath = findNextRouteNodesInPath(nodeInstance, nodeName);
            isInPath = isInPath || !nextNodesInPath.isEmpty();
        }
        return isInPath;
    }
    
    public List findRouteNodeInstances(Long documentId) {
        return this.routeNodeDAO.findRouteNodeInstances(documentId);
    }
    
	public void setRouteNodeDAO(RouteNodeDAO dao) {
		this.routeNodeDAO = dao;
	}
    
    public List findProcessNodeInstances(RouteNodeInstance process) {
       return this.routeNodeDAO.findProcessNodeInstances(process);
    }
    
    public Set findPreviousNodeNames(Long documentId) {
        List currentNodeInstances = KEWServiceLocator.getRouteNodeService().getCurrentNodeInstances(documentId);
        List nodeInstances = new ArrayList();
        for (Iterator iterator = currentNodeInstances.iterator(); iterator.hasNext();) {
            RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
            nodeInstances.addAll(nodeInstance.getPreviousNodeInstances());
        }
        Set nodeNames = new HashSet();
        while (!nodeInstances.isEmpty()) {
            RouteNodeInstance nodeInstance = (RouteNodeInstance)nodeInstances.remove(0);
            nodeNames.add(nodeInstance.getName());
            nodeInstances.addAll(nodeInstance.getPreviousNodeInstances());
        }
        return nodeNames;
    }
    
    public Set findFutureNodeNames(Long documentId) {
        List currentNodeInstances = KEWServiceLocator.getRouteNodeService().getCurrentNodeInstances(documentId);
        List nodes = new ArrayList();
        for (Iterator iterator = currentNodeInstances.iterator(); iterator.hasNext();) {
            RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
            nodes.addAll(nodeInstance.getRouteNode().getNextNodes());
        }
        Set nodeNames = new HashSet();
        while (!nodes.isEmpty()) {
            RouteNode node = (RouteNode)nodes.remove(0);
            nodeNames.add(node.getRouteNodeName());
            nodes.addAll(node.getNextNodes());
        }
        return nodeNames;
    }
    
    public List getFlattenedNodes(DocumentType documentType, boolean climbHierarchy) {
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
    
    public List getFlattenedNodes(Process process) {
        Map nodesMap = new HashMap();
        flattenNodeGraph(nodesMap, process.getInitialRouteNode());
        List nodes = new ArrayList(nodesMap.values());
        Collections.sort(nodes, new RouteNodeSorter());
        return nodes;
    }
    
    /**
     * Recursively walks the node graph and builds up the map.  Uses a map because we will
     * end up walking through duplicates, as is the case with Join nodes.
     */
    private void flattenNodeGraph(Map nodes, RouteNode node) {
        if (nodes.containsKey(node.getRouteNodeName())) {
            return;
        }
        nodes.put(node.getRouteNodeName(), node);
        for (Iterator iterator = node.getNextNodes().iterator(); iterator.hasNext();) {
            RouteNode nextNode = (RouteNode) iterator.next();
            flattenNodeGraph(nodes, nextNode);
        }
    }
    
    public List getFlattenedNodeInstances(DocumentRouteHeaderValue document, boolean includeProcesses) {
        List nodeInstances = new ArrayList();
        Set visitedNodeInstanceIds = new HashSet();
        for (Iterator iterator = document.getInitialRouteNodeInstances().iterator(); iterator.hasNext();) {
            RouteNodeInstance initialNodeInstance = (RouteNodeInstance) iterator.next();
            flattenNodeInstanceGraph(nodeInstances, visitedNodeInstanceIds, initialNodeInstance, includeProcesses);    
        }
        return nodeInstances;
    }
    
    private void flattenNodeInstanceGraph(List nodeInstances, Set visitedNodeInstanceIds, RouteNodeInstance nodeInstance, boolean includeProcesses) {
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
    
    public NodeGraphSearchResult searchNodeGraph(NodeGraphSearchCriteria criteria) {
    	NodeGraphContext context = new NodeGraphContext();
    	if (criteria.getSearchDirection() == NodeGraphSearchCriteria.SEARCH_DIRECTION_BACKWARD) {
        	searchNodeGraphBackward(context, criteria.getMatcher(), null, criteria.getStartingNodeInstances());
    	} else {
    		throw new UnsupportedOperationException("Search feature can only search backward currently.");
    	}
    	List exactPath = determineExactPath(context, criteria.getSearchDirection(), criteria.getStartingNodeInstances());
        return new NodeGraphSearchResult(context.getCurrentNodeInstance(), exactPath);
    }
    
    private void searchNodeGraphBackward(NodeGraphContext context, NodeMatcher matcher, RouteNodeInstance previousNodeInstance, Collection nodeInstances) {
        if (nodeInstances == null) {
            return;
        }
    	for (Iterator iterator = nodeInstances.iterator(); iterator.hasNext();) {
            RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
            context.setPreviousNodeInstance(previousNodeInstance);
            context.setCurrentNodeInstance(nodeInstance);
            searchNodeGraphBackward(context, matcher);
            if (context.getResultNodeInstance() != null) {
            	// we've located the node instance we're searching for, we're done
            	break;
            }
        }
    }
    
    private void searchNodeGraphBackward(NodeGraphContext context, NodeMatcher matcher) {
        RouteNodeInstance current = context.getCurrentNodeInstance();
        int numBranches = current.getNextNodeInstances().size();
        // if this is a split node, we want to wait here, until all branches join back to us
        if (numBranches > 1) {
        	// determine the number of branches that have joined back to the split thus far
            Integer joinCount = (Integer)context.getSplitState().get(current.getRouteNodeInstanceId());
            if (joinCount == null) {
                joinCount = new Integer(0);
            }
            // if this split is not a leaf node we increment the count
            if (context.getPreviousNodeInstance() != null) {
                joinCount = new Integer(joinCount.intValue()+1);
            }
            context.getSplitState().put(current.getRouteNodeInstanceId(), joinCount);
            // if not all branches have joined, stop and wait for other branches to join
            if (joinCount.intValue() != numBranches) {
                return;
            }
        }
        if (matcher.isMatch(context)) {
            context.setResultNodeInstance(current);
        } else {
            context.getVisited().put(current.getRouteNodeInstanceId(), current);
            searchNodeGraphBackward(context, matcher, current, current.getPreviousNodeInstances());
        }
    }
    
    public List getActiveNodeInstances(DocumentRouteHeaderValue document, String nodeName) {
		Collection activeNodes = getActiveNodeInstances(document.getRouteHeaderId());
		List foundNodes = new ArrayList();
        for (Iterator iterator = activeNodes.iterator(); iterator.hasNext();) {
            RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
            if (nodeInstance.getName().equals(nodeName)) {
            	foundNodes.add(nodeInstance);
            }
        }
        return foundNodes;
    }
    
        private List determineExactPath(NodeGraphContext context, int searchDirection, Collection startingNodeInstances) {
    	List exactPath = new ArrayList();
    	if (context.getResultNodeInstance() == null) {
    		exactPath.addAll(context.getVisited().values());
    	} else {
    		determineExactPath(exactPath, new HashMap(), startingNodeInstances, context.getResultNodeInstance());
    	}
    	if (NodeGraphSearchCriteria.SEARCH_DIRECTION_FORWARD == searchDirection) {
    		Collections.sort(exactPath, NODE_INSTANCE_BACKWARD_SORT);
    	} else {
    		Collections.sort(exactPath, NODE_INSTANCE_FORWARD_SORT);
    	}
    	return exactPath;
    }
    
    private void determineExactPath(List exactPath, Map visited, Collection startingNodeInstances, RouteNodeInstance nodeInstance) {
    	if (nodeInstance == null) {
    		return;
    	}
    	if (visited.containsKey(nodeInstance.getRouteNodeInstanceId())) {
    		return;
    	}
    	visited.put(nodeInstance.getRouteNodeInstanceId(), nodeInstance);
    	exactPath.add(nodeInstance);
    	for (Iterator iterator = startingNodeInstances.iterator(); iterator.hasNext(); ) {
			RouteNodeInstance startingNode = (RouteNodeInstance) iterator.next();
			if (startingNode.getRouteNodeInstanceId().equals(nodeInstance.getRouteNodeInstanceId())) {
				return;
			}
		}
    	for (Iterator iterator = nodeInstance.getNextNodeInstances().iterator(); iterator.hasNext(); ) {
			RouteNodeInstance nextNodeInstance = (RouteNodeInstance) iterator.next();
			determineExactPath(exactPath, visited, startingNodeInstances, nextNodeInstance);
		}
    }
    
    
    /**
     * Sorts by RouteNodeId or the order the nodes will be evaluated in *roughly*.  This is 
     * for display purposes when rendering a flattened list of nodes.
     * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
     */
    private static class RouteNodeSorter implements Comparator {
        public int compare(Object arg0, Object arg1) {
            RouteNode rn1 = (RouteNode)arg0;
            RouteNode rn2 = (RouteNode)arg1;
            return rn1.getRouteNodeId().compareTo(rn2.getRouteNodeId());
        }
    }
    
    private static class NodeInstanceIdSorter implements Comparator {
        public int compare(Object arg0, Object arg1) {
            RouteNodeInstance nodeInstance1 = (RouteNodeInstance)arg0;
            RouteNodeInstance nodeInstance2 = (RouteNodeInstance)arg1;
            return nodeInstance1.getRouteNodeInstanceId().compareTo(nodeInstance2.getRouteNodeInstanceId());
        }
    }
    
    
    public void deleteByRouteNodeInstance(RouteNodeInstance routeNodeInstance){
    	//update the route node instance link table to cancel the relationship between the to-be-deleted instance and the previous node instances
    	routeNodeDAO.deleteLinksToPreNodeInstances(routeNodeInstance);
    	//delete the routeNodeInstance and its next node instances
    	routeNodeDAO.deleteRouteNodeInstancesHereAfter(routeNodeInstance);
    }
    
    public void deleteNodeStateById(Long nodeStateId){
    	routeNodeDAO.deleteNodeStateById(nodeStateId);
    }
    
    public void deleteNodeStates(List statesToBeDeleted){
    	routeNodeDAO.deleteNodeStates(statesToBeDeleted);
    }
    
    /**
     * Records the revocation in the root BranchState of the document.
     */
    public void revokeNodeInstance(DocumentRouteHeaderValue document, RouteNodeInstance nodeInstance) {
    	if (document == null) {
    		throw new IllegalArgumentException("Document must not be null.");
    	}
		if (nodeInstance == null || nodeInstance.getRouteNodeInstanceId() == null) {
			throw new IllegalArgumentException("In order to revoke a final approval node the node instance must be persisent and have an id.");
		}
		// get the initial node instance, the root branch is where we will store the state
    	RouteNodeInstance initialInstance = (RouteNodeInstance)document.getInitialRouteNodeInstance(0);
    	Branch rootBranch = initialInstance.getBranch();
    	BranchState state = rootBranch.getBranchState(REVOKED_NODE_INSTANCES_STATE_KEY);
    	if (state == null) {
    		state = new BranchState(REVOKED_NODE_INSTANCES_STATE_KEY, "");
    		rootBranch.addBranchState(state);
    	}
    	if (state.getValue() == null) {
    		state.setValue("");
    	}
    	state.setValue(state.getValue() + nodeInstance.getRouteNodeInstanceId() + ",");
    	save(rootBranch);
	}

    /**
     * Queries the list of revoked node instances from the root BranchState of the Document
     * and returns a List of revoked RouteNodeInstances.
     */
	public List getRevokedNodeInstances(DocumentRouteHeaderValue document) {
		if (document == null) {
    		throw new IllegalArgumentException("Document must not be null.");
    	}
		List revokedNodeInstances = new ArrayList();
    	RouteNodeInstance initialInstance = (RouteNodeInstance)document.getInitialRouteNodeInstance(0);
    	Branch rootBranch = initialInstance.getBranch();
    	BranchState state = rootBranch.getBranchState(REVOKED_NODE_INSTANCES_STATE_KEY);
    	if (state == null || Utilities.isEmpty(state.getValue())) {
    		return revokedNodeInstances;
    	}
    	String[] revokedNodes = state.getValue().split(",");
    	for (int index = 0; index < revokedNodes.length; index++) {
			String revokedNodeInstanceIdValue = revokedNodes[index];
			Long revokedNodeInstanceId = Long.valueOf(revokedNodeInstanceIdValue);
			RouteNodeInstance revokedNodeInstance = findRouteNodeInstanceById(revokedNodeInstanceId);
			if (revokedNodeInstance == null) {
				LOG.warn("Could not locate revoked RouteNodeInstance with the given id: " + revokedNodeInstanceId);
			} else {
				revokedNodeInstances.add(revokedNodeInstance);
			}
		}
    	return revokedNodeInstances;
	}
    
    
}
