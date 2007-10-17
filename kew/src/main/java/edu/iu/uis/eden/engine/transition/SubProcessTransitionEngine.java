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
import edu.iu.uis.eden.engine.node.Process;
import edu.iu.uis.eden.engine.node.ProcessResult;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.engine.node.SubProcessNode;
import edu.iu.uis.eden.engine.node.SubProcessResult;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Handles transitions into and out of {@link SubProcessNode} nodes.
 * 
 * @see SubProcessNode
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SubProcessTransitionEngine extends TransitionEngine {
    
    public RouteNodeInstance transitionTo(RouteNodeInstance nextNodeInstance, RouteContext context) throws Exception {
        String processName = nextNodeInstance.getRouteNode().getRouteNodeName();
        Process process = context.getDocument().getDocumentType().getNamedProcess(processName);
        if (process == null) {
            throw new WorkflowException("Could not locate named sub process: " + processName);
        }
        RouteNodeInstance subProcessNodeInstance = nextNodeInstance;
        subProcessNodeInstance.setInitial(false);
        subProcessNodeInstance.setActive(false);
        nextNodeInstance = getRouteHelper().getNodeFactory().createRouteNodeInstance(subProcessNodeInstance.getDocumentId(), process.getInitialRouteNode());
        nextNodeInstance.setBranch(subProcessNodeInstance.getBranch());
        nextNodeInstance.setProcess(subProcessNodeInstance);
	    return nextNodeInstance;
	}

	public ProcessResult isComplete(RouteContext context) throws Exception {
        throw new UnsupportedOperationException("isComplete() should not be invoked on a SubProcess!");
    }

    public Transition transitionFrom(RouteContext context, ProcessResult processResult) throws Exception {
		RouteNodeInstance processInstance = context.getNodeInstance().getProcess();
        processInstance.setComplete(true);
		SubProcessNode node = (SubProcessNode)getNode(processInstance.getRouteNode(), SubProcessNode.class);
		SubProcessResult result = node.process(context);
		List nextNodeInstances = new ArrayList();
		if (result.isComplete()) {
			List nextNodes = processInstance.getRouteNode().getNextNodes();
	        for (Iterator iterator = nextNodes.iterator(); iterator.hasNext();) {
	            RouteNode nextNode = (RouteNode) iterator.next();
	            RouteNodeInstance nextNodeInstance = getRouteHelper().getNodeFactory().createRouteNodeInstance(processInstance.getDocumentId(), nextNode);
	            nextNodeInstance.setBranch(processInstance.getBranch());
	            nextNodeInstance.setProcess(processInstance.getProcess());
	            nextNodeInstances.add(nextNodeInstance);
	        }
		}
		return new Transition(nextNodeInstances);
	}

}
