package org.kuali.rice.kim.impl.identity.privacy;

import org.eclipse.persistence.annotations.Convert;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferencesContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "KRIM_ENTITY_PRIV_PREF_T")
public class EntityPrivacyPreferencesBo extends PersistableBusinessObjectBase implements EntityPrivacyPreferencesContract {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ENTITY_ID")
    private String entityId;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "SUPPRESS_NM_IND")
    private boolean suppressName;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "SUPPRESS_EMAIL_IND")
    private boolean suppressEmail;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "SUPPRESS_ADDR_IND")
    private boolean suppressAddress;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "SUPPRESS_PHONE_IND")
    private boolean suppressPhone;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "SUPPRESS_PRSNL_IND")
    private boolean suppressPersonal;

    public static EntityPrivacyPreferences to(EntityPrivacyPreferencesBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityPrivacyPreferences.Builder.create(bo).build();
    }

    /**
     * Creates a CountryBo business object from an immutable representation of a Country.
     *
     * @param immutable an immutable Country
     * @return a CountryBo
     */
    public static EntityPrivacyPreferencesBo from(EntityPrivacyPreferences immutable) {
        if (immutable == null) {
            return null;
        }

        EntityPrivacyPreferencesBo bo = new EntityPrivacyPreferencesBo();

        bo.entityId = immutable.getEntityId();
        bo.suppressAddress = immutable.isSuppressAddress();
        bo.suppressEmail = immutable.isSuppressEmail();
        bo.suppressName = immutable.isSuppressName();
        bo.suppressPersonal = immutable.isSuppressPersonal();
        bo.suppressPhone = immutable.isSuppressPhone();
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());

        return bo;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public boolean getSuppressName() {
        return suppressName;
    }

    @Override
    public boolean isSuppressName() {
        return suppressName;
    }

    public void setSuppressName(boolean suppressName) {
        this.suppressName = suppressName;
    }

    public boolean getSuppressEmail() {
        return suppressEmail;
    }

    @Override
    public boolean isSuppressEmail() {
        return suppressEmail;
    }

    public void setSuppressEmail(boolean suppressEmail) {
        this.suppressEmail = suppressEmail;
    }

    public boolean getSuppressAddress() {
        return suppressAddress;
    }

    @Override
    public boolean isSuppressAddress() {
        return suppressAddress;
    }

    public void setSuppressAddress(boolean suppressAddress) {
        this.suppressAddress = suppressAddress;
    }

    public boolean getSuppressPhone() {
        return suppressPhone;
    }

    @Override
    public boolean isSuppressPhone() {
        return suppressPhone;
    }

    public void setSuppressPhone(boolean suppressPhone) {
        this.suppressPhone = suppressPhone;
    }

    public boolean getSuppressPersonal() {
        return suppressPersonal;
    }

    @Override
    public boolean isSuppressPersonal() {
        return suppressPersonal;
    }

    public void setSuppressPersonal(boolean suppressPersonal) {
        this.suppressPersonal = suppressPersonal;
    }

}
