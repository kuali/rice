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
package org.kuali.rice.kns.authorization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.role.dto.PermissionAssigneeInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PermissionDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {

	private static PermissionService permissionService;
	private String roleNamesapce;
	private String roleName;
	private String permissionTemplateNamespace;
	private String permissionTemplateName;
	/**
	 * @return the roleNamesapce
	 */
	public String getRoleNamesapce() {
		return this.roleNamesapce;
	}
	/**
	 * @param roleNamesapce the roleNamesapce to set
	 */
	public void setRoleNamesapce(String roleNamesapce) {
		this.roleNamesapce = roleNamesapce;
	}
	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return this.roleName;
	}
	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	/**
	 * @return the permissionTemplateNamespace
	 */
	public String getPermissionTemplateNamespace() {
		return this.permissionTemplateNamespace;
	}
	/**
	 * @param permissionTemplateNamespace the permissionTemplateNamespace to set
	 */
	public void setPermissionTemplateNamespace(String permissionTemplateNamespace) {
		this.permissionTemplateNamespace = permissionTemplateNamespace;
	}
	/**
	 * @return the permissionTemplateName
	 */
	public String getPermissionTemplateName() {
		return this.permissionTemplateName;
	}
	/**
	 * @param permissionTemplateName the permissionTemplateName to set
	 */
	public void setPermissionTemplateName(String permissionTemplateName) {
		this.permissionTemplateName = permissionTemplateName;
	}
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getGroupIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	public List<String> getGroupIdsFromApplicationRole(String namespaceCode,
			String roleName, AttributeSet qualification) {
		List<PermissionAssigneeInfo> permissionAssignees = getPermissionAssignees(namespaceCode, roleName, qualification);
		List<String> groupIds = new ArrayList<String>();
		for (PermissionAssigneeInfo permissionAssigneeInfo : permissionAssignees) {
			if (StringUtils.isNotBlank(permissionAssigneeInfo.getGroupId())) {
				groupIds.add(permissionAssigneeInfo.getGroupId());
			}
		}
		return groupIds;
	}
	
	protected List<PermissionAssigneeInfo> getPermissionAssignees(String roleNamespace,
			String roleName, AttributeSet qualification) {
		if (!StringUtils.equals(roleNamespace, this.roleNamesapce) || !StringUtils.equals(roleName, this.roleName)) {
			throw new RuntimeException("The role type is not configured to support this role");
		}
		List<PermissionAssigneeInfo> permissionAssignees = getPermissionService().getPermissionAssigneesForTemplateName(permissionTemplateNamespace, permissionTemplateName, qualification, qualification);
		return permissionAssignees;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	public List<String> getPrincipalIdsFromApplicationRole(String namespaceCode,
			String roleName, AttributeSet qualification) {
		List<PermissionAssigneeInfo> permissionAssignees = getPermissionAssignees(namespaceCode, roleName, qualification);
		List<String> principalIds = new ArrayList<String>();
		for (PermissionAssigneeInfo permissionAssigneeInfo : permissionAssignees) {
			if (StringUtils.isNotBlank(permissionAssigneeInfo.getPrincipalId())) {
				principalIds.add(permissionAssigneeInfo.getPrincipalId());
			}
		}
		return principalIds;
	}
	
    /***
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#hasApplicationRole(java.lang.String, java.util.List, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public boolean hasApplicationRole(
            String principalId, List<String> groupIds, String namespaceCode, String roleName, AttributeSet qualification){
    	boolean hasApplicationRole = false;
    	List<PermissionAssigneeInfo> permissionAssignees = getPermissionAssignees(namespaceCode, roleName, qualification);
		for (PermissionAssigneeInfo permissionAssigneeInfo : permissionAssignees) {
			if (StringUtils.isNotBlank(permissionAssigneeInfo.getPrincipalId())) {
				hasApplicationRole = principalId.equals(permissionAssigneeInfo.getPrincipalId());
				break;
			}
		}
		return hasApplicationRole;
    }
    
    /**
     * @return the documentService
     */
    protected PermissionService getPermissionService(){
        if (permissionService == null ) {
        	permissionService = KIMServiceLocator.getPermissionService();
        }
        return permissionService;
    }
	
}
