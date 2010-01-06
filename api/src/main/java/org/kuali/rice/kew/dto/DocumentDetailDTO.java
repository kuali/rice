/*
 * Copyright 2005-2009 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Document plus it's requests and actions.  Primarily used by the routingReport method 
 * on WorkflowInfo
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentDetailDTO extends RouteHeaderDTO {

	private static final long serialVersionUID = -6089529693944755804L;
	
	private ActionRequestDTO[] actionRequests = new ActionRequestDTO[0];
	private ActionTakenDTO[] actionsTaken = new ActionTakenDTO[0];
    private RouteNodeInstanceDTO[] nodeInstances = new RouteNodeInstanceDTO[0];

    private Map nodeInstanceMap = null;
    
	public ActionRequestDTO[] getActionRequests() {
		return actionRequests;
	}

	public void setActionRequests(ActionRequestDTO[] actionRequests) {
		this.actionRequests = actionRequests;
	}

	public ActionTakenDTO[] getActionsTaken() {
		return actionsTaken;
	}

	public void setActionsTaken(ActionTakenDTO[] actionsTaken) {
		this.actionsTaken = actionsTaken;
	}
    
    public RouteNodeInstanceDTO[] getNodeInstances() {
        return nodeInstances;
    }

    public void setNodeInstances(RouteNodeInstanceDTO[] nodeInstances) {
        this.nodeInstances = nodeInstances;
    }

    public RouteNodeInstanceDTO getNodeInstance(Long nodeInstanceId) {
        if (nodeInstanceMap == null) {
            populateNodeInstanceMap();
        }
        return (RouteNodeInstanceDTO)nodeInstanceMap.get(nodeInstanceId);
    }
    
    private void populateNodeInstanceMap() {
        nodeInstanceMap = new HashMap();
        for (int index = 0; index < nodeInstances.length; index++) {
            RouteNodeInstanceDTO nodeInstance = nodeInstances[index];
            nodeInstanceMap.put(nodeInstance.getRouteNodeInstanceId(), nodeInstance);
        }
    }
    
    

}
