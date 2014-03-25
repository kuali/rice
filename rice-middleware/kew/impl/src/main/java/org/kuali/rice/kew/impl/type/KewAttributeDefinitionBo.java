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
package org.kuali.rice.kew.impl.type;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kew.api.repository.type.KewAttributeDefinition;
import org.kuali.rice.kew.api.repository.type.KewAttributeDefinitionContract;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Kuali workflow attribute definition business object.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KREW_ATTR_DEFN_T")
public class KewAttributeDefinitionBo implements KewAttributeDefinitionContract, MutableInactivatable {

    @Id
    @GeneratedValue(generator = "KREW_ATTR_DEFN_S")
    @PortableSequenceGenerator(name = "KREW_ATTR_DEFN_S")
    @Column(name = "ATTR_DEFN_ID", nullable = false)
    private String id;

    @Column(name = "NM", nullable = false)
    private String name;

    @Column(name = "NMSPC_CD", nullable = false)
    private String namespace;

    @Column(name = "LBL")
    private String label;

    @Column(name = "ACTV", nullable = false)
    @Convert(converter = BooleanYNConverter.class)
    private boolean active;

    @Column(name = "CMPNT_NM")
    private String componentName;

    @Version
    @Column(name = "VER_NBR", nullable = false)
    private Long versionNumber;

    @Column(name = "DESC_TXT")
    private String description;

    /**
     * Default constructor.
     */
    public KewAttributeDefinitionBo() { }

    /**
     * Converts a mutable bo to it's immutable counterpart
     * @param kadBo the mutable business object
     * @return the immutable object
     */
    public static KewAttributeDefinition to(KewAttributeDefinitionBo kadBo) {
        if(null == kadBo) {
            return null;
        } else {
            return org.kuali.rice.kew.api.repository.type.KewAttributeDefinition.Builder.create(kadBo).build();
        }
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     * @param kadIm immutable object
     * @return the mutable bo
     */
    public static KewAttributeDefinitionBo from(KewAttributeDefinition kadIm) {
        if (null == kadIm) {
            return null;
        } else {
            KewAttributeDefinitionBo kadBo = new KewAttributeDefinitionBo();
            kadBo.setId(kadIm.getId());
            kadBo.setName(kadIm.getName());
            kadBo.setNamespace(kadIm.getNamespace());
            kadBo.setLabel(kadIm.getLabel());
            kadBo.setDescription(kadIm.getDescription());
            kadBo.setActive(kadIm.isActive());
            kadBo.setComponentName(kadIm.getComponentName());
            kadBo.setVersionNumber(kadIm.getVersionNumber());

            return kadBo;
        }
    }

    /**
     * returns the unique identifier for this class
     * @return the unique identifier for this class
     */
    public String getId() {
        return id;
    }

    /**
     * @see #getId()
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the attribute name
     * @return the attribute name
     */
    public String getName() {
        return name;
    }

    /**
     * @see #getName()
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name space
     * @return the name space
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @see #getNamespace()
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Returns the label
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @see #getLabel
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the status of this record
     * @return TRUE if the record is active, FALSE otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see #isActive()
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns the component name.
     * @return the component name
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * @see #getComponentName()
     */
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    /**
     * Returns the description.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @see #getDescription()
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
