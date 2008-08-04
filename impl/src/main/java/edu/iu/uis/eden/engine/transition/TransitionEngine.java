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
package edu.iu.uis.eden.engine.transition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.RouteHelper;
import edu.iu.uis.eden.engine.node.Node;
import edu.iu.uis.eden.engine.node.ProcessResult;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;

/**
 * Common superclass for all Transition Engines.  A TransitionEngine handles transitioning into and out of
 * a {@link RouteNodeInstance}.  The TransitionEngine is also responsible for determining if a Node has completed.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class TransitionEngine {
    
	private RouteHelper helper;
	
	public RouteNodeInstance transitionTo(RouteNodeInstance nextNodeInstance, RouteContext context) throws Exception {
		return nextNodeInstance;
	}
    
    /**
     * Tell the WorkflowEngine processing the activeNodeInstance if the node is complete and transitionFrom can 
     * be called.
     * 
     * @param activeNodeInstance
     * @return boolean
     */
    public abstract ProcessResult isComplete(RouteContext context) throws Exception;
	
    public Transition transitionFrom(RouteContext context, ProcessResult processResult) throws Exception {
        return new Transition(resolveNextNodeInstances(context.getNodeInstance()));
    }
    
    protected void setRouteHelper(RouteHelper helper) {
    	this.helper = helper;
    }
    
    protected RouteHelper getRouteHelper() {
    	return helper;
    }
    
    protected Node getNode(RouteNode routeNode, Class nodeClass) throws Exception {
		return helper.getNode(routeNode);
    }
    
    /**
     * Determines the next nodes instances for the transition.  If the node instance already
     * has next nodes instances (i.e. a dynamic node), then those will be returned.  Otherwise
     * it will resolve the next nodes from the RouteNode prototype.
     */
    protected List resolveNextNodeInstances(RouteNodeInstance nodeInstance, List nextRouteNodes) {
        List nextNodeInstances = new ArrayList();
        for (Iterator iterator = nextRouteNodes.iterator(); iterator.hasNext();) {
            RouteNode nextNode = (RouteNode) iterator.next();
            RouteNodeInstance nextNodeInstance = getRouteHelper().getNodeFactory().createRouteNodeInstance(nodeInstance.getDocumentId(), nextNode);
            nextNodeInstance.setBranch(nodeInstance.getBranch());
            nextNodeInstance.setProcess(nodeInstance.getProcess());
            nextNodeInstances.add(nextNodeInstance);
        }
        return nextNodeInstances;
    }
    
    protected List resolveNextNodeInstances(RouteNodeInstance nodeInstance) {
        return resolveNextNodeInstances(nodeInstance, nodeInstance.getRouteNode().getNextNodes());
    }
    
}
