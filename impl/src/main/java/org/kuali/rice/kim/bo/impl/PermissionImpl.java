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
package org.kuali.rice.kim.bo.impl;

import java.util.List;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.group.impl.KimGroupImpl;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PermissionImpl extends KimPermissionImpl {

	private static final long serialVersionUID = 1L;
	
	List<KimRoleImpl> assignedToRoles = new TypedArrayList(KimRoleImpl.class);
	
	protected String assignedToRoleNamespaceForLookup;
	protected String assignedToRoleNameForLookup;
	protected KimRoleImpl assignedToRole;
	protected String assignedToPrincipalNameForLookup;
	protected Person assignedToPrincipal;
	protected String assignedToGroupNamespaceForLookup;
	protected String assignedToGroupNameForLookup;
	protected KimGroupImpl assignedToGroup;
	protected String attributeValue;
	
	/**
	 * @return the assignedToRoles
	 */
	public String getAssignedToRolesToDisplay() {
		StringBuffer assignedToRolesToDisplay = new StringBuffer();
		for(KimRoleImpl roleImpl: assignedToRoles){
			assignedToRolesToDisplay.append(getRoleDetailsToDisplay(roleImpl));
		}
        if(assignedToRolesToDisplay.toString().endsWith(KimConstants.COMMA_SEPARATOR))
        	assignedToRolesToDisplay.delete(
        			assignedToRolesToDisplay.length()-KimConstants.COMMA_SEPARATOR.length(), assignedToRolesToDisplay.length());

		return assignedToRolesToDisplay.toString();
	}

	public String getRoleDetailsToDisplay(KimRoleImpl roleImpl){
		return roleImpl.getNamespaceCode()+" "+roleImpl.getRoleName()+KimConstants.COMMA_SEPARATOR;
	}
	
	/**
	 * @return the assignedToGroupNameForLookup
	 */
	public String getAssignedToGroupNameForLookup() {
		return this.assignedToGroupNameForLookup;
	}

	/**
	 * @param assignedToGroupNameForLookup the assignedToGroupNameForLookup to set
	 */
	public void setAssignedToGroupNameForLookup(String assignedToGroupNameForLookup) {
		this.assignedToGroupNameForLookup = assignedToGroupNameForLookup;
	}

	/**
	 * @return the assignedToGroupNamespaceForLookup
	 */
	public String getAssignedToGroupNamespaceForLookup() {
		return this.assignedToGroupNamespaceForLookup;
	}

	/**
	 * @param assignedToGroupNamespaceForLookup the assignedToGroupNamespaceForLookup to set
	 */
	public void setAssignedToGroupNamespaceForLookup(
			String assignedToGroupNamespaceForLookup) {
		this.assignedToGroupNamespaceForLookup = assignedToGroupNamespaceForLookup;
	}

	/**
	 * @return the assignedToPrincipalNameForLookup
	 */
	public String getAssignedToPrincipalNameForLookup() {
		return this.assignedToPrincipalNameForLookup;
	}

	/**
	 * @param assignedToPrincipalNameForLookup the assignedToPrincipalNameForLookup to set
	 */
	public void setAssignedToPrincipalNameForLookup(
			String assignedToPrincipalNameForLookup) {
		this.assignedToPrincipalNameForLookup = assignedToPrincipalNameForLookup;
	}

	/**
	 * @return the assignedToRoleNameForLookup
	 */
	public String getAssignedToRoleNameForLookup() {
		return this.assignedToRoleNameForLookup;
	}

	/**
	 * @param assignedToRoleNameForLookup the assignedToRoleNameForLookup to set
	 */
	public void setAssignedToRoleNameForLookup(String assignedToRoleNameForLookup) {
		this.assignedToRoleNameForLookup = assignedToRoleNameForLookup;
	}

	/**
	 * @return the assignedToRoleNamespaceForLookup
	 */
	public String getAssignedToRoleNamespaceForLookup() {
		return this.assignedToRoleNamespaceForLookup;
	}

	/**
	 * @param assignedToRoleNamespaceForLookup the assignedToRoleNamespaceForLookup to set
	 */
	public void setAssignedToRoleNamespaceForLookup(
			String assignedToRoleNamespaceForLookup) {
		this.assignedToRoleNamespaceForLookup = assignedToRoleNamespaceForLookup;
	}

	/**
	 * @return the attributeValue
	 */
	public String getAttributeValue() {
		return this.attributeValue;
	}

	/**
	 * @param attributeValue the attributeValue to set
	 */
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	/**
	 * @return the assignedToRoles
	 */
	public List<KimRoleImpl> getAssignedToRoles() {
		return this.assignedToRoles;
	}

	/**
	 * @param assignedToRoles the assignedToRoles to set
	 */
	public void setAssignedToRoles(List<KimRoleImpl> assignedToRoles) {
		this.assignedToRoles = assignedToRoles;
	}

	/**
	 * @return the assignedToGroup
	 */
	public KimGroupImpl getAssignedToGroup() {
		return this.assignedToGroup;
	}

	/**
	 * @param assignedToGroup the assignedToGroup to set
	 */
	public void setAssignedToGroup(KimGroupImpl assignedToGroup) {
		this.assignedToGroup = assignedToGroup;
	}

	/**
	 * @return the assignedToPrincipal
	 */
	public Person getAssignedToPrincipal() {
		return this.assignedToPrincipal;
	}

	/**
	 * @param assignedToPrincipal the assignedToPrincipal to set
	 */
	public void setAssignedToPrincipal(Person assignedToPrincipal) {
		this.assignedToPrincipal = assignedToPrincipal;
	}

	/**
	 * @return the assignedToRole
	 */
	public KimRoleImpl getAssignedToRole() {
		return this.assignedToRole;
	}

	/**
	 * @param assignedToRole the assignedToRole to set
	 */
	public void setAssignedToRole(KimRoleImpl assignedToRole) {
		this.assignedToRole = assignedToRole;
	}

}