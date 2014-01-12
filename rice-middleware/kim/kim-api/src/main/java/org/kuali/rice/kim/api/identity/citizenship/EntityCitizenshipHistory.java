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
package org.kuali.rice.kim.api.identity.citizenship;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.core.api.util.jaxb.DateTimeAdapter;
import org.kuali.rice.kim.api.identity.CodedAttribute;
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

@XmlRootElement(name = EntityCitizenshipHistory.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityCitizenshipHistory.Constants.TYPE_NAME, propOrder = {
    EntityCitizenshipHistory.Elements.ID,
    EntityCitizenshipHistory.Elements.ENTITY_ID,
    EntityCitizenshipHistory.Elements.STATUS,
    EntityCitizenshipHistory.Elements.COUNTRY_CODE,
    EntityCitizenshipHistory.Elements.START_DATE,
    EntityCitizenshipHistory.Elements.END_DATE,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.OBJECT_ID,
    EntityCitizenshipHistory.Elements.ACTIVE,
    EntityCitizenship.Elements.CHANGE_DATE,
    EntityCitizenship.Elements.CHANGE_TYPE,
    CoreConstants.CommonElements.HISTORY_ID,
    CoreConstants.CommonElements.ACTIVE_FROM_DATE,
    CoreConstants.CommonElements.ACTIVE_TO_DATE,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityCitizenshipHistory extends AbstractDataTransferObject
    implements EntityCitizenshipHistoryContract
{
    private static final long serialVersionUID = 332559990542833022L;
    @XmlElement(name = Elements.ID, required = false)
    private final String id;
    @XmlElement(name = Elements.ENTITY_ID, required = false)
    private final String entityId;
    @XmlElement(name = Elements.STATUS, required = false)
    private final CodedAttributeHistory status;
    @XmlElement(name = Elements.COUNTRY_CODE, required = false)
    private final String countryCode;
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlElement(name = Elements.START_DATE, required = false)
    private final DateTime startDate;
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlElement(name = Elements.END_DATE, required = false)
    private final DateTime endDate;
    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
    @XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
    private final String objectId;
    @XmlElement(name = Elements.ACTIVE, required = false)
    private final boolean active;
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlElement(name = Elements.CHANGE_DATE, required = false)
    private final DateTime changeDate;
    @XmlElement(name = Elements.CHANGE_TYPE, required = false)
    private final CodedAttributeHistory changeType;
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
    private EntityCitizenshipHistory() {
        this.status = null;
        this.countryCode = null;
        this.startDate = null;
        this.endDate = null;
        this.versionNumber = null;
        this.objectId = null;
        this.active = false;
        this.id = null;
        this.entityId = null;
        this.changeDate = null;
        this.changeType = null;
        this.historyId = null;
        this.activeFromDate = null;
        this.activeToDate = null;
    }

    private EntityCitizenshipHistory(Builder builder) {
        this.status = builder.getStatus() != null ? builder.getStatus().build() : null;
        this.countryCode = builder.getCountryCode();
        this.startDate = builder.getStartDate();
        this.endDate = builder.getEndDate();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.active = builder.isActive();
        this.id = builder.getId();
        this.entityId = builder.getEntityId();
        this.changeDate = builder.getChangeDate();
        this.changeType = builder.getChangeType() != null ? builder.getChangeType().build() : null;
        this.historyId = builder.getHistoryId();
        this.activeFromDate = builder.getActiveFromDate();
        this.activeToDate = builder.getActiveToDate();
    }

    @Override
    public String getEntityId() {
        return this.entityId;
    }

    @Override
    public CodedAttributeHistory getStatus() {
        return this.status;
    }

    @Override
    public String getCountryCode() {
        return this.countryCode;
    }

    @Override
    public DateTime getStartDate() {
        return this.startDate;
    }

    @Override
    public DateTime getEndDate() {
        return this.endDate;
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
    public String getId() {
        return this.id;
    }

    @Override
    public DateTime getChangeDate() {
        return this.changeDate;
    }

    @Override
    public CodedAttributeHistory getChangeType() {
        return this.changeType;
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
     * A builder which can be used to construct {@link org.kuali.rice.kim.api.identity.citizenship.EntityCitizenshipHistory} instances.  Enforces the constraints of the {@link org.kuali.rice.kim.api.identity.citizenship.EntityCitizenshipContract}.
     * 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, EntityCitizenshipHistoryContract
    {
        private String entityId;
        private CodedAttributeHistory.Builder status;
        private String countryCode;
        private DateTime startDate;
        private DateTime endDate;
        private Long versionNumber;
        private String objectId;
        private boolean active;
        private String id;
        private DateTime changeDate;
        private CodedAttributeHistory.Builder changeType;
        private Long historyId;
        private DateTime activeFromDate;
        private DateTime activeToDate;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(EntityCitizenshipHistoryContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setEntityId(contract.getEntityId());
            if (contract.getStatus() != null) {
                builder.setStatus(CodedAttributeHistory.Builder.create(contract.getStatus()));
            }
            builder.setCountryCode(contract.getCountryCode());
            builder.setStartDate(contract.getStartDate());
            builder.setEndDate(contract.getEndDate());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setActive(contract.isActive());
            builder.setId(contract.getId());
            builder.setChangeDate(contract.getChangeDate());
            builder.setChangeType(contract.getChangeType() != null ? CodedAttributeHistory.Builder.create(contract.getChangeType()) : null);
            builder.setHistoryId(contract.getHistoryId());
            builder.setActiveFromDate(contract.getActiveFromDate());
            builder.setActiveToDate(contract.getActiveToDate());
            return builder;
        }

        public EntityCitizenshipHistory build() {
            return new EntityCitizenshipHistory(this);
        }

        @Override
        public String getEntityId() {
            return this.entityId;
        }

        @Override
        public CodedAttributeHistory.Builder getStatus() {
            return this.status;
        }

        @Override
        public String getCountryCode() {
            return this.countryCode;
        }

        @Override
        public DateTime getStartDate() {
            return this.startDate;
        }

        @Override
        public DateTime getEndDate() {
            return this.endDate;
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
        public String getId() {
            return this.id;
        }

        @Override
        public DateTime getChangeDate() {
            return this.changeDate;
        }

        @Override
        public CodedAttributeHistory.Builder getChangeType() {
            return this.changeType;
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
            this.entityId = entityId;
        }
        public void setStatus(CodedAttributeHistory.Builder status) {
            this.status = status;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public void setStartDate(DateTime startDate) {
            this.startDate = startDate;
        }

        public void setEndDate(DateTime endDate) {
            this.endDate = endDate;
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

        public void setId(String id) {
            if (StringUtils.isWhitespace(id)) {
                throw new IllegalArgumentException("id is blank");
            }
            this.id = id;
        }

        public void setChangeDate(DateTime changeDate) {
            this.changeDate = changeDate;
        }

        public void setChangeType(CodedAttributeHistory.Builder changeType) {
            this.changeType = changeType;
        }

    }


    /**
     * Defines some internal constants used on this class.
     * 
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "entityCitizenship";
        final static String TYPE_NAME = "EntityCitizenshipType";
    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {
        final static String ENTITY_ID = "entityId";
        final static String STATUS = "status";
        final static String COUNTRY_CODE = "countryCode";
        final static String START_DATE = "startDate";
        final static String END_DATE = "endDate";
        final static String ACTIVE = "active";
        final static String ID = "id";
        final static String CHANGE_DATE = "changeDate";
        final static String CHANGE_TYPE = "changeType";
    }

}