package org.kuali.rice.kim.impl.identity.name;

import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.framework.identity.name.EntityNameTypeEbo;
import org.kuali.rice.kim.impl.identity.CodedAttributeBo;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@AttributeOverrides({
        @AttributeOverride(name="code",column=@Column(name="ENT_NM_TYP_CD"))
})
@Table(name = "KRIM_ENT_NM_TYP_T")
public class EntityNameTypeBo extends CodedAttributeBo implements EntityNameTypeEbo {
    private static final long serialVersionUID = -3667881155839568604L;

    public static EntityNameTypeBo from(CodedAttribute immutable) {
        return CodedAttributeBo.from(EntityNameTypeBo.class, immutable);
    }
}
