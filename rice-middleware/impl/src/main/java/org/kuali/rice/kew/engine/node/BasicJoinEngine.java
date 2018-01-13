/**
 * Copyright 2005-2018 The Kuali Foundation
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

import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.krad.util.LegacyUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * A basic implementation of the JoinEngine which handles join setup and makes determinations
 * as to when a join condition has been satisfied.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BasicJoinEngine implements JoinEngine {

    public static final String EXPECTED_JOINERS = "ExpectedJoiners";
    public static final String ACTUAL_JOINERS = "ActualJoiners";
    
    public RouteNodeInstance createExpectedJoinState(RouteContext context, RouteNodeInstance joinInstance, RouteNodeInstance previousNodeInstance) {
        RouteNodeInstance splitNode = previousNodeInstance.getBranch().getSplitNode();
        if (splitNode == null) {
            throw new WorkflowRuntimeException("The split node retrieved from node with name '" + previousNodeInstance.getName() + "' and branch with name '" + previousNodeInstance.getBranch().getName() + "' was null");
        }
        for (Iterator iter = splitNode.getNextNodeInstances().iterator(); iter.hasNext();) {
            RouteNodeInstance splitNodeNextNode = (RouteNodeInstance) iter.next();
            joinInstance = saveNode(context, joinInstance);
            splitNodeNextNode.getBranch().setJoinNode(joinInstance);
            addExpectedJoiner(joinInstance, splitNodeNextNode.getBranch());
        }
        joinInstance.setBranch(splitNode.getBranch());
        joinInstance.setProcess(splitNode.getProcess());
        return joinInstance;
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
        if (expectedState == null || org.apache.commons.lang.StringUtils.isEmpty(expectedState.getValue())) {
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

    private RouteNodeInstance saveNode(RouteContext context, RouteNodeInstance nodeInstance) {
        if (!context.isSimulation()) {
            return KEWServiceLocator.getRouteNodeService().save(nodeInstance);
        } else {
            // if we are in simulation mode, lets go ahead and assign some id
            // values to our beans
            for (RouteNodeInstance routeNodeInstance : nodeInstance.getNextNodeInstances()) {
                if (routeNodeInstance.getRouteNodeInstanceId() == null) {
                    routeNodeInstance.setRouteNodeInstanceId(context.getEngineState().getNextSimulationId());
                }
            }
            if (nodeInstance.getProcess() != null && nodeInstance.getProcess().getRouteNodeInstanceId() == null) {
                nodeInstance.getProcess().setRouteNodeInstanceId(context.getEngineState().getNextSimulationId());
            }
            if (nodeInstance.getBranch() != null && nodeInstance.getBranch().getBranchId() == null) {
                nodeInstance.getBranch().setBranchId(context.getEngineState().getNextSimulationId());
            }
            return nodeInstance;
        }
    }

}
