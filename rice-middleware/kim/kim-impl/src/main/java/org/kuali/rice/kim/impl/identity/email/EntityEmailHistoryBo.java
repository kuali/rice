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
package org.kuali.rice.kim.impl.identity.email;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.kim.api.identity.email.EntityEmail;
import org.kuali.rice.kim.api.identity.email.EntityEmailHistory;
import org.kuali.rice.kim.api.identity.email.EntityEmailHistoryContract;

import javax.persistence.Column;
import java.sql.Timestamp;

public class EntityEmailHistoryBo extends EntityEmailBo implements EntityEmailHistoryContract {
    private static final long serialVersionUID = -8670268472560378016L;
    @Column(name ="HIST_ID")
    private Long historyId;
    @Column(name = "ACTV_FRM_DT")
    private Timestamp activeFromDateValue;
    @Column(name = "ACTV_TO_DT")
    private Timestamp activeToDateValue;
    private EntityEmailTypeHistoryBo emailType;

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
        return this.isActive() && InactivatableFromToUtils.isActive(getActiveFromDate(), getActiveToDate(), new DateTime(
                activeAsOfDate.getTime()));
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
    public EntityEmailTypeHistoryBo getEmailType() {
        return emailType;
    }

    public void setEmailType(EntityEmailTypeHistoryBo emailType) {
        this.emailType = emailType;
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static EntityEmailHistory to(EntityEmailHistoryBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityEmailHistory.Builder.create(bo).build();
    }
    /**
     * Converts a main object to its historical counterpart
     * @param email immutable object
     * @return the history bo
     */
    public static EntityEmailHistoryBo from(EntityEmail email,
            Timestamp fromDate,
            Timestamp toDate) {
        if (email == null) {
            return null;
        }

        EntityEmailHistoryBo bo = (EntityEmailHistoryBo) EntityEmailBo.from(email);
        bo.setEmailType(EntityEmailTypeHistoryBo.from(email.getEmailType(), fromDate, toDate));
        bo.setActiveFromDateValue(fromDate == null? null :fromDate);
        bo.setActiveToDateValue(toDate == null? null :toDate);

        return bo;
    }

    /**
     * Converts a main object to its historical counterpart
     * @param im immutable object
     * @return the history bo
     */
    public static EntityEmailHistoryBo from(EntityEmailHistory im) {
        if (im == null) {
            return null;
        }

        EntityEmailHistoryBo bo = new EntityEmailHistoryBo();

        bo.setActive(im.isActive());
        bo.setEntityTypeCode(im.getEntityTypeCode());
        if (im.getEmailType() != null) {
            bo.setEmailTypeCode(im.getEmailType().getCode());
        }

        bo.setId(im.getId());
        bo.setActive(im.isActive());

        bo.setEntityId(im.getEntityId());
        bo.setEntityTypeCode(im.getEntityTypeCode());
        if (im.getEmailType() != null) {
            bo.setEmailTypeCode(im.getEmailType().getCode());
            bo.setEmailType(EntityEmailTypeHistoryBo.from(im.getEmailType()));
        }

        bo.setEmailAddress(im.getEmailAddressUnmasked());

        bo.setDefaultValue(im.isDefaultValue());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        bo.setActiveFromDateValue(im.getActiveFromDate() == null? null : new Timestamp(
                im.getActiveFromDate().getMillis()));
        bo.setActiveToDateValue(im.getActiveToDate() == null ? null : new Timestamp(
                im.getActiveToDate().getMillis()));


        return bo;
    }
}
