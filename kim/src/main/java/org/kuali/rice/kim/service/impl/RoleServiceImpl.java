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

import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.GroupQualifiedRole;
import org.kuali.rice.kim.bo.Permission;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.PrincipalQualifiedRole;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.bo.Role;
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
    public List<Role> getAllRoles() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }
    
    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getRole(java.lang.Long)
     */
    public Role getRole(Long roleId) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getRole(java.lang.String)
     */
    public Role getRole(String roleName) {
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
    public List<GroupQualifiedRole> getGroupQualifiedRoles(String roleName, Map<String, String> qualifiedRoleAttributes) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getGroupQualifiedRoles(java.lang.String)
     */
    public List<GroupQualifiedRole> getGroupQualifiedRoles(String roleName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getGroupsWithRole(java.lang.String)
     */
    public List<Group> getGroupsWithRole(String roleName) {
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
    public List<Permission> getPermissionsForRole(String roleName) {
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
    public List<Person> getPersonsWithRole(String roleName) {
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
    public List<Principal> getPrincipalsWithRole(String roleName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getPrincipalQualifiedRoles(java.lang.String)
     */
    public List<PrincipalQualifiedRole> getPrincipalQualifiedRoles(String roleName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleService#getPrincipalQualifiedRoles(java.lang.String, java.util.Map)
     */
    public List<PrincipalQualifiedRole> getPrincipalQualifiedRoles(String roleName, Map<String, String> qualifiedRoleAttributes) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }
}
