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

import org.apache.commons.lang.StringUtils;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An immutable predicate which represents a "not in" statement which is
 * evaluated against a list of values.
 * 
 * <p>Constructed as part of a {@link Criteria} when built using a
 * {@link CriteriaBuilder}.
 * 
 * @see Criteria
 * @see CriteriaBuilder
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = NotInPredicate.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = NotInPredicate.Constants.TYPE_NAME, propOrder = {
    CriteriaSupportUtils.PropertyConstants.VALUES,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class NotInPredicate extends AbstractPredicate implements MultiValuedPredicate {

	private static final long serialVersionUID = -7676442296587603655L;
	
	@XmlAttribute(name = CriteriaSupportUtils.PropertyConstants.PROPERTY_PATH)
	private final String propertyPath;

	@XmlElements(value = {
            @XmlElement(name = CriteriaStringValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaStringValue.class, required = true),
            @XmlElement(name = CriteriaDateTimeValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaDateTimeValue.class, required = true),
            @XmlElement(name = CriteriaIntegerValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaIntegerValue.class, required = true),
            @XmlElement(name = CriteriaDecimalValue.Constants.ROOT_ELEMENT_NAME, type = CriteriaDecimalValue.class, required = true)
	})
	private final List<? extends CriteriaValue<?>> values;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

	/**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    private NotInPredicate() {
        this.propertyPath = null;
        this.values = null;
    }
    
    /**
	 * Constructs a NotInPredicate for the given propertyPath and list of criteria values.
	 * 
	 * @param propertyPath the property path for the predicate, must not be null or blank
	 * @param values the list of criteria values to use for this predicate, must be non-null,
	 * non-empty, and all CriteriaValues contained within must be of the same type.
	 * 
	 * @throws IllegalArgumentException if the propertyPath is null or blank
	 * @throws IllegalArgumentException if the list of values is null, empty, or contains {@link CriteriaValue} of different types
	 */
    NotInPredicate(String propertyPath, List<? extends CriteriaValue<?>> values) {
    	if (StringUtils.isBlank(propertyPath)) {
			throw new IllegalArgumentException("Property path cannot be null or blank.");
		}
    	CriteriaSupportUtils.validateValuesForMultiValuedPredicate(values);
		this.propertyPath = propertyPath;

        if (values == null) {
            this.values = new ArrayList<CriteriaValue<?>>();
        } else {
            this.values = new ArrayList<CriteriaValue<?>>(values);
        }
    }

    @Override
    public String getPropertyPath() {
    	return propertyPath;
    }
    
    @Override
    public List<CriteriaValue<?>> getValues() {
    	return Collections.unmodifiableList(values);
    }
        
	/**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "notIn";
        final static String TYPE_NAME = "NotInType";
    }
    
}
