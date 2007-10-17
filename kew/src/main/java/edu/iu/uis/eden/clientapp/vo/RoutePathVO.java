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

/**
 * Transport object responsble for holding the process a document has been through.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 * @workflow.webservice-object
 */
public class RoutePathVO implements Serializable {

    private static final long serialVersionUID = 2883128962971335387L;
    
    private ProcessVO[] processes = new ProcessVO[0];
    
    public ProcessVO[] getProcesses() {
        return processes;
    }

    public void setProcesses(ProcessVO[] processes) {
        this.processes = processes;
    }

    public ProcessVO getPrimaryProcess() {
        for (int index = 0; index < processes.length; index++) {
            ProcessVO process = processes[index];
            if (process.isInitial()) {
                return process;
            }
        }        
        return null;
    }
    
    public RouteNodeVO getRouteNode(Long routeNodeId) {
        for (int index = 0; index < processes.length; index++) {
            ProcessVO process = processes[index];
            RouteNodeVO node = process.getRouteNode(routeNodeId);
            if (node != null) {
                return node;
            }
        }
        return null;
    }
    
    
}
