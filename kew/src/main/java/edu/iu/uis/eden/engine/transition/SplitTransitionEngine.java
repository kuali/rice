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
import edu.iu.uis.eden.engine.node.Branch;
import edu.iu.uis.eden.engine.node.ProcessResult;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.engine.node.SplitNode;
import edu.iu.uis.eden.engine.node.SplitResult;

/**
 * Handles transitions into and out of {@link SplitNode} nodes.
 * 
 * @see SplitNode
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SplitTransitionEngine extends TransitionEngine {
	
    public ProcessResult isComplete(RouteContext context) throws Exception {
        SplitNode node = (SplitNode)getNode(context.getNodeInstance().getRouteNode(), SplitNode.class);
        return node.process(context, getRouteHelper());
    }
    
	public Transition transitionFrom(RouteContext context, ProcessResult processResult)
			throws Exception {
		RouteNodeInstance splitInstance = context.getNodeInstance();
		List nextNodeInstances = new ArrayList();
        SplitResult result = (SplitResult)processResult;
		for (Iterator iterator = result.getBranchNames().iterator(); iterator.hasNext(); ) {
			String branchName = (String)iterator.next();
			for (Iterator nodeIt = splitInstance.getRouteNode().getNextNodes().iterator(); nodeIt.hasNext(); ) {
				RouteNode routeNode = (RouteNode) nodeIt.next();
				if (routeNode.getBranch() != null && routeNode.getBranch().getName().equals(branchName)) {
				    nextNodeInstances.add(createSplitChild(branchName, routeNode, splitInstance));
				}
			}
		}
		return new Transition(nextNodeInstances);
	}
	
	public static RouteNodeInstance createSplitChild(String branchName, RouteNode routeNode, RouteNodeInstance splitInstance) {
	    RouteHelper routeHelper = new RouteHelper();
	    RouteNodeInstance nextNodeInstance = routeHelper.getNodeFactory().createRouteNodeInstance(splitInstance.getDocumentId(), routeNode);
		Branch branch = routeHelper.getNodeFactory().createBranch(branchName, splitInstance.getBranch(), nextNodeInstance);
		branch.setSplitNode(splitInstance);
		nextNodeInstance.setBranch(branch);
		nextNodeInstance.setProcess(splitInstance.getProcess());
		return nextNodeInstance;
	}
	
}
