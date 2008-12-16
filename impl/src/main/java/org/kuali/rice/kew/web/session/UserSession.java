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
package org.kuali.rice.kew.web.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.kew.actionlist.ActionListFilter;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.preferences.Preferences;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.AuthenticationUserId;
import org.kuali.rice.kew.user.EmplId;
import org.kuali.rice.kew.user.UserId;
import org.kuali.rice.kew.user.UserService;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.workgroup.GroupNameId;
import org.kuali.rice.kew.workgroup.Workgroup;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.service.KIMServiceLocator;


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
    private String sortOrder;
    private String sortCriteria;
    private int currentPage;
    private KimPrincipal	principal;

	/**
	 * @return the sortOrder
	 */
	public String getSortOrder() {
		return this.sortOrder;
	}

	/**
	 * @param sortOrder the sortOrder to set
	 */
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	/**
	 * @return the sortCriteria
	 */
	public String getSortCriteria() {
		return this.sortCriteria;
	}

	/**
	 * @param sortCriteria the sortCriteria to set
	 */
	public void setSortCriteria(String sortCriteria) {
		this.sortCriteria = sortCriteria;
	}

	/**
	 * @return the currentPage
	 */
	public int getCurrentPage() {
		return this.currentPage;
	}

	/**
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

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

    @Deprecated
    public WorkflowUser getHelpDeskActionListUser() {
        return helpDeskActionListUser;
    }

    public Person getHelpDeskActionListPerson() {
    	return convertToPerson(getHelpDeskActionListUser());
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

    @Deprecated
    public WorkflowUser getWorkflowUser() {
        if (backdoorId != null) {
            return backdoorWorkflowUser;
        } else {
            return workflowUser;
        }
    }

    public Person getPerson() {
    	return convertToPerson(getWorkflowUser());
    }

    private Person convertToPerson(WorkflowUser user) {
    	if (user == null) {
    		return null;
    	}
    	return KIMServiceLocator.getPersonService().getPersonByPrincipalName(user.getAuthenticationUserId().getId());
    }

    @Deprecated
    public WorkflowUser getLoggedInWorkflowUser() {
        return workflowUser;
    }

    public Person getLoggedInPerson() {
    	return convertToPerson(getLoggedInWorkflowUser());
    }

    public boolean setBackdoorId(String id) throws KEWUserNotFoundException {
        if (! KEWConstants.PROD_DEPLOYMENT_CODE.equalsIgnoreCase(ConfigContext.getCurrentContextConfig().getEnvironment())) {
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
        setPreferences(KEWServiceLocator.getPreferencesService().getPreferences(principal.getPrincipalId()));
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
    	 Workgroup workflowAdminGroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId(Utilities.getApplicationConstant(KEWConstants.WORKFLOW_ADMIN_WORKGROUP_NAME_KEY)));
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
    		}
    	}
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

	/**
	 * @return the principal
	 */
	public KimPrincipal getPrincipal() {
		return this.principal;
	}

	/**
	 * @param principal the principal to set
	 */
	public void setPrincipal(KimPrincipal principal) {
		this.principal = principal;
	}

}