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
package org.kuali.rice.coreservice.impl.component;

import org.kuali.rice.coreservice.api.component.Component;
import org.kuali.rice.coreservice.api.namespace.NamespaceService;
import org.kuali.rice.coreservice.framework.component.ComponentEbo;
import org.kuali.rice.coreservice.impl.namespace.NamespaceBo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@IdClass(ComponentId.class)
@Entity
@Table(name = "KRCR_CMPNT_T")
public class ComponentBo extends PersistableBusinessObjectBase implements ComponentEbo {

    private static final long serialVersionUID = 1L;

    private static transient NamespaceService namespaceService;

    @Id
    @Column(name = "NMSPC_CD")
    private String namespaceCode;

    @Id
    @Column(name = "CMPNT_CD")
    private String code;

    @Column(name = "NM")
    private String name;

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NMSPC_CD", insertable = false, updatable = false)
    private NamespaceBo namespace;

    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
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

    public NamespaceBo getNamespace() {
        return namespace;
    }

    public void setNamespace(NamespaceBo namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getComponentSetId() {
        return null;
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static Component to(ComponentBo bo) {
        if (bo == null) {
            return null;
        }

        return Component.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    public static ComponentBo from(Component im) {
        if (im == null) {
            return null;
        }

        ComponentBo bo = new ComponentBo();
        bo.code = im.getCode();
        bo.name = im.getName();
        bo.active = im.isActive();
        bo.namespaceCode = im.getNamespaceCode();
        bo.versionNumber = im.getVersionNumber();
        bo.objectId = im.getObjectId();

        bo.namespace = NamespaceBo.from(namespaceService.getNamespace(bo.namespaceCode));
        return bo;
    }

    public static void setNamespaceService(NamespaceService namespaceService) {
        ComponentBo.namespaceService = namespaceService;
    }

}
