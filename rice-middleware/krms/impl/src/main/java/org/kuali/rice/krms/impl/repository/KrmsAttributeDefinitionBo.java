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
package org.kuali.rice.krms.impl.repository;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinitionContract;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

@Entity
@Table(name = "KRMS_ATTR_DEFN_T")
public class KrmsAttributeDefinitionBo implements KrmsAttributeDefinitionContract, MutableInactivatable, Serializable {

    private static final long serialVersionUID = 1l;

    @PortableSequenceGenerator(name = "KRMS_ATTR_DEFN_S")
    @GeneratedValue(generator = "KRMS_ATTR_DEFN_S")
    @Id
    @Column(name = "ATTR_DEFN_ID")
    private String id;

    @Column(name = "NM")
    private String name;

    @Column(name = "NMSPC_CD")
    private String namespace;

    @Column(name = "LBL")
    private String label;

    @Column(name = "DESC_TXT")
    private String description;

    @Column(name = "ACTV")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active = true;

    @Column(name = "CMPNT_NM")
    private String componentName;

    @Version
    @Column(name="VER_NBR", length=8)
    protected Long versionNumber;

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static KrmsAttributeDefinition to(KrmsAttributeDefinitionBo bo) {
        if (bo == null) {
            return null;
        }

        return KrmsAttributeDefinition.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static KrmsAttributeDefinitionBo from(KrmsAttributeDefinition im) {
        if (im == null) {
            return null;
        }

        KrmsAttributeDefinitionBo bo = new KrmsAttributeDefinitionBo();
        bo.id = im.getId();
        bo.name = im.getName();
        bo.namespace = im.getNamespace();
        bo.label = im.getLabel();
        bo.description = im.getDescription();
        bo.active = im.isActive();
        bo.componentName = im.getComponentName();
        bo.setVersionNumber(im.getVersionNumber());

        return bo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getActive() {
        return active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }
}
