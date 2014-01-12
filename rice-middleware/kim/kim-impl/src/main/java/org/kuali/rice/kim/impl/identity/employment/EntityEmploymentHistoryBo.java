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
package org.kuali.rice.kim.impl.identity.employment;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.kim.api.identity.employment.EntityEmploymentHistory;
import org.kuali.rice.kim.api.identity.employment.EntityEmploymentHistoryContract;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationHistoryBo;
import org.kuali.rice.krad.data.jpa.eclipselink.PortableSequenceGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Timestamp;

@Entity
@Table(name = "KRIM_HIST_ENTITY_EMP_INFO_T")
public class EntityEmploymentHistoryBo extends EntityEmploymentBase implements EntityEmploymentHistoryContract {
    private static final long serialVersionUID = -8670268472560378016L;
    @Id
    @GeneratedValue(generator = "KRIM_HIST_ENTITY_EMP_ID_S")
    @PortableSequenceGenerator(name = "KRIM_HIST_ENTITY_EMP_ID_S")
    @Column(name ="HIST_ID")
    private Long historyId;

    @Column(name = "ENTITY_EMP_ID")
    private String id;

    @Column(name = "ACTV_FRM_DT")
    private Timestamp activeFromDateValue;

    @Column(name = "ACTV_TO_DT")
    private Timestamp activeToDateValue;

    @Transient
    private EntityEmploymentTypeHistoryBo employeeType;

    @Transient
    private EntityEmploymentStatusHistoryBo employeeStatus;

    @Transient
    private EntityAffiliationHistoryBo entityAffiliation;

    @Override
    public Long getHistoryId() {
        return historyId;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    /*public static EntityEmploymentHistoryBo from(EntityEmployment employment,
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
    }*/

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
