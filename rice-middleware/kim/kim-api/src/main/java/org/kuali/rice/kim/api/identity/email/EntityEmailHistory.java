/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kim.api.identity.email;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.core.api.util.jaxb.DateTimeAdapter;
import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.api.identity.CodedAttributeHistory;

import javax.xml.bind.Element;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Collection;

@XmlRootElement(name = EntityEmailHistory.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityEmailHistory.Constants.TYPE_NAME, propOrder = {
    EntityEmailHistory.Elements.ID,
    EntityEmailHistory.Elements.ENTITY_TYPE_CODE,
    EntityEmailHistory.Elements.ENTITY_ID,
    EntityEmailHistory.Elements.EMAIL_TYPE,
    EntityEmailHistory.Elements.EMAIL_ADDRESS,
    EntityEmailHistory.Elements.EMAIL_ADDRESS_UNMASKED,
    EntityEmailHistory.Elements.SUPPRESS_EMAIL,
    EntityEmailHistory.Elements.DEFAULT_VALUE,
    EntityEmailHistory.Elements.ACTIVE,
    CoreConstants.CommonElements.HISTORY_ID,
    CoreConstants.CommonElements.ACTIVE_FROM_DATE,
    CoreConstants.CommonElements.ACTIVE_TO_DATE,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.OBJECT_ID,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class EntityEmailHistory extends AbstractDataTransferObject implements EntityEmailHistoryContract{

    @XmlElement(name = Elements.ID, required = false)
    private final String id;
    @XmlElement(name = Elements.ENTITY_TYPE_CODE, required = false)
    private final String entityTypeCode;
    @XmlElement(name = Elements.ENTITY_ID, required = false)
    private final String entityId;
    @XmlElement(name = Elements.EMAIL_TYPE, required = false)
    private final CodedAttributeHistory emailType;
    @XmlElement(name = Elements.EMAIL_ADDRESS, required = false)
    private final String emailAddress;
    @XmlElement(name = Elements.EMAIL_ADDRESS_UNMASKED, required = false)
    private final String emailAddressUnmasked;
    @XmlElement(name = Elements.SUPPRESS_EMAIL, required = false)
    private final boolean suppressEmail;
    @XmlElement(name = Elements.DEFAULT_VALUE, required = false)
    private final boolean defaultValue;
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
     *
     */
    private EntityEmailHistory() {
        this.id = null;
        this.entityId = null;
        this.entityTypeCode = null;
        this.emailType = null;
        this.emailAddress = null;
        this.emailAddressUnmasked = null;
        this.suppressEmail = false;
        this.defaultValue = false;
        this.versionNumber = null;
        this.objectId = null;
        this.active = false;
        this.historyId = null;
        this.activeFromDate = null;
        this.activeToDate = null;
    }

    private EntityEmailHistory(Builder builder) {
        this.id = builder.getId();
        this.entityId = builder.getEntityId();
        this.entityTypeCode = builder.getEntityTypeCode();
        this.emailType = (builder.getEmailType() != null) ? builder.getEmailType().build() : null;
        this.emailAddress = builder.getEmailAddress();
        this.emailAddressUnmasked = builder.getEmailAddressUnmasked();
        this.suppressEmail = builder.isSuppressEmail();
        this.defaultValue = builder.isDefaultValue();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.active = builder.isActive();
        this.historyId = builder.getHistoryId();
        this.activeFromDate = builder.getActiveFromDate();
        this.activeToDate = builder.getActiveToDate();
    }

    @Override
    public String getId() {
        return this.id;
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
    public CodedAttributeHistory getEmailType() {
        return this.emailType;
    }

    @Override
    public String getEmailAddress() {
        return this.emailAddress;
    }

    @Override
    public String getEmailAddressUnmasked() {
        return this.emailAddressUnmasked;
    }

    @Override
    public boolean isSuppressEmail() {
        return this.suppressEmail;
    }

    @Override
    public boolean isDefaultValue() {
        return this.defaultValue;
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
     * A builder which can be used to construct {@link org.kuali.rice.kim.api.identity.email.EntityEmailHistory} instances.  Enforces the constraints of the {@link EntityEmailContract}.
     * 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, EntityEmailHistoryContract
    {

        private String id;
        private String entityId;
        private String entityTypeCode;
        private CodedAttributeHistory.Builder emailType;
        private String emailAddressUnmasked;
        private boolean suppressEmail;
        private boolean defaultValue;
        private Long versionNumber;
        private String objectId;
        private boolean active;
        private Long historyId;
        private DateTime activeFromDate;
        private DateTime activeToDate;

        private Builder() {

        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(EntityEmailHistoryContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setId(contract.getId());
            builder.setEntityId(contract.getEntityId());
            builder.setEntityTypeCode(contract.getEntityTypeCode());
            builder.setSuppressEmail(contract.isSuppressEmail());
            if (contract.getEmailType() != null) {
                builder.setEmailType(CodedAttributeHistory.Builder.create(contract.getEmailType()));
            }
            builder.setEmailAddress(contract.getEmailAddressUnmasked());
            builder.setDefaultValue(contract.isDefaultValue());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setActive(contract.isActive());
            builder.setHistoryId(contract.getHistoryId());
            builder.setActiveFromDate(contract.getActiveFromDate());
            builder.setActiveToDate(contract.getActiveToDate());
            return builder;
        }

        public EntityEmailHistory build() {
            return new EntityEmailHistory(this);
        }

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public String getEntityTypeCode() {
            return this.entityTypeCode;
        }

        @Override
        public String getEntityId() {
            return this.entityId;
        }

        @Override
        public CodedAttributeHistory.Builder getEmailType() {
            return this.emailType;
        }

        @Override
        public String getEmailAddress() {
            if (isSuppressEmail()) {
                return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
            }
            return this.emailAddressUnmasked;
        }

        @Override
        public String getEmailAddressUnmasked() {
            return this.emailAddressUnmasked;
        }

        @Override
        public boolean isSuppressEmail() {
            return this.suppressEmail;
        }

        @Override
        public boolean isDefaultValue() {
            return this.defaultValue;
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

        public void setId(String id) {
            if (StringUtils.isWhitespace(id)) {
                throw new IllegalArgumentException("id is blank");
            }
            this.id = id;
        }

        public void setEntityTypeCode(String entityTypeCode) {
            this.entityTypeCode = entityTypeCode;
        }

        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }

        public void setEmailType(CodedAttributeHistory.Builder emailType) {
            this.emailType = emailType;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddressUnmasked = emailAddress;
        }

        private void setSuppressEmail(boolean suppressEmail) {
            this.suppressEmail = suppressEmail;
        }

        public void setDefaultValue(boolean defaultValue) {
            this.defaultValue = defaultValue;
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

        final static String ROOT_ELEMENT_NAME = "entityEmail";
        final static String TYPE_NAME = "EntityEmailType";
    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {

        final static String ID = "id";
        final static String ENTITY_TYPE_CODE = "entityTypeCode";
        final static String ENTITY_ID = "entityId";
        final static String EMAIL_TYPE = "emailType";
        final static String EMAIL_ADDRESS = "emailAddress";
        final static String EMAIL_ADDRESS_UNMASKED = "emailAddressUnmasked";
        final static String SUPPRESS_EMAIL = "suppressEmail";
        final static String DEFAULT_VALUE = "defaultValue";
        final static String ACTIVE = "active";

    }

}
