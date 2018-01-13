/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krms.impl.repository;

import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.krms.api.repository.BaseAttributeContract;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;

/**
 * This class contains the common elements of a KRMS attribute.
 * <p>
 * Attributes provide a way to attach custom data to an entity based on that entity's type.
 * Rules, Actions, Contexts, Agendas and Term Resolvers have their own specific
 * attribute types. This class contains their common fields.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@MappedSuperclass
public abstract class BaseAttributeBo implements BaseAttributeContract, Versioned, Serializable {

    private static final long serialVersionUID = 3820684124163057591L;
    @Column(name="ATTR_VAL")
    private String value;

    @Version
    @Column(name="VER_NBR", length=8)
    protected Long versionNumber;

    public String getAttributeDefinitionId() {
        if (getAttributeDefinition() != null) {
            return getAttributeDefinition().getId();
        }

        return null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }
}
