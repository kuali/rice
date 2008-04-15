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

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * This is a base helper class that encapsulates common fields needed by several "Attribute" classes. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class AttributeBase extends PersistableBusinessObjectBase {

    private Long id;
    private Long attributeTypeId;
    private String attributeName;
    private String value;
    private AttributeType attributeType;

    /**
     * This constructs an AttributeBase object instance.
     * 
     */
    public AttributeBase() {
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
}