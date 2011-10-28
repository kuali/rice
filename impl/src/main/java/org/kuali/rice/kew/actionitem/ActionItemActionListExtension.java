/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kew.actionitem;

import org.kuali.rice.kew.api.actionlist.DisplayParameters;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.api.preferences.Preferences;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Alternate model object for action list fetches that do not automatically use
 * ojb collections.  This is here to make action list faster.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@MappedSuperclass
public class ActionItemActionListExtension extends ActionItem {

    private static final long serialVersionUID = -8801104028828059623L;

    @Transient
    private Person delegatorPerson = null;
    @Transient
    private String delegatorName = "";
    @Transient
    private DisplayParameters displayParameters;
    @Transient
    private boolean isInitialized = false;
    @Transient
    private Group delegatorGroup = null;
    @Transient
    private Group group = null;
    @Transient
    private DocumentRouteHeaderValue routeHeader;

    public Person getDelegatorPerson() {
        return delegatorPerson;
    }
    
    public String getDelegatorName() {
        return delegatorName;
    }

    public void initialize(Preferences preferences) throws WorkflowException {
    	if (isInitialized) {
    		return;
    	}
        if (getGroupId() != null) {
            group = super.getGroup();
        }
        if (getDelegatorPrincipalId() != null) {
        	delegatorPerson = KimApiServiceLocator.getPersonService().getPerson(getDelegatorPrincipalId());
            if (delegatorPerson != null) {
                delegatorName = delegatorPerson.getName();
            }
        }

        if (getDelegatorGroupId() != null) {
        	delegatorGroup = KimApiServiceLocator.getGroupService().getGroup(getDelegatorGroupId());
        	if (delegatorGroup !=null)
        		delegatorName = delegatorGroup.getName();
        }
        if (KewApiConstants.PREFERENCES_YES_VAL.equals(preferences.getShowDateApproved())) {
        	setLastApprovedDate(KEWServiceLocator.getActionTakenService().getLastApprovedDate(getDocumentId()));
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

	/**
	 * @return the group
	 */
	public Group getGroup() {
		return this.group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

	/**
	 * @return the delegatorGroup
	 */
	public Group getDelegatorGroup() {
		return this.delegatorGroup;
	}

	/**
	 * @param delegatorGroup the delegatorGroup to set
	 */
	public void setDelegatorGroup(Group delegatorGroup) {
		this.delegatorGroup = delegatorGroup;
	}

	public DocumentRouteHeaderValue getRouteHeader() {
		return this.routeHeader;
	}

	public void setRouteHeader(DocumentRouteHeaderValue routeHeader) {
		this.routeHeader = routeHeader;
	}

}

