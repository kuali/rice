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
import org.kuali.rice.kim.bo.EntityAttribute;
import org.kuali.rice.kim.bo.EntityType;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.Namespace;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.bo.PrincipalQualifiedRoleAttribute;
import org.kuali.rice.kim.dao.KIMServicesDao;
import org.kuali.rice.kim.dto.PersonAttributeDTO;
import org.kuali.rice.kim.dto.PersonDTO;
import org.kuali.rice.kim.service.PersonService;

/**
 * This is the default KIM PersonService implementation that is provided by
 * Rice. This will mature over time as the KIM component is developed.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PersonServiceImpl implements PersonService {
	private KIMServicesDao kimServicesDao;

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.PersonService#getAllPersonIds()
	 */
	public List<Long> getAllPersonIds() {
		final Collection<Principal> persons = kimServicesDao.findAllPersons();
		final ArrayList<Long> ids = new ArrayList<Long>(persons.size());
		for (Principal p : persons) {
			ids.add(p.getId());
		}
		return ids;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.PersonService#getAllPersons()
	 */
	public List<PersonDTO> getAllPersons() {
		final Collection<Principal> persons = kimServicesDao.findAllPersons();
		final ArrayList<PersonDTO> ids = new ArrayList<PersonDTO>(persons
				.size());
		for (Principal p : persons) {
			ids.add(Principal.toPersonDTO(p));
		}
		return ids;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.PersonService#getAttributeValue(java.lang.Long,
	 *      java.lang.String, java.lang.String)
	 */
	public String getAttributeValue(Long personId, String attributeName,
			String namespaceName) {
		return kimServicesDao.getPersonAttributeValue(personId, attributeName,
				namespaceName);
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.PersonService#getPersonIdsWithAttributes(java.util.Map,
	 *      java.lang.String)
	 */
	public List<Long> getPersonIdsWithAttributes(
			Map<String, String> personAttributes, String namespaceName) {
		final Collection<Principal> persons = kimServicesDao.findAllPersons();
		final ArrayList<Long> ids = new ArrayList<Long>(persons.size());
		for (Principal p : persons) {
			if (hasAttributes(p.getId(), personAttributes, namespaceName)) {
				ids.add(p.getId());
			}
		}
		return ids;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.PersonService#getPersonsWithAttributes(java.util.Map,
	 *      java.lang.String)
	 */
	public List<PersonDTO> getPersonsWithAttributes(
			Map<String, String> personAttributes, String namespaceName) {
		final Collection<Principal> principals = kimServicesDao
				.findAllPersons();
		final ArrayList<PersonDTO> persons = new ArrayList<PersonDTO>(
				principals.size());
		for (Principal p : principals) {
			if (hasAttributes(p.getId(), personAttributes, namespaceName)) {
				persons.add(Principal.toPersonDTO(p));
			}
		}
		return persons;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.PersonService#hasAttributes(java.lang.Long,
	 *      java.util.Map, java.lang.String)
	 */
	public boolean hasAttributes(Long personId,
			Map<String, String> personAttributes, String namespaceName) {

		final Collection<EntityAttribute> attributeValues = kimServicesDao
				.getPersonAttributesForNamespace(personId, namespaceName);

		if (attributeValues != null && attributeValues.size() > 0) {
			return ImplUtils
					.hasAllAttributes(personAttributes, attributeValues);
		}

		return false;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.PersonService#isMemberOfGroup(java.lang.Long,
	 *      java.lang.String)
	 */
	public boolean isMemberOfGroup(Long personId, String groupName) {
		final Principal principal = kimServicesDao.findPerson(personId);
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
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.PersonService#getPersonAttributesByNamespace(java.lang.Long)
	 */
	public HashMap<String, List<PersonAttributeDTO>> getPersonAttributesByNamespace(
			Long personId) {
		final HashMap<String, List<PersonAttributeDTO>> attributes = new HashMap<String, List<PersonAttributeDTO>>(0);

		final Principal principal = kimServicesDao.findPerson(personId);
		if (principal == null) {
			return attributes;
		}

		final Collection<Namespace> namespaces = (Collection<Namespace>) KNSServiceLocator
				.getBusinessObjectService().findAll(Namespace.class);
		for (Namespace namespace : namespaces) {
			final Collection<EntityAttribute> attributeValues = kimServicesDao
					.getPersonAttributesForNamespace(personId, namespace
							.getName());
			if (attributeValues != null && attributeValues.size() > 0) {
				final ArrayList<PersonAttributeDTO> personAttributes = new ArrayList<PersonAttributeDTO>(
						attributeValues.size());
				for (EntityAttribute ea : attributeValues) {
					personAttributes.add(EntityAttribute.toPersonDTO(ea));
				}
				attributes.put(namespace.getName(), personAttributes);
			}
		}
		return attributes;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.PersonService#getPersonAttributesForNamespace(java.lang.Long,
	 *      java.lang.String)
	 */
	public HashMap<String, PersonAttributeDTO> getPersonAttributesForNamespace(
			Long personId, String namespaceName) {

		final HashMap<String, PersonAttributeDTO> eas = new HashMap<String, PersonAttributeDTO>(0);

		final Collection<EntityAttribute> attributeValues = kimServicesDao
				.getPersonAttributesForNamespace(personId, namespaceName);
		if (attributeValues == null) {
			return eas;
		}

		for (EntityAttribute ea : attributeValues) {
			eas.put(ea.getAttributeName(), EntityAttribute.toPersonDTO(ea));
		}
		return eas;
	}

	/**
	 * @param kimServicesDao
	 *            the kimServicesDao to set
	 */
	public void setKimServicesDao(KIMServicesDao kimServicesDao) {
		this.kimServicesDao = kimServicesDao;
	}

}
