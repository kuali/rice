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
package org.kuali.rice.kim.api.identity.personal;

import org.kuali.rice.core.api.mo.common.Coded;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A DisabilityConditionStatusCode is an enum that represents valid type indicating a disability condition status.
 */
@XmlRootElement(name = "disabilityConditionStatusCodeType")
@XmlType(name = "disabilityConditionStatusCodeTypeType")
@XmlEnum
public enum DisabilityConditionStatusCode implements Coded {

    /**
     * Condition type corresponding to Permanent disabilities
     */
    @XmlEnumValue("P") PERMANENT("P"),

    /**
     * Condition type corresponding to Temporary disabilities
     */
    @XmlEnumValue("T") TEMPORARY("T");

    public final String code;

    private DisabilityConditionStatusCode(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    public static DisabilityConditionStatusCode fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (DisabilityConditionStatusCode codeType : values()) {
            if (codeType.code.equals(code)) {
                return codeType;
            }
        }
        throw new IllegalArgumentException("Failed to locate the DisabilityConditionStatusCode with the given code: " + code);
    }

}

