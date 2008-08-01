/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kim.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.Entity;
import org.kuali.rice.kim.bo.EntityAttribute;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.GroupQualifiedRoleAttribute;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.bo.PrincipalQualifiedRoleAttribute;
import org.kuali.rice.kim.web.form.GroupQualifiedRole;
import org.kuali.rice.kim.web.form.PrincipalQualifiedRole;

/**
 * This is a description of what this class does - lindholm don't forget to fill
 * this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface KIMServicesDao {
	/**
	 *
	 * This method finds all Principals with the Person entity
	 *
	 * @return
	 */
	public Collection<Principal> findAllPersons();

	/**
	 *
	 * This method returns the Principal representing a person
	 *
	 * @param personId
	 * @return
	 */
	public Principal findPerson(final Long personId);

	/**
	 *
	 * This method returns the person Principals attributes for this namespace
	 *
	 * @param personId
	 * @param namespaceName
	 * @return
	 */
	public Collection<EntityAttribute> getPersonAttributesForNamespace(
			Long personId, String namespaceName);

	/**
	 *
	 *This method returns the attribute value of an attribute for a person in a
	 * namespace
	 *
	 * @param personId
	 * @param attributeName
	 * @param namespaceName
	 * @return
	 */
	public String getPersonAttributeValue(Long personId, String attributeName,
			String namespaceName);

	/**
	 *
	 * This method returns the attribute value for an entity in a namespace
	 *
	 * @param attributeName
	 * @param namespaceName
	 * @param entityId
	 * @return
	 */
	public String getAttributeValueForNamespace(String attributeName,
			String namespaceName, final Long entityId);

	/**
	 *
	 * This method returns the Entity Attributes for a namespace
	 *
	 * @param namespaceName
	 * @param entityId
	 * @return
	 */
	public Collection<EntityAttribute> getAttributesForNamespace(
			String namespaceName, final Long entityId);

	/**
	 *
	 * This method finds a principal with the supplied permission name and namespace name
	 *
	 * @param principalName
	 * @param permissionName
	 * @param namespaceName
	 * @return
	 */
	public Principal findPrincipalWithPermission(String principalName, String permissionName, String namespaceName);

	/**
	 *
	 * This method finds a person with a specific role
	 *
	 * @param personId
	 * @param roleName
	 * @return
	 */
	public Principal findPersonWithRole(Long personId, String roleName);

	/**
	 *
	 * This method finds a the principal with the given name and role name
	 *
	 * @param principalName
	 * @param roleName
	 * @return
	 */
	public Principal findPrincipalWithRole(String principalName, String roleName);

	/**
	 *
	 * This method finds the entity with the given role name
	 *
	 * @param entityId
	 * @param roleName
	 * @return
	 */
	public Entity findEntityWithRole(Long entityId, String roleName);

	/**
	 *
	 * This method group with role
	 *
	 * @param groupName
	 * @param roleName
	 * @return
	 */
	public Group findGroupWithRole(String groupName, String roleName);

	/**
	 *
	 * This method finds a person with permission and namespace
	 *
	 * @param personId
	 * @param permissionName
	 * @param namespaceName
	 * @return
	 */
	public Principal findPersonWithPermission(Long personId, String permissionName,	String namespaceName);

	/**
	 *
	 * This method finds a entity with role and namespace
	 *
	 * @param entityId
	 * @param permissionName
	 * @param namespaceName
	 * @return
	 */
	public Entity findEntityWithPermission(Long entityId, String permissionName, String namespaceName);

	/**
	 *
	 * This method finds Groups Qualified by a Role, possibly constrained by attributes
	 *
	 * @param roleName
	 * @param attributes (optional)
	 * @return
	 */
	public Collection<GroupQualifiedRoleAttribute> findGroupQualifiedRole(String roleName, Map<String, String> attributes);

	/**
	 *
	 * This method finds Principals Qualified by a Role, possibly constrained by attributes
	 *
	 * @param roleName
	 * @param attributes (optional)
	 * @return
	 */
	public Collection<PrincipalQualifiedRoleAttribute> findPrincipalQualifiedRole(String roleName, Map<String, String> attributes);

	/**
	 *
	 * This method finds all Principals with the qualified attributes and Role
	 *
	 * @param roleName
	 * @param qualifiedRoleAttributes
	 * @return
	 */
	public Collection<Principal> findQualifiedPrincipalsWithRole(String roleName, Map<String, String> qualifiedRoleAttributes);

	/**
	 *
	 * This method finds all Persons with the qualified attributes and Role
	 *
	 * @param roleName
	 * @param qualifiedRoleAttributes
	 * @return
	 */
	public Collection<Principal> findQualifiedPersonsWithRole(String roleName, Map<String, String> qualifiedRoleAttributes);

	/**
	 * 
	 * This method finds a Person with the id, qualified attributes, and Role
	 * 
	 * @param personId
	 * @param roleName
	 * @param qualifiedRoleAttributes
	 * @return
	 */
	public Principal findQualifiedPersonWithRole(Long personId, String roleName, Map<String, String> qualifiedRoleAttributes);

	/**
	 * 
	 * This method finds a Person with the id, qualified attributes, and Role
	 * 
	 * @param principalName
	 * @param roleName
	 * @param qualifiedRoleAttributes
	 * @return
	 */
	public Principal findQualifiedPrincipalWithRole(String principalName, String roleName, Map<String, String> qualifiedRoleAttributes);

	/**
	 * 
	 * This method finds a Person with the qualified attributes, Role, and namespace
	 * 
	 * @param personId
	 * @param permissionName
	 * @param qualifiedRoleAttributes
	 * @param namespaceName
	 * @return
	 */
	public Principal findQualifiedPersonWithPermission(Long personId, String permissionName, Map<String, String> qualifiedRoleAttributes, String namespaceName);

	/**
	 * 
	 * This method finds a Principal with the qualified attributes, Role, and namespace
	 * 
	 * @param principalName
	 * @param permissionName
	 * @param qualifiedRoleAttributes
	 * @param namespaceName
	 * @return
	 */
	public Principal findQualifiedPrincipalWithPermission(String principalName, String permissionName, Map<String, String> qualifiedRoleAttributes, String namespaceName);

	/**
	 *
	 * This method finds all Entitys with the qualified attributes and Role
	 *
	 * @param roleName
	 * @param qualifiedRoleAttributes
	 * @return
	 */
	public Collection<Entity> findQualifiedEntitysWithRole(String roleName, Map<String, String> qualifiedRoleAttributes);

	
	/**
	 * 
	 * This method finds a specific entity with given role and qualified attributes
	 * 
	 * @param entityId
	 * @param roleName
	 * @param qualifiedRoleAttributes
	 * @return
	 */
	public Entity findQualifiedEntityWithRole(Long entityId, String roleName, Map<String, String> qualifiedRoleAttributes);

	/**
	 * 
	 * This method finds a specific entity with given role, qualified attributes, and namespace
	 * 
	 * @param entityId
	 * @param permissionName
	 * @param qualifiedRoleAttributes
	 * @param namespaceName
	 * @return
	 */
	public Entity findQualifiedEntityWithPermissionRole(Long entityId, String permissionName, Map<String, String> qualifiedRoleAttributes, String namespaceName);
	
	/**
	 * This method finds all Groups with the qualified attributes and Role
	 *
	 * @param roleName
	 * @param qualifiedRoleAttributes
	 * @return
	 */
	public Collection<Group> findQualifiedGroupsWithRole(String roleName, Map<String, String> qualifiedRoleAttributes);

	/**
	 * 
	 * This method finds a named group with given role and qualifed attributes
	 * 
	 * @param groupName
	 * @param roleName
	 * @param qualifiedRoleAttributes
	 * @return
	 */
	public Group findQualifiedGroupsWithRole(String groupName, String roleName, Map<String, String> qualifiedRoleAttributes);

	// public Collection<Principal> findAllPersonsWithRole(final String
	// roleName);
}
