/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.kim.impl.identity.affiliation;

import org.eclipse.persistence.annotations.Convert;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class EntityAffiliationBo extends PersistableBusinessObjectBase implements EntityAffiliationContract {
    private static final long serialVersionUID = 0L;
    @Id
    @Column(name = "ENTITY_AFLTN_ID")
    private String id;
    @Column(name = "ENTITY_ID")
    private String entityId;
    @Column(name = "AFLTN_TYP_CD")
    private String affiliationTypeCode;
    @Column(name = "CAMPUS_CD")
    private String campusCode;
    @ManyToOne(targetEntity = EntityAffiliationTypeBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(
            name = "AFLTN_TYP_CD", insertable = false, updatable = false)
    private EntityAffiliationTypeBo affiliationType;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "DFLT_IND")
    private boolean defaultValue;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active;

    public static EntityAffiliation to(EntityAffiliationBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityAffiliation.Builder.create(bo)
                .build();
    }

    /**
     * Creates a EntityAffiliationBo business object from an immutable representation of a EntityAffiliation.
     *
     * @param immutable an immutable EntityAffiliation
     * @return a EntityAffiliationBo
     */
    public static EntityAffiliationBo from(EntityAffiliation immutable) {
        if (immutable == null) {
            return null;
        }

        EntityAffiliationBo bo = new EntityAffiliationBo();
        bo.active = immutable.isActive();
        if (immutable.getAffiliationType() != null) {
            bo.affiliationTypeCode = immutable.getAffiliationType().getCode();
            bo.affiliationType = EntityAffiliationTypeBo.from(immutable.getAffiliationType());
        }

        bo.id = immutable.getId();
        bo.campusCode = immutable.getCampusCode();
        bo.entityId = immutable.getEntityId();
        bo.active = immutable.isActive();
        bo.defaultValue = immutable.isDefaultValue();
        bo.setVersionNumber(immutable.getVersionNumber());

        return bo;
    }

    @Override
    public EntityAffiliationTypeBo getAffiliationType() {
        return this.affiliationType;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getAffiliationTypeCode() {
        return affiliationTypeCode;
    }

    public void setAffiliationTypeCode(String affiliationTypeCode) {
        this.affiliationTypeCode = affiliationTypeCode;
    }

    @Override
    public String getCampusCode() {
        return campusCode;
    }

    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    public void setAffiliationType(EntityAffiliationTypeBo affiliationType) {
        this.affiliationType = affiliationType;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
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
