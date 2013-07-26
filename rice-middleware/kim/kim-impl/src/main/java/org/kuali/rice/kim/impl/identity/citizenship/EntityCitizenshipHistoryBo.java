/*
 * Copyright 2006-2013 The Kuali Foundation
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

import org.joda.time.DateTime;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenship;
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenshipHistory;
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenshipHistoryContract;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "")
public class EntityCitizenshipHistoryBo extends EntityCitizenshipBo implements EntityCitizenshipHistoryContract {
    @Column(name ="HIST_ID")
    private Long historyId;
    @Column(name = "ACTV_FRM_DT")
    private Timestamp activeFromDateValue;
    @Column(name = "ACTV_TO_DT")
    private Timestamp activeToDateValue;
    @ManyToOne(targetEntity = EntityCitizenshipStatusHistoryBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "CTZNSHP_STAT_CD", insertable = false, updatable = false)
    private EntityCitizenshipStatusHistoryBo status;

    @ManyToOne(targetEntity = EntityCitizenshipChangeTypeHistoryBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "CTZNSHP_CHNG_CD", insertable = false, updatable = false)
    private EntityCitizenshipChangeTypeHistoryBo changeType;

    @Override
    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Timestamp getActiveFromDateValue() {
        return activeFromDateValue;
    }

    public void setActiveFromDateValue(Timestamp activeFromDateValue) {
        this.activeFromDateValue = activeFromDateValue;
    }

    public Timestamp getActiveToDateValue() {
        return activeToDateValue;
    }

    public void setActiveToDateValue(Timestamp activeToDateValue) {
        this.activeToDateValue = activeToDateValue;
    }

    public boolean isActive(Timestamp activeAsOfDate) {
        return this.isActive() && InactivatableFromToUtils.isActive(getActiveFromDate(), getActiveToDate(),
                new DateTime(activeAsOfDate.getTime()));
    }

    @Override
    public EntityCitizenshipChangeTypeHistoryBo getChangeType() {
        return this.changeType;
    }

    public void setChangeType(EntityCitizenshipChangeTypeHistoryBo changeType) {
        this.changeType = changeType;
    }

    @Override
    public boolean isActive(DateTime activeAsOfDate) {
        return this.isActive() && InactivatableFromToUtils.isActive(getActiveFromDate(), getActiveToDate(), activeAsOfDate);
    }

    @Override
    public boolean isActiveNow() {
        return this.isActive() && InactivatableFromToUtils.isActive(getActiveFromDate(), getActiveToDate(), null);
    }

    @Override
    public DateTime getActiveFromDate() {
        return this.activeFromDateValue == null ? null : new DateTime(this.activeFromDateValue.getTime());
    }

    @Override
    public DateTime getActiveToDate() {
        return this.activeToDateValue == null ? null : new DateTime(this.activeToDateValue.getTime());
    }

    @Override
    public EntityCitizenshipStatusHistoryBo getStatus() {
        return this.status;
    }

    public void setStatus(EntityCitizenshipStatusHistoryBo status) {
        this.status = status;
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static EntityCitizenshipHistory to(EntityCitizenshipHistoryBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityCitizenshipHistory.Builder.create(bo).build();
    }
    /**
     * Converts a main object to its historical counterpart
     * @param ctzn immutable object
     * @return the history bo
     */
    public static EntityCitizenshipHistoryBo from(EntityCitizenship ctzn,
            Timestamp fromDate,
            Timestamp toDate) {
        if (ctzn == null) {
            return null;
        }

        EntityCitizenshipHistoryBo bo = (EntityCitizenshipHistoryBo) EntityCitizenshipBo.from(ctzn);
        bo.setChangeType(EntityCitizenshipChangeTypeHistoryBo.from(ctzn.getChangeType(), fromDate, toDate));
        bo.setStatus(EntityCitizenshipStatusHistoryBo.from(ctzn.getStatus(), fromDate, toDate));
        bo.setActiveFromDateValue(fromDate == null? null :fromDate);
        bo.setActiveToDateValue(toDate == null? null :toDate);

        return bo;
    }

    /**
     * Converts a main object to its historical counterpart
     * @param im immutable object
     * @return the history bo
     */
    public static EntityCitizenshipHistoryBo from(EntityCitizenshipHistory im) {
        if (im == null) {
            return null;
        }

        if (im == null) {
            return null;
        }

        EntityCitizenshipHistoryBo bo = new EntityCitizenshipHistoryBo();
        bo.setHistoryId(im.getHistoryId());
        if (im.getChangeType() != null) {
            bo.setChangeCode(im.getChangeType().getCode());
            bo.setChangeType(EntityCitizenshipChangeTypeHistoryBo.from(im.getChangeType()));
        }
        if (im.getStatus() != null) {
            bo.setStatusCode(im.getStatus().getCode());
            bo.setStatus(EntityCitizenshipStatusHistoryBo.from(im.getStatus()));
        }

        bo.setId(im.getId());
        bo.setEntityId(im.getEntityId());
        bo.setCountryCode(im.getCountryCode());
        if (im.getStartDate() != null) {
            bo.setStartDateValue(new Timestamp(im.getStartDate().getMillis()));
        }

        if (im.getEndDate() != null) {
            bo.setEndDateValue(new Timestamp(im.getEndDate().getMillis()));
        }
        if (im.getChangeDate() != null) {
            bo.setChangeDateValue(new Timestamp(im.getChangeDate().getMillis()));
        }

        bo.setActive(im.isActive());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        bo.setActiveFromDateValue(im.getActiveFromDate() == null? null : new Timestamp(
                im.getActiveFromDate().getMillis()));
        bo.setActiveToDateValue(im.getActiveToDate() == null ? null : new Timestamp(
                im.getActiveToDate().getMillis()));


        return bo;
    }
}
