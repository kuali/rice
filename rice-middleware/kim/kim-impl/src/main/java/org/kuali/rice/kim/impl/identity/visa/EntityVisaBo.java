package org.kuali.rice.kim.impl.identity.visa;

import org.eclipse.persistence.annotations.Customizer;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.visa.EntityVisa;
import org.kuali.rice.kim.api.identity.visa.EntityVisaContract;
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneTypeBo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.provider.jpa.eclipselink.EclipseLinkSequenceCustomizer;

import org.kuali.rice.krad.data.platform.generator.Sequence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Customizer(EclipseLinkSequenceCustomizer.class)
@Sequence(name="KRIM_ENTITY_VISA_ID_S", property = "id")
@Table(name = "KRIM_ENTITY_VISA_T")
public class EntityVisaBo extends PersistableBusinessObjectBase implements EntityVisaContract {
    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "VISA_TYP_CD")
    private String visaTypeCode;
    @Column(name = "ENTITY_ID")
    private String entityId;
    @Column(name = "VISA_TYPE_KEY")
    private String visaTypeKey;
    @Column(name = "VISA_ENTRY")
    private String visaEntry;
    @Column(name = "VISA_ID")
    private String visaId;
    @ManyToOne(targetEntity = EntityPhoneTypeBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(
            name = "VISA_TYP_CD", insertable = false, updatable = false)
    private CodedAttribute visaType;

    @Override
    public CodedAttribute getVisaType() {
        return this.visaType;
    }

    public static EntityVisa to(EntityVisaBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityVisa.Builder.create(bo).build();
    }

    /**
     * Creates a EntityVisaBo business object from an immutable representation of a EntityVisa.
     *
     * @param immutable an immutable EntityVisa
     * @return a EntityVisaBo
     */
    public static EntityVisaBo from(EntityVisa immutable) {
        if (immutable == null) {
            return null;
        }

        EntityVisaBo bo = new EntityVisaBo();
        bo.id = immutable.getId();
        bo.entityId = immutable.getEntityId();
        bo.visaTypeKey = immutable.getVisaTypeKey();
        bo.visaEntry = immutable.getVisaEntry();
        bo.visaId = immutable.getVisaId();
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());

        return bo;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVisaTypeCode() {
        return visaTypeCode;
    }

    public void setVisaTypeCode(String visaTypeCode) {
        this.visaTypeCode = visaTypeCode;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public String getVisaTypeKey() {
        return visaTypeKey;
    }

    public void setVisaTypeKey(String visaTypeKey) {
        this.visaTypeKey = visaTypeKey;
    }

    @Override
    public String getVisaEntry() {
        return visaEntry;
    }

    public void setVisaEntry(String visaEntry) {
        this.visaEntry = visaEntry;
    }

    @Override
    public String getVisaId() {
        return visaId;
    }

    public void setVisaId(String visaId) {
        this.visaId = visaId;
    }

    public void setVisaType(CodedAttribute visaType) {
        this.visaType = visaType;
    }
}
