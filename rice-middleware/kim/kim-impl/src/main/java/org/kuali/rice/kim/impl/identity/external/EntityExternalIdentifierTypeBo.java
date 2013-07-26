package org.kuali.rice.kim.impl.identity.external;

import org.eclipse.persistence.annotations.Convert;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierType;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierTypeContract;
import org.kuali.rice.kim.framework.identity.external.EntityExternalIdentifierTypeEbo;
import org.kuali.rice.kim.impl.identity.CodedAttributeBo;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@AttributeOverrides({
        @AttributeOverride(name="code",column=@Column(name="EXT_ID_TYP_CD"))
})
@Table(name = "KRIM_EXT_ID_TYP_T")
public class EntityExternalIdentifierTypeBo extends CodedAttributeBo implements EntityExternalIdentifierTypeEbo, EntityExternalIdentifierTypeContract {
    private static final long serialVersionUID = 1058518958597912170L;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "ENCR_REQ_IND")
    private boolean encryptionRequired;

    @Override
    public boolean isEncryptionRequired() {
        return encryptionRequired;
    }

    public void setEncryptionRequired(boolean encryptionRequired) {
        this.encryptionRequired = encryptionRequired;
    }

    public static EntityExternalIdentifierTypeBo from(EntityExternalIdentifierType immutable) {
        EntityExternalIdentifierTypeBo bo = CodedAttributeBo.from(EntityExternalIdentifierTypeBo.class, CodedAttribute.Builder.create(immutable).build());
        bo.setEncryptionRequired(immutable.isEncryptionRequired());
        return bo;
    }

    public static EntityExternalIdentifierType to(EntityExternalIdentifierTypeBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityExternalIdentifierType.Builder.create(bo).build();
    }



}
