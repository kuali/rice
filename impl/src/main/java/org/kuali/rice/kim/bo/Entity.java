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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.core.web.ui.Field;
import org.kuali.rice.kim.dto.EntityAttributeDTO;
import org.kuali.rice.kim.dto.EntityDTO;
import org.kuali.rice.kim.dto.PrincipalDTO;
import org.kuali.rice.kim.web.form.EntityAttributeForm;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * An Entity represents a specific instance of a person, process, company, system, etc in the system.  An Entity
 * has meta-data that hangs off of it.  User XYZ would be represented in the system as an Entity of type Person.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@javax.persistence.Entity
@Table(name="KIM_ENTITYS_T")
public class Entity extends AbstractEntityBase {
	private static final long serialVersionUID = 2232201572169570616L;

	//@OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
    //       targetEntity=org.kuali.rice.kim.bo.EntityAttribute.class, mappedBy="entity")
	@Transient
	private ArrayList<EntityAttribute> entityAttributes;
	//@OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
    //       targetEntity=org.kuali.rice.kim.bo.Principal.class, mappedBy="entity")
	@Transient
	private ArrayList<Principal> principals;
	
	//for web UI rendering only
	@Transient
	private ArrayList<EntityAttributeForm> entityAttributeForms;
	@Transient
	private HashMap<String, String> namespaceEntityAttributes;  // used for temporary storage from post to post

	public Entity() {
	    this.entityAttributes = new TypedArrayList(EntityAttribute.class);
	    this.principals = new TypedArrayList(Principal.class);
	    this.entityAttributeForms = new TypedArrayList(EntityAttributeForm.class);
	    this.namespaceEntityAttributes = new HashMap<String, String>();
	}

    /**
     * @return the entityAttributes
     */
    public ArrayList<EntityAttribute> getEntityAttributes() {
        return this.entityAttributes;
    }

    /**
     * @param entityAttributes the entityAttributes to set
     */
    public void setEntityAttributes(ArrayList<EntityAttribute> entityAttributes) {
        this.entityAttributes = entityAttributes;
    }
    
    /**
	 * @return the formEntityAttributes
	 */
	public ArrayList<EntityAttributeForm> getEntityAttributeForms() {
		return this.entityAttributeForms;
	}

	/**
	 * @param entityAttributeForms the formEntityAttributes to set
	 */
	public void setEntityAttributeForms(
			ArrayList<EntityAttributeForm> entityAttributeForms) {
		this.entityAttributeForms = entityAttributeForms;
	}
	
	/**
	 * @return the namespaceEntityAttributes
	 */
	public HashMap<String, String> getNamespaceEntityAttributes() {
		return this.namespaceEntityAttributes;
	}

	/**
	 * @param namespaceEntityAttributes the namespaceEntityAttributes to set
	 */
	public void setNamespaceEntityAttributes(
			HashMap<String, String> namespaceEntityAttributes) {
		this.namespaceEntityAttributes = namespaceEntityAttributes;
	}

	protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("entityType", getEntityType());
        return propMap;
	}

	public void refresh() {
		// not doing this unless we need it
	}

    /**
     * @return the principals
     */
    public ArrayList<Principal> getPrincipals() {
        return this.principals;
    }

    /**
     * @param principals the principals to set
     */
    public void setPrincipals(ArrayList<Principal> principals) {
        this.principals = principals;
    }

    /**
     *
     * This method creates a DTO from a BO
     *
     * @param Entity
     * @return EntityDTO
     */
    public static EntityDTO toDTO(final Entity entity) {
        final EntityDTO dto = new EntityDTO();
        dto.setEntityTypeId(entity.getEntityTypeId());
        dto.setId(entity.getId());
        dto.setEntityType(EntityType.toDTO(entity.getEntityType()));

        final HashMap<String, EntityAttributeDTO> attrs = new HashMap<String, EntityAttributeDTO>();
        for (EntityAttribute attr : entity.getEntityAttributes()) {
            attrs.put(attr.getAttributeName(), EntityAttribute.toDTO(attr));
        }
        dto.setEntityAttributesDtos(attrs);

        final HashMap<String,PrincipalDTO> principals = new HashMap<String, PrincipalDTO>();
        for (Principal principal : entity.getPrincipals()) {
            principals.put(principal.getName(), Principal.toDTO(principal));
        }
        dto.setPrincipalDtos(principals);

        return dto;
    }
}

