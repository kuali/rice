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
import org.kuali.rice.krms.api.repository.type.KrmsTypeAttribute;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinitionContract;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "KRMS_TYP_T")
public class KrmsTypeBo implements MutableInactivatable, KrmsTypeDefinitionContract, Serializable {

    private static final long serialVersionUID = 1l;

    @PortableSequenceGenerator(name = "KRMS_TYP_S")
    @GeneratedValue(generator = "KRMS_TYP_S")
    @Id
    @Column(name = "TYP_ID")
    private String id;

    @Column(name = "NM")
    private String name;

    @Column(name = "NMSPC_CD")
    private String namespace;

    @Column(name = "SRVC_NM")
    private String serviceName;

    @Column(name = "ACTV")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active = true;

    @Version
    @Column(name="VER_NBR", length=8)
    protected Long versionNumber;

    @OneToMany(targetEntity = KrmsTypeAttributeBo.class, fetch = FetchType.LAZY, orphanRemoval = true,
//            , mappedBy = "type",
            cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "TYP_ID", referencedColumnName = "TYP_ID")
    private List<KrmsTypeAttributeBo> attributes;

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static KrmsTypeDefinition to(KrmsTypeBo bo) {
        if (bo == null) {
            return null;
        }

        return KrmsTypeDefinition.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static KrmsTypeBo from(KrmsTypeDefinition im) {
        if (im == null) {
            return null;
        }

        KrmsTypeBo bo = new KrmsTypeBo();
        bo.id = im.getId();
        bo.name = im.getName();
        bo.namespace = im.getNamespace();
        bo.serviceName = im.getServiceName();
        bo.active = im.isActive();
        bo.attributes = new ArrayList<KrmsTypeAttributeBo>();

        for (KrmsTypeAttribute attr : im.getAttributes()) {
            KrmsTypeAttributeBo attrBo = KrmsTypeAttributeBo.from(attr);
            bo.attributes.add(attrBo);
            attrBo.setType(bo);
        }

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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public List<KrmsTypeAttributeBo> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<KrmsTypeAttributeBo> attributes) {
        this.attributes = attributes;
    }

}
