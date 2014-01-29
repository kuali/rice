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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationBo;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

@Entity
@Table(name = "KRIM_ENTITY_EMP_INFO_T")
public class EntityEmploymentBo extends EntityEmploymentBase {

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_ENTITY_EMP_ID_S")
    @GeneratedValue(generator = "KRIM_ENTITY_EMP_ID_S")
    @Id
    @Column(name = "ENTITY_EMP_ID")
    private String id;

    @ManyToOne(targetEntity = EntityEmploymentTypeBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "EMP_TYP_CD", referencedColumnName = "EMP_TYP_CD", insertable = false, updatable = false)
    private EntityEmploymentTypeBo employeeType;

    @ManyToOne(targetEntity = EntityEmploymentStatusBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "EMP_STAT_CD", referencedColumnName = "EMP_STAT_CD", insertable = false, updatable = false)
    private EntityEmploymentStatusBo employeeStatus;

    @ManyToOne(targetEntity = EntityAffiliationBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "ENTITY_AFLTN_ID", referencedColumnName = "ENTITY_AFLTN_ID", insertable = false, updatable = false)
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
        bo.setActive(immutable.isActive());
        bo.setEntityId(immutable.getEntityId());
        if (immutable.getEmployeeType() != null) {
            bo.setEmployeeTypeCode(immutable.getEmployeeType().getCode());
            bo.setEmployeeType(EntityEmploymentTypeBo.from(immutable.getEmployeeType()));
        }
        if (immutable.getEmployeeStatus() != null) {
            bo.setEmployeeStatusCode(immutable.getEmployeeStatus().getCode());
            bo.setEmployeeStatus(EntityEmploymentStatusBo.from(immutable.getEmployeeStatus()));
        }
        if (immutable.getEntityAffiliation() != null) {
            bo.setEntityAffiliationId(immutable.getEntityAffiliation().getId());
            bo.setEntityAffiliation(EntityAffiliationBo.from(immutable.getEntityAffiliation()));
        }
        bo.setPrimaryDepartmentCode(immutable.getPrimaryDepartmentCode());
        bo.setEmployeeId(immutable.getEmployeeId());
        bo.setEmploymentRecordId(immutable.getEmploymentRecordId());
        bo.setBaseSalaryAmount(immutable.getBaseSalaryAmount());
        bo.setPrimary(immutable.isPrimary());
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
