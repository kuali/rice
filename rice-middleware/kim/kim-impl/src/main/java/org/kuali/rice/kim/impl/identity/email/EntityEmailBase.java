package org.kuali.rice.kim.impl.identity.email;

import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.api.identity.email.EntityEmail;
import org.kuali.rice.kim.api.identity.email.EntityEmailContract;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@MappedSuperclass
public abstract class EntityEmailBase extends PersistableBusinessObjectBase implements EntityEmailContract {
    private static final long serialVersionUID = 1L;

    @Column(name = "ENTITY_ID")
    private String entityId;
    @Column(name = "ENT_TYP_CD")
    private String entityTypeCode;
    @Column(name = "EMAIL_TYP_CD")
    private String emailTypeCode;
    @Column(name = "EMAIL_ADDR")
    private String emailAddress;

    @Transient
    private boolean suppressEmail;
    @javax.persistence.Convert(converter=BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active;
    @javax.persistence.Convert(converter=BooleanYNConverter.class)
    @Column(name = "DFLT_IND")
    private boolean defaultValue;

    @Override
    public boolean isSuppressEmail() {
        try {
            EntityPrivacyPreferences privacy = KimApiServiceLocator.getIdentityService().getEntityPrivacyPreferences(
                    getEntityId());
            if (privacy != null) {
                this.suppressEmail = privacy.isSuppressEmail();
            } else {
                this.suppressEmail = false;
            }
        } catch (NullPointerException e) {
            return false;
        } catch (ClassCastException c) {
            return false;
        }
        return this.suppressEmail;
    }

    @Override
    public String getEmailAddressUnmasked() {
        return this.emailAddress;
    }

    @Override
    public String getEmailAddress() {
        if (isSuppressEmail()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return this.emailAddress;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public String getEntityTypeCode() {
        return entityTypeCode;
    }

    public void setEntityTypeCode(String entityTypeCode) {
        this.entityTypeCode = entityTypeCode;
    }

    public String getEmailTypeCode() {
        return emailTypeCode;
    }

    public void setEmailTypeCode(String emailTypeCode) {
        this.emailTypeCode = emailTypeCode;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean getSuppressEmail() {
        return suppressEmail;
    }

    public void setSuppressEmail(boolean suppressEmail) {
        this.suppressEmail = suppressEmail;
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

}
