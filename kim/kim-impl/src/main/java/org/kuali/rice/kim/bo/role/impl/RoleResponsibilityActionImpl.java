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
package org.kuali.rice.kim.bo.role.impl;

import org.hibernate.annotations.Type;
import org.kuali.rice.kim.bo.role.dto.RoleResponsibilityActionInfo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.*;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name="KRIM_ROLE_RSP_ACTN_T")
public class RoleResponsibilityActionImpl extends PersistableBusinessObjectBase {

	private static final long serialVersionUID = -2840071737863303404L;
	
	@Id
	@Column(name="ROLE_RSP_ACTN_ID")
	protected String roleResponsibilityActionId;
	@Column(name="ROLE_RSP_ID")
	protected String roleResponsibilityId;
	@Column(name="ROLE_MBR_ID")
	protected String roleMemberId;
	@Column(name="ACTN_TYP_CD")
	protected String actionTypeCode;
	@Column(name="ACTN_PLCY_CD")
	protected String actionPolicyCode;
	@Column(name="FRC_ACTN")
	@Type(type="yes_no")
	protected boolean forceAction;
	@Column(name="PRIORITY_NBR")
	protected Integer priorityNumber;
	
	@ManyToOne(targetEntity=RoleResponsibilityImpl.class, fetch = FetchType.EAGER, cascade = {})
	@JoinColumn(name="ROLE_RSP_ID",insertable=false, updatable=false)
	protected RoleResponsibilityImpl roleResponsibility;
	
	public String getRoleResponsibilityActionId() {
		return this.roleResponsibilityActionId;
	}
	public void setRoleResponsibilityActionId(String roleResponsibilityResolutionId) {
		this.roleResponsibilityActionId = roleResponsibilityResolutionId;
	}
	public String getRoleResponsibilityId() {
		return this.roleResponsibilityId;
	}
	public void setRoleResponsibilityId(String roleResponsibilityId) {
		this.roleResponsibilityId = roleResponsibilityId;
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

	public String getActionPolicyCode() {
		return this.actionPolicyCode;
	}
	public void setActionPolicyCode(String actionPolicyCode) {
		this.actionPolicyCode = actionPolicyCode;
	}
	public String getRoleMemberId() {
		return this.roleMemberId;
	}
	public void setRoleMemberId(String roleMemberId) {
		this.roleMemberId = roleMemberId;
	}
	public RoleResponsibilityImpl getRoleResponsibility() {
		return this.roleResponsibility;
	}
	public void setRoleResponsibility(RoleResponsibilityImpl roleResponsibility) {
		this.roleResponsibility = roleResponsibility;
	}
	/**
	 * @return the forceAction
	 */
	public boolean isForceAction() {
		return this.forceAction;
	}
	/**
	 * @param forceAction the forceAction to set
	 */
	public void setForceAction(boolean forceAction) {
		this.forceAction = forceAction;
	}
	
	public RoleResponsibilityActionInfo toSimpleInfo(){
		RoleResponsibilityActionInfo roleResponsibilityActionInfo = new RoleResponsibilityActionInfo();
		roleResponsibilityActionInfo.setActionPolicyCode(actionPolicyCode);
		roleResponsibilityActionInfo.setActionTypeCode(actionTypeCode);
		roleResponsibilityActionInfo.setForceAction(forceAction);
		roleResponsibilityActionInfo.setPriorityNumber(priorityNumber);
		roleResponsibilityActionInfo.setRoleMemberId(roleMemberId);
		roleResponsibilityActionInfo.setRoleResponsibilityInfo(roleResponsibility.toSimpleInfo());
		roleResponsibilityActionInfo.setRoleResponsibilityActionId(roleResponsibilityActionId);
		roleResponsibilityActionInfo.setRoleResponsibilityId(roleResponsibilityId);
		return roleResponsibilityActionInfo;
	}
}
