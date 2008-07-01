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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kim.bo.Entity;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.GroupQualifiedRole;
import org.kuali.rice.kim.bo.Permission;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.bo.PrincipalQualifiedRole;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.dao.KIMServicesDao;
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
 * This is the default KIM RoleService implementation that is provided by Rice.
 * This will mature over time as the KIM component is developed.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoleServiceImpl implements RoleService {
	KIMServicesDao kimServicesDao;

	public RoleServiceImpl() {
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getAllRoleNames()
	 */
	public List<String> getAllRoleNames() {
		final ArrayList<String> names = new ArrayList<String>(0);

		final Collection<Role> roles = (Collection<Role>) KNSServiceLocator
				.getBusinessObjectService().findAll(Role.class);
		if (roles == null) {
			return names;
		}

		names.ensureCapacity(roles.size());
		for (Role role : roles) {
			names.add(role.getName());
		}
		return names;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getAllRoles()
	 */
	public List<RoleDTO> getAllRoles() {
		ArrayList<RoleDTO> dto = new ArrayList<RoleDTO>(0);

		final Collection<Role> roles = (Collection<Role>) KNSServiceLocator
				.getBusinessObjectService().findAll(Role.class);
		if (roles == null) {
			return dto;
		}

		dto.ensureCapacity(roles.size());
		for (Role role : roles) {
			dto.add(Role.toDTO(role));
		}
		return dto;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getRole(java.lang.Long)
	 */
	public RoleDTO getRole(Long roleId) {
		final HashMap<String, Long> id = new HashMap<String, Long>();
		id.put("ID", roleId);
		final Role role = (Role) KNSServiceLocator.getBusinessObjectService()
				.findByPrimaryKey(Role.class, id);
		if (role == null) {
			return null;
		}

		return Role.toDTO(role);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getRole(java.lang.String)
	 */
	public RoleDTO getRole(String roleName) {

		Role role = findRoleByName(roleName);
		return Role.toDTO(role);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getGroupNamesWithRole(java.lang.String)
	 */
	public List<String> getGroupNamesWithRole(String roleName) {
		final ArrayList<String> groups = new ArrayList<String>();

		final Role role = findRoleByName(roleName);
		if (role == null) {
			return groups;
		}

		groups.ensureCapacity(role.getGroups().size());
		for (Group group : role.getGroups()) {
			groups.add(group.getName());
		}
		return groups;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getGroupQualifiedRoles(java.lang.String,
	 *      java.util.Map)
	 */
	public List<GroupQualifiedRoleDTO> getGroupQualifiedRoles(String roleName,
			Map<String, String> qualifiedRoleAttributes) {
//		Collection<GroupQualifiedRole> groupsWithRole = kimServicesDao
//				.findAllGroupsWithQualifiedRole(roleName);
//
//		final ArrayList<GroupQualifiedRoleDTO> groups = new ArrayList<GroupQualifiedRoleDTO>(
//				groupsWithRole.size());
//
//		for (GroupQualifiedRole attr : groupsWithRole) {
//			if (ImplUtils.hasAllQualifiedAttributes(qualifiedRoleAttributes,
//					attr.getQualifiedRoleAttributes())) {
//				groups.add(GroupQualifiedRole.toDTO(attr));
//			}
//		}
//		return groups;
		return new ArrayList();
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getGroupQualifiedRoles(java.lang.String)
	 */
	public List<GroupQualifiedRoleDTO> getGroupQualifiedRoles(String roleName) {
//		Collection<GroupQualifiedRole> groupsWithRole = kimServicesDao
//				.findAllGroupsWithQualifiedRole(roleName);
//
//		final ArrayList<GroupQualifiedRoleDTO> groups = new ArrayList<GroupQualifiedRoleDTO>(
//				groupsWithRole.size());
//
//		for (GroupQualifiedRole attr : groupsWithRole) {
//			groups.add(GroupQualifiedRole.toDTO(attr));
//		}
//		return groups;
		return new ArrayList();
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getGroupsWithRole(java.lang.String)
	 */
	public List<GroupDTO> getGroupsWithRole(String roleName) {
		final ArrayList<GroupDTO> groups = new ArrayList<GroupDTO>(0);

		final Role role = findRoleByName(roleName);
		if (role == null) {
			return groups;
		}

		groups.ensureCapacity(role.getGroups().size());
		for (Group group : role.getGroups()) {
			groups.add(Group.toDTO(group));
		}
		return groups;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getPermissionNamesForRole(java.lang.String)
	 */
	public List<String> getPermissionNamesForRole(String roleName) {
		final ArrayList<String> permissions = new ArrayList<String>(0);

		final Role role = findRoleByName(roleName);
		if (role == null) {
			return permissions;
		}

		permissions.ensureCapacity(role.getPermissions().size());
		for (Permission permission : role.getPermissions()) {
			permissions.add(permission.getName());
		}
		return permissions;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getPermissionsForRole(java.lang.String)
	 */
	public List<PermissionDTO> getPermissionsForRole(String roleName) {
		final ArrayList<PermissionDTO> permissions = new ArrayList<PermissionDTO>(
				0);

		final Role role = findRoleByName(roleName);
		if (role == null) {
			return permissions;
		}

		permissions.ensureCapacity(role.getPermissions().size());
		for (Permission permission : role.getPermissions()) {
			permissions.add(Permission.toDTO(permission));
		}
		return permissions;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getPersonIdsWithRole(java.lang.String)
	 */
	public List<Long> getPersonIdsWithRole(String roleName) {
		ArrayList<Long> personIds = new ArrayList<Long>(0);

		final Collection<Principal> personPrincipals = kimServicesDao.findAllPersons();

		personIds.ensureCapacity(personPrincipals.size());
		for (Principal person : personPrincipals) {
			for (Role role : person.getRoles()) {
				if (role.getName().equalsIgnoreCase(roleName)) {
					personIds.add(person.getId());
				}
			}
		}
		return personIds;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getPersonsWithRole(java.lang.String)
	 */
	public List<PersonDTO> getPersonsWithRole(String roleName) {
		final Collection<Principal> personPrincipals = kimServicesDao.findAllPersons();

		ArrayList<PersonDTO> persons = new ArrayList<PersonDTO>();
		for (Principal person : personPrincipals) {
			for (Role role : person.getRoles()) {
				if (role.getName().equalsIgnoreCase(roleName)) {
					persons.add(Principal.toPersonDTO(person));
				}
			}
		}
		return persons;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getPrincipalNamesWithRole(java.lang.String)
	 */
	public List<String> getPrincipalNamesWithRole(String roleName) {
		final ArrayList<String> principals = new ArrayList<String>(0);

		final Role role = findRoleByName(roleName);
		if (role == null) {
			return principals;
		}

		principals.ensureCapacity(role.getPrincipals().size());
		for (Principal principal : role.getPrincipals()) {
			principals.add(principal.getName());
		}
		return principals;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getPrincipalsWithRole(java.lang.String)
	 */
	public List<PrincipalDTO> getPrincipalsWithRole(String roleName) {
		final ArrayList<PrincipalDTO> principals = new ArrayList<PrincipalDTO>(
				0);

		final Role role = findRoleByName(roleName);
		if (role == null) {
			return principals;
		}

		principals.ensureCapacity(role.getPrincipals().size());
		for (Principal principal : role.getPrincipals()) {
			principals.add(Principal.toDTO(principal));
		}
		return principals;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getPrincipalQualifiedRoles(java.lang.String)
	 */
	public List<PrincipalQualifiedRoleDTO> getPrincipalQualifiedRoles(
			String roleName) {
//		final ArrayList<PrincipalQualifiedRoleDTO> principals = new ArrayList<PrincipalQualifiedRoleDTO>(
//				0);
//		final Role role = findRoleByName(roleName);
//		if (role == null) {
//			return principals;
//		}
//
//		principals.ensureCapacity(role.getPrincipals().size());
//		for (PrincipalQualifiedRole pqr : role.getPrincipalQualifiedRoles()) {
//			principals.add(PrincipalQualifiedRole.toDTO(pqr));
//		}
//
//		return principals;
		return new ArrayList();
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getPrincipalQualifiedRoles(java.lang.String,
	 *      java.util.Map)
	 */
	public List<PrincipalQualifiedRoleDTO> getPrincipalQualifiedRoles(
			String roleName, Map<String, String> qualifiedRoleAttributes) {
//		final ArrayList<PrincipalQualifiedRoleDTO> pqrs = new ArrayList<PrincipalQualifiedRoleDTO>(
//				0);
//
//		final Role role = findRoleByName(roleName);
//		if (role == null) {
//			return pqrs;
//		}
//
//		for (PrincipalQualifiedRole p : role.getPrincipalQualifiedRoles()) {
//			if (ImplUtils.hasAllQualifiedAttributes(qualifiedRoleAttributes, p
//					.getPrincipalQualifiedRoleAttributes())) {
//				pqrs.add(PrincipalQualifiedRole.toDTO(p));
//			}
//		}
//		return pqrs;
		return new ArrayList();
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getEntityIdsWithRole(java.lang.String)
	 */
	public List<Long> getEntityIdsWithRole(String roleName) {
		final ArrayList<Long> entitys = new ArrayList<Long>(0);

		final Role role = findRoleByName(roleName);
		if (role == null) {
			return entitys;
		}

		entitys.ensureCapacity(role.getPrincipals().size());
		for (Principal principal : role.getPrincipals()) {
			entitys.add(principal.getEntityId());
		}

		return entitys;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.RoleService#getEntitysWithRole(java.lang.String)
	 */
	public List<EntityDTO> getEntitysWithRole(String roleName) {
		final ArrayList<EntityDTO> entitys = new ArrayList<EntityDTO>(0);

		final Role role = findRoleByName(roleName);
		if (role == null) {
			return entitys;
		}

		entitys.ensureCapacity(role.getPrincipals().size());
		for (Principal principal : role.getPrincipals()) {
			entitys.add(Entity.toDTO(principal.getEntity()));
		}

		return entitys;
	}

	/**
	 *
	 * This method ...
	 *
	 * @param roleName
	 * @return
	 */
	private static Role findRoleByName(final String roleName) {
		final HashMap<String, String> name = new HashMap<String, String>();
		name.put("NAME", roleName);
		return ((Collection<Role>) KNSServiceLocator.getBusinessObjectService()
				.findMatching(Role.class, name)).iterator().next();
	}

	/**
	 *
	 * @param kimServicesDao
	 *            the kimServicesDao to set
	 */
	public void setKimServicesDao(KIMServicesDao kimServicesDao) {
		this.kimServicesDao = kimServicesDao;
	}

}
