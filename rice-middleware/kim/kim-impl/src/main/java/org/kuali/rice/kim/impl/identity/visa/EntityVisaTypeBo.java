package org.kuali.rice.kim.impl.identity.visa;

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
public class EntityVisaTypeBo extends CodedAttributeBo {
    private static final long serialVersionUID = -7999904356580992741L;

    public static EntityVisaTypeBo from(CodedAttribute immutable) {
        return CodedAttributeBo.from(EntityVisaTypeBo.class, immutable);
    }
}
