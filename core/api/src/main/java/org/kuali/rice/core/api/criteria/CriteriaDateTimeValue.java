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

import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = CriteriaDateTimeValue.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = CriteriaDateTimeValue.Constants.TYPE_NAME)
public final class CriteriaDateTimeValue implements CriteriaValue<Calendar> {

    @XmlValue
    private final Calendar value;
    
    CriteriaDateTimeValue() {
        this.value = null;
    }
    
    CriteriaDateTimeValue(Calendar value) {
    	validateValue(value);
        this.value = value;
    }
    
    CriteriaDateTimeValue(Date value) {
    	validateValue(value);
    	Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(value.getTime());
        this.value = calendar;
    }
    
    private static void validateValue(Object value) {
    	if (value == null) {
    		throw new IllegalArgumentException("Value cannot be null.");
    	}
    }
        
    public Calendar getValue() {
        return value;
    }
    
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Two CriteriaDateTimeValues are equal if they have the same epoch value (Calendar.getTimeInMillis())
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
    	if (obj instanceof CriteriaDateTimeValue) {
    		return value.getTimeInMillis() == ((CriteriaDateTimeValue)obj).value.getTimeInMillis();
    	}
        return false;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "dateTimeValue";
        final static String TYPE_NAME = "CriteriaDateTimeValueType";
    }
    
}
