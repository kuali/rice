package org.kuali.rice.kim.api.identity.type;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.kim.api.identity.address.EntityAddress;
import org.kuali.rice.kim.api.identity.address.EntityAddressContract;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.email.EntityEmail;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifier;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.phone.EntityPhone;
import org.kuali.rice.kim.api.identity.phone.EntityPhoneContract;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@XmlRootElement(name = EntityTypeDataDefault.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityTypeDataDefault.Constants.TYPE_NAME, propOrder = {
    EntityTypeDataDefault.Elements.ENTITY_TYPE_CODE,
    EntityTypeDataDefault.Elements.DEFAULT_ADDRESS,
    EntityTypeDataDefault.Elements.DEFAULT_EMAIL_ADDRESS,
    EntityTypeDataDefault.Elements.DEFAULT_PHONE_NUMBER,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityTypeDataDefault implements ModelObjectComplete
{
    @XmlElement(name = Elements.ENTITY_TYPE_CODE, required = true)
    private final String entityTypeCode;
    @XmlElement(name = Elements.DEFAULT_ADDRESS, required = false)
    private final EntityAddress defaultAddress;
    @XmlElement(name = Elements.DEFAULT_EMAIL_ADDRESS, required = false)
    private final EntityEmail defaultEmailAddress;
    @XmlElement(name = Elements.DEFAULT_PHONE_NUMBER, required = false)
    private final EntityPhone defaultPhoneNumber;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private EntityTypeDataDefault() {
        this.entityTypeCode = null;
        this.defaultAddress = null;
        this.defaultEmailAddress = null;
        this.defaultPhoneNumber = null;

    }

    public EntityTypeDataDefault(String entityTypeCode, EntityAddress defaultAddress, EntityEmail defaultEmailAddress, EntityPhone defaultPhoneNumber) {
        this.entityTypeCode = entityTypeCode;
        this.defaultAddress = defaultAddress;
        this.defaultEmailAddress = defaultEmailAddress;
        this.defaultPhoneNumber = defaultPhoneNumber;
    }

    public EntityTypeDataDefault(Builder builder) {
        this.entityTypeCode = builder.getEntityTypeCode();
        this.defaultAddress = builder.getDefaultAddress() == null ? null : builder.getDefaultAddress().build();
        this.defaultEmailAddress = builder.getDefaultEmailAddress() == null ? null : builder.getDefaultEmailAddress().build();
        this.defaultPhoneNumber = builder.getDefaultPhoneNumber() == null ? null : builder.getDefaultPhoneNumber().build();

    }

    public String getEntityTypeCode() {
        return this.entityTypeCode;
    }
    public EntityAddress getDefaultAddress() {
        return this.defaultAddress;
    }

    public EntityEmail getDefaultEmailAddress() {
        return this.defaultEmailAddress;
    }

    public EntityPhone getDefaultPhoneNumber() {
        return this.defaultPhoneNumber;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(object, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public final static class Builder
        implements Serializable, ModelBuilder
    {
        private String entityTypeCode;
        private EntityAddress.Builder defaultAddress;
        private EntityEmail.Builder defaultEmailAddress;
        private EntityPhone.Builder defaultPhoneNumber;

        private Builder() { }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(EntityTypeDataDefault immutable) {
            if (immutable == null) {
                throw new IllegalArgumentException("EntityTypeDataDefault is null");
            }
            Builder builder = new Builder();
            builder.setEntityTypeCode(immutable.entityTypeCode);
            if (immutable.getDefaultAddress() != null) {
                builder.setDefaultAddress(EntityAddress.Builder.create(immutable.getDefaultAddress()));
            }
            if (immutable.getDefaultEmailAddress() != null) {
                builder.setDefaultEmailAddress(EntityEmail.Builder.create(immutable.getDefaultEmailAddress()));
            }
            if (immutable.getDefaultPhoneNumber() != null) {
                builder.setDefaultPhoneNumber(EntityPhone.Builder.create(immutable.getDefaultPhoneNumber()));
            }
            return builder;
        }

        public static Builder create(EntityTypeDataContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
            Builder builder = new Builder();
            builder.setEntityTypeCode(contract.getEntityTypeCode());
            if (contract.getDefaultAddress() != null) {
                builder.setDefaultAddress(EntityAddress.Builder.create(contract.getDefaultAddress()));
            }
            if (contract.getDefaultEmailAddress() != null) {
                builder.setDefaultEmailAddress(EntityEmail.Builder.create(contract.getDefaultEmailAddress()));
            }
            if (contract.getDefaultPhoneNumber() != null) {
                builder.setDefaultPhoneNumber(EntityPhone.Builder.create(contract.getDefaultPhoneNumber()));
            }
            return builder;
        }

        public EntityTypeDataDefault build() {
            return new EntityTypeDataDefault(this);
        }

        public String getEntityTypeCode() {
            return entityTypeCode;
        }

        public void setEntityTypeCode(String entityTypeCode) {
            this.entityTypeCode = entityTypeCode;
        }

        public EntityAddress.Builder getDefaultAddress() {
            return defaultAddress;
        }

        public void setDefaultAddress(EntityAddress.Builder defaultAddress) {
            this.defaultAddress = defaultAddress;
        }

        public EntityEmail.Builder getDefaultEmailAddress() {
            return defaultEmailAddress;
        }

        public void setDefaultEmailAddress(EntityEmail.Builder defaultEmailAddress) {
            this.defaultEmailAddress = defaultEmailAddress;
        }

        public EntityPhone.Builder getDefaultPhoneNumber() {
            return defaultPhoneNumber;
        }

        public void setDefaultPhoneNumber(EntityPhone.Builder defaultPhoneNumber) {
            this.defaultPhoneNumber = defaultPhoneNumber;
        }
    }



    /**
     * Defines some internal constants used on this class.
     *
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "entityTypeDataDefault";
        final static String TYPE_NAME = "EntityTypeDataDefaultType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] {CoreConstants.CommonElements.FUTURE_ELEMENTS };

    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     *
     */
    static class Elements {
        final static String ENTITY_TYPE_CODE = "entityTypeCode";
        final static String DEFAULT_ADDRESS = "defaultAddress";
        final static String DEFAULT_EMAIL_ADDRESS = "defaultEmailAddress";
        final static String DEFAULT_PHONE_NUMBER = "defaultPhoneNumber";

    }

}
