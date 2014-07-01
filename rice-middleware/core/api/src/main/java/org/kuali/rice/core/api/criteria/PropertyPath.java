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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * This is a class which references a property on a specified data object for use in Predicates.  
 * The property does not
 * need to be directly on the given data type as long as it is reachable via standard JavaBeans
 * "dot" notation. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since Rice 2.4.2
 */
@XmlRootElement(name = PropertyPath.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = PropertyPath.Constants.TYPE_NAME)
public class PropertyPath {
    
    @XmlAttribute(name = Elements.DATA_TYPE, required = false)
    private final String dataType;
    
    @XmlAttribute(name = Elements.PROPERTY_PATH, required = true)
    private final String propertyPath;

    /**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    private PropertyPath() {
        dataType = null;
        propertyPath = null;
    }
    
    /**
     * Constructs a PropertyPath for the given data type and property path.
     * 
     * @param dataType The data type against which to resolve the property path.  Or, null if should 
     * be resolved against the main object of the containing query.
     * @param propertyPath the property path for the predicate, must not be null or blank
     * 
     * @throws IllegalArgumentException if the propertyPath is null or blank
     */
    public PropertyPath(String dataType, String propertyPath) {
        super();
        if ( StringUtils.isBlank(propertyPath) ) {
            throw new IllegalArgumentException("propertyPath may not be blank or null");
        }
        this.dataType = dataType;
        this.propertyPath = propertyPath;
    }

    /**
     * The type of the object to which the property path belongs.
     * 
     * This may be null, in which case the property path will be resolved against
     * the main data type of the containing query. 
     */
    public String getDataType() {
        return this.dataType;
    }

    /**
     * Returns the property path for this value which will be resolved into a field reference
     * when building the query within the persistence provider.
     */
    public String getPropertyPath() {
        return this.propertyPath;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this);
    }
    
    @Override
    public String toString() {
        if ( StringUtils.isNotBlank(dataType) ) {
            return dataType + "." + propertyPath;
        } else {
            return propertyPath;
        }
    }
    
    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "propertyPath";
        final static String TYPE_NAME = "PropertyPathType";
    }

    /**
     * A private class which exposes constants which define the XML element
     * names to use when this object is marshaled to XML.
     */
    static class Elements {
        final static String DATA_TYPE = "dataType";
        final static String PROPERTY_PATH = CriteriaSupportUtils.PropertyConstants.PROPERTY_PATH;
    }    
}
