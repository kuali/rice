/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kew.dto;

import java.io.Serializable;

/**
 * Transport for representing a principal, group, or role associated with and request
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ResponsiblePartyDTO implements Serializable {

    static final long serialVersionUID = 5716093378476396724L;

    private String principalId;
	private String roleName;
	private String groupId;
	    
    public boolean isPrincipal() {
        return principalId != null;
    }
    
    public boolean isGroup() {
        return groupId != null;
    }
    
    public boolean isRole() {
        return roleName != null;
    }
    	
	public String getPrincipalId() {
		return principalId;
	}
	
	public void setPrincipalId(String principalId) {
	    this.principalId = principalId;
	}

    public String getRoleName() {
        return roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public static ResponsiblePartyDTO fromGroupId(String groupId) {
		ResponsiblePartyDTO responsibleParty = new ResponsiblePartyDTO();
		responsibleParty.setGroupId(groupId);
		return responsibleParty;
	}
	
	public static ResponsiblePartyDTO fromPrincipalId(String principalId) {
		ResponsiblePartyDTO responsibleParty = new ResponsiblePartyDTO();
		responsibleParty.setPrincipalId(principalId);
		return responsibleParty;
	}
	
	public static ResponsiblePartyDTO fromRoleName(String roleName) {
		ResponsiblePartyDTO responsibleParty = new ResponsiblePartyDTO();
		responsibleParty.setRoleName(roleName);
		return responsibleParty;
	}
	
    public String toString() {
        StringBuffer sb = new StringBuffer("[");
        sb.append("principalId=").append(getPrincipalId()).append(", ");
        sb.append("groupId=").append(getGroupId()).append(", ");
        sb.append("roleName=").append(getRoleName());
        sb.append("]");
        return sb.toString();
    }

}
