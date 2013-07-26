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

package org.kuali.rice.kim.impl.identity.affiliation;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationHistory;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationHistoryContract;
import org.kuali.rice.kim.impl.identity.CodedAttributeHistoryBoContract;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

public class EntityAffiliationHistoryBo extends EntityAffiliationBo implements EntityAffiliationHistoryContract {
    private static final long serialVersionUID = 6295317931353366718L;
    @Column(name ="HIST_ID")
    private Long historyId;
    @Column(name = "ACTV_FRM_DT")
    private Timestamp activeFromDateValue;
    @Column(name = "ACTV_TO_DT")
    private Timestamp activeToDateValue;
    @ManyToOne(targetEntity = EntityAffiliationTypeHistoryBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "AFFLN_TYP_CD", insertable = false, updatable = false)
    private EntityAffiliationTypeHistoryBo affiliationType;

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
        return isActive(new DateTime(activeAsOfDate.getTime()));
    }

    @Override
    public boolean isActive(DateTime activeAsOfDate) {
        return this.isActive() && InactivatableFromToUtils.isActive(getActiveFromDate(), getActiveToDate(), activeAsOfDate);
    }

    @Override
    public boolean isActiveNow() {
        return isActive(new DateTime());
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
    public EntityAffiliationTypeHistoryBo getAffiliationType() {
        return affiliationType;
    }

    public void setAffiliationType(EntityAffiliationTypeHistoryBo affiliationType) {
        this.affiliationType = affiliationType;
    }

    /**
     * Converts a mutable EntityAffiliationHistoryBo to an immutable AddressTypeHistory representation.
     *
     * @param bo
     * @return an immutable AddressTypeHistory
     */
    public static EntityAffiliationHistory to(EntityAffiliationHistoryBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityAffiliationHistory.Builder.create(bo).build();
    }

    /**
     * Creates a AddressTypeHistory business object from an immutable representation of a AddressTypeHistory.
     *
     * @param immutable an immutable AddressType
     * @return a AddressTypeBo
     */
    public static EntityAffiliationHistoryBo from(EntityAffiliationHistory immutable) {
        if (immutable == null) {
            return null;
        }

        EntityAffiliationHistoryBo bo = new EntityAffiliationHistoryBo();
        bo.setActive(immutable.isActive());
        if (immutable.getAffiliationType() != null) {
            bo.setAffiliationTypeCode(immutable.getAffiliationType().getCode());
        }
        bo.setAffiliationType(EntityAffiliationTypeHistoryBo.from(immutable.getAffiliationType()));
        bo.setId(immutable.getId());
        bo.setCampusCode(immutable.getCampusCode());
        bo.setEntityId(immutable.getEntityId());
        bo.setActive(immutable.isActive());
        bo.setDefaultValue(immutable.isDefaultValue());
        bo.setVersionNumber(immutable.getVersionNumber());

        bo.setHistoryId(immutable.getHistoryId());
        bo.setActiveFromDateValue(immutable.getActiveFromDate() == null? null : new Timestamp(
                immutable.getActiveFromDate().getMillis()));
        bo.setActiveToDateValue(immutable.getActiveToDate() == null ? null : new Timestamp(
                immutable.getActiveToDate().getMillis()));

        return bo;
    }

    /**
     * Creates a AddressType business object from an immutable representation of a AddressType.
     *
     * @param im an immutable AddressType
     * @return a AddressTypeBo
     */
    public static EntityAffiliationHistoryBo from(EntityAffiliation im,
            Timestamp fromDate,
            Timestamp toDate) {
        if (im == null) {
            return null;
        }

        EntityAffiliationHistoryBo bo = (EntityAffiliationHistoryBo) EntityAffiliationBo.from(im);
        bo.setAffiliationType(EntityAffiliationTypeHistoryBo.from(im.getAffiliationType(), fromDate, toDate));
        bo.setActiveFromDateValue(fromDate == null ? null : new Timestamp(fromDate.getTime()));
        bo.setActiveToDateValue(toDate == null ? null : new Timestamp(toDate.getTime()));

        return bo;
    }
}
