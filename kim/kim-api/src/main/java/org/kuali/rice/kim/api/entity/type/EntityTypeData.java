package org.kuali.rice.kim.api.entity.type;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.kim.api.entity.EntityUtils;
import org.kuali.rice.kim.api.entity.Type;
import org.kuali.rice.kim.api.entity.TypeContract;
import org.kuali.rice.kim.api.entity.address.EntityAddress;
import org.kuali.rice.kim.api.entity.address.EntityAddressContract;
import org.kuali.rice.kim.api.entity.email.EntityEmail;
import org.kuali.rice.kim.api.entity.email.EntityEmailContract;
import org.kuali.rice.kim.api.entity.phone.EntityPhone;
import org.kuali.rice.kim.api.entity.phone.EntityPhoneContract;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@XmlRootElement(name = EntityTypeData.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityTypeData.Constants.TYPE_NAME, propOrder = {
    EntityTypeData.Elements.ENTITY_ID,
    EntityTypeData.Elements.ENTITY_TYPE_CODE,
    EntityTypeData.Elements.ENTITY_TYPE,
    EntityTypeData.Elements.ADDRESSES,
    EntityTypeData.Elements.EMAIL_ADDRESSES,
    EntityTypeData.Elements.PHONE_NUMBERS,
    EntityTypeData.Elements.DEFAULT_ADDRESS,
    EntityTypeData.Elements.DEFAULT_EMAIL_ADDRESS,
    EntityTypeData.Elements.DEFAULT_PHONE_NUMBER,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.OBJECT_ID,
    EntityTypeData.Elements.ACTIVE,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityTypeData
    implements ModelObjectComplete, EntityTypeDataContract
{

    @XmlElement(name = Elements.ENTITY_ID, required = true)
    private final String entityId;
    @XmlElement(name = Elements.ENTITY_TYPE_CODE, required = true)
    private final String entityTypeCode;
    @XmlElement(name = Elements.ENTITY_TYPE, required = false)
    private final Type entityType;
    @XmlElementWrapper(name = Elements.ADDRESSES, required = false)
    @XmlElement(name = Elements.ADDRESS, required = false)
    private final List<EntityAddress> addresses;
    @XmlElementWrapper(name = Elements.EMAIL_ADDRESSES, required = false)
    @XmlElement(name = Elements.EMAIL_ADDRESS, required = false)
    private final List<EntityEmail> emailAddresses;
    @XmlElementWrapper(name = Elements.PHONE_NUMBERS, required = false)
    @XmlElement(name = Elements.PHONE_NUMBER, required = false)
    private final List<EntityPhone> phoneNumbers;
    @XmlElement(name = Elements.DEFAULT_ADDRESS, required = false)
    private final EntityAddress defaultAddress;
    @XmlElement(name = Elements.DEFAULT_EMAIL_ADDRESS, required = false)
    private final EntityEmail defaultEmailAddress;
    @XmlElement(name = Elements.DEFAULT_PHONE_NUMBER, required = false)
    private final EntityPhone defaultPhoneNumber;
    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
    @XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
    private final String objectId;
    @XmlElement(name = Elements.ACTIVE, required = false)
    private final boolean active;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private EntityTypeData() {
        this.entityId = null;
        this.entityTypeCode = null;
        this.entityType = null;
        this.addresses = null;
        this.emailAddresses = null;
        this.phoneNumbers = null;
        this.defaultAddress = null;
        this.defaultEmailAddress = null;
        this.defaultPhoneNumber = null;
        this.versionNumber = null;
        this.objectId = null;
        this.active = false;
    }

    private EntityTypeData(Builder builder) {
        this.entityId = builder.getEntityId();
        this.entityTypeCode = builder.getEntityTypeCode();
        this.entityType = (builder.getEntityType() != null) ? builder.getEntityType().build() : null;
        this.addresses = new ArrayList<EntityAddress>();
        if (!CollectionUtils.isEmpty(builder.getAddresses())) {
            for (EntityAddress.Builder address : builder.getAddresses()) {
                this.addresses.add(address.build());
            }
        }

        this.emailAddresses = new ArrayList<EntityEmail>();
        if (!CollectionUtils.isEmpty(builder.getEmailAddresses())) {
            for (EntityEmail.Builder email : builder.getEmailAddresses()) {
                this.emailAddresses.add(email.build());
            }
        }
        this.phoneNumbers = new ArrayList<EntityPhone>();
        if (!CollectionUtils.isEmpty(builder.getPhoneNumbers())) {
            for (EntityPhone.Builder phoneNumber : builder.getPhoneNumbers()) {
                this.phoneNumbers.add(phoneNumber.build());
            }
        }
        this.defaultAddress = builder.getDefaultAddress() != null ? builder.getDefaultAddress().build() : null;
        this.defaultEmailAddress = builder.getDefaultEmailAddress() != null ? builder.getDefaultEmailAddress().build() : null;
        this.defaultPhoneNumber = builder.getDefaultPhoneNumber() != null ? builder.getDefaultPhoneNumber().build() : null;
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.active = builder.isActive();
    }

    @Override
    public String getEntityId() {
        return this.entityId;
    }

    @Override
    public String getEntityTypeCode() {
        return this.entityTypeCode;
    }

    @Override
    public TypeContract getEntityType() {
        return this.entityType;
    }

    @Override
    public List<EntityAddress> getAddresses() {
        return this.addresses;
    }

    @Override
    public List<EntityEmail> getEmailAddresses() {
        return this.emailAddresses;
    }

    @Override
    public List<EntityPhone> getPhoneNumbers() {
        return this.phoneNumbers;
    }

    @Override
    public EntityAddressContract getDefaultAddress() {
        return this.defaultAddress;
    }

    @Override
    public EntityEmail getDefaultEmailAddress() {
        return this.defaultEmailAddress;
    }

    @Override
    public EntityPhoneContract getDefaultPhoneNumber() {
        return this.defaultPhoneNumber;
    }

    @Override
    public Long getVersionNumber() {
        return this.versionNumber;
    }

    @Override
    public String getObjectId() {
        return this.objectId;
    }

    @Override
    public boolean isActive() {
        return this.active;
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


    /**
     * A builder which can be used to construct {@link EntityTypeData} instances.  Enforces the constraints of the {@link EntityTypeDataContract}.
     *
     */
    public final static class Builder
        implements Serializable, ModelBuilder, EntityTypeDataContract
    {
        private String entityId;
        private String entityTypeCode;
        private Type.Builder entityType;
        private List<EntityAddress.Builder> addresses;
        private List<EntityEmail.Builder> emailAddresses;
        private List<EntityPhone.Builder> phoneNumbers;
        private Long versionNumber;
        private String objectId;
        private boolean active;

        private Builder(String entityId, String entityTypeCode) {
            setEntityId(entityId);
            setEntityTypeCode(entityTypeCode);
            setEntityType(Type.Builder.create(entityTypeCode));
        }

        public static Builder create(String entityId, String entityTypeCode) {
            return new Builder(entityId, entityTypeCode);
        }

        public static Builder create(EntityTypeDataContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create(contract.getEntityId(), contract.getEntityTypeCode());
            if (contract.getEntityType() != null) {
                builder.setEntityType(Type.Builder.create(contract.getEntityType()));
            }
            builder.addresses = new ArrayList<EntityAddress.Builder>();
            if (!CollectionUtils.isEmpty(contract.getAddresses())) {
                for (EntityAddressContract addressContract : contract.getAddresses()) {
                    builder.addresses.add(EntityAddress.Builder.create(addressContract));
                }
            }
            builder.emailAddresses = new ArrayList<EntityEmail.Builder>();
            if (!CollectionUtils.isEmpty(contract.getEmailAddresses())) {
                for (EntityEmailContract emailContract : contract.getEmailAddresses()) {
                    builder.emailAddresses.add(EntityEmail.Builder.create(emailContract));
                }
            }
            builder.phoneNumbers = new ArrayList<EntityPhone.Builder>();
            if (!CollectionUtils.isEmpty(contract.getPhoneNumbers())) {
                for (EntityPhoneContract phoneContract : contract.getPhoneNumbers()) {
                    builder.phoneNumbers.add(EntityPhone.Builder.create(phoneContract));
                }
            }
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setActive(contract.isActive());
            return builder;
        }

        public EntityTypeData build() {
            return new EntityTypeData(this);
        }

        @Override
        public String getEntityId() {
            return this.entityId;
        }

        @Override
        public String getEntityTypeCode() {
            return this.entityTypeCode;
        }

        @Override
        public Type.Builder getEntityType() {
            return this.entityType;
        }

        @Override
        public List<EntityAddress.Builder> getAddresses() {
            return this.addresses;
        }

        @Override
        public List<EntityEmail.Builder> getEmailAddresses() {
            return this.emailAddresses;
        }

        @Override
        public List<EntityPhone.Builder> getPhoneNumbers() {
            return this.phoneNumbers;
        }

        @Override
        public EntityAddress.Builder getDefaultAddress() {
            return EntityUtils.getDefaultItem(this.addresses);
        }

        @Override
        public EntityEmail.Builder getDefaultEmailAddress() {
            return EntityUtils.getDefaultItem(this.emailAddresses);
        }

        @Override
        public EntityPhone.Builder getDefaultPhoneNumber() {
            return EntityUtils.getDefaultItem(this.phoneNumbers);
        }

        @Override
        public Long getVersionNumber() {
            return this.versionNumber;
        }

        @Override
        public String getObjectId() {
            return this.objectId;
        }

        @Override
        public boolean isActive() {
            return this.active;
        }

        public void setEntityId(String entityId) {
            if (StringUtils.isEmpty(entityId)) {
                throw new IllegalArgumentException("entityId is empty");
            }
            this.entityId = entityId;
        }

        public void setEntityTypeCode(String entityTypeCode) {
            if (StringUtils.isEmpty(entityTypeCode)) {
                throw new IllegalArgumentException("entityTypeCode is empty");
            }
            this.entityTypeCode = entityTypeCode;
        }

        public void setEntityType(Type.Builder entityType) {
            this.entityType = entityType;
        }

        public void setAddresses(List addresses) {
            this.addresses = addresses;
        }

        public void setEmailAddresses(List emailAddresses) {
            this.emailAddresses = emailAddresses;
        }

        public void setPhoneNumbers(List phoneNumbers) {
            this.phoneNumbers = phoneNumbers;
        }

        public void setVersionNumber(Long versionNumber) {
            this.versionNumber = versionNumber;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }


    /**
     * Defines some internal constants used on this class.
     *
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "entityTypeData";
        final static String TYPE_NAME = "EntityTypeDataType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] {CoreConstants.CommonElements.FUTURE_ELEMENTS };

    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     *
     */
    static class Elements {

        final static String ENTITY_ID = "entityId";
        final static String ENTITY_TYPE_CODE = "entityTypeCode";
        final static String ENTITY_TYPE = "entityType";
        final static String ADDRESSES = "addresses";
        final static String ADDRESS = "address";
        final static String EMAIL_ADDRESSES = "emailAddresses";
        final static String EMAIL_ADDRESS = "emailAddress";
        final static String PHONE_NUMBERS = "phoneNumbers";
        final static String PHONE_NUMBER = "phoneNumber";
        final static String DEFAULT_ADDRESS = "defaultAddress";
        final static String DEFAULT_EMAIL_ADDRESS = "defaultEmailAddress";
        final static String DEFAULT_PHONE_NUMBER = "defaultPhoneNumber";
        final static String ACTIVE = "active";

    }

}
