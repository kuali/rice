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
package edu.iu.uis.eden.user.web;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import edu.iu.uis.eden.lookupable.WebLookupableDecorator;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.EmplId;
import edu.iu.uis.eden.user.UuId;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * A decorator on the {@link WorkflowUser} which adds some convienance methods for
 * use by the web-tier of the application.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WebWorkflowUser extends WebLookupableDecorator implements WorkflowUser {

	private static final long serialVersionUID = 3165333859388741253L;

	private WorkflowUser workflowUser;
    private Boolean newUuid;
    private Boolean newEmplId;
    private Boolean newAuthenticationId;
    protected Integer lockVerNbr;
    private Timestamp createDate;
    private UuId oldUuid;
    private EmplId oldEmplId;
    private AuthenticationUserId oldAuthenticationUserId;
    
	public WebWorkflowUser(WorkflowUser workflowUser) {
		this.workflowUser = workflowUser;
	}
	
	public String getWorkflowId() {
	    return getWorkflowUserId().getWorkflowId();
	}
	
	public AuthenticationUserId getAuthenticationUserId() {
		return workflowUser.getAuthenticationUserId();
	}
	public String getDisplayName() {
		return workflowUser.getDisplayName();
	}
	public String getEmailAddress() {
		return workflowUser.getEmailAddress();
	}
	public EmplId getEmplId() {
		return workflowUser.getEmplId();
	}
	public String getGivenName() {
		return workflowUser.getGivenName();
	}
	public String getLastName() {
		return workflowUser.getLastName();
	}
	public String getTransposedName() {
		return workflowUser.getTransposedName();
	}
	public UuId getUuId() {
		return workflowUser.getUuId();
	}
	public WorkflowUserId getWorkflowUserId() {
		return workflowUser.getWorkflowUserId();
	}
	public void setAuthenticationUserId(
			AuthenticationUserId authenticationUserId) {
		workflowUser.setAuthenticationUserId(authenticationUserId);
	}
	public void setDisplayName(String displayName) {
		workflowUser.setDisplayName(displayName);
	}
	public void setEmailAddress(String emailAddress) {
		workflowUser.setEmailAddress(emailAddress);
	}
	public void setEmplId(EmplId emplId) {
		workflowUser.setEmplId(emplId);
	}
	public void setGivenName(String givenName) {
		workflowUser.setGivenName(givenName);
	}
	public void setLastName(String lastName) {
		workflowUser.setLastName(lastName);
	}
	public void setUuId(UuId uuId) {
		workflowUser.setUuId(uuId);
	}
	public void setWorkflowUserId(WorkflowUserId workflowUserId) {
		workflowUser.setWorkflowUserId(workflowUserId);
	}
	public boolean hasId() {
		return workflowUser.hasId();
	}

    public Boolean isNewAuthenticationId() {
        return newAuthenticationId;
    }

    public void setNewAuthenticationId(Boolean newAuthenticationId) {
        this.newAuthenticationId = newAuthenticationId;
    }

    public Boolean isNewEmplId() {
        return newEmplId;
    }

    public void setNewEmplId(Boolean newEmplId) {
        this.newEmplId = newEmplId;
    }
 
    public Boolean isNewUuid() {
        return newUuid;
    }

    public void setNewUuid(Boolean newUuid) {
        this.newUuid = newUuid;
    }

    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }    

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
    
    public AuthenticationUserId getOldAuthenticationUserId() {
        return oldAuthenticationUserId;
    }    

    public void setOldAuthenticationUserId(AuthenticationUserId oldAuthenticationUserId) {
        this.oldAuthenticationUserId = oldAuthenticationUserId;
    }
 
    public EmplId getOldEmplId() {
        return oldEmplId;
    }
 
    public void setOldEmplId(EmplId oldEmplId) {
        this.oldEmplId = oldEmplId;
    }
 
    public UuId getOldUuid() {
        return oldUuid;
    }
 
    public void setOldUuid(UuId oldUuid) {
        this.oldUuid = oldUuid;
    }

    public boolean isEmailRestricted() {
		return workflowUser.isEmailRestricted();
}
	public boolean isNameRestricted() {
		return workflowUser.isNameRestricted();
	}

	public String getDisplayNameSafe() {
		return workflowUser.getDisplayNameSafe();
	}

	public String getEmailAddressSafe() {
		return workflowUser.getEmailAddressSafe();
	}

	public String getGivenNameSafe() {
		return workflowUser.getGivenNameSafe();
	}

	public String getLastNameSafe() {
		return workflowUser.getLastNameSafe();
	}

	public String getTransposedNameSafe() {
		if (!UserSession.getAuthenticatedUser().getWorkflowUser().getWorkflowId().equals(getWorkflowId())) {
        	return workflowUser.getTransposedNameSafe();
        }
		return workflowUser.getTransposedName();
	}

	public String toString() {
        return new ToStringBuilder(this).append("workflowUser", workflowUser)
                                        .append("oldUuid", oldUuid)
                                        .append("oldEmplId", oldEmplId)
                                        .append("oldAuthenticationUserId", oldAuthenticationUserId)
                                        .append("newUuid", newUuid)
                                        .append("newEmplId", newEmplId)
                                        .append("newAuthenticationId", newAuthenticationId)
                                        .append("lockVerNbr", lockVerNbr)
                                        .append("createDate", createDate).toString();
    }
}
