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
package org.kuali.rice.kim.api.identity.visa;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.core.api.util.jaxb.DateTimeAdapter;
import org.kuali.rice.kim.api.identity.CodedAttributeHistory;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Collection;

@XmlRootElement(name = EntityVisaHistory.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityVisaHistory.Constants.TYPE_NAME, propOrder = {
    EntityVisaHistory.Elements.ID,
    EntityVisaHistory.Elements.ENTITY_ID,
    EntityVisaHistory.Elements.VISA_TYPE_KEY,
    EntityVisaHistory.Elements.VISA_ENTRY,
    EntityVisaHistory.Elements.VISA_ID,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.OBJECT_ID,
    EntityVisaHistory.Elements.VISA_TYPE,
    CoreConstants.CommonElements.HISTORY_ID,
    CoreConstants.CommonElements.ACTIVE_FROM_DATE,
    CoreConstants.CommonElements.ACTIVE_TO_DATE,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityVisaHistory extends AbstractDataTransferObject
    implements EntityVisaHistoryContract
{

    @XmlElement(name = Elements.ENTITY_ID, required = false)
    private final String entityId;
    @XmlElement(name = Elements.VISA_TYPE_KEY, required = false)
    private final String visaTypeKey;
    @XmlElement(name = Elements.VISA_ENTRY, required = false)
    private final String visaEntry;
    @XmlElement(name = Elements.VISA_ID, required = false)
    private final String visaId;
    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
    @XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
    private final String objectId;
    @XmlElement(name = Elements.ID, required = false)
    private final String id;
    @XmlElement(name = Elements.VISA_TYPE, required = false)
    private final CodedAttributeHistory visaType;
    @XmlElement(name = CoreConstants.CommonElements.HISTORY_ID, required = false)
    private final Long historyId;
    @XmlElement(name = CoreConstants.CommonElements.ACTIVE_FROM_DATE, required = false)
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private final DateTime activeFromDate;
    @XmlElement(name = CoreConstants.CommonElements.ACTIVE_TO_DATE, required = false)
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private final DateTime activeToDate;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     *
     */
    private EntityVisaHistory() {
        this.entityId = null;
        this.visaTypeKey = null;
        this.visaEntry = null;
        this.visaId = null;
        this.versionNumber = null;
        this.objectId = null;
        this.id = null;
        this.visaType = null;
        this.historyId = null;
        this.activeFromDate = null;
        this.activeToDate = null;
    }

    private EntityVisaHistory(Builder builder) {
        this.entityId = builder.getEntityId();
        this.visaTypeKey = builder.getVisaTypeKey();
        this.visaEntry = builder.getVisaEntry();
        this.visaId = builder.getVisaId();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.id = builder.getId();
        this.visaType = builder.getVisaType() == null ? null : builder.getVisaType().build();
        this.historyId = builder.getHistoryId();
        this.activeFromDate = builder.getActiveFromDate();
        this.activeToDate = builder.getActiveToDate();
    }

    @Override
    public String getEntityId() {
        return this.entityId;
    }

    @Override
    public String getVisaTypeKey() {
        return this.visaTypeKey;
    }

    @Override
    public String getVisaEntry() {
        return this.visaEntry;
    }

    @Override
    public String getVisaId() {
        return this.visaId;
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
    public String getId() {
        return this.id;
    }

    @Override
    public CodedAttributeHistory getVisaType() {
        return this.visaType;
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
        return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, null);
    }

    @Override
    public boolean isActive() {
        return isActiveNow();
    }

    @Override
    public boolean isActive(DateTime activeAsOf) {
        return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, activeAsOf);
    }



    /**
     * A builder which can be used to construct {@link org.kuali.rice.kim.api.identity.visa.EntityVisaHistory} instances.  Enforces the constraints of the {@link org.kuali.rice.kim.api.identity.visa.EntityVisaContract}.
     *
     */
    public final static class Builder
        implements Serializable, ModelBuilder, EntityVisaHistoryContract
    {

        private String entityId;
        private String visaTypeKey;
        private String visaEntry;
        private String visaId;
        private Long versionNumber;
        private String objectId;
        private String id;
        private CodedAttributeHistory.Builder visaType;
        private Long historyId;
        private DateTime activeFromDate;
        private DateTime activeToDate;

        private Builder() { }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(EntityVisaHistoryContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setEntityId(contract.getEntityId());
            builder.setVisaTypeKey(contract.getVisaTypeKey());
            builder.setVisaEntry(contract.getVisaEntry());
            builder.setVisaId(contract.getVisaId());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setId(contract.getId());
            if (contract.getVisaType() != null) {
                builder.setVisaType(CodedAttributeHistory.Builder.create(contract.getVisaType()));
            }
            builder.setHistoryId(contract.getHistoryId());
            builder.setActiveFromDate(contract.getActiveFromDate());
            builder.setActiveToDate(contract.getActiveToDate());
            return builder;
        }

        public EntityVisaHistory build() {
            return new EntityVisaHistory(this);
        }

        @Override
        public String getEntityId() {
            return this.entityId;
        }

        @Override
        public String getVisaTypeKey() {
            return this.visaTypeKey;
        }

        @Override
        public String getVisaEntry() {
            return this.visaEntry;
        }

        @Override
        public String getVisaId() {
            return this.visaId;
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
        public String getId() {
            return this.id;
        }

        @Override
        public CodedAttributeHistory.Builder getVisaType() {
            return this.visaType;
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
        public boolean isActive() {
            return isActiveNow();
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
            this.entityId = entityId;
        }

        public void setVisaTypeKey(String visaTypeKey) {
            this.visaTypeKey = visaTypeKey;
        }

        public void setVisaEntry(String visaEntry) {
            this.visaEntry = visaEntry;
        }

        public void setVisaId(String visaId) {
            this.visaId = visaId;
        }

        public void setVersionNumber(Long versionNumber) {
            this.versionNumber = versionNumber;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public void setId(String id) {
            if (StringUtils.isWhitespace(id)) {
                throw new IllegalArgumentException("id is blank");
            }
            this.id = id;
        }

        public void setVisaType(CodedAttributeHistory.Builder visaType) {
            this.visaType = visaType;
        }

    }


    /**
     * Defines some internal constants used on this class.
     *
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "entityVisa";
        final static String TYPE_NAME = "EntityVisaType";
    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     *
     */
    static class Elements {

        final static String ENTITY_ID = "entityId";
        final static String VISA_TYPE_KEY = "visaTypeKey";
        final static String VISA_ENTRY = "visaEntry";
        final static String VISA_ID = "visaId";
        final static String VISA_TYPE = "visaType";
        final static String ID = "id";

    }

}