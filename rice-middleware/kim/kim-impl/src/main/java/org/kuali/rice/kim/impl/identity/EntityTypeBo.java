package org.kuali.rice.kim.impl.identity;

import org.eclipse.persistence.annotations.Convert;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.framework.identity.EntityTypeEbo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "KRIM_ADDR_TYP_T")
public class EntityTypeBo extends PersistableBusinessObjectBase implements EntityTypeEbo {
    @Id
    @Column(name = "ADDR_TYP_CD")
    private String code;

    @Column(name = "NM")
    private String name;

    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active;

    @Column(name = "DISPLAY_SORT_CD")
    private String sortCode;

    /**
     * Converts a mutable AddressTypeBo to an immutable AddressType representation.
     *
     * @param bo
     * @return an immutable AddressType
     */
    public static CodedAttribute to(EntityTypeBo bo) {
        if (bo == null) {
            return null;
        }

        return CodedAttribute.Builder.create(bo).build();
    }

    /**
     * Creates a AddressType business object from an immutable representation of a AddressType.
     *
     * @param an immutable AddressType
     * @return a AddressTypeBo
     */
    public static EntityTypeBo from(CodedAttribute immutable) {
        if (immutable == null) {
            return null;
        }

        EntityTypeBo bo = new EntityTypeBo();
        bo.code = immutable.getCode();
        bo.name = immutable.getName();
        bo.sortCode = immutable.getSortCode();
        bo.active = immutable.isActive();
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());

        return bo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getActive() {
        return active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }


}
