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
package edu.iu.uis.eden.workgroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kuali.workflow.attribute.Extension;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.routeheader.Routable;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.util.CodeTranslator;

/**
 * A simple Workgroup implementation.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BaseWorkgroup implements Workgroup, Routable {

	private static final long serialVersionUID = 8048445959385570128L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BaseWorkgroup.class);

    protected Long workgroupId;
    protected String workgroupName;
    protected String description;
    protected Boolean activeInd = Boolean.TRUE;
    protected String workgroupType;
    protected Integer versionNumber = new Integer(0);
    protected Integer lockVerNbr = new Integer(0);
    protected List<Recipient> members = new ArrayList<Recipient>();

    protected List<BaseWorkgroupMember> workgroupMembers = new ArrayList<BaseWorkgroupMember>();
    protected List<Extension> extensions = new ArrayList<Extension>();

    protected Long documentId;
    protected Boolean currentInd;

    public BaseWorkgroup() {}

    public Boolean getActiveInd() {
        return activeInd;
    }

    public void setActiveInd(Boolean activeInd) {
        this.activeInd = activeInd;
    }

    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    public void setLockVerNbr(Integer lockVerNbr) {
    	this.lockVerNbr = lockVerNbr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorkgroupType() {
        return workgroupType;
    }

    public void setWorkgroupType(String workgroupTypeCode) {
        this.workgroupType = workgroupTypeCode;
    }

	public GroupNameId getGroupNameId() {
        return (workgroupName == null ? null : new GroupNameId(workgroupName));
    }

    public void setGroupNameId(GroupNameId groupNameId) {
    	if (groupNameId == null || groupNameId.getNameId() == null) {
    		this.workgroupName = null;
    	} else {
    		this.workgroupName = groupNameId.getNameId();
    	}
    }

    public WorkflowGroupId getWorkflowGroupId() {
        return (workgroupId == null ? null : new WorkflowGroupId(workgroupId));
    }

    public void setWorkflowGroupId(WorkflowGroupId workflowGroupId) {
    	if (workflowGroupId == null || workflowGroupId.getGroupId() == null) {
    		this.workgroupId = null;
    	} else {
    		this.workgroupId = workflowGroupId.getGroupId();
    	}
    }

    public void setWorkgroupId(Long workgroupId) {
    	this.workgroupId = workgroupId;
    }

    public Long getWorkgroupId() {
    	return this.workgroupId;
    }

    public String getWorkgroupName() {
    	return workgroupName;
    }

    public List<Recipient> getMembers() {
		return members;
	}

	public void setMembers(List<Recipient> members) {
		this.members = members;
	}


	public List<WorkflowUser> getUsers() {
		List<WorkflowUser> users = new ArrayList<WorkflowUser>();
		Set<String> userIds = new HashSet<String>();
		Set<String> workgroupNames = new HashSet<String>();
		for (Recipient recipient : getMembers()) {
			processRecipientUsers(recipient, users, userIds, workgroupNames);
		}
        return users;
    }

	/**
	 * Prevents duplicate users from occuring in the users list as well as preventing infinite loops
	 * should there happen to be cycles in the workgroups.
	 */
	protected void processRecipientUsers(Recipient recipient, List<WorkflowUser> users, Set<String> userIds, Set<String> workgroupNames) {
		if (recipient instanceof WorkflowUser) {
			WorkflowUser user = (WorkflowUser)recipient;
			if (!userIds.contains(user.getWorkflowId())) {
				userIds.add(user.getWorkflowId());
				users.add(user);
			}
		} else if (recipient instanceof Workgroup) {
			Workgroup workgroup = (Workgroup)recipient;
			if (!workgroupNames.contains(workgroup.getGroupNameId().getNameId())) {
				workgroupNames.add(workgroup.getGroupNameId().getNameId());
				for (Recipient workgroupRecipient : workgroup.getMembers()) {
					processRecipientUsers(workgroupRecipient, users, userIds, workgroupNames);
				}
			}
		} else {
			LOG.error("Invalid recipient type found for workgroup member: " + recipient.getClass().getName());
		}
	}

    public boolean hasMember(Recipient member) {
    	for (Recipient recipient : getMembers()) {
    		if (recipient instanceof WorkflowUser && member instanceof WorkflowUser) {
    			WorkflowUser user1 = (WorkflowUser)recipient;
    			WorkflowUser user2 = (WorkflowUser)member;
				if (user1.getWorkflowUserId().equals(user2.getWorkflowUserId())) {
					return true;
				}
			} else if (recipient instanceof WorkflowUser && member instanceof Workgroup) {
				continue;
			} else if (recipient instanceof Workgroup && member instanceof WorkflowUser) {
				Workgroup workgroup = (Workgroup)recipient;
				if (workgroup.hasMember(member)) {
					return true;
				}
			} else if (recipient instanceof Workgroup && member instanceof Workgroup) {
				Workgroup workgroup1 = (Workgroup)recipient;
				Workgroup workgroup2 = (Workgroup)member;
				if (workgroup1.getWorkflowGroupId().equals(workgroup2.getWorkflowGroupId())) {
					return true;
				}
			}
			else {
				LOG.error("Invalid recipient type found for workgroup member: " + recipient.getClass().getName());
			}
    	}
    	return false;
    }

	public String getDisplayName() {
        if (getGroupNameId() == null) return null;
        return getGroupNameId().getNameId();
    }

    public Integer getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(Integer versionNumber) {
		this.versionNumber = versionNumber;
	}

	public Recipient getWorkgroupMember(int index) {
        return getMembers().get(index);
    }

    public String getActiveIndDisplay() {
        if (getActiveInd() == null) {
            return EdenConstants.INACTIVE_LABEL_LOWER;
        }
        return CodeTranslator.getActiveIndicatorLabel(getActiveInd());
    }

    public List<BaseWorkgroupMember> getWorkgroupMembers() {
        return workgroupMembers;
    }

    public void setWorkgroupMembers(List<BaseWorkgroupMember> members) {
        this.workgroupMembers = members;
    }

    public Boolean getCurrentInd() {
		return currentInd;
	}

	public void setCurrentInd(Boolean currentInd) {
		this.currentInd = currentInd;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public List<Extension> getExtensions() {
		return extensions;
	}

	public void setExtensions(List<Extension> extensions) {
		this.extensions = extensions;
	}

	/**
     * Constructs the List of WorkflowUsers for the given workgroup from it's list of "workgroupMembers" and sets that
     * as the List of "members" on this Workgroup.
     */
	public void materializeMembers() throws EdenUserNotFoundException {
    	// clear it out first, just to be safe
    	getMembers().clear();
		for (Iterator iter = getWorkgroupMembers().iterator(); iter.hasNext();) {
			BaseWorkgroupMember member = (BaseWorkgroupMember) iter.next();
			if (EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD.equals(member.getMemberType())) {
				try {
					getMembers().add(KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(member.getWorkflowId())));
				} catch (IllegalArgumentException e) {
					LOG.error("Problem retrieving user from user service, workflowId=" + member.getWorkflowId(), e);
				}
			} else if (EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD.equals(member.getMemberType())) {
				Workgroup workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(Long.parseLong(member.getWorkflowId())));
				if (workgroup == null) {
					LOG.error("Problem retrieving workgroup from workgroup service, workgroup id=" + member.getWorkgroupId());
					continue;
				}
				getMembers().add(workgroup);
			}
		}
    }

	public int hashCode() {
        if (workgroupId == null) return super.hashCode();
        return workgroupId.hashCode();
    }

    public String toString() {
        return "[BaseWorkgroup: name=" + workgroupName
                + ", type=" + workgroupType
                + ", groupId=" + workgroupId
                + ", displayName=" + getDisplayName()
                + ", description=" + description
                + ", lockVerNbr=" + lockVerNbr
                + ", activeInd=" + activeInd + "]";
    }
}