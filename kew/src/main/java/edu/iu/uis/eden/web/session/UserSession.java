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
package edu.iu.uis.eden.web.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.core.Core;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionlist.ActionListFilter;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.preferences.Preferences;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.EmplId;
import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Represents an authenticated user within the Workflow system.
 * 
 * <p>The current authenticated UserSession is stored in a ThreadLocal and can be 
 * accessed using UserSession.getAuthenticatedUser().
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class UserSession implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static ThreadLocal currentUserSession = new ThreadLocal();
    
    private UserId backdoorId;
    private WorkflowUser workflowUser;
    private WorkflowUser backdoorWorkflowUser;
    private WorkflowUser helpDeskActionListUser;
    private int nextObjectKey;
    private transient Map objectMap = new HashMap();
    private ActionListFilter actionListFilter;
    private Preferences preferences;
    private List authentications = new ArrayList();
    private Set<String> groups = new HashSet<String>();


    public UserSession (WorkflowUser user) {
        this.workflowUser = user;
        this.nextObjectKey = 0;
    }
    
    public static UserSession getAuthenticatedUser() {
    	return (UserSession)currentUserSession.get();
    }
    
    public static void setAuthenticatedUser(UserSession currentUserSession) {
    	UserSession.currentUserSession.set(currentUserSession);
    }
    
    public String getValue(String value) {
    	return value;
    }

    public WorkflowUser getHelpDeskActionListUser() {
        return helpDeskActionListUser;
    }
    
    public void setHelpDeskActionListUser(WorkflowUser helpDeskActionListUser) {
        this.helpDeskActionListUser = helpDeskActionListUser;
    }
    
    public Preferences getPreferences() {
        return preferences;
    }
    
    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }
    
    public ActionListFilter getActionListFilter() {
        return actionListFilter;
    }
    
    public void setActionListFilter(ActionListFilter actionListFilter) {
        this.actionListFilter = actionListFilter;
    }
    
    public String getNetworkId() {
        if (backdoorId != null) {
            return backdoorWorkflowUser.getAuthenticationUserId().getAuthenticationId();
        } else {
            return workflowUser.getAuthenticationUserId().getAuthenticationId();
        }
    }
    
    public WorkflowUser getWorkflowUser() {
        if (backdoorId != null) {
            return backdoorWorkflowUser;
        } else {
            return workflowUser;
        }
    }
    
    public WorkflowUser getLoggedInWorkflowUser() {
        return workflowUser;
    }
    
    public boolean setBackdoorId(String id) throws EdenUserNotFoundException {
        if (! EdenConstants.PROD_DEPLOYMENT_CODE.equalsIgnoreCase(Core.getCurrentContextConfig().getEnvironment())) {
            if (id.matches("^\\d*$")) {
                this.backdoorId = new EmplId(id);
            } else {
                this.backdoorId = new AuthenticationUserId(id);
            }
            this.backdoorWorkflowUser = ((UserService)KEWServiceLocator.getUserService()).getWorkflowUser(this.backdoorId);
        }
        return this.backdoorWorkflowUser != null;
    }
    
    public void clearBackdoor() {
        this.backdoorId = null;
        setPreferences(KEWServiceLocator.getPreferencesService().getPreferences(workflowUser));
    }

    public String addObject(Object object) {
        String objectKey = nextObjectKey++ + "";
        getObjectMap().put(objectKey, object);
        return objectKey;
    }

    public Object retrieveObject(String objectKey) {
        return getObjectMap().get(objectKey);
    }

    public void removeObject(String objectKey) {
        getObjectMap().remove(objectKey);
    }
    
    public boolean isBackdoorInUse() {
        return backdoorId != null;
    }

    public String getEmailAddress() {
        if (backdoorId != null) {
            return backdoorWorkflowUser.getEmailAddress();
        } else {
            return workflowUser.getEmailAddress();
        }
    }

    public int getNextObjectKey() {
        return nextObjectKey;
    }
    public void setNextObjectKey(int nextObjectKey) {
        this.nextObjectKey = nextObjectKey;
    }
    public Map getObjectMap() {
    	if (objectMap == null) {
    		objectMap = new HashMap();
    	}
        return objectMap;
    }
    public void setObjectMap(Map objectMap) {
        this.objectMap = objectMap;
    }
    public String getDisplayName() {
        if (backdoorId != null) {
            return backdoorWorkflowUser.getDisplayName();
        } else {
            return workflowUser.getDisplayName();
        }
    }
    public UserId getBackdoorId() {
        return backdoorId;
    }
    
    public boolean isAdmin(){
    	 Workgroup workflowAdminGroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId(Utilities.getApplicationConstant(EdenConstants.WORKFLOW_ADMIN_WORKGROUP_NAME_KEY)));
    	 return workflowAdminGroup.hasMember(getWorkflowUser()); 
    }
    
    /**
     * Returns a List of Authentications on the UserSession.  This List identifies the various types of
     * authentications that the user has performed (i.e. Kerberos, Safeword, etc.) 
     */
    public List getAuthentications() {
    	return authentications;
    }
    
    public void addAuthentication(Authentication authentication) {
    	getAuthentications().add(authentication);
    }
    
    public void removeAuthentication(Authentication authentication) {
    	getAuthentications().remove(authentication);
    }

    public boolean hasRole(String role) {
    	for (Iterator iterator = getAuthentications().iterator(); iterator.hasNext();) {
    		Authentication auth = (Authentication) iterator.next();
    		if (auth.getAuthority().equalsIgnoreCase(role)) {
    			return true;
}    	}
    	return false;
    }

    public Set<String> getGroups() {
		return groups;
	}

	public void setGroups(Set<String> groups) {
		this.groups = groups;
	}

	public boolean isMemberOfGroup(String groupName) {
		return getGroups().contains(groupName);
	}

}