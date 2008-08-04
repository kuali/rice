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
package org.kuali.notification.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.kuali.notification.service.NotificationRecipientService;
import org.kuali.notification.util.NotificationConstants;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.web.WebWorkflowUser;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.Workgroup;
import edu.iu.uis.eden.workgroup.WorkgroupService;
import edu.iu.uis.eden.workgroup.web.WebWorkgroup;

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
     * @see org.kuali.notification.service.NotificationRecipientService#getGroupMembers(java.lang.String)
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
    * @see org.kuali.notification.service.NotificationRecipientService#isRecipientValid(java.lang.String, java.lang.String)
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
     * @see org.kuali.notification.service.NotificationRecipientService#isGroupRecipientValid(java.lang.String)
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
     * @see org.kuali.notification.service.NotificationRecipientService#isUserRecipientValid(java.lang.String)
     */
    public boolean isUserRecipientValid(String userRecipientId) {
	try {
	    WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdVO(userRecipientId));
	    if(user != null) {
		return true;
	    } else {
		return false;
	    }
	} catch (EdenUserNotFoundException e) {
	    return false;
	}
    }

    /**
     * Uses the UserService from KEW to get at all users.
     * @see org.kuali.notification.service.NotificationRecipientService#getAllGroups()
     */
    public Collection getAllUsers() {
	UserService userService = KEWServiceLocator.getUserService();
	WebWorkflowUser userTemplate = new WebWorkflowUser(userService.getBlankUser());
	List<WorkflowUser> list = userService.search(userTemplate, true);
	Collections.sort(list, new Comparator(){
	    public int compare(Object obj1, Object obj2) {
               return ((WorkflowUser)obj1).getDisplayName().compareTo(((WorkflowUser)obj2).getDisplayName());                    
            }
	}
	);
	return list;
	//return userService.search(userTemplate, true);
    }

    /**
     * Uses the WorkgroupService from KEW to get at all groups.
     * @see org.kuali.notification.service.NotificationRecipientService#getAllUsers()
     */
    public Collection getAllGroups() {
	WorkgroupService workgroupService = KEWServiceLocator.getWorkgroupService();
	WebWorkgroup workgroupTemplate = new WebWorkgroup(workgroupService.getBlankWorkgroup());
	List<Workgroup> list = workgroupService.search(workgroupTemplate, null, true);
	Collections.sort(list, new Comparator(){
	    public int compare(Object obj1, Object obj2) {
               return ((Workgroup)obj1).getDisplayName().compareTo(((Workgroup)obj2).getDisplayName());                    
            }
	}
	);
	return list;
	
	//return workgroupService.search(workgroupTemplate, true);
    }

    /**
     * Uses the UserService from KEW to get at the user's full display name.
     * @see org.kuali.notification.service.NotificationRecipientService#getUserDisplayName(java.lang.String)
     */
    public String getUserDisplayName(String userId) {
	try {
	    WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdVO(userId));
	    if(user != null) {
		return user.getDisplayName();
	    } else {
		return "";
	    }
	} catch (EdenUserNotFoundException e) {
	    return "";
	}
    }
}