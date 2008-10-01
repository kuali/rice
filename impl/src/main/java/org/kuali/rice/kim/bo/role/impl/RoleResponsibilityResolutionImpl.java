/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.role.impl;

import org.kuali.rice.kim.bo.role.RoleResponsibilityResolution;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoleResponsibilityResolutionImpl implements RoleResponsibilityResolution {

	protected String roleResponsibilityResolutionId;
	protected String responsibilityId;
	protected String roleId;
	protected String principalId;
	protected String actionTypeCode;
	protected Integer priorityNumber;
	protected boolean active;
	
	protected KimResponsibilityImpl kimResponsibility;
	
	public String getRoleResponsibilityResolutionId() {
		return this.roleResponsibilityResolutionId;
	}
	public void setRoleResponsibilityResolutionId(String roleResponsibilityResolutionId) {
		this.roleResponsibilityResolutionId = roleResponsibilityResolutionId;
	}
	public String getResponsibilityId() {
		return this.responsibilityId;
	}
	public void setResponsibilityId(String responsibilityId) {
		this.responsibilityId = responsibilityId;
	}
	public String getRoleId() {
		return this.roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getPrincipalId() {
		return this.principalId;
	}
	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}
	public String getActionTypeCode() {
		return this.actionTypeCode;
	}
	public void setActionTypeCode(String actionTypeCode) {
		this.actionTypeCode = actionTypeCode;
	}
	public Integer getPriorityNumber() {
		return this.priorityNumber;
	}
	public void setPriorityNumber(Integer priorityNumber) {
		this.priorityNumber = priorityNumber;
	}
	public boolean isActive() {
		return this.active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public KimResponsibilityImpl getKimResponsibility() {
		return this.kimResponsibility;
	}
	public void setKimResponsibility(KimResponsibilityImpl kimResponsibility) {
		this.kimResponsibility = kimResponsibility;
	}
	
	
}
