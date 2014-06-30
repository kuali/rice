/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.core.api.criteria;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A CriteriaValue which stores date and time information in the form of a
 * {@link String} value.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since Rice 2.4.2
 */
@XmlRootElement(name = CriteriaPropertyPathValue.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = CriteriaPropertyPathValue.Constants.TYPE_NAME)
public final class CriteriaPropertyPathValue implements CriteriaValue<PropertyPath> {
    
    @XmlElement(name = PropertyPath.Constants.ROOT_ELEMENT_NAME, type = PropertyPath.class, required = true)
    private final PropertyPath value;
    
    /**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    private CriteriaPropertyPathValue() {
        this.value = null;
    }
        
    CriteriaPropertyPathValue(PropertyPath value) {
    	if (value == null) {
    		throw new IllegalArgumentException("Value cannot be null.");
    	}
    	this.value = value;
    }
    
    @Override
    public PropertyPath getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this);
    }
    
    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "propertyPathValue";
        final static String TYPE_NAME = "CriteriaPropertyPathValueType";
    }
    
}
