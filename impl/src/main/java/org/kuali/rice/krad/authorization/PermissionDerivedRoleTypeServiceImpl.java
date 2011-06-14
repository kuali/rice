/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.krad.authorization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.role.dto.PermissionAssigneeInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PermissionDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {

	private static IdentityManagementService identityManagementService;
	private String permissionTemplateNamespace;
	private String permissionTemplateName;
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
	
	protected List<PermissionAssigneeInfo> getPermissionAssignees(AttributeSet qualification) {
		return getIdentityManagementService().getPermissionAssigneesForTemplateName(permissionTemplateNamespace, permissionTemplateName, qualification, qualification);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.RoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.core.util.AttributeSet)
	 */
	@Override
    public List<RoleMembershipInfo> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
		List<PermissionAssigneeInfo> permissionAssignees = getPermissionAssignees(qualification);
		List<RoleMembershipInfo> members = new ArrayList<RoleMembershipInfo>();
		for (PermissionAssigneeInfo permissionAssigneeInfo : permissionAssignees) {
			if (StringUtils.isNotBlank(permissionAssigneeInfo.getPrincipalId())) {
			    members.add( new RoleMembershipInfo( null/*roleId*/, null, permissionAssigneeInfo.getPrincipalId(), Role.PRINCIPAL_MEMBER_TYPE, null));
			} else if (StringUtils.isNotBlank(permissionAssigneeInfo.getGroupId())) {
                members.add( new RoleMembershipInfo( null/*roleId*/, null, permissionAssigneeInfo.getGroupId(), Role.GROUP_MEMBER_TYPE, null));
			}
		}
		return members;
	}
	
    /***
     * @see org.kuali.rice.kim.service.support.impl.RoleTypeServiceBase#hasApplicationRole(java.lang.String, java.util.List, java.lang.String, java.lang.String, org.kuali.rice.core.util.AttributeSet)
     */
    @Override
    public boolean hasApplicationRole(
            String principalId, List<String> groupIds, String namespaceCode, String roleName, AttributeSet qualification){
        // FIXME: dangerous - data changes could cause an infinite loop - should add thread-local to trap state and abort
    	return getIdentityManagementService().isAuthorizedByTemplateName(principalId,permissionTemplateNamespace, permissionTemplateName, qualification, qualification);    
	}
    
    /**
     * @return the documentService
     */
    protected IdentityManagementService getIdentityManagementService(){
        if (identityManagementService == null ) {
        	identityManagementService = KimApiServiceLocator.getIdentityManagementService();
        }
        return identityManagementService;
    }
	
}
