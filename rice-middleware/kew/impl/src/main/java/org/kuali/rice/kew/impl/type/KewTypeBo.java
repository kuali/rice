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
import org.kuali.rice.kew.api.repository.type.KewTypeAttribute;
import org.kuali.rice.kew.api.repository.type.KewTypeDefinition;
import org.kuali.rice.kew.api.repository.type.KewTypeDefinitionContract;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.ArrayList;
import java.util.List;

/**
 * Kuali workflow type business object.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KREW_TYP_T")
public class KewTypeBo implements KewTypeDefinitionContract, MutableInactivatable {

    @Id
    @GeneratedValue(generator = "KREW_TYP_S")
    @PortableSequenceGenerator(name = "KREW_TYP_S")
    @Column(name = "TYP_ID", nullable = false)
    private String id;

    @Column(name = "NM", nullable = false)
    private String name;

    @Column(name = "NMSPC_CD", nullable = false)
    private String namespace;

    @Column(name = "SRVC_NM")
    private String serviceName;

    @Column(name = "ACTV", nullable = false)
    @Convert(converter = BooleanYNConverter.class)
    private boolean active;

    @Version
    @Column(name = "VER_NBR", nullable = false)
    private Long versionNumber;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "type", orphanRemoval = true)
    private List<KewTypeAttributeBo> attributes;

    /**
     * Converts a mutable bo to it's immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static KewTypeDefinition to(KewTypeBo bo) {
        if (null == bo) {
            return null;
        } else {
            return KewTypeDefinition.Builder.create(bo).build();
        }
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    public static KewTypeBo from(org.kuali.rice.kew.api.repository.type.KewTypeDefinition im) {
        if (null == im) {
            return null;
        } else {
            KewTypeBo bo = new KewTypeBo();
            bo.setId(im.getId());
            bo.setName(im.getName());
            bo.setNamespace(im.getNamespace());
            bo.setServiceName(im.getServiceName());
            bo.setActive(im.isActive());
            bo.setAttributes(new ArrayList<KewTypeAttributeBo>());
            if (null != im.getAttributes() && !im.getAttributes().isEmpty()) {
                for(KewTypeAttribute attr : im.getAttributes()) {
                    bo.getAttributes().add(KewTypeAttributeBo.from(attr, bo));
                }
            }

            return bo;
        }
    }

    /**
     * Default constructor.
     */
    public KewTypeBo() { }

    /**
     * Returns the type id.
     * @return the type id
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
     * Returns the KEW type name.
     * @return the KEW type name
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
     * Returns the name space.
     * @return the name space.
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
     * Returns the service name.
     * @return the service name
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @see #getServiceName()
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Returns the status of this KEW type
     * @return TRUE if the type is active, FALSE otherwise.
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
     * Returns the version number.
     * @return the version number.
     */
    public Long getVersionNumber() {
        return versionNumber;
    }

    /**
     * @see #getVersionNumber()
     */
    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    /**
     * Returns a {@link List} of {@link KewTypeAttributeBo}.
     * @return a {@link List} of {@link KewTypeAttributeBo}
     */
    public List<KewTypeAttributeBo> getAttributes() {
        if (attributes == null) {
            attributes = new ArrayList<KewTypeAttributeBo>();
        }
        return attributes;
    }

    /**
     * @see #getAttributes()
     */
    public void setAttributes(List<KewTypeAttributeBo> attributes) {
        this.attributes = attributes;
    }
}
