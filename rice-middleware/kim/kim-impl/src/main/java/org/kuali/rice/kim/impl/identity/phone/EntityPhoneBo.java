package org.kuali.rice.kim.impl.identity.phone;

import org.apache.commons.lang.StringUtils;
import org.eclipse.persistence.annotations.Convert;
import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.api.identity.phone.EntityPhone;
import org.kuali.rice.kim.api.identity.phone.EntityPhoneContract;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "KRIM_ENTITY_PHONE_T")
public class EntityPhoneBo extends PersistableBusinessObjectBase implements EntityPhoneContract {
    @Id
    @Column(name = "ENTITY_PHONE_ID")
    private String id;

    @Column(name = "ENTITY_ID")
    private String entityId;

    @Column(name = "ENT_TYP_CD")
    private String entityTypeCode;

    @Column(name = "PHONE_TYP_CD")
    private String phoneTypeCode;

    @Column(name = "PHONE_NBR")
    private String phoneNumber;

    @Column(name = "PHONE_EXTN_NBR")
    private String extensionNumber;

    @Column(name = "POSTAL_CNTRY_CD")
    private String countryCode;

    @ManyToOne(targetEntity = EntityPhoneTypeBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "PHONE_TYP_CD", insertable = false, updatable = false)
    private EntityPhoneTypeBo phoneType;

    @Transient
    private boolean suppressPhone;

    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active;

    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "DFLT_IND")
    private boolean defaultValue;

    public String getPhoneTypeCode() {
        return this.phoneTypeCode;
    }

    public static EntityPhone to(EntityPhoneBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityPhone.Builder.create(bo).build();
    }

    /**
     * Creates a CountryBo business object from an immutable representation of a Country.
     *
     * @param an immutable Country
     * @return a CountryBo
     */
    public static EntityPhoneBo from(EntityPhone immutable) {
        if (immutable == null) {
            return null;
        }

        EntityPhoneBo bo = new EntityPhoneBo();
        bo.id = immutable.getId();
        bo.active = immutable.isActive();

        bo.entityId = immutable.getEntityId();
        bo.entityTypeCode = immutable.getEntityTypeCode();
        if (immutable.getPhoneType() != null) {
            bo.phoneTypeCode = immutable.getPhoneType().getCode();
        }

        bo.phoneType = EntityPhoneTypeBo.from(immutable.getPhoneType());
        bo.defaultValue = immutable.isDefaultValue();
        bo.countryCode = immutable.getCountryCodeUnmasked();
        bo.phoneNumber = immutable.getPhoneNumberUnmasked();
        bo.extensionNumber = immutable.getExtensionNumberUnmasked();
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());

        return bo;
    }

    @Override
    public EntityPhoneTypeBo getPhoneType() {
        return this.phoneType;
    }

    public void setPhoneType(EntityPhoneTypeBo phoneType) {
        this.phoneType = phoneType;
    }

    @Override
    public boolean isSuppressPhone() {
        try {
            EntityPrivacyPreferences privacy = KimApiServiceLocator.getIdentityService().getEntityPrivacyPreferences(
                    getEntityId());
            if (privacy != null) {
                this.suppressPhone = privacy.isSuppressPhone();
            } else {
                this.suppressPhone = false;
            }

        } catch (NullPointerException e) {
            return false;
        } catch (ClassCastException c) {
            return false;
        }
        return this.suppressPhone;
    }

    @Override
    public String getFormattedPhoneNumber() {
        if (isSuppressPhone()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return getFormattedPhoneNumberUnmasked();
    }

    @Override
    public String getPhoneNumberUnmasked() {
        return this.phoneNumber;
    }

    @Override
    public String getExtensionNumberUnmasked() {
        return this.extensionNumber;
    }

    @Override
    public String getCountryCodeUnmasked() {
        return this.countryCode;
    }

    @Override
    public String getFormattedPhoneNumberUnmasked() {
        StringBuffer sb = new StringBuffer(30);

        // TODO: get extension from country code table
        // TODO: append "+xxx" if country is not the default country
        sb.append(this.phoneNumber);
        if (StringUtils.isNotBlank(this.extensionNumber)) {
            sb.append(" x");
            sb.append(this.extensionNumber);
        }

        return sb.toString();
    }

    @Override
    public String getPhoneNumber() {
        if (isSuppressPhone()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK_PHONE;
        }

        return this.phoneNumber;
    }

    @Override
    public String getCountryCode() {
        if (isSuppressPhone()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK_CODE;
        }

        return this.countryCode;
    }

    @Override
    public String getExtensionNumber() {
        if (isSuppressPhone()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return this.extensionNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityTypeCode() {
        return entityTypeCode;
    }

    public void setEntityTypeCode(String entityTypeCode) {
        this.entityTypeCode = entityTypeCode;
    }

    public void setPhoneTypeCode(String phoneTypeCode) {
        this.phoneTypeCode = phoneTypeCode;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setExtensionNumber(String extensionNumber) {
        this.extensionNumber = extensionNumber;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean getSuppressPhone() {
        return suppressPhone;
    }

    public void setSuppressPhone(boolean suppressPhone) {
        this.suppressPhone = suppressPhone;
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

    public boolean getDefaultValue() {
        return defaultValue;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    private static final long serialVersionUID = 1L;

}
