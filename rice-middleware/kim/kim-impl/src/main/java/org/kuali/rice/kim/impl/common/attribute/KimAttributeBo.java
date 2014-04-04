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
package org.kuali.rice.kim.impl.common.attribute;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.kim.api.common.attribute.KimAttribute;
import org.kuali.rice.kim.api.common.attribute.KimAttributeContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

@Entity
@Table(name = "KRIM_ATTR_DEFN_T")
public class KimAttributeBo extends PersistableBusinessObjectBase implements KimAttributeContract {

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_ATTR_DEFN_ID_S")
    @GeneratedValue(generator = "KRIM_ATTR_DEFN_ID_S")
    @Id
    @Column(name = "KIM_ATTR_DEFN_ID")
    private String id;

    @Column(name = "CMPNT_NM")
    private String componentName;

    @Column(name = "NM")
    private String attributeName;

    @Column(name = "NMSPC_CD")
    private String namespaceCode;

    @Column(name = "LBL")
    private String attributeLabel;

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active;

    public static KimAttribute to(KimAttributeBo bo) {
        if (bo == null) {
            return null;
        }
        return KimAttribute.Builder.create(bo).build();
    }

    public static KimAttributeBo from(KimAttribute im) {
        if (im == null) {
            return null;
        }
        KimAttributeBo bo = new KimAttributeBo();
        bo.setId(im.getId());
        bo.setComponentName(im.getComponentName());
        bo.setAttributeName(im.getAttributeName());
        bo.setNamespaceCode(im.getNamespaceCode());
        bo.setAttributeLabel(im.getAttributeLabel());
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
    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    @Override
    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    @Override
    public String getAttributeLabel() {
        return attributeLabel;
    }

    public void setAttributeLabel(String attributeLabel) {
        this.attributeLabel = attributeLabel;
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
}
