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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

/**
 * TODO 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = NotNullExpression.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = NotNullExpression.Constants.TYPE_NAME)
public final class NotNullExpression extends AbstractExpression implements PropertyPathExpression {
	
	private static final long serialVersionUID = 6723462533500402423L;
	@XmlAttribute(name = PROPERTY_PATH)
	private final String propertyPath;
	
	/**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    private NotNullExpression() {
        this.propertyPath = null;
    }
    
    /**
	 * Constructs a NotNullExpression for the given propertyPath.
	 * 
	 * @param propertyPath the property path for the expression, must not be null or blank
	 * 
	 * @throws IllegalArgumentException if the propertyPath is null or blank
	 */
    NotNullExpression(String propertyPath) {
    	if (StringUtils.isBlank(propertyPath)) {
			throw new IllegalArgumentException("Property path cannot be null or blank.");
		}
		this.propertyPath = propertyPath;
    }

    @Override
    public String getPropertyPath() {
    	return propertyPath;
    }
        
	/**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "notNull";
        final static String TYPE_NAME = "NotNullType";
    }
    
}
