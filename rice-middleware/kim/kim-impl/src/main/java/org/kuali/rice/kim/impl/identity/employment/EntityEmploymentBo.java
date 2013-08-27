package org.kuali.rice.kim.impl.identity.employment;

import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationBo;
import org.kuali.rice.krad.data.jpa.eclipselink.PortableSequenceGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "KRIM_ENTITY_EMP_INFO_T")
public class EntityEmploymentBo extends EntityEmploymentBase {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "KRIM_ENTITY_EMP_ID_S")
    @PortableSequenceGenerator(name = "KRIM_ENTITY_EMP_ID_S")
    @Column(name = "ENTITY_EMP_ID")
    private String id;

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
