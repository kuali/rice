package org.kuali.rice.kim.impl.identity.address;

import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.framework.identity.address.EntityAddressTypeEbo;
import org.kuali.rice.kim.impl.identity.CodedAttributeBo;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@AttributeOverrides({
        @AttributeOverride(name="code",column=@Column(name="ADDR_TYP_CD"))
})
@Table(name = "KRIM_ADDR_TYP_T")
public class EntityAddressTypeBo extends CodedAttributeBo implements EntityAddressTypeEbo {

    private static final long serialVersionUID = 4854680896968903593L;

    public static EntityAddressTypeBo from(CodedAttribute immutable) {
        return CodedAttributeBo.from(EntityAddressTypeBo.class, immutable);
    }
}
