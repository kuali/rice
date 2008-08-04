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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kim.dto.AbstractAttributeBaseDTO;

/**
 * This is a base helper class that encapsulates common fields needed by several "Attribute" classes.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@MappedSuperclass
public abstract class AbstractAttributeBase extends PersistableBusinessObjectBase implements Serializable {

	@Id
	@Column(name="ID")
	private Long id;
	@Column(name="ATTRIBUTE_TYPE_ID")
    private Long attributeTypeId;
	@Column(name="ATTRIBUTE_NAME")
    private String attributeName;
	@Column(name="ATTRIBUTE_VALUE")
    private String value;
    @Transient
    private AttributeType attributeType;

    /**
     * This constructs an AttributeBase object instance.
     *
     */
    public AbstractAttributeBase() {
        super();
    }

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

    public String getValue() {
    	return value;
    }

    public void setValue(String value) {
    	this.value = value;
    }

    /**
     * @return the attributeType
     */
    public AttributeType getAttributeType() {
        return this.attributeType;
    }

    /**
     * @param attributeType the attributeType to set
     */
    public void setAttributeType(AttributeType attributeType) {
        this.attributeType = attributeType;
    }

    /**
     *
     * This method fills in the base attributes values in the DTO from the BO
     *
     * @param dto
     * @param bo
     */
    protected static void fillInDTO(final AbstractAttributeBaseDTO dto, final AbstractAttributeBase bo) {
        dto.setAttributeTypeId(bo.getAttributeTypeId());
        dto.setId(bo.getId());
        dto.setValue(bo.getValue());
        dto.setAttributeName(bo.getAttributeName());
    }
}
