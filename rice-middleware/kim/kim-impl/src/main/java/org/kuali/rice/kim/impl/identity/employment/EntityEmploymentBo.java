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
package org.kuali.rice.kim.impl.identity.employment;

import org.eclipse.persistence.annotations.Convert;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.kuali.rice.kim.api.identity.employment.EntityEmploymentContract;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationBo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class EntityEmploymentBo extends PersistableBusinessObjectBase implements EntityEmploymentContract {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ENTITY_EMP_ID")
    private String id;
    @Column(name = "ENTITY_ID")
    private String entityId;
    @Column(name = "EMP_ID")
    private String employeeId;
    @Column(name = "EMP_REC_ID")
    private String employmentRecordId;
    @Column(name = "ENTITY_AFLTN_ID")
    private String entityAffiliationId;
    @Column(name = "EMP_STAT_CD")
    private String employeeStatusCode;
    @Column(name = "EMP_TYP_CD")
    private String employeeTypeCode;
    @Column(name = "PRMRY_DEPT_CD")
    private String primaryDepartmentCode;
    @Convert("kualiDecimalConverter")
    @Column(name = "BASE_SLRY_AMT")
    private KualiDecimal baseSalaryAmount;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "PRMRY_IND")
    private boolean primary;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "TNR_IND")
    private boolean tenured;
    @ManyToOne(targetEntity = EntityEmploymentTypeBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(
            name = "EMP_TYP_CD", insertable = false, updatable = false)
    private EntityEmploymentTypeBo employeeType;
    @ManyToOne(targetEntity = EntityEmploymentStatusBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(
            name = "EMP_STAT_CD", insertable = false, updatable = false)
    private EntityEmploymentStatusBo employeeStatus;
    @ManyToOne(targetEntity = EntityAffiliationBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(
            name = "ENTITY_AFLTN_ID", insertable = false, updatable = false)
    private EntityAffiliationBo entityAffiliation;

    @Override
    public EntityAffiliationBo getEntityAffiliation() {
        return this.entityAffiliation;
    }

    @Override
    public EntityEmploymentStatusBo getEmployeeStatus() {
        return this.employeeStatus;
    }

    @Override
    public EntityEmploymentTypeBo getEmployeeType() {
        return this.employeeType;
    }

    public boolean isTenured() {
        return tenured;
    }

    public void setTenured(boolean tenured) {
        this.tenured = tenured;
    }

    public static EntityEmployment to(EntityEmploymentBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityEmployment.Builder.create(bo).build();
    }

    /**
     * Creates a EntityEmploymentBo business object from an immutable representation of a EntityEmployment.
     *
     * @param immutable an immutable EntityEmployment
     * @return a EntityEmploymentBo
     */
    public static EntityEmploymentBo from(EntityEmployment immutable) {
        if (immutable == null) {
            return null;
        }

        EntityEmploymentBo bo = new EntityEmploymentBo();
        bo.id = immutable.getId();
        bo.active = immutable.isActive();

        bo.entityId = immutable.getEntityId();
        if (immutable.getEmployeeType() != null) {
            bo.employeeTypeCode = immutable.getEmployeeType().getCode();
            bo.employeeType = EntityEmploymentTypeBo.from(immutable.getEmployeeType());
        }

        if (immutable.getEmployeeStatus() != null) {
            bo.employeeStatusCode = immutable.getEmployeeStatus().getCode();
            bo.employeeStatus = EntityEmploymentStatusBo.from(immutable.getEmployeeStatus());
        }

        if (immutable.getEntityAffiliation() != null) {
            bo.entityAffiliationId = immutable.getEntityAffiliation().getId();
            bo.entityAffiliation = EntityAffiliationBo.from(immutable.getEntityAffiliation());
        }

        bo.primaryDepartmentCode = immutable.getPrimaryDepartmentCode();
        bo.employeeId = immutable.getEmployeeId();
        bo.employmentRecordId = immutable.getEmploymentRecordId();
        bo.baseSalaryAmount = immutable.getBaseSalaryAmount();
        bo.primary = immutable.isPrimary();
        bo.setTenured(immutable.isTenured());
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());

        return bo;
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

    @Override
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String getEmploymentRecordId() {
        return employmentRecordId;
    }

    public void setEmploymentRecordId(String employmentRecordId) {
        this.employmentRecordId = employmentRecordId;
    }

    public String getEntityAffiliationId() {
        return entityAffiliationId;
    }

    public void setEntityAffiliationId(String entityAffiliationId) {
        this.entityAffiliationId = entityAffiliationId;
    }

    public String getEmployeeStatusCode() {
        return employeeStatusCode;
    }

    public void setEmployeeStatusCode(String employeeStatusCode) {
        this.employeeStatusCode = employeeStatusCode;
    }

    public String getEmployeeTypeCode() {
        return employeeTypeCode;
    }

    public void setEmployeeTypeCode(String employeeTypeCode) {
        this.employeeTypeCode = employeeTypeCode;
    }

    @Override
    public String getPrimaryDepartmentCode() {
        return primaryDepartmentCode;
    }

    public void setPrimaryDepartmentCode(String primaryDepartmentCode) {
        this.primaryDepartmentCode = primaryDepartmentCode;
    }

    @Override
    public KualiDecimal getBaseSalaryAmount() {
        return baseSalaryAmount;
    }

    public void setBaseSalaryAmount(KualiDecimal baseSalaryAmount) {
        this.baseSalaryAmount = baseSalaryAmount;
    }

    public boolean getPrimary() {
        return primary;
    }

    @Override
    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
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

    public void setEmployeeType(EntityEmploymentTypeBo employeeType) {
        this.employeeType = employeeType;
    }

    public void setEmployeeStatus(EntityEmploymentStatusBo employeeStatus) {
        this.employeeStatus = employeeStatus;
    }

    public void setEntityAffiliation(EntityAffiliationBo entityAffiliation) {
        this.entityAffiliation = entityAffiliation;
    }

}
