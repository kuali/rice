/*
 * Copyright 2005-2007 The Kuali Foundation.
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
import java.util.Map;

import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kim.bo.Entity;
import org.kuali.rice.kim.bo.EntityAttribute;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.GroupQualifiedRoleAttribute;
import org.kuali.rice.kim.bo.Permission;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.bo.PrincipalQualifiedRoleAttribute;
import org.kuali.rice.kim.web.form.GroupQualifiedRole;
import org.kuali.rice.kim.web.form.PrincipalQualifiedRole;

public class KIMServicesDaoProxy implements KIMServicesDao {

	private KIMServicesDao kimServicesDaoJpa;
	private KIMServicesDao kimServicesDaoOjb;

	private KIMServicesDao getDao(Class clazz) {
		return (OrmUtils.isJpaAnnotated(clazz) && OrmUtils.isJpaEnabled()) ? kimServicesDaoJpa
				: kimServicesDaoOjb;
	}

	public Collection<Principal> findAllPersons() {
		return getDao(Principal.class).findAllPersons();
	}

	public Principal findPerson(final Long personId) {
		return getDao(Principal.class).findPerson(personId);
	}

	public Collection<EntityAttribute> getPersonAttributesForNamespace(
			Long personId, String namespaceName) {
		return getDao(EntityAttribute.class).getPersonAttributesForNamespace(personId, namespaceName);
	}

	public String getPersonAttributeValue(Long personId, String attributeName,
			String namespaceName) {
		return getDao(EntityAttribute.class).getPersonAttributeValue(personId, attributeName, namespaceName);
	}

	public String getAttributeValueForNamespace(String attributeName, String namespaceName,
			final Long entityId) {
		return getDao(EntityAttribute.class).getAttributeValueForNamespace(attributeName, namespaceName, entityId);
	}

	public Collection<EntityAttribute> getAttributesForNamespace(
			String namespaceName, final Long entityId) {
		return getDao(EntityAttribute.class).getAttributesForNamespace(namespaceName, entityId);
	}

	public Principal findPrincipalWithPermission(String permissionName, String namespaceName, String principalName) {
		return getDao(Permission.class).findPrincipalWithPermission(permissionName, namespaceName, principalName);
	}

	public Principal findPersonWithRole(Long personId, String roleName) {
		return getDao(Principal.class).findPersonWithRole(personId, roleName);
	}

	public Principal findPrincipalWithRole(String principalName, String roleName) {
		return getDao(Principal.class).findPrincipalWithRole(principalName, roleName);
	}

	public Entity findEntityWithRole(Long entityId, String roleName) {
		return getDao(Entity.class).findEntityWithRole(entityId, roleName);
	}

	public Group findGroupWithRole(String groupName, String roleName) {
		return getDao(Group.class).findGroupWithRole(groupName, roleName);
	}

	public Principal findPersonWithPermission(Long personId, String permissionName,	String namespaceName) {
		return getDao(Principal.class).findPersonWithPermission(personId, permissionName, namespaceName);
	}

	public Entity findEntityWithPermission(Long entityId, String permissionName, String namespaceName) {
		return getDao(Entity.class).findEntityWithPermission(entityId, permissionName, namespaceName);
	}

	public Collection<GroupQualifiedRoleAttribute> findGroupQualifiedRole(String roleName, Map<String, String> attributes) {
		return getDao(GroupQualifiedRoleAttribute.class).findGroupQualifiedRole(roleName, attributes);
	}

	public Collection<PrincipalQualifiedRoleAttribute> findPrincipalQualifiedRole(String roleName, Map<String, String> attributes) {
		return getDao(PrincipalQualifiedRoleAttribute.class).findPrincipalQualifiedRole(roleName, attributes);
	}


	public Collection<Principal> findQualifiedPrincipalsWithRole(String roleName, Map<String, String> qualifiedRoleAttributes) {
		return getDao(Principal.class).findQualifiedPrincipalsWithRole(roleName, qualifiedRoleAttributes);
	}

	public Collection<Principal> findQualifiedPersonsWithRole(String roleName, Map<String, String> qualifiedRoleAttributes) {
		return getDao(Principal.class).findQualifiedPersonsWithRole(roleName, qualifiedRoleAttributes);
	}

	public Principal findQualifiedPersonWithRole(Long personId, String roleName, Map<String, String> qualifiedRoleAttributes) {
		return getDao(Principal.class).findQualifiedPersonWithRole(personId, roleName, qualifiedRoleAttributes);
	}

	public Principal findQualifiedPrincipalWithRole(String principalName, String roleName, Map<String, String> qualifiedRoleAttributes) {
		return getDao(Principal.class).findQualifiedPrincipalWithRole(principalName, roleName, qualifiedRoleAttributes);
	}

	public Principal findQualifiedPrincipalWithPermission(String principalName, String permissionName, Map<String, String> qualifiedRoleAttributes, String namespaceName) {
		return getDao(Principal.class).findQualifiedPrincipalWithPermission(principalName, permissionName, qualifiedRoleAttributes, namespaceName);
	}

	public Principal findQualifiedPersonWithPermission(Long personId, String permissionName, Map<String, String> qualifiedRoleAttributes, String namespaceName) {
		return getDao(Principal.class).findQualifiedPersonWithPermission(personId, permissionName, qualifiedRoleAttributes, namespaceName);
	}

	public Collection<Entity> findQualifiedEntitysWithRole(String roleName, Map<String, String> qualifiedRoleAttributes) {
		return getDao(Entity.class).findQualifiedEntitysWithRole(roleName, qualifiedRoleAttributes);
	}

	public Entity findQualifiedEntityWithRole(Long entityId, String roleName, Map<String, String> qualifiedRoleAttributes) {
		return getDao(Entity.class).findQualifiedEntityWithRole(entityId, roleName, qualifiedRoleAttributes);
	}

	public Entity findQualifiedEntityWithPermissionRole(Long entityId, String permissionName, Map<String, String> qualifiedRoleAttributes, String namespaceName) {
		return getDao(Entity.class).findQualifiedEntityWithPermissionRole(entityId, permissionName, qualifiedRoleAttributes, namespaceName);
	}

	public Collection<Group> findQualifiedGroupsWithRole(String roleName, Map<String, String> qualifiedRoleAttributes) {
		return getDao(Group.class).findQualifiedGroupsWithRole(roleName, qualifiedRoleAttributes);
	}

	public Group findQualifiedGroupsWithRole(String groupName, String roleName, Map<String, String> qualifiedRoleAttributes) {
		return getDao(Group.class).findQualifiedGroupsWithRole(groupName, roleName, qualifiedRoleAttributes);
	}


	/**
	 * @param servicesDaoJpa the kIMServicesDaoJpa to set
	 */
	public void setKimServicesDaoJpa(final KIMServicesDao kimServicesDaoJpa) {
		this.kimServicesDaoJpa = kimServicesDaoJpa;
	}

	/**
	 * @param servicesDaoOjb the kIMServicesDaoOjb to set
	 */
	public void setKimServicesDaoOjb(final KIMServicesDao kimServicesDaoOjb) {
		this.kimServicesDaoOjb = kimServicesDaoOjb;
	}

}
