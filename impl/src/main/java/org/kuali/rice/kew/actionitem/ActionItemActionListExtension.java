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
package org.kuali.rice.kew.actionitem;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kew.actionlist.DisplayParameters;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.preferences.Preferences;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.user.WorkflowUserId;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.service.KIMServiceLocator;


/**
 * Alternate model object for action list fetches that do not automatically use
 * ojb collections.  This is here to make action list faster. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
@Entity
@Table(name="KREW_ACTN_ITM_T")
public class ActionItemActionListExtension extends ActionItem {
    
    private static final long serialVersionUID = -8801104028828059623L;
    
    @Transient
    private WorkflowUser delegatorUser = null;
    @Transient
    private KimGroup delegatorWorkgroup = null;
    @Transient
    private String delegatorName = "";
    @Transient
    private KimGroup group = null;   
    @Transient
    private DisplayParameters displayParameters;
    @Transient
    private boolean isInitialized = false;
    
    public KimGroup getGroup() {
        return group; 
    }
    
    public WorkflowUser getDelegatorUser() throws KEWUserNotFoundException {
        WorkflowUser delegator = null;
        if (getDelegatorWorkflowId() != null) {
            delegator = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(getDelegatorWorkflowId()));
        }
        return delegator;
    }
    
    public KimGroup getDelegatorGroup() {
        KimGroup delegator = null;
        if (getDelegatorGroupId() != null) {
            delegator = KIMServiceLocator.getIdentityManagementService().getGroup("" + getDelegatorGroupId());
        }
        return delegator;
    }
    
    public String getDelegatorName() throws KEWUserNotFoundException {
        return delegatorName;
    }
    
    public void initialize(Preferences preferences) throws WorkflowException {
    	if (isInitialized) {
    		return;
    	}
        if (getGroupId() != null) {
            group = super.getGroup();
        }
        if (getDelegatorWorkflowId() != null) {
            delegatorUser = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(getDelegatorWorkflowId()));
            if (delegatorUser != null) {
                delegatorName = delegatorUser.getTransposedName();
            }
        }
        if (getDelegatorGroupId() != null) {
            delegatorWorkgroup = KIMServiceLocator.getIdentityManagementService().getGroup(""+getDelegatorGroupId());
            if (delegatorWorkgroup != null) {
                delegatorName = delegatorWorkgroup.getGroupName();
            }
        }
        if (KEWConstants.PREFERENCES_YES_VAL.equals(preferences.getShowDateApproved())) {
        	setLastApprovedDate(KEWServiceLocator.getActionTakenService().getLastApprovedDate(getRouteHeaderId()));
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

