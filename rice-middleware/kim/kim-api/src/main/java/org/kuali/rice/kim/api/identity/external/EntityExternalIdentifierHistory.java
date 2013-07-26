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
package org.kuali.rice.kim.api.identity.external;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.core.api.util.jaxb.DateTimeAdapter;
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

@XmlRootElement(name = EntityExternalIdentifierHistory.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityExternalIdentifierHistory.Constants.TYPE_NAME, propOrder = {
    EntityExternalIdentifierHistory.Elements.ID,
    EntityExternalIdentifierHistory.Elements.ENTITY_ID,
    EntityExternalIdentifierHistory.Elements.EXTERNAL_IDENTIFIER_TYPE_CODE,
    EntityExternalIdentifierHistory.Elements.EXTERNAL_IDENTIFIER_TYPE,
    EntityExternalIdentifierHistory.Elements.EXTERNAL_ID,
    CoreConstants.CommonElements.HISTORY_ID,
    CoreConstants.CommonElements.ACTIVE_FROM_DATE,
    CoreConstants.CommonElements.ACTIVE_TO_DATE,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.OBJECT_ID,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityExternalIdentifierHistory extends AbstractDataTransferObject
    implements EntityExternalIdentifierHistoryContract
{
    @XmlElement(name = Elements.ENTITY_ID, required = false)
    private final String entityId;
    @XmlElement(name = Elements.EXTERNAL_IDENTIFIER_TYPE_CODE, required = false)
    private final String externalIdentifierTypeCode;
    @XmlElement(name = Elements.EXTERNAL_IDENTIFIER_TYPE, required = false)
    private final EntityExternalIdentifierTypeHistory externalIdentifierType;
    @XmlElement(name = Elements.EXTERNAL_ID, required = false)
    private final String externalId;
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
    @XmlElement(name = Elements.ID, required = false)
    private final String id;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     *
     */
    private EntityExternalIdentifierHistory() {
        this.entityId = null;
        this.externalIdentifierTypeCode = null;
        this.externalIdentifierType = null;
        this.externalId = null;
        this.versionNumber = null;
        this.objectId = null;
        this.id = null;
        this.historyId = null;
        this.activeFromDate = null;
        this.activeToDate = null;
    }

    private EntityExternalIdentifierHistory(Builder builder) {
        this.entityId = builder.getEntityId();
        this.externalIdentifierTypeCode = builder.getExternalIdentifierTypeCode();
        this.externalIdentifierType = builder.getExternalIdentifierType() != null ? builder.getExternalIdentifierType().build() : null;
        this.externalId = builder.getExternalId();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.id = builder.getId();
        this.historyId = builder.getHistoryId();
        this.activeFromDate = builder.getActiveFromDate();
        this.activeToDate = builder.getActiveToDate();
    }

    @Override
    public String getEntityId() {
        return this.entityId;
    }

    @Override
    public String getExternalIdentifierTypeCode() {
        return this.externalIdentifierTypeCode;
    }

    @Override
    public EntityExternalIdentifierTypeHistory getExternalIdentifierType() {
        return this.externalIdentifierType;
    }

    @Override
    public String getExternalId() {
        return this.externalId;
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
    public boolean isActive(DateTime activeAsOf) {
        return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, activeAsOf);
    }

    @Override
    public boolean isActive() {
        return isActiveNow();
    }

    /**
     * A builder which can be used to construct {@link org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierHistory} instances.  Enforces the constraints of the {@link org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierContract}.
     * 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, EntityExternalIdentifierHistoryContract
    {

        private String entityId;
        private String externalIdentifierTypeCode;
        private EntityExternalIdentifierTypeHistory.Builder externalIdentifierType;
        private String externalId;
        private Long versionNumber;
        private String objectId;
        private String id;
        private Long historyId;
        private DateTime activeFromDate;
        private DateTime activeToDate;

        private Builder() { }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(EntityExternalIdentifierHistoryContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setEntityId(contract.getEntityId());
            builder.setExternalIdentifierTypeCode(contract.getExternalIdentifierTypeCode());
            if (contract.getExternalIdentifierType() != null) {
                builder.setExternalIdentifierType(
                        EntityExternalIdentifierTypeHistory.Builder.create(contract.getExternalIdentifierType()));
            }
            builder.setExternalId(contract.getExternalId());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setId(contract.getId());
            builder.setHistoryId(contract.getHistoryId());
            builder.setActiveFromDate(contract.getActiveFromDate());
            builder.setActiveToDate(contract.getActiveToDate());
            return builder;
        }

        public EntityExternalIdentifierHistory build() {
            return new EntityExternalIdentifierHistory(this);
        }

        @Override
        public String getEntityId() {
            return this.entityId;
        }

        @Override
        public String getExternalIdentifierTypeCode() {
            return this.externalIdentifierTypeCode;
        }

        @Override
        public EntityExternalIdentifierTypeHistory.Builder getExternalIdentifierType() {
            return this.externalIdentifierType;
        }

        @Override
        public String getExternalId() {
            return this.externalId;
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
        public Long getHistoryId() {
            return this.historyId;
        }

        @Override
        public boolean isActiveNow() {
            return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, null);
        }

        @Override
        public boolean isActive(DateTime activeAsOf) {
            return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, activeAsOf);
        }

        @Override
        public DateTime getActiveFromDate() {
            return this.activeFromDate;
        }

        @Override
        public boolean isActive() {
            return isActiveNow();
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

        public void setExternalIdentifierTypeCode(String externalIdentifierTypeCode) {
            this.externalIdentifierTypeCode = externalIdentifierTypeCode;
        }

        public void setExternalIdentifierType(EntityExternalIdentifierTypeHistory.Builder externalIdentifierType) {
            this.externalIdentifierType = externalIdentifierType;
        }

        public void setExternalId(String externalId) {
            this.externalId = externalId;
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

    }


    /**
     * Defines some internal constants used on this class.
     * 
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "entityExternalIdentifier";
        final static String TYPE_NAME = "EntityExternalIdentifierType";
    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {

        final static String ENTITY_ID = "entityId";
        final static String EXTERNAL_IDENTIFIER_TYPE = "externalIdentifierType";
        final static String EXTERNAL_IDENTIFIER_TYPE_CODE = "externalIdentifierTypeCode";
        final static String EXTERNAL_ID = "externalId";
        final static String ID = "id";

    }

}