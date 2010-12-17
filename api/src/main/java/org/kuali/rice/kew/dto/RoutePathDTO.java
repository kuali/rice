/*
 * Copyright 2005-2009 The Kuali Foundation
 * 
 *  * Licensed under the Educational Community License, Version 2.0 (the "License");

 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  * http://www.opensource.org/licenses/ecl2.php

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
 * Transport object responsible for holding the process a document has been through.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RoutePathDTO implements Serializable {

    private static final long serialVersionUID = 2883128962971335387L;
    
    private ProcessDTO[] processes = new ProcessDTO[0];
    
    public ProcessDTO[] getProcesses() {
        return processes;
    }

    public void setProcesses(ProcessDTO[] processes) {
        this.processes = processes;
    }

    public ProcessDTO getPrimaryProcess() {
        for (int index = 0; index < processes.length; index++) {
            ProcessDTO process = processes[index];
            if (process.isInitial()) {
                return process;
            }
        }        
        return null;
    }    
    
    
}
