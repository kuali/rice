/**
 * Copyright 2005-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.api.identity.type;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.core.api.util.jaxb.DateTimeAdapter;
import org.kuali.rice.kim.api.identity.CodedAttributeHistory;
import org.kuali.rice.kim.api.identity.EntityUtils;
import org.kuali.rice.kim.api.identity.address.EntityAddressHistory;
import org.kuali.rice.kim.api.identity.address.EntityAddressHistoryContract;
import org.kuali.rice.kim.api.identity.email.EntityEmailHistory;
import org.kuali.rice.kim.api.identity.email.EntityEmailHistoryContract;
import org.kuali.rice.kim.api.identity.phone.EntityPhoneHistory;
import org.kuali.rice.kim.api.identity.phone.EntityPhoneHistoryContract;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@XmlRootElement(name = EntityTypeContactInfoHistory.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityTypeContactInfoHistory.Constants.TYPE_NAME, propOrder = {
    EntityTypeContactInfoHistory.Elements.ENTITY_ID,
    EntityTypeContactInfoHistory.Elements.ENTITY_TYPE_CODE,
    EntityTypeContactInfoHistory.Elements.ENTITY_TYPE,
    EntityTypeContactInfoHistory.Elements.ADDRESSES,
    EntityTypeContactInfoHistory.Elements.EMAIL_ADDRESSES,
    EntityTypeContactInfoHistory.Elements.PHONE_NUMBERS,
    EntityTypeContactInfoHistory.Elements.DEFAULT_ADDRESS,
    EntityTypeContactInfoHistory.Elements.DEFAULT_EMAIL_ADDRESS,
    EntityTypeContactInfoHistory.Elements.DEFAULT_PHONE_NUMBER,
    CoreConstants.CommonElements.HISTORY_ID,
    CoreConstants.CommonElements.ACTIVE_FROM_DATE,
    CoreConstants.CommonElements.ACTIVE_TO_DATE,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.OBJECT_ID,
    EntityTypeContactInfoHistory.Elements.ACTIVE,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityTypeContactInfoHistory extends AbstractDataTransferObject
    implements EntityTypeContactInfoHistoryContract
{

    @XmlElement(name = Elements.ENTITY_ID, required = true)
    private final String entityId;

    @XmlElement(name = Elements.ENTITY_TYPE_CODE, required = true)
    private final String entityTypeCode;

    @XmlElement(name = Elements.ENTITY_TYPE, required = false)
    private final CodedAttributeHistory entityType;

    @XmlElementWrapper(name = Elements.ADDRESSES, required = false)
    @XmlElement(name = Elements.ADDRESS, required = false)
    private final List<EntityAddressHistory> addresses;

    @XmlElementWrapper(name = Elements.EMAIL_ADDRESSES, required = false)
    @XmlElement(name = Elements.EMAIL_ADDRESS, required = false)
    private final List<EntityEmailHistory> emailAddresses;

    @XmlElementWrapper(name = Elements.PHONE_NUMBERS, required = false)
    @XmlElement(name = Elements.PHONE_NUMBER, required = false)
    private final List<EntityPhoneHistory> phoneNumbers;

    @XmlElement(name = Elements.DEFAULT_ADDRESS, required = false)
    private final EntityAddressHistory defaultAddress;

    @XmlElement(name = Elements.DEFAULT_EMAIL_ADDRESS, required = false)
    private final EntityEmailHistory defaultEmailAddress;

    @XmlElement(name = Elements.DEFAULT_PHONE_NUMBER, required = false)
    private final EntityPhoneHistory defaultPhoneNumber;

    @XmlElement(name = CoreConstants.CommonElements.HISTORY_ID, required = false)
    private final Long historyId;

    @XmlElement(name = CoreConstants.CommonElements.ACTIVE_FROM_DATE, required = false)
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private final DateTime activeFromDate;

    @XmlElement(name = CoreConstants.CommonElements.ACTIVE_TO_DATE, required = false)
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private final DateTime activeToDate;

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
    private EntityTypeContactInfoHistory() {
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
        this.historyId = null;
        this.activeFromDate = null;
        this.activeToDate = null;
    }

    private EntityTypeContactInfoHistory(Builder builder) {
        this.entityId = builder.getEntityId();
        this.entityTypeCode = builder.getEntityTypeCode();
        this.entityType = (builder.getEntityType() != null) ? builder.getEntityType().build() : null;
        this.addresses = new ArrayList<EntityAddressHistory>();
        if (!CollectionUtils.isEmpty(builder.getAddresses())) {
            for (EntityAddressHistory.Builder address : builder.getAddresses()) {
                this.addresses.add(address.build());
            }
        }

        this.emailAddresses = new ArrayList<EntityEmailHistory>();
        if (!CollectionUtils.isEmpty(builder.getEmailAddresses())) {
            for (EntityEmailHistory.Builder email : builder.getEmailAddresses()) {
                this.emailAddresses.add(email.build());
            }
        }
        this.phoneNumbers = new ArrayList<EntityPhoneHistory>();
        if (!CollectionUtils.isEmpty(builder.getPhoneNumbers())) {
            for (EntityPhoneHistory.Builder phoneNumber : builder.getPhoneNumbers()) {
                this.phoneNumbers.add(phoneNumber.build());
            }
        }
        this.defaultAddress = builder.getDefaultAddress() != null ? builder.getDefaultAddress().build() : null;
        this.defaultEmailAddress = builder.getDefaultEmailAddress() != null ? builder.getDefaultEmailAddress().build() : null;
        this.defaultPhoneNumber = builder.getDefaultPhoneNumber() != null ? builder.getDefaultPhoneNumber().build() : null;
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.active = builder.isActive();
        this.historyId = builder.getHistoryId();
        this.activeFromDate = builder.getActiveFromDate();
        this.activeToDate = builder.getActiveToDate();
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
    public CodedAttributeHistory getEntityType() {
        return this.entityType;
    }

    @Override
    public List<EntityAddressHistory> getAddresses() {
        return this.addresses;
    }

    @Override
    public List<EntityEmailHistory> getEmailAddresses() {
        return this.emailAddresses;
    }

    @Override
    public List<EntityPhoneHistory> getPhoneNumbers() {
        return this.phoneNumbers;
    }

    @Override
    public EntityAddressHistory getDefaultAddress() {
        return this.defaultAddress;
    }

    @Override
    public EntityEmailHistory getDefaultEmailAddress() {
        return this.defaultEmailAddress;
    }

    @Override
    public EntityPhoneHistory getDefaultPhoneNumber() {
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
    public Long getHistoryId() {
        return this.historyId;
    }

    @Override
    public DateTime getActiveFromDate() {
        return this.activeFromDate;
    }

    @Override
    public DateTime getActiveToDate() {
        return this.activeToDate;
    }

    @Override
    public boolean isActiveNow() {
        return isActive() && InactivatableFromToUtils.isActive(activeFromDate, activeToDate, null);
    }

    @Override
    public boolean isActive(DateTime activeAsOf) {
        return isActive() && InactivatableFromToUtils.isActive(activeFromDate, activeToDate, activeAsOf);
    }


    /**
     * A builder which can be used to construct {@link org.kuali.rice.kim.api.identity.type.EntityTypeContactInfoHistory} instances.  Enforces the constraints of the {@link org.kuali.rice.kim.api.identity.type.EntityTypeContactInfoContract}.
     *
     */
    public final static class Builder
        implements Serializable, ModelBuilder, EntityTypeContactInfoHistoryContract
    {
        private String entityId;
        private String entityTypeCode;
        private CodedAttributeHistory.Builder entityType;
        private List<EntityAddressHistory.Builder> addresses;
        private List<EntityEmailHistory.Builder> emailAddresses;
        private List<EntityPhoneHistory.Builder> phoneNumbers;
        private Long versionNumber;
        private String objectId;
        private boolean active;
        private Long historyId;
        private DateTime activeFromDate;
        private DateTime activeToDate;

        private Builder(String entityId, String entityTypeCode) {
            setEntityId(entityId);
            setEntityTypeCode(entityTypeCode);
            setEntityType(CodedAttributeHistory.Builder.create(entityTypeCode));
        }

        public static Builder create(String entityId, String entityTypeCode) {
            return new Builder(entityId, entityTypeCode);
        }

        public static Builder create(EntityTypeContactInfoHistoryContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create(contract.getEntityId(), contract.getEntityTypeCode());
            if (contract.getEntityType() != null) {
                builder.setEntityType(CodedAttributeHistory.Builder.create(contract.getEntityType()));
            }
            builder.addresses = new ArrayList<EntityAddressHistory.Builder>();
            if (!CollectionUtils.isEmpty(contract.getAddresses())) {
                for (EntityAddressHistoryContract addressContract : contract.getAddresses()) {
                    builder.addresses.add(EntityAddressHistory.Builder.create(addressContract));
                }
            }
            builder.emailAddresses = new ArrayList<EntityEmailHistory.Builder>();
            if (!CollectionUtils.isEmpty(contract.getEmailAddresses())) {
                for (EntityEmailHistoryContract emailContract : contract.getEmailAddresses()) {
                    builder.emailAddresses.add(EntityEmailHistory.Builder.create(emailContract));
                }
            }
            builder.phoneNumbers = new ArrayList<EntityPhoneHistory.Builder>();
            if (!CollectionUtils.isEmpty(contract.getPhoneNumbers())) {
                for (EntityPhoneHistoryContract phoneContract : contract.getPhoneNumbers()) {
                    builder.phoneNumbers.add(EntityPhoneHistory.Builder.create(phoneContract));
                }
            }
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setActive(contract.isActive());
            builder.setHistoryId(contract.getHistoryId());
            builder.setActiveFromDate(contract.getActiveFromDate());
            builder.setActiveToDate(contract.getActiveToDate());
            return builder;
        }

        public EntityTypeContactInfoHistory build() {
            return new EntityTypeContactInfoHistory(this);
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
        public CodedAttributeHistory.Builder getEntityType() {
            return this.entityType;
        }

        @Override
        public List<EntityAddressHistory.Builder> getAddresses() {
            return this.addresses;
        }

        @Override
        public List<EntityEmailHistory.Builder> getEmailAddresses() {
            return this.emailAddresses;
        }

        @Override
        public List<EntityPhoneHistory.Builder> getPhoneNumbers() {
            return this.phoneNumbers;
        }

        @Override
        public EntityAddressHistory.Builder getDefaultAddress() {
            return EntityUtils.getDefaultItem(this.addresses);
        }

        @Override
        public EntityEmailHistory.Builder getDefaultEmailAddress() {
            return EntityUtils.getDefaultItem(this.emailAddresses);
        }

        @Override
        public EntityPhoneHistory.Builder getDefaultPhoneNumber() {
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

        @Override
        public Long getHistoryId() {
            return this.historyId;
        }

        @Override
        public boolean isActiveNow() {
            return isActive() && InactivatableFromToUtils.isActive(activeFromDate, activeToDate, null);
        }

        @Override
        public boolean isActive(DateTime activeAsOf) {
            return isActive() && InactivatableFromToUtils.isActive(activeFromDate, activeToDate, activeAsOf);
        }

        @Override
        public DateTime getActiveFromDate() {
            return this.activeFromDate;
        }

        @Override
        public DateTime getActiveToDate() {
            return this.activeToDate;
        }

        public void setHistoryId(Long historyId) {
            this.historyId = historyId;
        }

        public void setActiveFromDate(DateTime activeFromDate) {
            this.activeFromDate = activeFromDate;
        }

        public void setActiveToDate(DateTime activeToDate) {
            this.activeToDate = activeToDate;
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

        public void setEntityType(CodedAttributeHistory.Builder entityType) {
            this.entityType = entityType;
        }

        public void setAddresses(List<EntityAddressHistory.Builder> addresses) {
            this.addresses = addresses;
        }

        public void setEmailAddresses(List<EntityEmailHistory.Builder> emailAddresses) {
            this.emailAddresses = emailAddresses;
        }

        public void setPhoneNumbers(List<EntityPhoneHistory.Builder> phoneNumbers) {
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

        final static String ROOT_ELEMENT_NAME = "entityTypeContactInfo";
        final static String TYPE_NAME = "EntityTypeContactInfoType";
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
