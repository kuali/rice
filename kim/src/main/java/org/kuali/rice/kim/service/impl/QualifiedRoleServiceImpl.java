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
import org.kuali.rice.kim.bo.AbstractAttributeBase;
import org.kuali.rice.kim.bo.Entity;
import org.kuali.rice.kim.bo.EntityAttribute;
import org.kuali.rice.kim.bo.EntityType;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.GroupQualifiedRole;
import org.kuali.rice.kim.bo.GroupQualifiedRoleAttribute;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.bo.PrincipalQualifiedRole;
import org.kuali.rice.kim.bo.PrincipalQualifiedRoleAttribute;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.RoleAttribute;
import org.kuali.rice.kim.bo.RoleQualificationForPrincipal;
import org.kuali.rice.kim.dao.KIMServicesDao;
import org.kuali.rice.kim.dto.EntityDTO;
import org.kuali.rice.kim.dto.GroupDTO;
import org.kuali.rice.kim.dto.PersonDTO;
import org.kuali.rice.kim.dto.PrincipalDTO;
import org.kuali.rice.kim.dto.RoleDTO;
import org.kuali.rice.kim.service.QualifiedRoleService;

/**
 * This is the default KIM QualifiedRoleService implementation that is provided
 * by Rice. This will mature over time as the KIM component is developed.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class QualifiedRoleServiceImpl implements QualifiedRoleService {
	private KIMServicesDao kimServicesDao;

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.QualifiedRoleService#getGroupNames(java.lang.String,
	 *      java.util.Map)
	 */
	public List<String> getGroupNames(String roleName,
			Map<String, String> qualifiedRoleAttributes) {
		final ArrayList<String> groups = new ArrayList<String>();

		final HashMap<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("NAME", roleName);
		final Role role = ((Collection<Role>) KNSServiceLocator
				.getBusinessObjectService().findMatching(Role.class, criteria))
				.iterator().next();

		for (Group g : role.getGroups()) {
			if (ImplUtils.hasAllQualifiedAttributes(qualifiedRoleAttributes, g
					.getQualifiedRoleAttributes())) {
				groups.add(g.getName());
			}
		}
		return groups;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.QualifiedRoleService#getGroups(java.lang.String,
	 *      java.util.Map)
	 */
	public List<GroupDTO> getGroups(String roleName,
			Map<String, String> qualifiedRoleAttributes) {
		final ArrayList<GroupDTO> groups = new ArrayList<GroupDTO>(0);

		final HashMap<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("NAME", roleName);
		final Role role = ((Collection<Role>) KNSServiceLocator
				.getBusinessObjectService().findMatching(Role.class, criteria))
				.iterator().next();

		for (Group g : role.getGroups()) {
			if (ImplUtils.hasAllQualifiedAttributes(qualifiedRoleAttributes, g
					.getQualifiedRoleAttributes())) {
				groups.add(Group.toDTO(g));
			}
		}
		return groups;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.QualifiedRoleService#getPersonIds(java.lang.String,
	 *      java.util.Map)
	 */
	public List<Long> getPersonIds(String roleName,
			Map<String, String> qualifiedRoleAttributes) {
		Collection<PrincipalQualifiedRole> principals = kimServicesDao
				.findAllPersonsWithQualifiedRole(roleName);

		final ArrayList<Long> persons = new ArrayList<Long>();
		for (PrincipalQualifiedRole p : principals) {
			// TODO getPrincipal() is an ugly hack to get around
			// PrincipalQualifiedRole not being filled in
			if (ImplUtils.hasAllQualifiedAttributes(qualifiedRoleAttributes, p
					.getPrincipal().getPrincipalQualifiedRoleAttributes())) {
				persons.add(p.getPrincipalId());
			}

		}
		return persons;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.QualifiedRoleService#getPersons(java.lang.String,
	 *      java.util.Map)
	 */
	public List<PersonDTO> getPersons(String roleName,
			Map<String, String> qualifiedRoleAttributes) {
		final Collection<PrincipalQualifiedRole> principals = kimServicesDao
				.findAllPersonsWithQualifiedRole(roleName);

		final ArrayList<PersonDTO> persons = new ArrayList<PersonDTO>();
		for (PrincipalQualifiedRole p : principals) {
			// TODO getPrincipal() is an ugly hack to get around
			// PrincipalQualifiedRole not being filled in
			if (ImplUtils.hasAllQualifiedAttributes(qualifiedRoleAttributes, p
					.getPrincipal().getPrincipalQualifiedRoleAttributes())) {
				persons.add(Principal.toPersonDTO(p.getPrincipal()));
			}

		}
		return persons;

	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.QualifiedRoleService#getPrincipalNames(java.lang.String,
	 *      java.util.Map)
	 */
	public List<String> getPrincipalNames(String roleName,
			Map<String, String> qualifiedRoleAttributes) {
		final Collection<PrincipalQualifiedRole> qualifiedPrincipals = kimServicesDao
				.findAllPrincipalsWithQualifiedRole(roleName);

		final ArrayList<String> principals = new ArrayList<String>();
		for (PrincipalQualifiedRole p : qualifiedPrincipals) {
			// TODO getPrincipal() is an ugly hack to get around
			// PrincipalQualifiedRole not being filled in
			if (ImplUtils.hasAllQualifiedAttributes(qualifiedRoleAttributes, p
					.getPrincipal().getPrincipalQualifiedRoleAttributes())) {
				principals.add(p.getPrincipal().getName());
			}
		}
		return principals;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.QualifiedRoleService#getPrincipals(java.lang.String,
	 *      java.util.Map)
	 */
	public List<PrincipalDTO> getPrincipals(String roleName,
			Map<String, String> qualifiedRoleAttributes) {
		final ArrayList<PrincipalDTO> principals = new ArrayList<PrincipalDTO>();
		final Collection<PrincipalQualifiedRole> qualifiedPrincipals = kimServicesDao
				.findAllPrincipalsWithQualifiedRole(roleName);

		for (PrincipalQualifiedRole p : qualifiedPrincipals) {
			// TODO getPrincipal() is an ugly hack to get around
			// PrincipalQualifiedRole not being filled in
			if (ImplUtils.hasAllQualifiedAttributes(qualifiedRoleAttributes, p
					.getPrincipal().getPrincipalQualifiedRoleAttributes())) {
				principals.add(Principal.toDTO(p.getPrincipal()));
			}

		}
		return principals;

	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.QualifiedRoleService#getRoleNames(java.util.Map)
	 */
	public List<String> getRoleNames(Map<String, String> qualifiedRoleAttributes) {
		final Collection<Role> roles = (Collection<Role>) KNSServiceLocator
				.getBusinessObjectService().findAll(Role.class);
		final ArrayList<String> names = new ArrayList<String>();
		for (Role r : roles) {
			if (ImplUtils.hasAllAttributes(qualifiedRoleAttributes, r
					.getRoleAttributes())) {
				names.add(r.getName());
			}

		}
		return names;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.QualifiedRoleService#getRoles(java.util.Map)
	 */
	public List<RoleDTO> getRoles(Map<String, String> qualifiedRoleAttributes) {
		final Collection<Role> allRoles = (Collection<Role>) KNSServiceLocator
				.getBusinessObjectService().findAll(Role.class);
		final ArrayList<RoleDTO> roles = new ArrayList<RoleDTO>();
		for (Role r : allRoles) {
			if (ImplUtils.hasAllAttributes(qualifiedRoleAttributes, r
					.getRoleAttributes())) {
				roles.add(Role.toDTO(r));
			}
		}
		return roles;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.QualifiedRoleService#getEntityIds(java.lang.String,
	 *      java.util.Map)
	 */
	public List<Long> getEntityIds(String roleName,
			Map<String, String> qualifiedRoleAttributes) {
		// TODO
		return null;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.QualifiedRoleService#getEntitys(java.lang.String,
	 *      java.util.Map)
	 */
	public List<EntityDTO> getEntitys(String roleName,
			Map<String, String> qualifiedRoleAttributes) {
		// TODO
		return null;
	}

	/**
	 * @param kimServicesDao
	 *            the kimServicesDao to set
	 */
	public void setKimServicesDao(KIMServicesDao kimServicesDao) {
		this.kimServicesDao = kimServicesDao;
	}

}
