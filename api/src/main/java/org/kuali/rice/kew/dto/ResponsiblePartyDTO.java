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
package org.kuali.rice.kew.dto;

import java.io.Serializable;

import org.kuali.rice.kim.bo.group.dto.GroupInfo;


/**
 * Transport for representing a user, workgroup, or role associated with and request
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ResponsiblePartyDTO implements Serializable {

    static final long serialVersionUID = 5716093378476396724L;

    private UserIdDTO userId;
	private String roleName;
	private GroupInfo groupInfo;
	
    public ResponsiblePartyDTO() {}
    
    public ResponsiblePartyDTO(GroupInfo grpInfo)
    {
    	this.groupInfo =grpInfo;
    }
    
    public ResponsiblePartyDTO(UserIdDTO userId) {
        this.userId = userId;
    }

    public ResponsiblePartyDTO(String roleName) {
        this.roleName = roleName;
    }
    
    public boolean isUser() {
        return userId != null;
    }
    
    public boolean isGroup() {
        return groupInfo != null;
    }
    
    public boolean isRole() {
        return roleName != null;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("[");
        if (isUser()) {
            sb.append("user=");
            sb.append(userId == null ? "null" : userId.toString());
        } else {
            sb.append("groupInfo=");
            sb.append(groupInfo == null ? "null" : groupInfo.toString());
        }

        sb.append("]");

        return sb.toString();
    }
    	
	public UserIdDTO getUserId() {
		return userId;
	}
	
	public void setUserId(UserIdDTO userId) {
	    this.userId = userId;
	}

    public String getRoleName() {
        return roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

	/**
	 * @return the groupInfo
	 */
	public GroupInfo getGroupInfo() {
		return this.groupInfo;
	}

	/**
	 * @param groupInfo the groupInfo to set
	 */
	public void setGroupInfo(GroupInfo groupInfo) {
		this.groupInfo = groupInfo;
	}
}
