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

import edu.iu.uis.eden.workgroup.WorkflowGroupId;

/**
 * Transport object representing a {@link WorkflowGroupId}.
 * 
 * @workflow.webservice-object 
 */
public class WorkflowGroupIdVO extends WorkgroupIdVO {
    
	private static final long serialVersionUID = 8008851999610283526L;
	private Long workgroupId;
    
    public WorkflowGroupIdVO() {
        
    }
    
    public WorkflowGroupIdVO(Long workgroupId) {
        this.workgroupId = workgroupId;
    }
    
    public Long getWorkgroupId() {
        return workgroupId;
    }
    public void setWorkgroupId(Long workgroupId) {
        this.workgroupId = workgroupId;
    }
    
    public boolean equals(Object object) {
        if (object instanceof WorkflowGroupIdVO) {
            Long objectId = ((WorkflowGroupIdVO)object).getWorkgroupId();
            return ((workgroupId == null && objectId == null) || (workgroupId != null && workgroupId.equals(objectId)));
        }
        return false;
    }
    
    public int hashCode() {
        return (workgroupId == null ? 0 : workgroupId.hashCode());
    }
    
    public String toString() {
        return (workgroupId == null ? "null" : workgroupId.toString());
    }
}
