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


/**
 * Transport for representing a user, workgroup, or role associated with and request
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ResponsiblePartyDTO implements Serializable {

    static final long serialVersionUID = 5716093378476396724L;

    private UserIdDTO userId;
	private WorkgroupIdDTO workgroupId;
	private String roleName;

    public ResponsiblePartyDTO() {}
    
    public ResponsiblePartyDTO(WorkgroupIdDTO workgroupId) {
        this.workgroupId = workgroupId;
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
    
    public boolean isWorkgroup() {
        return workgroupId != null;
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
            sb.append("workgroupID=");
            sb.append(workgroupId == null ? "null" : workgroupId.toString());
        }

        sb.append("]");

        return sb.toString();
    }
    
	public WorkgroupIdDTO getWorkgroupId() {
		return workgroupId;
	}
	
	public void setWorkgroupId(WorkgroupIdDTO workgroupId) {
	    this.workgroupId = workgroupId;
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
}
