/* Copyright 2007 The Kuali Foundation
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

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kim.dto.NamespaceDefaultAttributeDTO;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * A NamespaceDefaultAttribute represents a single meta-data attribute in the system that shows up on
 * the Entity maintenance screen when that Entity is given permissions to use functionality
 * within a certain Namespace in the system.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@javax.persistence.Entity
@Table(name="KIM_NAMESPACE_DFLT_ATTRIBS_T")
public class NamespaceDefaultAttribute extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = -8332284694172302250L;
	@Id
	@Column(name="ID")
	private Long id;
	@Column(name="NAMESPACE_ID")
	private Long namespaceId;
	@Column(name="ATTRIBUTE_TYPE_ID")
	private Long attributeTypeId;
	@Column(name="ATTRIBUTE_NAME")
	private String attributeName;
	@Column(name="DESCRIPTION")
	private String description;
	@Column(name="REQUIRED")
	private boolean required;
	@Column(name="ACTIVE")
	private boolean active;

	//@OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	//@JoinColumn(name="ATTRIBUTE_TYPE_ID", insertable=false, updatable=false)
	@Transient
	private AttributeType attributeType;
	//@ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	//@JoinColumn(name="NAMESPACE_ID", insertable=false, updatable=false)
	@Transient
	private Namespace namespace;

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public Long getAttributeTypeId() {
		return attributeTypeId;
	}

	public void setAttributeTypeId(Long attributeTypeId) {
		this.attributeTypeId = attributeTypeId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("namespaceId", getNamespaceId());
        propMap.put("attributeTypeId", getAttributeTypeId());
        propMap.put("attributeName", getAttributeName());
        propMap.put("value", getDescription());
        propMap.put("required", isRequired());
        propMap.put("active", isActive());
        return propMap;
	}

	public void refresh() {
		// not going to add unless needed
	}

	public Long getNamespaceId() {
	    return this.namespaceId;
	}

	public void setNamespaceId(Long namespaceId) {
	    this.namespaceId = namespaceId;
	}

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return this.required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public AttributeType getAttributeType() {
        return this.attributeType;
    }

    public void setAttributeType(AttributeType attributeType) {
        this.attributeType = attributeType;
    }

    public Namespace getNamespace() {
        return this.namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    /**
     * This method creates a NamespaceDefaultAttributeDTO from a NamespaceDefaultAttribute
     *
     * @param nda
     * @return NamespaceDefaultAttributeDTO
     */
    public static NamespaceDefaultAttributeDTO toDTO(final NamespaceDefaultAttribute nda) {
        final NamespaceDefaultAttributeDTO dto = new NamespaceDefaultAttributeDTO();
        dto.setActive(nda.isActive());
        dto.setAttributeName(nda.getAttributeName());
        dto.setAttributeTypeId(nda.getAttributeTypeId());
        dto.setDescription(nda.getDescription());
        dto.setId(nda.getId());
        dto.setNamespaceId(nda.getNamespaceId());
        dto.setRequired(nda.isRequired());
        return dto;
    }
    
    /**
     * This method creates a NamespaceDefaultAttribute from a NamespaceDefaultAttributeDTO
     *
     * @param ndaDTO
     * @return NamespaceDefaultAttribute
     */
    public static NamespaceDefaultAttribute toBO(final NamespaceDefaultAttributeDTO ndaDTO) {
        final NamespaceDefaultAttribute bo = new NamespaceDefaultAttribute();
        bo.setActive(ndaDTO.getActive());
        bo.setAttributeName(ndaDTO.getAttributeName());
        bo.setAttributeTypeId(ndaDTO.getAttributeTypeId());
        bo.setDescription(ndaDTO.getDescription());
        bo.setId(ndaDTO.getId());
        bo.setNamespaceId(ndaDTO.getNamespaceId());
        bo.setRequired(ndaDTO.getRequired());
        return bo;
    }
}

