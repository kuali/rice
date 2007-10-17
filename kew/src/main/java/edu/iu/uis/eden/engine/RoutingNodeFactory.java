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
package edu.iu.uis.eden.engine;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.engine.node.Branch;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;

/**
 * Provides factory methods for creating {@link Branch} objects and {@link RouteNodeInstance} object.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoutingNodeFactory {

    public Branch createBranch(String name, Branch parentBranch, RouteNodeInstance initialNodeInstance) {
        Branch branch = new Branch();
        branch.setName(name);
        branch.setParentBranch(parentBranch);
        branch.setInitialNode(initialNodeInstance);
        initialNodeInstance.setBranch(branch);
        return branch;
    }
    
    public RouteNodeInstance createRouteNodeInstance(Long documentId, RouteNode node) {
        RouteNodeInstance nodeInstance = new RouteNodeInstance();
        nodeInstance.setActive(false);
        nodeInstance.setComplete(false);
        nodeInstance.setRouteNode(node);
        nodeInstance.setDocumentId(documentId);
        return nodeInstance;
    }
    
    public RouteNode getRouteNode(RouteContext context, String name) {
        return KEWServiceLocator.getRouteNodeService().findRouteNodeByName(context.getDocument().getDocumentType().getDocumentTypeId(), name);
    }
    
}
