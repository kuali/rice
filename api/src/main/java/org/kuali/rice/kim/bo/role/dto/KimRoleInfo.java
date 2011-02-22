/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.role.dto;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.kuali.rice.kim.bo.Role;

import java.io.Serializable;


/**
 * Simple Role data designed in DTO form for consumption by web services. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimRoleInfo implements Role, Serializable {

	protected String roleId;
	protected String roleName;
	protected String roleDescription;
	protected boolean active;
	protected String kimTypeId;
	protected String namespaceCode;
	
	public String getRoleId() {
		return this.roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return this.roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleDescription() {
		return this.roleDescription;
	}
	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}
	public boolean isActive() {
		return this.active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getKimTypeId() {
		return this.kimTypeId;
	}
	public void setKimTypeId(String kimTypeId) {
		this.kimTypeId = kimTypeId;
	}
	public String getNamespaceCode() {
		return this.namespaceCode;
	}
	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.append("roleId", this.roleId)
				.append("namespaceCode", this.namespaceCode)
				.append("roleName", this.roleName)
				.append("active", this.active)
				.append("kimTypeId", this.kimTypeId)
				.toString();
	}
	
    public void refresh(){
    	
    }
}
