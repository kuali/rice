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

import java.io.Serializable;

import edu.iu.uis.eden.engine.node.Process;

/**
 * Transport object for a {@link Process}.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 * @workflow.webservice-object
 */
public class ProcessVO implements Serializable {

    private static final long serialVersionUID = 1802130981664121439L;
    
    private Long processId;
    private String name;
    private RouteNodeVO initialRouteNode;
    private boolean initial = false;
    private RouteNodeVO[] nodes = new RouteNodeVO[0];
    
    public boolean isInitial() {
        return initial;
    }
    public void setInitial(boolean initial) {
        this.initial = initial;
    }
    public RouteNodeVO getInitialRouteNode() {
        return initialRouteNode;
    }
    public void setInitialRouteNode(RouteNodeVO initialRouteNode) {
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
    public RouteNodeVO getRouteNode(Long routeNodeId) {
        for (int index = 0; index < nodes.length; index++) {
            RouteNodeVO node = nodes[index];
            if (node.getRouteNodeId().equals(routeNodeId)) {
                return node;
            }
        }
        return null;
    }
    
    
}
