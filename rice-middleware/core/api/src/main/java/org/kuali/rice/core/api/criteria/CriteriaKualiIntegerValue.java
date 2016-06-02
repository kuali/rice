/**
 * Copyright 2005-2016 The Kuali Foundation
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
import org.kuali.rice.core.api.util.type.KualiInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A CriteriaValue which stores {@link org.kuali.rice.core.api.util.type.KualiInteger} information in the form of a
 * {@link java.math.BigInteger} value.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = CriteriaKualiIntegerValue.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = CriteriaKualiIntegerValue.Constants.TYPE_NAME)
public final class CriteriaKualiIntegerValue implements CriteriaValue<KualiInteger> {

    @XmlValue
    private final BigInteger value;

    CriteriaKualiIntegerValue() {
        this.value = null;
    }

    CriteriaKualiIntegerValue(KualiInteger value) {
    	validateValue(value);
        this.value = safeInstance(value);
    }

    CriteriaKualiIntegerValue(long value) {
        validateValue(value);
        this.value = new KualiInteger(value).bigIntegerValue();
    }

    CriteriaKualiIntegerValue(int value) {
        validateValue(value);
        this.value = new KualiInteger((long)value).bigIntegerValue();
    }

    CriteriaKualiIntegerValue(String value) {
        validateValue(value);
        this.value = new KualiInteger(value).bigIntegerValue();
    }

    CriteriaKualiIntegerValue(BigDecimal value) {
        validateValue(value);
        this.value = new KualiInteger(value).bigIntegerValue();
    }

    private static void validateValue(Object value) {
    	if (value == null) {
    		throw new IllegalArgumentException("Value cannot be null.");
    	}
    }

    /**
     * Since KualiInteger is not technically immutable we defensively copy when needed.
     * <p>
     * see Effective Java 2nd ed. page 79 for details.
     * </p>
     * @param val the KualiInteger to check
     * @return the safe BigInteger
     */
    private static BigInteger safeInstance(KualiInteger val) {
        return new BigInteger(val.bigIntegerValue().toString());
    }
    
    @Override
    public KualiInteger getValue() {
        return new KualiInteger(this.value);
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
        final static String ROOT_ELEMENT_NAME = "kualiIntegerValue";
        final static String TYPE_NAME = "CriteriaKualiIntegerValueType";
    }
    
}
