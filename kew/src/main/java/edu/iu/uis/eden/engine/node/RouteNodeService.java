/*
 * Copyright 2005-2006 The Kuali Foundation.
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

import java.util.List;
import java.util.Set;

import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;

/**
 * A service which provides data access for {@link RouteNode}, {@link RouteNodeInstance}, 
 * {@link NodeState}, and {@link Branch} objects.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface RouteNodeService {

    public void save(RouteNode node);
    public void save(RouteNodeInstance nodeInstance);
    public void save(NodeState nodeState);
    public void save(Branch branch);
    public RouteNode findRouteNodeById(Long nodeId);
    public RouteNodeInstance findRouteNodeInstanceById(Long nodeInstanceId);

    /**
     * Retrieves the initial node instances of the given document.  The initial node instances are 
     * those node instances which are at the very beginning of the route.  Usually, this will
     * just be a single node instance.
     */
    public List getInitialNodeInstances(Long documentId);

    /**
     * Retrieves the active node instances of the given Document.  The active node instances
     * represent where in the route path the document is currently located.
     */
    public List getActiveNodeInstances(Long documentId);
    
    public List getActiveNodeInstances(DocumentRouteHeaderValue document);
    
    /**
     * Retrieves the terminal node instances of the given Document.  The terminal node instances
     * are nodes in the route path which are both inactive and complete and have no next nodes
     * in their path.  Terminal node instances will typically only exist on documents which are no
     * longer Enroute.
     */
    public List getTerminalNodeInstances(Long documentId);
    
    /**
     * Returns the node instances representing the most recent node instances in the document.
     * The algorithm for locating the current nodes is as follows: If the document has
     * active node instances, return those, otherwise return it's terminal node instances.
     */
    public List getCurrentNodeInstances(Long documentId);

    public NodeState findNodeState(Long nodeInstanceId, String key);
    public RouteNode findRouteNodeByName(Long documentTypeId, String name);
    public List findFinalApprovalRouteNodes(Long documentTypeId);
    public List findNextRouteNodesInPath(RouteNodeInstance nodeInstance, String nodeName);
    public boolean isNodeInPath(DocumentRouteHeaderValue document, String nodeName);
    public List findRouteNodeInstances(Long documentId);
    public List findProcessNodeInstances(RouteNodeInstance process);
    public Set findPreviousNodeNames(Long documentId);
    public Set findFutureNodeNames(Long documentId);
    
    /**
     * Flatten all the document types route nodes into a single List.  This includes all processes 
     * on the DocumentType.
     * 
     * @param documentType DocumentType who's nodes will be flattened.
     * @param climbHierarchy whether to include the parents nodes if the passed in DocumentType contains no nodes
     * @return List or empty List
     */
    public List getFlattenedNodes(DocumentType documentType, boolean climbHierarchy);
    public List getFlattenedNodes(Process process);
    
    /**
     * Returns a flattened list of RouteNodeInstances on the given document.  If the includeProcesses flag is
     * true than this method includes process RouteNodeInstances, otherwise they are excluded.
     * which are processes.
     */
    public List getFlattenedNodeInstances(DocumentRouteHeaderValue document, boolean includeProcesses);
    
    public NodeGraphSearchResult searchNodeGraph(NodeGraphSearchCriteria criteria);
    
    /**
     * Returns a list of active node instances associated with the document that are active
     * @param document
     * @param nodeName
     * @return
     */
    public List getActiveNodeInstances(DocumentRouteHeaderValue document, String nodeName);
        public void deleteByRouteNodeInstance(RouteNodeInstance routeNodeInstance);
    public void deleteNodeStateById(Long nodeStateId);
    public void deleteNodeStates(List statesToBeDeleted);
    
    /**
	 * Record that the given RouteNodeInstance on the Document was revoked.  This will happen when an 
	 * action such as Return to Previous or Move Document bypasses the given RouteNodeInstance on it's
	 * path back to a previous point in the history of the document's route path. 
	 */
    public void revokeNodeInstance(DocumentRouteHeaderValue document, RouteNodeInstance nodeInstance);
    
    /**
     * Returns a List of the revoked RouteNodeInstances on the given Document.
     * 
     * @see revokeNodeInstance
     */
    public List getRevokedNodeInstances(DocumentRouteHeaderValue document);
    
}
