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

package org.kuali.rice.kim.impl.identity.employment;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.kim.api.identity.email.EntityEmail;
import org.kuali.rice.kim.api.identity.email.EntityEmailHistory;
import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.kuali.rice.kim.api.identity.employment.EntityEmploymentHistory;
import org.kuali.rice.kim.api.identity.employment.EntityEmploymentHistoryContract;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationBo;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationHistoryBo;
import org.kuali.rice.kim.impl.identity.email.EntityEmailBo;
import org.kuali.rice.kim.impl.identity.email.EntityEmailTypeHistoryBo;

import javax.persistence.Column;
import java.sql.Timestamp;

public class EntityEmploymentHistoryBo extends EntityEmploymentBo implements EntityEmploymentHistoryContract {
    private static final long serialVersionUID = -8670268472560378016L;
    @Column(name ="HIST_ID")
    private Long historyId;
    @Column(name = "ACTV_FRM_DT")
    private Timestamp activeFromDateValue;
    @Column(name = "ACTV_TO_DT")
    private Timestamp activeToDateValue;
    private EntityEmploymentTypeHistoryBo employeeType;
    private EntityEmploymentStatusHistoryBo employeeStatus;
    private EntityAffiliationHistoryBo entityAffiliation;

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
    public EntityEmploymentStatusHistoryBo getEmployeeStatus() {
        return employeeStatus;
    }

    public void setEmployeeStatus(EntityEmploymentStatusHistoryBo employeeStatus) {
        this.employeeStatus = employeeStatus;
    }

    @Override
    public EntityEmploymentTypeHistoryBo getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(EntityEmploymentTypeHistoryBo employeeType) {
        this.employeeType = employeeType;
    }

    @Override
    public EntityAffiliationHistoryBo getEntityAffiliation() {
        return entityAffiliation;
    }

    public void setEntityAffiliation(EntityAffiliationHistoryBo entityAffiliation) {
        this.entityAffiliation = entityAffiliation;
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static EntityEmploymentHistory to(EntityEmploymentHistoryBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityEmploymentHistory.Builder.create(bo).build();
    }
    /**
     * Converts a main object to its historical counterpart
     * @param employment immutable object
     * @return the history bo
     */
    public static EntityEmploymentHistoryBo from(EntityEmployment employment,
            Timestamp fromDate,
            Timestamp toDate) {
        if (employment == null) {
            return null;
        }

        EntityEmploymentHistoryBo bo = (EntityEmploymentHistoryBo) EntityEmploymentBo.from(employment);
        bo.setActiveFromDateValue(fromDate == null? null :fromDate);
        bo.setActiveToDateValue(toDate == null? null :toDate);
        bo.setEmployeeStatus(EntityEmploymentStatusHistoryBo.from(employment.getEmployeeStatus(), fromDate, toDate));
        bo.setEmployeeType(EntityEmploymentTypeHistoryBo.from(employment.getEmployeeType(), fromDate, toDate));
        bo.setEntityAffiliation(EntityAffiliationHistoryBo.from(employment.getEntityAffiliation(), fromDate, toDate));
        return bo;
    }

    /**
     * Converts a main object to its historical counterpart
     * @param im immutable object
     * @return the history bo
     */
    public static EntityEmploymentHistoryBo from(EntityEmploymentHistory im) {
        if (im == null) {
            return null;
        }

        EntityEmploymentHistoryBo bo = new EntityEmploymentHistoryBo();

        bo.setActive(im.isActive());
        bo.setEntityId(im.getEntityId());
        if (im.getEmployeeType() != null) {
            bo.setEmployeeTypeCode(im.getEmployeeType().getCode());
            bo.setEmployeeType(EntityEmploymentTypeHistoryBo.from(im.getEmployeeType()));
        }

        if (im.getEmployeeStatus() != null) {
            bo.setEmployeeStatusCode(im.getEmployeeStatus().getCode());
            bo.setEmployeeStatus(EntityEmploymentStatusHistoryBo.from(im.getEmployeeStatus()));
        }

        if (im.getEntityAffiliation() != null) {
            bo.setEntityAffiliationId(im.getEntityAffiliation().getId());
            bo.setEntityAffiliation(EntityAffiliationHistoryBo.from(im.getEntityAffiliation()));
        }

        bo.setPrimaryDepartmentCode(im.getPrimaryDepartmentCode());
        bo.setEmployeeId(im.getEmployeeId());
        bo.setEmploymentRecordId(im.getEmploymentRecordId());
        bo.setBaseSalaryAmount(im.getBaseSalaryAmount());
        bo.setPrimary(im.isPrimary());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        bo.setActiveFromDateValue(im.getActiveFromDate() == null? null : new Timestamp(
                im.getActiveFromDate().getMillis()));
        bo.setActiveToDateValue(im.getActiveToDate() == null ? null : new Timestamp(
                im.getActiveToDate().getMillis()));


        return bo;
    }
}
