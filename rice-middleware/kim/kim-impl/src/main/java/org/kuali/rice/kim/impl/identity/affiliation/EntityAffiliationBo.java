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
package org.kuali.rice.kim.impl.identity.affiliation;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

@Entity
@Table(name = "KRIM_ENTITY_AFLTN_T")
public class EntityAffiliationBo extends EntityAffiliationBase {

    private static final long serialVersionUID = 0L;

    @PortableSequenceGenerator(name = "KRIM_ENTITY_AFLTN_ID_S")
    @GeneratedValue(generator = "KRIM_ENTITY_AFLTN_ID_S")
    @Id
    @Column(name = "ENTITY_AFLTN_ID")
    private String id;

    @ManyToOne(targetEntity = EntityAffiliationTypeBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "AFLTN_TYP_CD", referencedColumnName = "AFLTN_TYP_CD", insertable = false, updatable = false)
    private EntityAffiliationTypeBo affiliationType;

    public static EntityAffiliation to(EntityAffiliationBo bo) {
        if (bo == null) {
            return null;
        }
        return EntityAffiliation.Builder.create(bo).build();
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
        bo.setActive(immutable.isActive());
        if (immutable.getAffiliationType() != null) {
            bo.setAffiliationTypeCode(immutable.getAffiliationType().getCode());
            bo.setAffiliationType(EntityAffiliationTypeBo.from(immutable.getAffiliationType()));
        }
        bo.setId(immutable.getId());
        bo.setCampusCode(immutable.getCampusCode());
        bo.setEntityId(immutable.getEntityId());
        bo.setActive(immutable.isActive());
        bo.setDefaultValue(immutable.isDefaultValue());
        bo.setVersionNumber(immutable.getVersionNumber());
        return bo;
    }

    @Override
    public EntityAffiliationTypeBo getAffiliationType() {
        return this.affiliationType;
    }

    public void setAffiliationType(EntityAffiliationTypeBo affiliationType) {
        this.affiliationType = affiliationType;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
