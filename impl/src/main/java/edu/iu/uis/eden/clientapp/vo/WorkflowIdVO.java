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

import edu.iu.uis.eden.user.WorkflowUserId;

/**
 * Transport object representing {@link WorkflowUserId}
 * 
 * @workflow.webservice-object 
 */
public class WorkflowIdVO extends UserIdVO {

	private static final long serialVersionUID = -2268620878385726910L;
	public WorkflowIdVO() {}
    
    public WorkflowIdVO(String workflowId) {
        super(workflowId);
    }
    
    public String getWorkflowId() {
        return getId();
    }
    public void setWorkflowId(String workflowId) {
        setId(workflowId);
    }
}
