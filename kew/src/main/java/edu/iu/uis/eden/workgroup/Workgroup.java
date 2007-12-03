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

import java.io.Serializable;
import java.util.List;

import org.kuali.workflow.attribute.Extension;

import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * A group of users.
 *
 * @see WorkflowUser
 * @see WorkgroupService
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface Workgroup extends Recipient, Serializable {


	/**
	 * Retrieves the ID of the workgroup.
	 */
	public WorkflowGroupId getWorkflowGroupId();

	/**
	 * Retrieves the name of the workgroup.
	 */
	public GroupNameId getGroupNameId();

	/**
	 * Retrieves the description of the workgroup.
	 */
    public String getDescription();

    /**
     * Returns all users and workgroups that are members of this workgroup.
     */
    public List<Recipient> getMembers();

    /**
     * Returns all users in this workgroup, including those that are members
     * of any nested workgroups.
     */
    public List<WorkflowUser> getUsers();

    public boolean hasMember(Recipient member);
    public Boolean getActiveInd();
    public Integer getLockVerNbr();
    public String getWorkgroupType();
    public List<Extension> getExtensions();

    public void setWorkflowGroupId(WorkflowGroupId workflowGroupId);
    public void setGroupNameId(GroupNameId groupNameId);
    public void setDescription(String description);
    public void setMembers(List<Recipient> members);
    public void setActiveInd(Boolean activeInd);
    public void setWorkgroupType(String workgroupType);
    public void setExtensions(List<Extension> extensions);

}