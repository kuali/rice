/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.core.api.criteria;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

/**
 * TODO 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = NotInExpression.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = NotInExpression.Constants.TYPE_NAME)
public final class NotInExpression extends AbstractExpression implements PropertyPathExpression {

	private static final long serialVersionUID = -7676442296587603655L;
	
	@XmlAttribute(name = PROPERTY_PATH)
	private final String propertyPath;
	@XmlElements(value = {
            @XmlElement(name = CriteriaStringValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaStringValue.class, required = true),
            @XmlElement(name = CriteriaDateTimeValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaDateTimeValue.class, required = true),
            @XmlElement(name = CriteriaIntegerValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaIntegerValue.class, required = true),
            @XmlElement(name = CriteriaDecimalValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaDecimalValue.class, required = true)
	})
	private final List<? extends CriteriaValue<?>> values;
	
	/**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    private NotInExpression() {
        this.propertyPath = null;
        this.values = null;
    }
    
    /**
	 * Constructs a NotInExpression for the given propertyPath and list of criteria values.
	 * 
	 * @param propertyPath the property path for the expression, must not be null or blank
	 * @param values the list of criteria values to use for this expression, must be non-null,
	 * non-empty, and all CriteriaValues contained within must be of the same type.
	 * 
	 * @throws IllegalArgumentException if the propertyPath is null or blank
	 * @throws IllegalArgumentException if the list of values is null, empty, or contains {@link CriteriaValue} of different types
	 */
    NotInExpression(String propertyPath, List<? extends CriteriaValue<?>> values) {
    	if (StringUtils.isBlank(propertyPath)) {
			throw new IllegalArgumentException("Property path cannot be null or blank.");
		}
    	validateValues(values);
		this.propertyPath = propertyPath;
		this.values = values;
    }
    
    private static void validateValues(List<? extends CriteriaValue<?>> values) {
    	if (values == null) {
    		throw new IllegalArgumentException("Criteria values cannot be null.");
    	} else if (values.isEmpty()) {
    		throw new IllegalArgumentException("Criteria values cannot be empty.");
    	}
    	Class<?> previousType = null;
    	for (CriteriaValue<?> value : values) {
    		Class<?> currentType = value.getClass();
    		if (previousType != null) {
    			if (!currentType.equals(previousType)) {
    				throw new IllegalArgumentException("Encountered criteria values which do not match.  One was: " + previousType + " the other was: " + currentType);
    			}
    		}
    		previousType = currentType;
    	}
    }

    @Override
    public String getPropertyPath() {
    	return propertyPath;
    }
    
    public List<CriteriaValue<?>> getValues() {
    	return new ArrayList<CriteriaValue<?>>(values);
    }
        
	/**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "notIn";
        final static String TYPE_NAME = "NotInType";
    }
    
}
