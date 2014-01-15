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

package org.kuali.rice.coreservice.impl.parameter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import java.io.Serializable;

public class ParameterId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name="NMSPC_CD")
    private final String namespaceCode;

    @Column(name="CMPNT_CD")
    private final String componentCode;

    @Column(name="PARM_NM")
    private final String name;

    @Column(name="APPL_ID")
    private final String applicationId;

    public ParameterId() {
        namespaceCode = null;
        componentCode = null;
        name = null;
        applicationId = null;
    }

    public ParameterId(String namespaceCode, String componentCode, String name, String applicationId) {
        this.namespaceCode = namespaceCode;
        this.componentCode = componentCode;
        this.name = name;
        this.applicationId = applicationId;
    }

    public String getNamespaceCode() {
        return namespaceCode;
    }

    public String getComponentCode() {
        return componentCode;
    }

    public String getName() {
        return name;
    }

    public String getApplicationId() {
        return applicationId;
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

    public String getCacheKey() {
        return this.applicationId + this.componentCode + this.namespaceCode + this.name;
    }
}

