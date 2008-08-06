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
package org.kuali.rice.kew.workgroup.web;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.attribute.Extension;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.lookupable.MyColumns;
import org.kuali.rice.kew.lookupable.WebLookupableDecorator;
import org.kuali.rice.kew.user.Recipient;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.util.CodeTranslator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.workgroup.GroupNameId;
import org.kuali.rice.kew.workgroup.WorkflowGroupId;
import org.kuali.rice.kew.workgroup.Workgroup;
import org.kuali.rice.kew.workgroup.WorkgroupType;


/**
 * A decorator for {@link Workgroup} which allows for the bean to be more
 * easily used by the web-tier.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WebWorkgroup extends WebLookupableDecorator implements Workgroup {

	private static final long serialVersionUID = -6073955034528195631L;

	private Workgroup workgroup;
	private WorkgroupType workgroupType;
	private MyColumns myColumns;

    public WebWorkgroup(Workgroup workgroup) {
        this.workgroup = workgroup;
        if (StringUtils.isBlank(workgroup.getWorkgroupType()) || workgroup.getWorkgroupType().equals(KEWConstants.LEGACY_DEFAULT_WORKGROUP_TYPE)) {
        	workgroupType = WorkgroupForm.createDefaultWorkgroupType();
        } else {
        	workgroupType = KEWServiceLocator.getWorkgroupTypeService().findByName(workgroup.getWorkgroupType());
        	if (workgroupType == null) {
        		throw new WorkflowRuntimeException("Workgroup with name '" + workgroup.getGroupNameId().getNameId() + "' has an invalid workgroup type of '" + workgroup.getWorkgroupType() + "'");
        	}
        }
    }

    /**
     * @return
     */
    public Integer getLockVerNbr() {
        return workgroup.getLockVerNbr();
    }
    /**
     * @return
     */
    public String getWorkgroupType() {
        return workgroup.getWorkgroupType();
    }

    /**
     * @return
     */
    public Boolean getActiveInd() {
        return workgroup.getActiveInd();
    }
    /**
     * @param activeInd
     */
    public void setActiveInd(Boolean activeInd) {
        workgroup.setActiveInd(activeInd);
    }

    public String getActiveIndDisplay() {
        if (workgroup.getActiveInd() == null) {
            return KEWConstants.INACTIVE_LABEL_LOWER;
        }
        return CodeTranslator.getActiveIndicatorLabel(workgroup.getActiveInd());
    }
    /**
     * @return
     */
    public String getDescription() {
        return workgroup.getDescription();
    }
    /**
     * @return
     */
    public GroupNameId getGroupNameId() {
        return workgroup.getGroupNameId();
    }

    /**
     * @return
     */
    public WorkflowGroupId getWorkflowGroupId() {
        return workgroup.getWorkflowGroupId();
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        workgroup.setDescription(description);
    }
    /**
     * @param groupNameId
     */
    public void setGroupNameId(GroupNameId groupNameId) {
        workgroup.setGroupNameId(groupNameId);
    }

    /**
     * @param workflowGroupId
     */
    public void setWorkflowGroupId(WorkflowGroupId workflowGroupId) {
        workgroup.setWorkflowGroupId(workflowGroupId);
    }

    public Long getWorkgroupId() {
        if (getWorkflowGroupId() == null) return null;
        return getWorkflowGroupId().getGroupId();
    }

    public String getWorkgroupName() {
        if (getGroupNameId() == null) return null;
        return getGroupNameId().getNameId();
    }
    /**
     * @param workgroupType
     */
    public void setWorkgroupType(String workgroupType) {
        workgroup.setWorkgroupType(workgroupType);
    }

    public String getDisplayName() {
        return workgroup.getDisplayName();
    }

    public String toString() {
        return "[WebWorkgroup: workgroup=" + workgroup + "]";
    }

	public List<Extension> getExtensions() {
		return workgroup.getExtensions();
	}

	public void setExtensions(List<Extension> extensions) {
		workgroup.setExtensions(extensions);
	}

	public MyColumns getMyColumns() {
		return myColumns;
	}

	public void setMyColumns(MyColumns myColumns) {
		this.myColumns = myColumns;
	}

	public List<Recipient> getMembers() {
		return workgroup.getMembers();
	}

	public List<WorkflowUser> getUsers() {
		return workgroup.getUsers();
	}

	public boolean hasMember(Recipient member) {
		return workgroup.hasMember(member);
	}

	public void setMembers(List<Recipient> members) {
		workgroup.setMembers(members);
	}


}