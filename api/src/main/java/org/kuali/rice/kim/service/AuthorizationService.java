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

import java.util.Map;

/**
 * This service class is responsible for checking whether Persons, Groups, and Principals have the 
 * authorization for performing certain actions (i.e. Permissions). 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface AuthorizationService {
    /**
     * Determines if a Principal is authorized for a given permission.
     * 
     * @param   principalName        name identifying a principal
     * @param   permissionName       name identifying a unique permission
     * @param   namespaceName        The associated namespace to scope to
     * @return                       boolean indicating if Principal is authorized
     * 
     */
    public boolean isPrincipalAuthorized(String principalName, String permissionName, String namespaceName);
    
    /**
     * Determines if a Person is authorized for a given permission.  This service assumes that the id
     * being passed in is an entity id for an entity with an Entity Type of a Person.
     * 
     * @param   personId             person id identifying a unique KIM Person
     * @param   permissionName       name identifying a unique permission
     * @param   namespaceName        The associated namespace to scope to
     * @return                       boolean indicating if Person is authorized
     * 
     */
    public boolean isPersonAuthorized(Long personId, String permissionName, String namespaceName);
    
    /**
     * Determines if an Entity is authorized for a given permission.
     * 
     * @param   entityId             entity id identifying a unique KIM Entity
     * @param   permissionName       name identifying a unique permission
     * @param   namespaceName        The associated namespace to scope to
     * @return                       boolean indicating if Person is authorized
     * 
     */
    public boolean isEntityAuthorized(Long entityId, String permissionName, String namespaceName);
    
    /**
     * Determines if a Principal possesses a given permission and set of role attributes.
     * 
     * @param   principalName            name identifying a principal
     * @param   permissionName           name identifying a unique permission
     * @param   qualifiedRoleAttributes  Map<String, String> of role attribute name/value pairs
     *                                   to qualify a Person
     * @param   namespaceName            The associated namespace to scope to
     * @return                           boolean indicating if Principal possesses the permission and 
     *                                   all given Role attributes
     * 
     */
    public boolean isPrincipalAuthorizedForQualifiedPermission(String principalName, String permissionName, 
        Map<String, String> qualifiedRoleAttributes, String namespaceName);
    
    /**
     * Determines if a Person possesses a given qualified permission. This service assumes that the id
     * being passed in is an entity id for an entity with an Entity Type of a Person.
     * 
     * @param   personId                 person id identifying a unique Person
     * @param   permissionName           name identifying a unique permission
     * @param   qualifiedRoleAttributes  Map<String, String> of role attribute name/value pairs
     *                                   to qualify a Person
     * @param   namespaceName            The associated namespace to scope to
     * @return                           boolean indicating if Person possesses the permission and 
     *                                   all given Role attributes
     * 
     */
    public boolean isPersonAuthorizedForQualifiedPermission(Long personId, String permissionName, 
        Map<String, String> qualifiedRoleAttributes, String namespaceName);
    
    /**
     * Determines if an Entity possesses a given qualified permission.  
     * 
     * @param   entityId                 entity id identifying a unique Entity
     * @param   permissionName           name identifying a unique permission
     * @param   qualifiedRoleAttributes  Map<String, String> of role attribute name/value pairs
     *                                   to qualify a Person
     * @param   namespaceName            The associated namespace to scope to
     * @return                           boolean indicating if Person possesses the permission and 
     *                                   all given Role attributes
     * 
     */
    public boolean isEntityAuthorizedForQualifiedPermission(Long entityId, String permissionName, 
        Map<String, String> qualifiedRoleAttributes, String namespaceName);
    
    /**
     * Determines if a given Principal has been assigned to a given role.
     * 
     * @param   principalName        name identifying a principal
     * @param   roleName             name identifying a unique Role
     * @return                       boolean indicating if Principal has been assigned to Role
     * 
     */
    public boolean principalHasRole(String principalName, String roleName);
    
    /**
     * Determines if a given Person has been assigned to a given role. This service assumes that the id
     * being passed in is an entity id for an entity with an Entity Type of a Person.
     * 
     * @param   personId             person id uniquely identifying a person
     * @param   roleName             name identifying a unique Role
     * @return                       boolean indicating if Person has been assigned to Role
     * 
     */
    public boolean personHasRole(Long personId, String roleName);
    
    /**
     * Determines if a given Entity has been assigned to a given role.
     * 
     * @param   entityId             entity id uniquely identifying an Entity
     * @param   roleName             name identifying a unique Role
     * @return                       boolean indicating if Person has been assigned to Role
     * 
     */
    public boolean entityHasRole(Long entityId, String roleName);
    
    /**
     * Determines if a given Group has been assigned to a given role.
     * 
     * @param   groupName            group name uniquely identifying a group
     * @param   roleName             name identifying a unique Role
     * @return                       boolean indicating if Group has been assigned to Role
     * 
     */
    public boolean groupHasRole(String groupName, String roleName);
    
    /**
     * Determines if a given Principal possesses all given Role attributes.
     * 
     * @param   principalName               name identifying a principal
     * @param   roleName                    name identifying a unique Role
     * @param   qualifiedRoleAttributes     Map<String, String> of role attribute name/value pairs
     *                                      to qualify a Role
     * @return                              boolean indicating if principal possesses all given Role attributes
     * 
     */
    public boolean principalHasQualifiedRole(String principalName, String roleName, 
        Map<String, String> qualifiedRoleAttributes);
    
    /**
     * Determines if a given Person possesses all given Role attributes.  This service assumes that the id
     * being passed in is an entity id for an entity with an Entity Type of a Person.
     * 
     * @param   personId                    person id uniquely identifying a person
     * @param   roleName                    name identifying a unique Role
     * @param   qualifiedRoleAttributes     Map<String, String> of role attribute name/value pairs
     *                                      to qualify a Role
     * @return                              boolean indicating if Person possesses all given Role attributes
     * 
     */
    public boolean personHasQualifiedRole(Long personId, String roleName, 
        Map<String, String> qualifiedRoleAttributes);
    
    /**
     * Determines if a given Entity possesses all given Role attributes.
     * 
     * @param   entityId                    entity id uniquely identifying a entity
     * @param   roleName                    name identifying a unique Role
     * @param   qualifiedRoleAttributes     Map<String, String> of role attribute name/value pairs
     *                                      to qualify a Role
     * @return                              boolean indicating if Entity possesses all given Role attributes
     * 
     */
    public boolean entityHasQualifiedRole(Long entityId, String roleName, 
        Map<String, String> qualifiedRoleAttributes);
    
    /**
     * Determines if a given Group possesses all given Role attributes.
     * 
     * @param   groupName                   group name uniquely identifying a group
     * @param   roleName                    name identifying a unique Role
     * @param   qualifiedRoleAttributes     Map<String, String> of role attribute name/value pairs
     *                                      to qualify a Role
     * @return                              boolean indicating if a Group possesses all given Role attributes
     * 
     */
    public boolean groupHasQualifiedRole(String groupName, String roleName, 
        Map<String, String> qualifiedRoleAttributes);
}