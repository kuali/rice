/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.ken.service.impl;

import java.util.Iterator;
import java.util.List;

import org.kuali.rice.ken.service.NotificationRecipientService;
import org.kuali.rice.ken.util.NotificationConstants;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.workgroup.GroupNameId;
import org.kuali.rice.kew.workgroup.Workgroup;


/**
 * NotificationRecipientService implementation - this is the default out-of-the-box implementation of the service.  
 * These implementations rely on the the user and group management services that are built into the KEW system.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationRecipientServiceKewImpl implements NotificationRecipientService {
    /**
     * Constructs a NotificationRecipientServiceImpl.java.
     */
    public NotificationRecipientServiceKewImpl() {
    }

    /**
     * Uses the WorkgroupService of KEW to get at the members of a workgroup.
     * @see org.kuali.rice.ken.service.NotificationRecipientService#getGroupMembers(java.lang.String)
     */
    public String[] getGroupMembers(String groupRecipientId) {
	GroupNameId groupNameId = new GroupNameId(groupRecipientId);
	Workgroup w = KEWServiceLocator.getWorkgroupService().getWorkgroup(groupNameId);
	
	List<WorkflowUser> users = w.getUsers();
	Iterator<WorkflowUser> i = users.iterator();
	String[] members = new String[users.size()];
	for(int j = 0; i.hasNext(); j++) {
	    members[j] = i.next().getWorkflowId();
	}
	return members;
    }
    
   /**
    * @see org.kuali.rice.ken.service.NotificationRecipientService#isRecipientValid(java.lang.String, java.lang.String)
    */
    public boolean isRecipientValid(String recipientId, String recipientType) {
	if(NotificationConstants.RECIPIENT_TYPES.USER.equals(recipientType)) {
	    return isUserRecipientValid(recipientId);
	} else if(NotificationConstants.RECIPIENT_TYPES.GROUP.equals(recipientType)) {
	    return isGroupRecipientValid(recipientId);
	} else {
	    return false;
	}
    }

    /**
     * Uses the WorkgroupService from KEW to get at a workgroup.
     * @see org.kuali.rice.ken.service.NotificationRecipientService#isGroupRecipientValid(java.lang.String)
     */
    public boolean isGroupRecipientValid(String groupRecipientId) {
	GroupNameId groupNameId = new GroupNameId(groupRecipientId);
	Workgroup group = KEWServiceLocator.getWorkgroupService().getWorkgroup(groupNameId);
	
	if(group != null) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Uses the UserService from KEW to get at a user.
     * @see org.kuali.rice.ken.service.NotificationRecipientService#isUserRecipientValid(java.lang.String)
     */
    public boolean isUserRecipientValid(String userRecipientId) {
	try {
	    WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdDTO(userRecipientId));
	    if(user != null) {
		return true;
	    } else {
		return false;
	    }
	} catch (KEWUserNotFoundException e) {
	    return false;
	}
    }

    /**
     * Uses the UserService from KEW to get at the user's full display name.
     * @see org.kuali.rice.ken.service.NotificationRecipientService#getUserDisplayName(java.lang.String)
     */
    public String getUserDisplayName(String userId) {
	try {
	    WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdDTO(userId));
	    if(user != null) {
		return user.getDisplayName();
	    } else {
		return "";
	    }
	} catch (KEWUserNotFoundException e) {
	    return "";
	}
    }
}