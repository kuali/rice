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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiPercent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import java.math.BigDecimal;

/**
 * A CriteriaValue which stores {@link org.kuali.rice.core.api.util.type.KualiDecimal} information in the form of a
 * {@link java.math.BigDecimal} value.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = CriteriaKualiDecimalValue.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = CriteriaKualiDecimalValue.Constants.TYPE_NAME)
public final class CriteriaKualiDecimalValue implements CriteriaValue<KualiDecimal> {

    @XmlValue
    private final BigDecimal value;

    CriteriaKualiDecimalValue() {
        this.value = null;
    }

    CriteriaKualiDecimalValue(KualiDecimal value) {
    	validateValue(value);
        this.value = safeInstance(value);
        this.value.setScale(KualiDecimal.SCALE);
    }

    CriteriaKualiDecimalValue(double value) {
        validateValue(value);
        this.value = new KualiDecimal(value).bigDecimalValue();
    }

    CriteriaKualiDecimalValue(int value) {
        validateValue(value);
        this.value = new KualiDecimal(value).bigDecimalValue();
    }

    CriteriaKualiDecimalValue(String value) {
        validateValue(value);
        this.value = new KualiDecimal(value).bigDecimalValue();
    }


    CriteriaKualiDecimalValue(BigDecimal value) {
        validateValue(value);
        this.value = new KualiDecimal(value).bigDecimalValue();
    }

    private static void validateValue(Object value) {
    	if (value == null) {
    		throw new IllegalArgumentException("Value cannot be null.");
    	}
    }

    /**
     * Since KualiDecimal is not technically immutable we defensively copy when needed.
     *
     * see Effective Java 2nd ed. page 79 for details.
     *
     * @param val the KualiDecimal to check
     * @return the safe BigDecimal
     */
    private static BigDecimal safeInstance(KualiDecimal val) {
        return new BigDecimal(val.bigDecimalValue().toPlainString());
    }
    
    @Override
    public KualiDecimal getValue() {
        return new KualiDecimal(this.value);
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
        return ToStringBuilder.reflectionToString(this);
    }
    
    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "kualiDecimalValue";
        final static String TYPE_NAME = "CriteriaKualiDecimalValueType";
    }
    
}
