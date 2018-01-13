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
package org.kuali.rice.coreservice.impl.namespace;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.kuali.rice.coreservice.api.namespace.Namespace;
import org.kuali.rice.coreservice.framework.namespace.NamespaceEbo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="KRCR_NMSPC_T")
public class NamespaceBo extends PersistableBusinessObjectBase implements NamespaceEbo {

    private static final long serialVersionUID = 1L;

    @Column(name="APPL_ID")
    private String applicationId;

    @Id
    @Column(name="NMSPC_CD")
    private String code;

    @Column(name="NM")
    private String name;

    @Column(name="ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active = true;

    @Override
    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static Namespace to(NamespaceBo bo) {
        if (bo == null) {
            return null;
        }

        return Namespace.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    public static NamespaceBo from(Namespace im) {
        if (im == null) {
            return null;
        }

        NamespaceBo bo = new NamespaceBo();
        bo.applicationId = im.getApplicationId();
        bo.active = im.isActive();
        bo.code = im.getCode();
        bo.name = im.getName();
        bo.versionNumber = im.getVersionNumber();
        bo.objectId = im.getObjectId();

        return bo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return EqualsBuilder.reflectionEquals(o, this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}