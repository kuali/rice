package org.kuali.rice.kim.impl.identity.employment;

import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.framework.identity.employment.EntityEmploymentTypeEbo;
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
@Table(name = "KRIM_EMP_TYP_T")
public class EntityEmploymentTypeBo extends CodedAttributeBo implements EntityEmploymentTypeEbo {
    private static final long serialVersionUID = -5365214631480350557L;

    public static EntityEmploymentTypeBo from(CodedAttribute immutable) {
        return CodedAttributeBo.from(EntityEmploymentTypeBo.class, immutable);
    }
}
