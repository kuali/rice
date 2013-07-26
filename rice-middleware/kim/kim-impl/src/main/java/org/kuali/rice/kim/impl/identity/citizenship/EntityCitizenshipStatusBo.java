package org.kuali.rice.kim.impl.identity.citizenship;

import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.framework.identity.citizenship.EntityCitizenshipStatusEbo;
import org.kuali.rice.kim.impl.identity.CodedAttributeBo;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@AttributeOverrides({
        @AttributeOverride(name="code",column=@Column(name="CTZNSHP_STAT_CD"))
})
@Table(name = "KRIM_CTZNSHP_STAT_T")
public class EntityCitizenshipStatusBo extends CodedAttributeBo implements EntityCitizenshipStatusEbo {
    private static final long serialVersionUID = 8402224265524503990L;

    public static EntityCitizenshipStatusBo from(CodedAttribute immutable) {
        return CodedAttributeBo.from(EntityCitizenshipStatusBo.class, immutable);
    }

}
