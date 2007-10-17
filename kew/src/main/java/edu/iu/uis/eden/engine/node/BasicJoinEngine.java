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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.util.Utilities;

/**
 * A basic implementation of the JoinEngine which handles join setup and makes determinations
 * as to when a join condition has been satisfied.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BasicJoinEngine implements JoinEngine {

    public static final String EXPECTED_JOINERS = "ExpectedJoiners";
    public static final String ACTUAL_JOINERS = "ActualJoiners";
    
    public void createExpectedJoinState(RouteContext context, RouteNodeInstance joinInstance, RouteNodeInstance previousNodeInstance) {
        RouteNodeInstance splitNode = previousNodeInstance.getBranch().getSplitNode();
        if (splitNode == null) {
            throw new WorkflowRuntimeException("The split node retrieved from node with name '" + previousNodeInstance.getName() + "' and branch with name '" + previousNodeInstance.getBranch().getName() + "' was null");
        }
        for (Iterator iter = splitNode.getNextNodeInstances().iterator(); iter.hasNext();) {
            RouteNodeInstance splitNodeNextNode = (RouteNodeInstance) iter.next();
            splitNodeNextNode.getBranch().setJoinNode(joinInstance);
            saveBranch(context, splitNodeNextNode.getBranch());
            addExpectedJoiner(joinInstance, splitNodeNextNode.getBranch());
        }
        joinInstance.setBranch(splitNode.getBranch());
        joinInstance.setProcess(splitNode.getProcess());
    }
    
    public void addExpectedJoiner(RouteNodeInstance nodeInstance, Branch branch) {
        addJoinState(nodeInstance, branch, EXPECTED_JOINERS);
    }

    public void addActualJoiner(RouteNodeInstance nodeInstance, Branch branch) {
        addJoinState(nodeInstance, branch, ACTUAL_JOINERS);
    }
    
    private void addJoinState(RouteNodeInstance nodeInstance, Branch branch, String key) {
        NodeState state = nodeInstance.getNodeState(key);
        if (state == null) {
            state = new NodeState();
            state.setKey(key);
            state.setValue("");
            state.setNodeInstance(nodeInstance);
            nodeInstance.addNodeState(state);
        }
        state.setValue(state.getValue()+branch.getBranchId()+",");
    }

    public boolean isJoined(RouteNodeInstance nodeInstance) {
        NodeState expectedState = nodeInstance.getNodeState(EXPECTED_JOINERS);
        if (expectedState == null || Utilities.isEmpty(expectedState.getValue())) {
            return true;
        }
        NodeState actualState = nodeInstance.getNodeState(ACTUAL_JOINERS);
        Set expectedSet = loadIntoSet(expectedState);
        Set actualSet = loadIntoSet(actualState);
        for (Iterator iterator = expectedSet.iterator(); iterator.hasNext();) {
            String value = (String) iterator.next();
            if (actualSet.contains(value)) {
                iterator.remove();
            }            
        }
        return expectedSet.size() == 0;
    }
    
    private Set loadIntoSet(NodeState state) {
        Set set = new HashSet();
        StringTokenizer tokenizer = new StringTokenizer(state.getValue(), ",");
        while (tokenizer.hasMoreTokens()) {
            set.add(tokenizer.nextToken());
        }
        return set;
    }
    
    private void saveBranch(RouteContext context, Branch branch) {
        if (!context.isSimulation()) {
            KEWServiceLocator.getRouteNodeService().save(branch);
        }
    }

}
