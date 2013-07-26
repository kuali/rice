package org.kuali.rice.kim.impl.identity.phone;

import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.framework.identity.phone.EntityPhoneTypeEbo;
import org.kuali.rice.kim.impl.identity.CodedAttributeBo;
import org.kuali.rice.kim.impl.identity.name.EntityNameTypeBo;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@AttributeOverrides({
        @AttributeOverride(name="code",column=@Column(name="PHONE_TYP_CD")),
        @AttributeOverride(name="name",column=@Column(name="PHONE_TYP_NM"))
})
@Table(name = "KRIM_PHONE_TYP_T")
public class EntityPhoneTypeBo extends CodedAttributeBo implements EntityPhoneTypeEbo {
    private static final long serialVersionUID = -7999904356580992741L;

    public static EntityPhoneTypeBo from(CodedAttribute immutable) {
        return CodedAttributeBo.from(EntityPhoneTypeBo.class, immutable);
    }
}
