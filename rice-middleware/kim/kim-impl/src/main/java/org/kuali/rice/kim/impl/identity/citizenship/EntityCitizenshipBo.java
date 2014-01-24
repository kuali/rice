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
package org.kuali.rice.kim.impl.identity.citizenship;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenship;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

@Entity
@Table(name = "KRIM_ENTITY_CTZNSHP_T")
public class EntityCitizenshipBo extends EntityCitizenshipBase {

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_ENTITY_CTZNSHP_ID_S")
    @GeneratedValue(generator = "KRIM_ENTITY_CTZNSHP_ID_S")
    @Id
    @Column(name = "ENTITY_CTZNSHP_ID")
    private String id;

    @ManyToOne(targetEntity = EntityCitizenshipStatusBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "CTZNSHP_STAT_CD", referencedColumnName = "CTZNSHP_STAT_CD", insertable = false, updatable = false)
    private EntityCitizenshipStatusBo status;

    @Transient
    private EntityCitizenshipChangeTypeBo changeType;

    public static EntityCitizenship to(EntityCitizenshipBo bo) {
        if (bo == null) {
            return null;
        }
        return EntityCitizenship.Builder.create(bo).build();
    }

    /**
     * Creates a EntityCitizenshipBo business object from an immutable representation of a EntityCitizenship.
     *
     * @param immutable an immutable EntityCitizenship
     * @return a EntityCitizenshipBo
     */
    public static EntityCitizenshipBo from(EntityCitizenship immutable) {
        if (immutable == null) {
            return null;
        }
        EntityCitizenshipBo bo = new EntityCitizenshipBo();
        bo.setActive(immutable.isActive());
        if (immutable.getStatus() != null) {
            bo.setStatusCode(immutable.getStatus().getCode());
            bo.setStatus(EntityCitizenshipStatusBo.from(immutable.getStatus()));
        }
        bo.setId(immutable.getId());
        bo.setEntityId(immutable.getEntityId());
        bo.setCountryCode(immutable.getCountryCode());
        if (immutable.getStartDate() != null) {
            bo.setStartDateValue(new Timestamp(immutable.getStartDate().getMillis()));
        }
        if (immutable.getEndDate() != null) {
            bo.setEndDateValue(new Timestamp(immutable.getEndDate().getMillis()));
        }
        bo.setActive(immutable.isActive());
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());
        return bo;
    }

    @Override
    public EntityCitizenshipStatusBo getStatus() {
        return this.status;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(EntityCitizenshipStatusBo status) {
        this.status = status;
    }
}
