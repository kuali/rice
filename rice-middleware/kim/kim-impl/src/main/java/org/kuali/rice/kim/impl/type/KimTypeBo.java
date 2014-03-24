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
package org.kuali.rice.kim.impl.type;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.api.type.KimTypeContract;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.springframework.util.AutoPopulatingList;

@Entity
@Table(name = "KRIM_TYP_T")
public class KimTypeBo extends DataObjectBase implements KimTypeContract, BusinessObject {
    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_TYP_ID_S")
    @GeneratedValue(generator = "KRIM_TYP_ID_S")
    @Id
    @Column(name = "KIM_TYP_ID")
    private String id;

    @Column(name = "SRVC_NM")
    private String serviceName;

    @Column(name = "NMSPC_CD")
    private String namespaceCode;

    @Column(name = "NM")
    private String name;

    @OneToMany(targetEntity = KimTypeAttributeBo.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "KIM_TYP_ID", referencedColumnName = "KIM_TYP_ID", insertable = false, updatable = false)
    private List<KimTypeAttributeBo> attributeDefinitions = new AutoPopulatingList<KimTypeAttributeBo>(KimTypeAttributeBo.class);

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active;

    /**
     * Converts a mutable bo to its immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static KimType to(KimTypeBo bo) {
        if (bo == null) {
            return null;
        }
        return KimType.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static KimTypeBo from(KimType im) {
        if (im == null) {
            return null;
        }
        KimTypeBo bo = new KimTypeBo();
        bo.setId(im.getId());
        bo.setServiceName(im.getServiceName());
        bo.setNamespaceCode(im.getNamespaceCode());
        bo.setName(im.getName());
        List<KimTypeAttributeBo> attributeBos = new ArrayList<KimTypeAttributeBo>();
        if (CollectionUtils.isNotEmpty(im.getAttributeDefinitions())) {
            for (KimTypeAttribute kimTypeAttribute : im.getAttributeDefinitions()) {
                attributeBos.add(KimTypeAttributeBo.from(kimTypeAttribute));
            }
            bo.setAttributeDefinitions(attributeBos);
        }
        bo.setActive(im.isActive());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        return bo;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<KimTypeAttributeBo> getAttributeDefinitions() {
        return attributeDefinitions;
    }

    public void setAttributeDefinitions(List<KimTypeAttributeBo> attributeDefinitions) {
        this.attributeDefinitions = attributeDefinitions;
    }

    public boolean getActive() {
        return active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void refresh() {
    }
}
