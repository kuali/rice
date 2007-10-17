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
package edu.iu.uis.eden.clientapp.vo;

import java.util.HashMap;
import java.util.Map;

import edu.iu.uis.eden.clientapp.WorkflowInfo;

/**
 * Document plus it's requests and actions.  Primarily used by the routingReport method 
 * on the {@link WorkflowInfo}
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 * @workflow.webservice-object
 */
public class DocumentDetailVO extends RouteHeaderVO {

	private static final long serialVersionUID = -6089529693944755804L;
	
	private ActionRequestVO[] actionRequests = new ActionRequestVO[0];
	private ActionTakenVO[] actionsTaken = new ActionTakenVO[0];
    private RouteNodeInstanceVO[] nodeInstances = new RouteNodeInstanceVO[0];

    private Map nodeInstanceMap = null;
    
	public ActionRequestVO[] getActionRequests() {
		return actionRequests;
	}

	public void setActionRequests(ActionRequestVO[] actionRequests) {
		this.actionRequests = actionRequests;
	}

	public ActionTakenVO[] getActionsTaken() {
		return actionsTaken;
	}

	public void setActionsTaken(ActionTakenVO[] actionsTaken) {
		this.actionsTaken = actionsTaken;
	}
    
    public RouteNodeInstanceVO[] getNodeInstances() {
        return nodeInstances;
    }

    public void setNodeInstances(RouteNodeInstanceVO[] nodeInstances) {
        this.nodeInstances = nodeInstances;
    }

    public RouteNodeInstanceVO getNodeInstance(Long nodeInstanceId) {
        if (nodeInstanceMap == null) {
            populateNodeInstanceMap();
        }
        return (RouteNodeInstanceVO)nodeInstanceMap.get(nodeInstanceId);
    }
    
    private void populateNodeInstanceMap() {
        nodeInstanceMap = new HashMap();
        for (int index = 0; index < nodeInstances.length; index++) {
            RouteNodeInstanceVO nodeInstance = nodeInstances[index];
            nodeInstanceMap.put(nodeInstance.getRouteNodeInstanceId(), nodeInstance);
        }
    }
    
    

}
