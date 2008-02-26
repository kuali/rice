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
package org.kuali.rice.kim.service.impl;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.dto.EntityDTO;
import org.kuali.rice.kim.dto.GroupDTO;
import org.kuali.rice.kim.dto.GroupQualifiedRoleDTO;
import org.kuali.rice.kim.dto.PermissionDTO;
import org.kuali.rice.kim.dto.PersonDTO;
import org.kuali.rice.kim.dto.PrincipalDTO;
import org.kuali.rice.kim.dto.PrincipalQualifiedRoleDTO;
import org.kuali.rice.kim.dto.RoleDTO;
import org.kuali.rice.kim.service.RoleService;

/**
 * This is the default KIM RoleService implementation that is provided by Rice.  This will mature over time as the KIM 
 * component is developed.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoleServiceImpl implements RoleService {
    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getAllRoleNames()
     */
    public List<String> getAllRoleNames() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getAllRoles()
     */
    public List<RoleDTO> getAllRoles() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }
    
    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getRole(java.lang.Long)
     */
    public RoleDTO getRole(Long roleId) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getRole(java.lang.String)
     */
    public RoleDTO getRole(String roleName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getGroupNamesWithRole(java.lang.String)
     */
    public List<String> getGroupNamesWithRole(String roleName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getGroupQualifiedRoles(java.lang.String, java.util.Map)
     */
    public List<GroupQualifiedRoleDTO> getGroupQualifiedRoles(String roleName, Map<String, String> qualifiedRoleAttributes) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getGroupQualifiedRoles(java.lang.String)
     */
    public List<GroupQualifiedRoleDTO> getGroupQualifiedRoles(String roleName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getGroupsWithRole(java.lang.String)
     */
    public List<GroupDTO> getGroupsWithRole(String roleName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getPermissionNamesForRole(java.lang.String)
     */
    public List<String> getPermissionNamesForRole(String roleName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getPermissionsForRole(java.lang.String)
     */
    public List<PermissionDTO> getPermissionsForRole(String roleName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getPersonIdsWithRole(java.lang.String)
     */
    public List<Long> getPersonIdsWithRole(String roleName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getPersonsWithRole(java.lang.String)
     */
    public List<PersonDTO> getPersonsWithRole(String roleName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getPrincipalNamesWithRole(java.lang.String)
     */
    public List<String> getPrincipalNamesWithRole(String roleName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getPrincipalsWithRole(java.lang.String)
     */
    public List<PrincipalDTO> getPrincipalsWithRole(String roleName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getPrincipalQualifiedRoles(java.lang.String)
     */
    public List<PrincipalQualifiedRoleDTO> getPrincipalQualifiedRoles(String roleName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getPrincipalQualifiedRoles(java.lang.String, java.util.Map)
     */
    public List<PrincipalQualifiedRoleDTO> getPrincipalQualifiedRoles(String roleName, Map<String, String> qualifiedRoleAttributes) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getEntityIdsWithRole(java.lang.String)
     */
    public List<Long> getEntityIdsWithRole(String roleName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getEntitysWithRole(java.lang.String)
     */
    public List<EntityDTO> getEntitysWithRole(String roleName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }
    
    
}
