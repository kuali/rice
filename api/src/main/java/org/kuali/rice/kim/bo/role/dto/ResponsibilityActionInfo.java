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

import org.kuali.rice.core.util.AttributeSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ResponsibilityActionInfo implements Serializable {

	private static final long serialVersionUID = 308199072590100177L;
	
	protected String principalId;
	protected String groupId;
	protected String memberRoleId; 
	protected String responsibilityNamespaceCode;
	protected String responsibilityName;
	protected String responsibilityId;
	protected String roleId;
	protected String actionTypeCode;
	protected String actionPolicyCode;
	protected String roleResponsibilityActionId;
	protected String parallelRoutingGroupingCode = "";
	protected boolean forceAction;
	protected Integer priorityNumber;
	protected AttributeSet qualifier;
	protected List<DelegateInfo> delegates = new ArrayList<DelegateInfo>();

	/**
	 * 
	 */
	public ResponsibilityActionInfo() {
	}
	
	public ResponsibilityActionInfo(String principalId, String groupId, String memberRoleId, KimResponsibilityInfo responsibility,
			String roleId, AttributeSet qualifier, List<DelegateInfo> delegates ) {
		this.principalId = principalId;
		this.groupId = groupId;
		this.memberRoleId = memberRoleId;
		this.responsibilityNamespaceCode = responsibility.getNamespaceCode();
		this.responsibilityName = responsibility.getName();
		this.responsibilityId = responsibility.getResponsibilityId();
		this.roleId = roleId;
		this.qualifier = qualifier;
		this.delegates = delegates;
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

	public String getPrincipalId() {
		return this.principalId;
	}

	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}

	public String getResponsibilityName() {
		return this.responsibilityName;
	}

	public void setResponsibilityName(String responsibilityName) {
		this.responsibilityName = responsibilityName;
	}

	public String getRoleId() {
		return this.roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public AttributeSet getQualifier() {
		return this.qualifier;
	}

	public void setQualifier(AttributeSet qualifier) {
		this.qualifier = qualifier;
	}

	public List<DelegateInfo> getDelegates() {
		return this.delegates;
	}

	public void setDelegates(List<DelegateInfo> delegates) {
		this.delegates = delegates;
	}

	public String getResponsibilityNamespaceCode() {
		return this.responsibilityNamespaceCode;
	}

	public void setResponsibilityNamespaceCode(String responsibilityNamespaceCode) {
		this.responsibilityNamespaceCode = responsibilityNamespaceCode;
	}

	/**
	 * @return the responsibilityId
	 */
	public String getResponsibilityId() {
		return this.responsibilityId;
	}

	/**
	 * @param responsibilityId the responsibilityId to set
	 */
	public void setResponsibilityId(String responsibilityId) {
		this.responsibilityId = responsibilityId;
	}

	public String getActionPolicyCode() {
		return this.actionPolicyCode;
	}

	public void setActionPolicyCode(String actionPolicyCode) {
		this.actionPolicyCode = actionPolicyCode;
	}

	public String getMemberRoleId() {
		return this.memberRoleId;
	}

	public void setMemberRoleId(String memberRoleId) {
		this.memberRoleId = memberRoleId;
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

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation 
	 * of this object.
	 */
	public String toString() {
	    final String TAB = "\n";
	    
	    String retValue = "ResponsibilityActionInfo ( "
	        + "principalId = " + this.principalId + TAB
	        + "groupId = " + this.groupId + TAB
	        + "memberRoleId = " + this.memberRoleId + TAB
	        + "responsibilityNamespaceCode = " + this.responsibilityNamespaceCode + TAB
	        + "responsibilityName = " + this.responsibilityName + TAB
	        + "responsibilityId = " + this.responsibilityId + TAB
	        + "roleId = " + this.roleId + TAB
	        + "actionTypeCode = " + this.actionTypeCode + TAB
	        + "actionPolicyCode = " + this.actionPolicyCode + TAB
	        + "forceAction = " + this.forceAction + TAB
	        + "priorityNumber = " + this.priorityNumber + TAB
	        + "qualifier = " + this.qualifier + TAB
	        + "delegates = " + this.delegates + TAB
	        + " )";
	
	    return retValue;
	}

	public String getParallelRoutingGroupingCode() {
		return this.parallelRoutingGroupingCode;
	}

	public void setParallelRoutingGroupingCode(String actionGroupingCode) {
		this.parallelRoutingGroupingCode = actionGroupingCode;
	}

	public String getRoleResponsibilityActionId() {
		return this.roleResponsibilityActionId;
	}

	public void setRoleResponsibilityActionId(String roleResponsibilityActionId) {
		this.roleResponsibilityActionId = roleResponsibilityActionId;
	}
	
	
}
