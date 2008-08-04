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
package org.kuali.rice.kim.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kim.bo.Entity;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.Permission;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.dao.KIMServicesDao;
import org.kuali.rice.kim.service.AuthorizationService;

/**
 * This implements the KIM Authorization Service
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class AuthorizationServiceImpl implements AuthorizationService {
	private KIMServicesDao kimServicesDao;

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#entityHasQualifiedRole(java.lang.Long, java.lang.String, java.util.Map)
	 */

	public boolean entityHasQualifiedRole(Long entityId, String roleName,
			Map<String, String> qualifiedRoleAttributes) {
		final Entity e = kimServicesDao.findQualifiedEntityWithRole(entityId, roleName, qualifiedRoleAttributes);
		return e != null;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#entityHasRole(java.lang.Long, java.lang.String)
	 */

	public boolean entityHasRole(Long entityId, String roleName) {
		final Entity entity = kimServicesDao.findEntityWithRole(entityId, roleName);
		return entity != null;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#groupHasQualifiedRole(java.lang.String, java.lang.String, java.util.Map)
	 */

	public boolean groupHasQualifiedRole(String groupName, String roleName,
			Map<String, String> qualifiedRoleAttributes) {
		Group group = kimServicesDao.findQualifiedGroupsWithRole(groupName, roleName, qualifiedRoleAttributes);
		return group != null;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#groupHasRole(java.lang.String, java.lang.String)
	 */

	public boolean groupHasRole(String groupName, String roleName) {
		final Group group = kimServicesDao.findGroupWithRole(groupName, roleName);
		return group != null;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#isEntityAuthorized(java.lang.Long, java.lang.String, java.lang.String)
	 */

	public boolean isEntityAuthorized(Long entityId, String permissionName,
			String namespaceName) {
		Entity entity = kimServicesDao.findEntityWithPermission(entityId, permissionName, namespaceName);
		return entity != null;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#isEntityAuthorizedForQualifiedPermission(java.lang.Long, java.lang.String, java.util.Map, java.lang.String)
	 */

	public boolean isEntityAuthorizedForQualifiedPermission(Long entityId,
			String permissionName, Map<String, String> qualifiedRoleAttributes,
			String namespaceName) {
		final Entity e = kimServicesDao.findQualifiedEntityWithPermissionRole(entityId, permissionName, qualifiedRoleAttributes, namespaceName);
		return e != null;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#isPersonAuthorized(java.lang.Long, java.lang.String, java.lang.String)
	 */

	public boolean isPersonAuthorized(Long personId, String permissionName,
			String namespaceName) {
		final Principal principal = kimServicesDao.findPersonWithPermission(personId, permissionName, namespaceName);
		return principal != null;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#isPersonAuthorizedForQualifiedPermission(java.lang.Long, java.lang.String, java.util.Map, java.lang.String)
	 */

	public boolean isPersonAuthorizedForQualifiedPermission(Long personId,
			String permissionName, Map<String, String> qualifiedRoleAttributes,
			String namespaceName) {
		final Principal p = kimServicesDao.findQualifiedPersonWithPermission(personId, permissionName, qualifiedRoleAttributes, namespaceName);
		return p != null;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#isPrincipalAuthorized(java.lang.String, java.lang.String, java.lang.String)
	 */

	public boolean isPrincipalAuthorized(String principalName,
			String permissionName, String namespaceName) {

		Principal principal = kimServicesDao.findPrincipalWithPermission(principalName, permissionName, namespaceName);
		return principal != null;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#isPrincipalAuthorizedForQualifiedPermission(java.lang.String, java.lang.String, java.util.Map, java.lang.String)
	 */

	public boolean isPrincipalAuthorizedForQualifiedPermission(
			String principalName, String permissionName,
			Map<String, String> qualifiedRoleAttributes, String namespaceName) {
		final Principal p = kimServicesDao.findQualifiedPrincipalWithPermission(principalName, permissionName, qualifiedRoleAttributes, namespaceName);
		return p != null;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#personHasQualifiedRole(java.lang.Long, java.lang.String, java.util.Map)
	 */

	public boolean personHasQualifiedRole(Long personId, String roleName,
			Map<String, String> qualifiedRoleAttributes) {
		final Principal p = kimServicesDao.findQualifiedPersonWithRole(personId, roleName, qualifiedRoleAttributes);
		return p != null;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#personHasRole(java.lang.Long, java.lang.String)
	 */

	public boolean personHasRole(Long personId, String roleName) {
		Principal principal = kimServicesDao.findPersonWithRole(personId, roleName);
		return principal != null;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#principalHasQualifiedRole(java.lang.String, java.lang.String, java.util.Map)
	 */

	public boolean principalHasQualifiedRole(String principalName,
			String roleName, Map<String, String> qualifiedRoleAttributes) {
		final Principal p = kimServicesDao.findQualifiedPrincipalWithRole(principalName, roleName, qualifiedRoleAttributes);
		return p != null;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#principalHasRole(java.lang.String, java.lang.String)
	 */

	public boolean principalHasRole(String principalName, String roleName) {
		Principal principal = kimServicesDao.findPrincipalWithRole(principalName, roleName);
		return principal != null;
	}

	/**
	 * @param kimServicesDao the kimServicesDao to set
	 */
	public void setKimServicesDao(KIMServicesDao kimServicesDao) {
		this.kimServicesDao = kimServicesDao;
	}

	/**
	 * @return the kimServicesDao
	 */
	public KIMServicesDao getKimServicesDao() {
		return kimServicesDao;
	}

}
