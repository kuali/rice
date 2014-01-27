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
import org.kuali.rice.coreservice.api.component.ComponentContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.DisableVersioning;
import org.kuali.rice.krad.data.jpa.RemoveMapping;
import org.kuali.rice.krad.data.jpa.RemoveMappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@IdClass(ComponentId.class)
@Entity
@Table(name = "KRCR_DRVD_CMPNT_T")
@DisableVersioning
@RemoveMappings({
        @RemoveMapping(name = "versionNumber"),
        @RemoveMapping(name = "objectId")
})
public class DerivedComponentBo extends PersistableBusinessObjectBase implements ComponentContract {

    @Id
    @Column(name="NMSPC_CD")
    private String namespaceCode;

    @Id
    @Column(name="CMPNT_CD")
    private String code;

    @Column(name="NM")
    private String name;

    @Column(name="CMPNT_SET_ID")
    private String componentSetId;

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
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
    public String getComponentSetId() {
        return componentSetId;
    }

    public void setComponentSetId(String componentSetId) {
        this.componentSetId = componentSetId;
    }

    @Override
    protected void preUpdate() {
        // override to do nothing so that the object id doesn't get automatically set prior to an update
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static Component to(DerivedComponentBo bo) {
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
    public static DerivedComponentBo from(Component im) {
        if (im == null) {
            return null;
        }

        DerivedComponentBo bo = new DerivedComponentBo();
        bo.code = im.getCode();
        bo.name = im.getName();
        bo.namespaceCode = im.getNamespaceCode();
        bo.componentSetId = im.getComponentSetId();

        return bo;
    }

    public static ComponentBo toComponentBo(DerivedComponentBo derivedComponentBo) {
        if (derivedComponentBo == null) {
            return null;
        }
        Component component = DerivedComponentBo.to(derivedComponentBo);
        return ComponentBo.from(component);
    }
}
