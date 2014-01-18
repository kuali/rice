/*
 * Copyright 2006-2014 The Kuali Foundation
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

package org.kuali.rice.location.impl.postalcode;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public final class PostalCodeId implements Serializable {

    private static final long serialVersionUID = 5680559929187623010L;

    @Id
    @Column(name = "POSTAL_CNTRY_CD")
    private final String countryCode;

    @Id
    @Column(name = "POSTAL_CD")
    private final String code;

    public PostalCodeId(String countryCode, String code) {
        this.countryCode = countryCode;
        this.code = code;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCode() {
        return code;
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

}
