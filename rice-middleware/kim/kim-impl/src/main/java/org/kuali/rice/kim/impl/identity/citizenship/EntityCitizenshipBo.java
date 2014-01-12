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

import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenship;
import org.kuali.rice.krad.data.jpa.eclipselink.PortableSequenceGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "KRIM_ENTITY_CTZNSHP_T")
public class EntityCitizenshipBo extends EntityCitizenshipBase {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "KRIM_ENTITY_CTZNSHP_ID_S")
    @PortableSequenceGenerator(name = "KRIM_ENTITY_CTZNSHP_ID_S")
    @Column(name = "ENTITY_CTZNSHP_ID")
    private String id;

    @ManyToOne(targetEntity = EntityCitizenshipStatusBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(
            name = "CTZNSHP_STAT_CD", insertable = false, updatable = false)
    private EntityCitizenshipStatusBo status;

    @ManyToOne(targetEntity = EntityCitizenshipChangeTypeBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(
            name = "CTZNSHP_CHNG_CD", insertable = false, updatable = false)
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

        if (immutable.getChangeType() != null) {
            bo.setChangeCode(immutable.getChangeType().getCode());
            bo.setChangeType(EntityCitizenshipChangeTypeBo.from(immutable.getChangeType()));
        }
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
        if (immutable.getChangeDate() != null) {
            bo.setChangeDateValue(new Timestamp(immutable.getEndDate().getMillis()));
        }
        if (immutable.getChangeDate() != null) {
            bo.setChangeDateValue(new Timestamp(immutable.getChangeDate().getMillis()));
        }

        bo.setActive(immutable.isActive());
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());

        return bo;
    }

    @Override
    public EntityCitizenshipChangeTypeBo getChangeType() {
        return this.changeType;
    }

    public void setChangeType(EntityCitizenshipChangeTypeBo changeType) {
        this.changeType = changeType;
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
