package org.kuali.rice.kim.impl.identity.affiliation;

import org.eclipse.persistence.annotations.Convert;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationType;
import org.kuali.rice.kim.framework.identity.affiliation.EntityAffiliationTypeEbo;
import org.kuali.rice.kim.impl.identity.CodedAttributeBo;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@AttributeOverrides({
        @AttributeOverride(name="code",column=@Column(name="EMP_TYP_CD"))
})
@Table(name = "KRIM_AFLTN_TYP_T")
public class EntityAffiliationTypeBo extends CodedAttributeBo implements EntityAffiliationTypeEbo {
    private static final long serialVersionUID = 4973602240626940004L;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "EMP_AFLTN_TYP_IND")
    private boolean employmentAffiliationType;

    public static EntityAffiliationTypeBo from(EntityAffiliationType immutable) {
        EntityAffiliationTypeBo bo = CodedAttributeBo.from(EntityAffiliationTypeBo.class, CodedAttribute.Builder.create(immutable).build());
        bo.setEmploymentAffiliationType(immutable.isEmploymentAffiliationType());
        return bo;
    }

    public static EntityAffiliationType to(EntityAffiliationTypeBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityAffiliationType.Builder.create(bo).build();
    }

    @Override
    public boolean isEmploymentAffiliationType() {
        return employmentAffiliationType;
    }

    public void setEmploymentAffiliationType(boolean employmentAffiliationType) {
        this.employmentAffiliationType = employmentAffiliationType;
    }

}
