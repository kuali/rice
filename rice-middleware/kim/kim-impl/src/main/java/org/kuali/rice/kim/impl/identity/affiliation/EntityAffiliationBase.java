package org.kuali.rice.kim.impl.identity.affiliation;

import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class EntityAffiliationBase extends PersistableBusinessObjectBase implements EntityAffiliationContract {
    private static final long serialVersionUID = 0L;
    @Column(name = "ENTITY_ID")
    private String entityId;
    @Column(name = "AFLTN_TYP_CD")
    private String affiliationTypeCode;
    @Column(name = "CAMPUS_CD")
    private String campusCode;
    //@ManyToOne(targetEntity = EntityAffiliationTypeBo.class, fetch = FetchType.EAGER, cascade = {})
    //@JoinColumn(
    //        name = "AFLTN_TYP_CD", insertable = false, updatable = false)
    //private EntityAffiliationTypeBo affiliationType;
    @javax.persistence.Convert(converter=BooleanYNConverter.class)
    @Column(name = "DFLT_IND")
    private boolean defaultValue;
    @javax.persistence.Convert(converter=BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active;

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getAffiliationTypeCode() {
        return affiliationTypeCode;
    }

    public void setAffiliationTypeCode(String affiliationTypeCode) {
        this.affiliationTypeCode = affiliationTypeCode;
    }

    @Override
    public String getCampusCode() {
        return campusCode;
    }

    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }


    public boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean getActive() {
        return active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


}
