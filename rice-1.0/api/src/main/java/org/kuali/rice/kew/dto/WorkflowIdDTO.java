/*
 * Copyright 2005-2008 The Kuali Foundation
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

/**
 * Transport object representing a principal ID
 */
public class WorkflowIdDTO extends UserIdDTO {

	private static final long serialVersionUID = -2268620878385726910L;
	public WorkflowIdDTO() {}
    
    public WorkflowIdDTO(String workflowId) {
        super(workflowId);
    }
    
    public String getWorkflowId() {
        return getId();
    }
    public void setWorkflowId(String workflowId) {
        setId(workflowId);
    }
}
