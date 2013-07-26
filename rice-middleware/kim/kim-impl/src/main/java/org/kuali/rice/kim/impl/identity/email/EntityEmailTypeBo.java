package org.kuali.rice.kim.impl.identity.email;

import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.framework.identity.email.EntityEmailTypeEbo;
import org.kuali.rice.kim.impl.identity.CodedAttributeBo;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@AttributeOverrides({
        @AttributeOverride(name="code",column=@Column(name="EMAIL_TYP_CD"))
})
@Table(name = "KRIM_EMAIL_TYP_T")
public class EntityEmailTypeBo extends CodedAttributeBo implements EntityEmailTypeEbo {
    private static final long serialVersionUID = -3026253282051151336L;

    public static EntityEmailTypeBo from(CodedAttribute immutable) {
        return CodedAttributeBo.from(EntityEmailTypeBo.class, immutable);
    }
}
