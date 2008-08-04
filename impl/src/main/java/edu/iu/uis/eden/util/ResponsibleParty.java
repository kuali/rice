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
package edu.iu.uis.eden.util;

import java.io.Serializable;

import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.workgroup.GroupId;

/**
 * A user, workgroup, or role who is responsible for an Action Request.
 * 
 * @see ActionRequestValue
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ResponsibleParty implements Serializable {

	private static final long serialVersionUID = 6788236688949489851L;

	private UserId userId;
    private GroupId groupId;
    private String roleName;

    public ResponsibleParty() {
    }

    public ResponsibleParty(GroupId groupId) {
        this.groupId = groupId;
    }

    public ResponsibleParty(UserId userId) {
        this.userId = userId;
    }

    public ResponsibleParty(String roleName) {
        this.roleName = roleName;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("[");
        if (userId != null) {
            sb.append("user=");
            sb.append(userId.toString());
        } else if (groupId != null) {
            sb.append("workgroupID=");
            sb.append(groupId.toString());
        } else if (roleName != null) {
            sb.append("roleName=");
            sb.append(roleName);
        }
        sb.append("]");
        return sb.toString();
    }

    public GroupId getGroupId() {
        return groupId;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setGroupId(GroupId groupId) {
        this.groupId = groupId;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public boolean isUser() {
        return getUserId() != null;
    }

    public boolean isWorkgroup() {
        return getGroupId() != null;
    }

    public boolean isRole() {
        return getRoleName() != null;
    }
    
}
