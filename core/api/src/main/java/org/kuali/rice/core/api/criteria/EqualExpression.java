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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = EqualExpression.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EqualExpression.Constants.TYPE_NAME)
public final class EqualExpression extends AbstractExpression implements SimpleExpression {
	
	private static final long serialVersionUID = 7159459561133496549L;
	
	@XmlAttribute(name = SimpleExpression.PROPERTY_PATH)
	private final String propertyPath;
	@XmlElements(value = {
    		@XmlElement(name = CriteriaStringValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaStringValue.class, required = true),
    		@XmlElement(name = CriteriaDateTimeValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaDateTimeValue.class, required = true),
    		@XmlElement(name = CriteriaDecimalValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaDecimalValue.class, required = true),
    		@XmlElement(name = CriteriaIntegerValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaIntegerValue.class, required = true)
    })
	private final CriteriaValue<?> value;
	
    /**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    private EqualExpression() {
        this.propertyPath = null;
        this.value = null;
    }
    
    /**
	 * Constructs an EqualExpression for the given path and value.  EqualExpression supports the following {@link CriteriaValue}:
	 * 
	 * <ul>
	 *   <li>{@link CriteriaStringValue}</li>
	 *   <li>{@link CriteriaDateTimeValue}</li>
	 *   <li>{@link CriteriaDecimalValue}</li>
	 *   <li>{@link CriteriaIntegerValue}</li>
	 * </ul>
	 * 
	 * @param propertyPath the property path for the expression, must not be null or blank
	 * @param value the value to evaluation the path against, must not be null.
	 * 
	 * @throws IllegalArgumentException if the propertyPath is null or blank
	 * @throws IllegalArgumentException if the value is null
	 * @throws IllegalArgumentException if this expression does not support the given type of {@link CriteriaValue}
	 */
    EqualExpression(String propertyPath, CriteriaValue<?> value) {
    	CriteriaSupportUtils.validateSimpleExpressionConstruction(getClass(), propertyPath, value);
		this.propertyPath = propertyPath;
		this.value = value;
    }
        
    @Override
    public String getPropertyPath() {
    	return propertyPath;
    }
    
	@Override
	public CriteriaValue<?> getValue() {
		return value;
	}
    
	/**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "equal";
        final static String TYPE_NAME = "EqualType";
    }
    
}
