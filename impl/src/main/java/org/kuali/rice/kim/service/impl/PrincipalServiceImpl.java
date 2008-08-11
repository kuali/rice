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

import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.dao.KIMServicesDao;
import org.kuali.rice.kim.dto.PrincipalDTO;
import org.kuali.rice.kim.service.PrincipalService;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is a description of what this class does - lindholm don't forget to fill
 * this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PrincipalServiceImpl implements PrincipalService {
	private KIMServicesDao kimServicesDao;

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.PrincipalService#getAllPrincipalNames()
	 */
	public List<String> getAllPrincipalNames() {
		final Collection<Principal> principals = (Collection<Principal>) KNSServiceLocator
				.getBusinessObjectService().findAll(Principal.class);
		List<String> names = new ArrayList<String>(principals.size());
		for (Principal principal : principals) {
			names.add(principal.getName());
		}
		return names;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.PrincipalService#getAllPrincipals()
	 */
	public List<PrincipalDTO> getAllPrincipals() {
		final Collection<Principal> principals = (Collection<Principal>) KNSServiceLocator
				.getBusinessObjectService().findAll(Principal.class);
		List<PrincipalDTO> dto = new ArrayList<PrincipalDTO>(principals.size());
		for (Principal principal : principals) {
			dto.add(Principal.toDTO(principal));
		}
		return dto;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.PrincipalService#getPrincipal(java.lang.String)
	 */
	public PrincipalDTO getPrincipal(final String principalName) {
		final HashMap<String, String> criteria = new HashMap<String, String>();
		criteria.put("NAME", principalName);
		final Collection<Principal> principal = (Collection<Principal>) KNSServiceLocator
				.getBusinessObjectService().findMatching(Principal.class,
						criteria);
		return Principal.toDTO(principal.iterator().next());
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.PrincipalService#getPrincipalNamesForEntity(java.lang.Long)
	 */
	public List<String> getPrincipalNamesForEntity(Long entityId) {
		final HashMap<String, Long> criteria = new HashMap<String, Long>();
		criteria.put("ENTITY_ID", entityId);
		final Collection<Principal> principals = (Collection<Principal>) KNSServiceLocator
				.getBusinessObjectService().findMatching(Principal.class,
						criteria);
		final ArrayList<String> names = new ArrayList<String>(principals.size());
		for (Principal p : principals) {
			names.add(p.getName());
		}
		return names;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.PrincipalService#getPrincipalsForEntity(java.lang.Long)
	 */
	public List<PrincipalDTO> getPrincipalsForEntity(Long entityId) {
		final HashMap<String, Long> criteria = new HashMap<String, Long>();
		criteria.put("ENTITY_ID", entityId);
		final Collection<Principal> entityPrincipals = (Collection<Principal>) KNSServiceLocator
				.getBusinessObjectService().findMatching(Principal.class,
						criteria);
		final ArrayList<PrincipalDTO> principals = new ArrayList<PrincipalDTO>(
				entityPrincipals.size());
		for (Principal p : entityPrincipals) {
			principals.add(Principal.toDTO(p));
		}
		return principals;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.PrincipalService#getPrincipalNamesForPerson(java.lang.Long)
	 */
	public List<String> getPrincipalNamesForPerson(Long personId) {
		final Principal person = kimServicesDao.findPerson(personId);

		return getPrincipalNamesForEntity(person.getEntityId());
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.PrincipalService#getPrincipalsForPerson(java.lang.Long)
	 */
	public List<PrincipalDTO> getPrincipalsForPerson(Long personId) {
		final Principal person = kimServicesDao.findPerson(personId);

		return getPrincipalsForEntity(person.getEntityId());
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.PrincipalService#isMemberOfGroup(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean isMemberOfGroup(String principalName, String groupName) {
		final HashMap<String, String> criteria = new HashMap<String, String>();
		criteria.put("NAME", principalName);
		final Principal principal = ((Collection<Principal>) KNSServiceLocator
				.getBusinessObjectService().findMatching(Principal.class,
						criteria)).iterator().next();
		if (principal == null) {
			return false;
		}

		for (Group g : principal.getGroups()) {
			if (g.getName().equals(groupName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param kimServicesDao the kimServicesDao to set
	 */
	public void setKimServicesDao(KIMServicesDao kimServicesDao) {
		this.kimServicesDao = kimServicesDao;
	}

}
