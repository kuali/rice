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
package org.kuali.rice.kim.service;

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

/**
 * Service API for accessing KIM Role services.  This contract should be used by all 
 * Kuali software which needs to leverage identity management features that require fine-grained
 * Role attributes. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface RoleService {
    /**
     * KIM service API method that returns a complete collection of Role objects for the application.
     * 
     * @return         List of RoleDTO objects for the application
     * 
     */
    public List<RoleDTO> getAllRoles();
    
    /**
     * KIM service API method that returns a complete collection of Role names for the application.
     * 
     * @return         List of Role names for the application
     * 
     */
    public List<String> getAllRoleNames();
    
    /**
     * KIM service API method that returns a RoleDTO object.
     * @param roleName
     * @return A RoleDTO object
     * 
     */
    public RoleDTO getRole(String roleName);
    
    /**
     * KIM service API method that returns a RoleDTO object.
     * @param roleId
     * @return A RoleDTO object
     * 
     */
    public RoleDTO getRole(Long roleId);
    
    /**
     * KIM Role service API method that returns all PrincipalDTO objects within an application
     * that belong to a given Role.
     * 
     * @param   roleName             String that identifies a unique KIM Role
     * @return                       List of all PrincipalDTO objects that are assigned to the Role
     * 
     */
    public List<PrincipalDTO> getPrincipalsWithRole(String roleName);
    
    /**
     * KIM Role service API method that returns principal names for all Principal objects 
     * within an application that belong to a given Role.
     * 
     * @param   roleName             name of KIM Role
     * @return                       List of principal names associated with Principal objects 
     *                               assigned to the Role
     * 
     */
    public List<String> getPrincipalNamesWithRole(String roleName);
    
    /**
     * KIM Role service API method that returns all PersonDTO objects within an application
     * that belong to a given Role.
     * 
     * @param   roleName             String that identifies a unique KIM Role
     * @return                       List of all PersonDTO objects that are assigned to the Role
     * 
     */
    public List<PersonDTO> getPersonsWithRole(String roleName);
    
    /**
     * KIM Role service API method that returns person ids for all Person objects 
     * within an application that belong to a given Role.
     * 
     * @param   roleName             name of KIM Role
     * @return                       List of person ids associated with Person objects 
     *                               assigned to the Role
     * 
     */
    public List<Long> getPersonIdsWithRole(String roleName);
    
    /**
     * KIM Role service API method that returns all EntityDTO objects within an application
     * that belong to a given Role.
     * 
     * @param   roleName             String that identifies a unique KIM Role
     * @return                       List of all EntityDTO objects that are assigned to the Role
     * 
     */
    public List<EntityDTO> getEntitysWithRole(String roleName);
    
    /**
     * KIM Role service API method that returns entity ids for all Entity objects 
     * within an application that belong to a given Role.
     * 
     * @param   roleName             name of KIM Role
     * @return                       List of entity ids associated with Entity objects 
     *                               assigned to the Role
     * 
     */
    public List<Long> getEntityIdsWithRole(String roleName);
    
    /**
     * KIM Role service API method that returns all GroupDTO objects within an application
     * that have been assigned a given Role.
     * 
     * @param   roleName             name of KIM Role
     * @return                       List of all GroupDTO objects assigned to the Role
     * 
     */
    public List<GroupDTO> getGroupsWithRole(String roleName);
    
    /**
     * KIM Role service API method that returns group names identifying all Group objects within an 
     * application that have been assigned a given Role.
     * 
     * @param   roleName             name of KIM Role
     * @return                       List of all Group names assigned to the Role
     * 
     */
    public List<String> getGroupNamesWithRole(String roleName);
    
    /**
     * KIM Role service API method that returns all PermissionDTO objects within an application
     * that satisfy a given Role.
     * 
     * @param   roleName             name of KIM Role
     * @return                       List of all PermissionDTO objects that satisfy the Role
     * 
     */
    public List<PermissionDTO> getPermissionsForRole(String roleName);
    
    /**
     * KIM Role service API method that returns permissions names identifying all Permission objects within 
     * an application that satisfy a given Role.
     * 
     * @param   roleName             name of KIM Role
     * @return                       List of all Permission names that satisfy the Role
     * 
     */
    public List<String> getPermissionNamesForRole(String roleName);
    
    /**
     * KIM Role service API method that returns all PrincipalQualifiedRoleDTO objects within an application
     * that associate with a given Role.
     * 
     * @param   roleName             name of KIM Role
     * @return                       List of all PrincipalQualifiedRoleDTO objects associated with the Role
     * 
     */
    public List<PrincipalQualifiedRoleDTO> getPrincipalQualifiedRoles(String roleName);

    /**
     * KIM Role service API method that returns all PrincipalQualifiedRoleDTO objects within an application
     * that match a given Role and also match List of qualified role attributes.
     * 
     * @param   roleName                 String that identifies a unique KIM Role
     * @param   qualifiedRoleAttributes  Map<String, String> of role attribute name/value pairs
     *                                   to qualify a person
     * @return                           List of all PrincipalQualifiedRoleDTO objects that match 
     *                                   the role and qualified role attributes
     * 
     */
     public List<PrincipalQualifiedRoleDTO> getPrincipalQualifiedRoles(String roleName, 
	     Map<String, String> qualifiedRoleAttributes);
    /**
     * KIM Role service API method that returns all GroupQualifiedRoleDTO objects within an application
     * that associate with a given Role.
     * 
     * @param   roleName             String that identifies a unique KIM Role
     * @return                       List of all GroupQualifiedRoleDTO objects associated with the Role 
     */
    public List<GroupQualifiedRoleDTO> getGroupQualifiedRoles(String roleName);
    /**
     * KIM Role service API method that returns all GroupQualifiedRoleDTO objects within an application
     * that match a given Role and also match List of qualified role attributes.
     * 
     * @param   roleName                 String that identifies a unique KIM Role
     * @param   qualifiedRoleAttributes  Map<String, String> of role attribute name/value pairs
     *                                   to qualify a group
     * @return                           List of all GroupQualifiedRoleDTO objects that match 
     *                                   the role and qualified role attributes
     * 
     */
    public List<GroupQualifiedRoleDTO> getGroupQualifiedRoles(String roleName, 
	    Map<String, String> qualifiedRoleAttributes);

}
