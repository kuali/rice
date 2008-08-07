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
package org.kuali.rice.kim.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.kim.bo.Entity;
import org.kuali.rice.kim.bo.EntityAttribute;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.GroupQualifiedRoleAttribute;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.bo.PrincipalQualifiedRoleAttribute;
import org.kuali.rice.kim.dao.KIMServicesDao;
import org.kuali.rice.kim.web.form.GroupQualifiedRole;
import org.kuali.rice.kim.web.form.PrincipalQualifiedRole;

/**
 * This is a description of what this class does - lindholm don't forget to fill
 * this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KIMServicesDaoJpa implements KIMServicesDao {

	public Collection<Principal> findAllPersons() {
		return null;
	}

	public Principal findPerson(final Long personId) {
		return null;
	}

	public Collection<EntityAttribute> getPersonAttributesForNamespace(
			Long personId, String namespaceName) {
		return null;
	}

	public String getPersonAttributeValue(Long personId, String attributeName,
			String namespaceName) {
		return null;
	}

	public String getAttributeValueForNamespace(String attributeName, String namespaceName,
			final Long entityId) {
		return null;
	}

	public Collection<EntityAttribute> getAttributesForNamespace(
			String namespaceName, final Long entityId) {
		return null;
	}

	public Collection<Principal> findAllPersonsWithRole(final String roleName) {
		return null;
	}

	public Principal findPrincipalWithPermission(String principalName, String permissionName, String namespaceName) {
		return null;
	}
	public Principal findPersonWithRole(Long personId, String roleName) {
		return null;
	}
	public Principal findPrincipalWithRole(String principalName, String roleName) {
		return null;
	}

	public Entity findEntityWithRole(Long entityId, String roleName) {
		return null;
	}

	public Group findGroupWithRole(String groupName, String roleName) {
		return null;
	}
	public Principal findPersonWithPermission(Long personId, String permissionName,	String namespaceName) {
		return null;
	}
	public Entity findEntityWithPermission(Long entityId, String permissionName, String namespaceName) {
		return null;
	}

	public Collection<GroupQualifiedRoleAttribute> findGroupQualifiedRole(String roleName, Map<String, String> attributes) {
		return null;
	}

	public Collection<PrincipalQualifiedRoleAttribute> findPrincipalQualifiedRole(String roleName, Map<String, String> attributes) {
		return null;
	}

	public Collection<Principal> findQualifiedPrincipalsWithRole(String roleName, Map<String, String> qualifiedRoleAttributes) {
		return null;
	}

	public Collection<Principal> findQualifiedPersonsWithRole(String roleName, Map<String, String> qualifiedRoleAttributes) {
		return null;
	}
	
	public Principal findQualifiedPersonWithRole(Long personId, String roleName, Map<String, String> qualifiedRoleAttributes) {
		return null;
	}

	public Principal findQualifiedPrincipalWithRole(String principalName, String roleName, Map<String, String> qualifiedRoleAttributes) {
		return null;
	}

	public Principal findQualifiedPrincipalWithPermission(String principalName, String permissionName, Map<String, String> qualifiedRoleAttributes, String namespaceName) {
		return null;
	}
	
	public Principal findQualifiedPersonWithPermission(Long personId, String permissionName, Map<String, String> qualifiedRoleAttributes, String namespaceName) {
		return null;
	}
	
	public Collection<Entity> findQualifiedEntitysWithRole(String roleName, Map<String, String> qualifiedRoleAttributes) {
		return null;
	}

	public Entity findQualifiedEntityWithRole(Long entityId, String roleName, Map<String, String> qualifiedRoleAttributes) {
		return null;
	}
	
	public Entity findQualifiedEntityWithPermissionRole(Long entityId, String permissionName, Map<String, String> qualifiedRoleAttributes, String namespaceName) {
		return null;
	}
	
	public Collection<Group> findQualifiedGroupsWithRole(String roleName, Map<String, String> qualifiedRoleAttributes) {
		return null;
	}
	
	public Group findQualifiedGroupsWithRole(String groupName, String roleName, Map<String, String> qualifiedRoleAttributes) {
		return null;
	}
	
}
