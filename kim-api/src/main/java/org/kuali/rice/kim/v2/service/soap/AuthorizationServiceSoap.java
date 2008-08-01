package org.kuali.rice.kim.v2.service.soap;

import java.util.List;

import org.kuali.rice.kim.dto.PermissionDTO;
import org.kuali.rice.kim.dto.RoleDTO;
import org.kuali.rice.kim.v2.service.AuthorizationServiceBase;

public interface AuthorizationServiceSoap extends AuthorizationServiceBase {
    public List<RoleDTO> getWebServiceSafeRolesForPrincipal(String principalId);
    
    public List<PermissionDTO> getWebServiceSafePermissionsForRole(String roleId);
}