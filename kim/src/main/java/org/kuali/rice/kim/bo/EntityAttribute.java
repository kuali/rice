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
package org.kuali.rice.kim.bo;

import java.util.LinkedHashMap;

import org.kuali.rice.kim.dto.EntityAttributeDTO;

/**
 * This class represents an instance of a meta-data attribute hanging off of an Entity in the system.
 * These are group by namespace; hence, the namespace relationship.  An example of an entity attribute would
 * be "name" or "address"... these are essentially used for identity attribute data.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EntityAttribute extends AbstractAttributeBase {

	private static final long serialVersionUID = 2861440911751860350L;
	private Long entityId;
	private Long namespaceId;

    private Entity entity;
    private Namespace namespace;

	public Long getNamespaceId() {
		return namespaceId;
	}

	public void setNamespaceId(Long namespaceId) {
		this.namespaceId = namespaceId;
	}

	public void refresh() {
		// not going to implement unless needed
	}

    /**
     * @return the entityId
     */
    public Long getEntityId() {
        return this.entityId;
    }

    /**
     * @param entityId the entityId to set
     */
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    /**
     * @return the entity
     */
    public Entity getEntity() {
        return this.entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    /**
     * @return the namespace
     */
    public Namespace getNamespace() {
        return this.namespace;
    }

    /**
     * @param namespace the namespace to set
     */
    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("entityId", getEntityId());
        propMap.put("attributeTypeId", getAttributeTypeId());
        propMap.put("namespaceId", getNamespaceId());
        propMap.put("attributeName", getAttributeName());
        propMap.put("value", getValue());
        return propMap;
    }

    /**
     *
     * This method creates a DTO from a BO
     *
     * @param EntityAttribute
     * @return EntityAttributeDTO
     */
    public static EntityAttributeDTO toDTO(final EntityAttribute ea) {
        final EntityAttributeDTO dto = new EntityAttributeDTO();
        AbstractAttributeBase.fillInDTO(dto, ea);
        dto.setNamespaceId(ea.getNamespaceId());
        dto.setEntityId(ea.getEntityId());

        return dto;
    }
}
