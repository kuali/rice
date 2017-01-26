/**
 * Copyright 2005-2017 The Kuali Foundation
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

import org.kuali.rice.core.api.CoreConstants;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import java.util.Collection;

/**
 * An immutable predicate which represents a "greater than" statement which is
 * evaluated the {@link CriteriaValue} of this predicate.
 * 
 * @see PredicateFactory for a convenient way to construct this class.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
@XmlRootElement(name = GreaterThanPredicate.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = GreaterThanPredicate.Constants.TYPE_NAME, propOrder = {
    CriteriaSupportUtils.PropertyConstants.VALUE,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class GreaterThanPredicate extends AbstractPredicate implements SingleValuedPredicate {
	    
	private static final long serialVersionUID = 2576163857285296720L;
	
	@XmlAttribute(name = CriteriaSupportUtils.PropertyConstants.PROPERTY_PATH)
	private final String propertyPath;

	@XmlElements(value = {
            @XmlElement(name = CriteriaStringValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaStringValue.class, required = true),
            @XmlElement(name = CriteriaPropertyPathValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaPropertyPathValue.class, required = true),
			@XmlElement(name = CriteriaDecimalValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaDecimalValue.class, required = true),
            @XmlElement(name = CriteriaKualiDecimalValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaKualiDecimalValue.class, required = true),
            @XmlElement(name = CriteriaKualiPercentValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaKualiPercentValue.class, required = true),
            @XmlElement(name = CriteriaKualiIntegerValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaKualiIntegerValue.class, required = true),
            @XmlElement(name = CriteriaIntegerValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaIntegerValue.class, required = true),
            @XmlElement(name = CriteriaDateTimeValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaDateTimeValue.class, required = true)
    })
	private final CriteriaValue<?> value;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

	/**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    private GreaterThanPredicate() {
        this.propertyPath = null;
        this.value = null;
    }
    
    /**
	 * Constructs a GreaterThanPredicate for the given path and value.  GreaterThanPredicate supports the following {@link CriteriaValue}:
	 * 
	 * <ul>
	 *   <li>{@link CriteriaDateTimeValue}</li>
	 *   <li>{@link CriteriaDecimalValue}</li>
	 *   <li>{@link CriteriaIntegerValue}</li>
	 * </ul>
	 * 
	 * @param propertyPath the property path for the predicate, must not be null or blank
	 * @param value the value to evaluation the path against, must not be null.
	 * 
	 * @throws IllegalArgumentException if the propertyPath is null or blank
	 * @throws IllegalArgumentException if the value is null
	 * @throws IllegalArgumentException if this predicate does not support the given type of {@link CriteriaValue}
	 */
    GreaterThanPredicate(String propertyPath, CriteriaValue<?> value) {
    	CriteriaSupportUtils.validateValuedConstruction(getClass(), propertyPath, value);
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
        final static String ROOT_ELEMENT_NAME = "greaterThan";
        final static String TYPE_NAME = "GreaterThanType";
    }

    @Override
    public String toString() {
        return CriteriaSupportUtils.toString(this);
    }
}
