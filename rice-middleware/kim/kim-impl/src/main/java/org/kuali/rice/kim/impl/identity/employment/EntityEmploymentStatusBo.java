package org.kuali.rice.kim.impl.identity.employment;

import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.framework.identity.employment.EntityEmploymentStatusEbo;
import org.kuali.rice.kim.impl.identity.CodedAttributeBo;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@AttributeOverrides({
        @AttributeOverride(name="code",column=@Column(name="EMP_STAT_CD"))
})
@Table(name = "KRIM_EMP_STAT_T")
public class EntityEmploymentStatusBo extends CodedAttributeBo implements EntityEmploymentStatusEbo {
    private static final long serialVersionUID = 103798378630101884L;

    public static EntityEmploymentStatusBo from(CodedAttribute immutable) {
        return CodedAttributeBo.from(EntityEmploymentStatusBo.class, immutable);
    }
}
