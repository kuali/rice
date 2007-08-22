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
package edu.iu.uis.eden.engine.node.dao;

import java.util.List;

import edu.iu.uis.eden.engine.node.Branch;
import edu.iu.uis.eden.engine.node.NodeState;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;


public interface RouteNodeDAO {

    public void save(RouteNode node);
    public void save(RouteNodeInstance nodeInstance);
    public void save(NodeState nodeState);
    public void save(Branch branch);
    public RouteNode findRouteNodeById(Long nodeId);
    public RouteNodeInstance findRouteNodeInstanceById(Long nodeInstanceId);
    public List getActiveNodeInstances(Long documentId);
    public List getTerminalNodeInstances(Long documentId);
    public List getInitialNodeInstances(Long documentId);
    public NodeState findNodeState(Long nodeInstanceId, String key);
    public RouteNode findRouteNodeByName(Long documentTypeId, String name);
    public List findFinalApprovalRouteNodes(Long documentTypeId);
    public List findProcessNodeInstances(RouteNodeInstance process);
    public List findRouteNodeInstances(Long documentId);
    public void deleteLinksToPreNodeInstances(RouteNodeInstance routeNodeInstance);
    public void deleteRouteNodeInstancesHereAfter(RouteNodeInstance routeNodeInstance);
    public void deleteNodeStateById(Long nodeStateId);
    public void deleteNodeStates(List statesToBeDeleted);
	
}
