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

import org.kuali.rice.kim.bo.Entity;
import org.kuali.rice.kim.bo.EntityAttribute;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.Namespace;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.dao.KIMServicesDao;
import org.kuali.rice.kim.dto.EntityAttributeDTO;
import org.kuali.rice.kim.dto.EntityDTO;
import org.kuali.rice.kim.service.EntityService;
import org.kuali.rice.kns.KNSServiceLocator;

/**
 * This is a description of what this class does - lindholm don't forget to fill
 * this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EntityServiceImpl implements EntityService {
	private KIMServicesDao kimServicesDao;

	/**
	 *
	 * @see org.kuali.rice.kim.service.EntityService#getAllEntityIds()
	 */
	public List<Long> getAllEntityIds() {
		final ArrayList<Long> entityIds = new ArrayList<Long>(0);

		final Collection<Entity> entitys = (Collection<Entity>) KNSServiceLocator
				.getBusinessObjectService().findAll(Entity.class);
		if (entitys == null) {
			return entityIds;
		}

		entityIds.ensureCapacity(entitys.size());
		for (Entity entity : entitys) {
			entityIds.add(entity.getId());
		}
		return entityIds;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.EntityService#getAllEntitys()
	 */
	public List<EntityDTO> getAllEntitys() {
		final ArrayList<EntityDTO> dto = new ArrayList<EntityDTO>(0);


		final Collection<Entity> entitys = (Collection<Entity>) KNSServiceLocator
				.getBusinessObjectService().findAll(Entity.class);
		if (entitys == null) {
			return dto;
		}

		dto.ensureCapacity(entitys
				.size());
		for (Entity entity : entitys) {
			dto.add(Entity.toDTO(entity));
		}
		return dto;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.EntityService#getAttributeValue(java.lang.Long,
	 *      java.lang.String, java.lang.String)
	 */
	public String getAttributeValue(Long entityId, String attributeName,
			String namespaceName) {
		return kimServicesDao.getAttributeValueForNamespace(attributeName,
				namespaceName, entityId);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.EntityService#getEntityAttributesByNamespace(java.lang.Long)
	 */
	public HashMap<String, List<EntityAttributeDTO>> getEntityAttributesByNamespace(
			Long entityId) {
		final HashMap<String, List<EntityAttributeDTO>> dtos = new HashMap<String, List<EntityAttributeDTO>>(0);

		final Collection<Namespace> namespaces = (Collection<Namespace>) KNSServiceLocator
				.getBusinessObjectService().findAll(Namespace.class);
		if (namespaces == null) {
			return dtos;
		}

		for (Namespace namespace : namespaces) {
			Collection<EntityAttribute> eas = kimServicesDao
					.getAttributesForNamespace(namespace.getName(), entityId);
			if (eas != null) {
				final ArrayList<EntityAttributeDTO> dto = new ArrayList<EntityAttributeDTO>();
				for (EntityAttribute ea : eas) {
					dto.add(EntityAttribute.toDTO(ea));
				}
				if (dto.size() > 0) {
					dtos.put(namespace.getName(), dto);
				}
			}
		}
		return dtos;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.EntityService#getEntityAttributesForNamespace(java.lang.Long,
	 *      java.lang.String)
	 */
	public HashMap<String, EntityAttributeDTO> getEntityAttributesForNamespace(
			Long entityId, String namespaceName) {
		Collection<EntityAttribute> eas = kimServicesDao
				.getAttributesForNamespace(namespaceName, entityId);
		final HashMap<String, EntityAttributeDTO> dtos = new HashMap<String, EntityAttributeDTO>();
		for (EntityAttribute ea : eas) {
			dtos.put(ea.getAttributeName(), EntityAttribute.toDTO(ea));
		}
		return dtos;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.EntityService#getEntityIdsWithAttributes(java.util.Map,
	 *      java.lang.String)
	 */
	public List<Long> getEntityIdsWithAttributes(
			Map<String, String> entityAttributes, String namespaceName) {
		final ArrayList<Long> ids = new ArrayList<Long>(0);

		final Collection<EntityAttribute> eas = getEntityAttributes(namespaceName);
		if (eas == null) {
			return ids;
		}

		ids.ensureCapacity(eas.size());
		for (EntityAttribute ea : eas) {
			for (String key : entityAttributes.keySet()) {
				if (key.equals(ea.getAttributeName())
						&& entityAttributes.get(key).equals(ea.getValue())) {
					ids.add(ea.getEntityId());
				}
			}
		}
		return ids;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.EntityService#getEntitysWithAttributes(java.util.Map,
	 *      java.lang.String)
	 */
	public List<EntityDTO> getEntitysWithAttributes(
			Map<String, String> entityAttributes, String namespaceName) {
		final ArrayList<EntityDTO> dto = new ArrayList<EntityDTO>(0);

		final Collection<EntityAttribute> eas = getEntityAttributes(namespaceName);
		if (eas == null) {
			return dto;
		}

		dto.ensureCapacity(eas.size());
		for (EntityAttribute ea : eas) {
			for (String key : entityAttributes.keySet()) {
				if (key.equals(ea.getAttributeName())
						&& entityAttributes.get(key).equals(ea.getValue())) {
					dto.add(Entity.toDTO(ea.getEntity()));
				}
			}
		}
		return dto;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.EntityService#hasAttributes(java.lang.Long,
	 *      java.util.Map, java.lang.String)
	 */
	public boolean hasAttributes(Long entityId,
			Map<String, String> entityAttributes, String namespaceName) {
		final Namespace namespace = findNamespace(namespaceName);
		if (namespace == null) {
			return false;
		}

		final HashMap<String, Long> criteriaLong = new HashMap<String, Long>();
		criteriaLong.put("SPONSOR_NAMESPACE_ID", namespace.getId());
		criteriaLong.put("ENTITY_ID", entityId);
		final Collection<EntityAttribute> eas = (Collection<EntityAttribute>) KNSServiceLocator
				.getBusinessObjectService().findMatching(EntityAttribute.class,
						criteriaLong);
		if (eas == null) {
			return false;
		}
		boolean hasAttrs = true;
		for (String key : entityAttributes.keySet()) {
			for (EntityAttribute ea : eas) {
				if (!(key.equals(ea.getAttributeName()) && entityAttributes
						.get(key).equals(ea.getValue()))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.EntityService#isMemberOfGroup(java.lang.Long,
	 *      java.lang.String)
	 */
	public boolean isMemberOfGroup(Long entityId, String groupName) {
		final HashMap<String, String> criteriaString = new HashMap<String, String>();
		criteriaString.put("NAME", groupName);
		final Group group = ((Collection<Group>) KNSServiceLocator
				.getBusinessObjectService().findMatching(Group.class,
						criteriaString)).iterator().next();

		final HashMap<String, Long> criteriaLong = new HashMap<String, Long>();
		criteriaString.put("NAME", groupName);
		final Principal principal = ((Collection<Principal>) KNSServiceLocator
				.getBusinessObjectService().findMatching(Principal.class,
						criteriaLong)).iterator().next();

		for (Group g : principal.getGroups()) {
			if (g.getId() == group.getId()) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @param namespaceName
	 * @return
	 */
	private Collection<EntityAttribute> getEntityAttributes(String namespaceName) {
		final Namespace namespace = findNamespace(namespaceName);
		if (namespace == null) {
			return new ArrayList<EntityAttribute>(0);
		}

		final HashMap<String, Long> criteriaLong = new HashMap<String, Long>();
		criteriaLong.put("SPONSOR_NAMESPACE_ID", namespace.getId());
		final Collection<EntityAttribute> eas = (Collection<EntityAttribute>) KNSServiceLocator
				.getBusinessObjectService().findMatching(EntityAttribute.class,
						criteriaLong);
		return eas;
	}

	/**
	 *
	 *
	 * @param namespaceName
	 * @return
	 */
	private Namespace findNamespace(String namespaceName) {
		final HashMap<String, String> criteriaString = new HashMap<String, String>();
		criteriaString.put("NAME", namespaceName);
		final Namespace namespace = ((Collection<Namespace>) KNSServiceLocator
				.getBusinessObjectService().findMatching(Namespace.class,
						criteriaString)).iterator().next();
		return namespace;
	}


	/**
	 * @param kimServicesDao
	 *            the kimServicesDao to set
	 */
	public void setKimServicesDao(KIMServicesDao kimServicesDao) {
		this.kimServicesDao = kimServicesDao;
	}

}
