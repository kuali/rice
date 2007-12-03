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
package edu.iu.uis.eden.actionitem;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionlist.DisplayParameters;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Alternate model object for action list fetches that do not automatically use
 * ojb collections.  This is here to make action list faster. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class ActionItemActionListExtension extends ActionItem {
    
    private static final long serialVersionUID = -8801104028828059623L;
    
    private WorkflowUser delegatorUser = null;
    private Workgroup delegatorWorkgroup = null;
    private String delegatorName = "";
    private Workgroup workgroup = null;   
    private DisplayParameters displayParameters;
    private boolean isInitialized = false;
    
    public Workgroup getWorkgroup() {
        return workgroup; 
    }
    
    public WorkflowUser getDelegatorUser() throws EdenUserNotFoundException {
        WorkflowUser delegator = null;
        if (getDelegatorWorkflowId() != null) {
            delegator = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(getDelegatorWorkflowId()));
        }
        return delegator;
    }
    
    public Workgroup getDelegatorWorkgroup() {
        Workgroup delegator = null;
        if (getDelegatorWorkgroupId() != null) {
            delegator = KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(getDelegatorWorkgroupId()));
        }
        return delegator;
    }
    
    public String getDelegatorName() throws EdenUserNotFoundException {
        return delegatorName;
    }
    
    public void initialize() throws WorkflowException {
    	if (isInitialized) {
    		return;
    	}
        if (getWorkgroupId() != null) {
            workgroup = super.getWorkgroup();
        }
        if (getDelegatorWorkflowId() != null) {
            delegatorUser = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(getDelegatorWorkflowId()));
            if (delegatorUser != null) {
                delegatorName = delegatorUser.getTransposedName();
            }
        }
        if (getDelegatorWorkgroupId() != null) {
            delegatorWorkgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(getDelegatorWorkgroupId()));
            if (delegatorWorkgroup != null) {
                delegatorName = delegatorWorkgroup.getGroupNameId().getNameId();
            }
        }
        isInitialized = true;
    }
    
    public boolean isInitialized() {
    	return isInitialized;
    }

	public DisplayParameters getDisplayParameters() {
		return displayParameters;
	}

	public void setDisplayParameters(DisplayParameters displayParameters) {
		this.displayParameters = displayParameters;
	}

}
