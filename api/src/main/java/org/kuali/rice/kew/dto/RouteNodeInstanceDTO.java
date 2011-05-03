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

import java.io.Serializable;

/**
 * Transport object representing a RouteNodeInstance.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RouteNodeInstanceDTO implements Serializable {

    private static final long serialVersionUID = -5456548621231617447L;
    
    private Long routeNodeInstanceId;
    private String documentId;
    private Long branchId;
    private Long routeNodeId;
    private Long processId;
    private String name;
    private boolean active;
    private boolean complete;
    private boolean initial;
    private StateDTO[] state = new StateDTO[0];
    private RouteNodeInstanceDTO[] nextNodes = new RouteNodeInstanceDTO[0];
    
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public Long getBranchId() {
        return branchId;
    }
    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }
    public boolean isComplete() {
        return complete;
    }
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public boolean isInitial() {
        return initial;
    }
    public void setInitial(boolean initial) {
        this.initial = initial;
    }
    public Long getProcessId() {
        return processId;
    }
    public void setProcessId(Long processId) {
        this.processId = processId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getRouteNodeId() {
        return routeNodeId;
    }
    public void setRouteNodeId(Long routeNodeId) {
        this.routeNodeId = routeNodeId;
    }
    public Long getRouteNodeInstanceId() {
        return routeNodeInstanceId;
    }
    public void setRouteNodeInstanceId(Long routeNodeInstanceId) {
        this.routeNodeInstanceId = routeNodeInstanceId;
    }
    public StateDTO[] getState() {
        return state;
    }
    public void setState(StateDTO[] state) {
        this.state = state;
    }
    
    public StateDTO getState(String key) {
        for (int index = 0; index < getState().length; index++) {
            StateDTO nodeState = (StateDTO) getState()[index];
            if (nodeState.getKey().equals(key)) {
                return nodeState;
            }
        }
        return null;
    }
    
    public RouteNodeInstanceDTO[] getNextNodes() {
        return nextNodes;
    }
 
    public void setNextNodes(RouteNodeInstanceDTO[] nextNodes) {
        this.nextNodes = nextNodes;
    }
}
