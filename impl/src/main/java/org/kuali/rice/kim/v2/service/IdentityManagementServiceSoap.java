package org.kuali.rice.kim.v2.service;

import java.util.List;

import org.kuali.rice.kim.dto.EntityDTO;
import org.kuali.rice.kim.dto.GroupDTO;
import org.kuali.rice.kim.dto.PermissionDTO;
import org.kuali.rice.kim.dto.RoleDTO;

public interface IdentityManagementServiceSoap extends IdentityManagementServiceBase {
    public List<RoleDTO> getWebServiceSafeRolesForPrincipal(String principalId);
    
    public List<PermissionDTO> getWebServiceSafePermissionsForRole(String roleId);
    
    public List<GroupDTO> getWebServiceSafeGroupsForPrincipal(String principalId);

    public List<GroupDTO> getWebServiceSafeMemberGroups(String groupId);

    public List<GroupDTO> getWebServiceSafeDirectMemberGroups(String groupId);

	public EntityDTO getWebServiceSafeEntityByPrincipalName(String principalName);
}
