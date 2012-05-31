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
 * Transport object for a Process.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ProcessDTO implements Serializable {

    private static final long serialVersionUID = 1802130981664121439L;
    
    private Long processId;
    private String name;
    private RouteNodeDTO initialRouteNode;
    private boolean initial = false;
   
    
    public boolean isInitial() {
        return initial;
    }
    public void setInitial(boolean initial) {
        this.initial = initial;
    }
    public RouteNodeDTO getInitialRouteNode() {
        return initialRouteNode;
    }
    public void setInitialRouteNode(RouteNodeDTO initialRouteNode) {
        this.initialRouteNode = initialRouteNode;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getProcessId() {
        return processId;
    }
    public void setProcessId(Long processId) {
        this.processId = processId;
    }
    
    
    
}
