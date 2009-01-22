/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.bo.ui;

import java.util.LinkedHashMap;

import org.kuali.rice.kew.util.CodeTranslator;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimDocumentRoleResponsibilityAction extends KimDocumentBoBase {
	protected String roleResponsibilityActionId;
	protected String roleResponsibilityId;
	protected String roleMemberId;
	protected String actionTypeCode;
	protected String actionPolicyCode;
	protected Integer priorityNumber;
	
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
	
	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap lhm = new LinkedHashMap();
		lhm.put( "roleResponsibilityActionId", roleResponsibilityActionId );
		lhm.put( "roleResponsibilityId", roleResponsibilityId );
		lhm.put( "roleMemberId", roleMemberId );
		lhm.put( "actionTypeCode", actionTypeCode );
		return lhm;
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
	 * 
	 * This method fore readonlyalterdisplay
	 * 
	 * @return
	 */
	public String getActionPolicyDescription() {
		return (String)CodeTranslator.approvePolicyLabels.get(this.actionPolicyCode);
	}

	/**
	 * 
	 * This method fore readonlyalterdisplay
	 * 
	 * @return
	 */
	public String getActionTypeDescription() {
		return (String)CodeTranslator.arLabels.get(this.actionTypeCode);
	}

}
