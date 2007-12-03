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
package edu.iu.uis.eden.user;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanNameAware;

/**
 * A simple user implementation which can be used as the base for other
 * user implementations of {@link WorkflowUser}.
 * 
 * @see WorkflowUser
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BaseWorkflowUser implements WorkflowUser, BeanNameAware {

	private static final long serialVersionUID = -1498627602071447775L;

	private static final String MASKED = "xxxxxx";

	private String workflowUserId;
	private String authenticationUserId;
	private String uuId;
	private String emplId;

    private String givenName;
    private String lastName;
    private String displayName;
    private String emailAddress;

    private Timestamp createDate;
    private Timestamp lastUpdateDate;
    private boolean defaulted;
    private boolean nameRestricted;
    private boolean emailRestricted;

    private Integer lockVerNbr = new Integer(0);

    public BaseWorkflowUser() {
    }

    public BaseWorkflowUser(String defaultId) {
        setDefaultId(defaultId);
    }

    /**
     * Convenience setter which initializes all fields
     * based on Spring bean name
     * @param name the Spring bean name
     */
    public void setBeanName(String name) {
        if (!defaulted) setDefaultId(name);
    }

    /**
     * Convenience setter which initializes all fields
     * based on a default id
     * @param name the user name
     */
    public void setDefaultId(String name) {
        setDisplayName(name);
        setGivenName(name);
        setLastName(name + "-lastname");
        setEmailAddress(name + "@localhost");
        setAuthenticationUserId(new AuthenticationUserId(name));
        setWorkflowUserId(new WorkflowUserId(name));
        setEmplId(new EmplId(name));
        setUuId(new UuId(name));
        defaulted = true;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public AuthenticationUserId getAuthenticationUserId() {
        return new AuthenticationUserId(authenticationUserId);
    }

    public void setAuthenticationUserId(AuthenticationUserId authenticationUserId) {
        this.authenticationUserId = (authenticationUserId == null ? null : authenticationUserId.getAuthenticationId());
    }

    public WorkflowUserId getWorkflowUserId() {
        return new WorkflowUserId(workflowUserId);
    }

    public void setWorkflowUserId(WorkflowUserId workflowUserId) {
        this.workflowUserId = (workflowUserId == null ? null : workflowUserId.getWorkflowId());
    }

    public EmplId getEmplId() {
		return new EmplId(emplId);
	}

	public void setEmplId(EmplId emplId) {
		this.emplId = (emplId == null ? null : emplId.getEmplId());
	}

	public UuId getUuId() {
		return new UuId(uuId);
	}

	public void setUuId(UuId uuId) {
		this.uuId = (uuId == null ? null : uuId.getUuId());
	}

    /**
     * Utility to determine whether a UserId is missing or empty
     * @param userId the userId
     * @return whether a UserId is missing or empty
     */
    private static final boolean idIsEmpty(UserId userId) {
        return userId == null || userId.isEmpty();
    }

    public boolean hasId() {
        boolean noId = idIsEmpty(getAuthenticationUserId()) && idIsEmpty(getEmplId()) && idIsEmpty(getUuId()) && idIsEmpty(getWorkflowUserId());
        return !noId;
    }

    public String getTransposedName() {
        return this.getLastName() + ", " + this.getGivenName();
    }

    // FIXME: -- extra properties for Struts forms w/ beanutils
    public void setWorkflowId(String id) {
        setWorkflowUserId(new WorkflowUserId(id));
    }
    public String getWorkflowId() {
        return (getWorkflowUserId() == null ? null : getWorkflowUserId().getWorkflowId());
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

    public Timestamp getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Timestamp lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

	public boolean isEmailRestricted() {
		return emailRestricted;
	}

	public void setEmailRestricted(boolean emailRestricted) {
		this.emailRestricted = emailRestricted;
	}

	public boolean isNameRestricted() {
		return nameRestricted;
	}

	public void setNameRestricted(boolean nameRestricted) {
		this.nameRestricted = nameRestricted;
	}

	public String getDisplayNameSafe() {
		if (!StringUtils.isBlank(getDisplayName()) && isNameRestricted()) {
			return MASKED;
		}
		return getDisplayName();
	}

	public String getEmailAddressSafe() {
		if (!StringUtils.isBlank(getEmailAddress()) && isEmailRestricted()) {
			return MASKED;
		}
		return getEmailAddress();
	}

	public String getGivenNameSafe() {
		if (!StringUtils.isBlank(getGivenName()) && isNameRestricted()) {
			return MASKED;
		}
		return getGivenName();
	}

	public String getLastNameSafe() {
		if (!StringUtils.isBlank(getLastName()) && isNameRestricted()) {
			return MASKED;
		}
		return getLastName();
	}

	public String getTransposedNameSafe() {
		if (!StringUtils.isBlank(getTransposedName()) && isNameRestricted()) {
			return MASKED;
		}
		return getTransposedName();

	}

	public String toString() {
        return "[SimpleWorkflowUser: " + ", displayName=" + displayName + ", givenName=" + givenName + ", lastName=" + lastName + ", emailAddress=" + emailAddress + ", emplId=" + emplId + ", uuId=" + uuId + ", authenticationId=" + authenticationUserId + ", workflowId=" + workflowUserId + "]";
    }

}