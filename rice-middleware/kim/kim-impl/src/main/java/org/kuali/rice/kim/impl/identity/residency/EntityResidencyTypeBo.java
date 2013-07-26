package org.kuali.rice.kim.impl.identity.residency;

import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.framework.identity.phone.EntityPhoneTypeEbo;
import org.kuali.rice.kim.impl.identity.CodedAttributeBo;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@AttributeOverrides({
        @AttributeOverride(name="code",column=@Column(name="RES_TYP_CD"))
})
@Table(name = "")
public class EntityResidencyTypeBo extends CodedAttributeBo {
    private static final long serialVersionUID = -7999904356580992741L;

    public static EntityResidencyTypeBo from(CodedAttribute immutable) {
        return CodedAttributeBo.from(EntityResidencyTypeBo.class, immutable);
    }
}
