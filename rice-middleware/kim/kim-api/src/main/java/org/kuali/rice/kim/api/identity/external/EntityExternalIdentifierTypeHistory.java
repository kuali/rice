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

@XmlRootElement(name = EntityExternalIdentifierTypeHistory.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityExternalIdentifierTypeHistory.Constants.TYPE_NAME, propOrder = {
    EntityExternalIdentifierTypeHistory.Elements.CODE,
    EntityExternalIdentifierTypeHistory.Elements.NAME,
    EntityExternalIdentifierTypeHistory.Elements.SORT_CODE,
    EntityExternalIdentifierTypeHistory.Elements.ACTIVE,
    EntityExternalIdentifierTypeHistory.Elements.ENCRYPTION_REQUIRED,
    CoreConstants.CommonElements.HISTORY_ID,
    CoreConstants.CommonElements.ACTIVE_FROM_DATE,
    CoreConstants.CommonElements.ACTIVE_TO_DATE,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.OBJECT_ID,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityExternalIdentifierTypeHistory extends AbstractDataTransferObject
    implements EntityExternalIdentifierTypeHistoryContract
{
    @XmlElement(name = Elements.CODE, required = true)
    private final String code;
    @XmlElement(name = Elements.NAME, required = false)
    private final String name;
    @XmlElement(name = Elements.SORT_CODE, required = false)
    private final String sortCode;
    @XmlElement(name = Elements.ENCRYPTION_REQUIRED, required = false)
    private final boolean encryptionRequired;
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
    private EntityExternalIdentifierTypeHistory() {
        this.name = null;
        this.code = null;
        this.sortCode = null;
        this.versionNumber = null;
        this.objectId = null;
        this.active = false;
        this.encryptionRequired = false;
        this.historyId = null;
        this.activeFromDate = null;
        this.activeToDate = null;
    }

    private EntityExternalIdentifierTypeHistory(Builder builder) {
        this.name = builder.getName();
        this.code = builder.getCode();
        this.sortCode = builder.getSortCode();
        this.encryptionRequired = builder.isEncryptionRequired();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.active = builder.isActive();
        this.historyId = builder.getHistoryId();
        this.activeFromDate = builder.getActiveFromDate();
        this.activeToDate = builder.getActiveToDate();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getSortCode() {
        return this.sortCode;
    }

    @Override
    public boolean isEncryptionRequired() {
        return this.encryptionRequired;
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
     * A builder which can be used to construct {@link CodedAttribute} instances.  Enforces the constraints of the {@link CodedAttributeContract}.
     * 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, EntityExternalIdentifierTypeHistoryContract
    {

        private String name;
        private String code;
        private String sortCode;
        private boolean encryptionRequired;
        private Long versionNumber;
        private String objectId;
        private boolean active;
        private Long historyId;
        private DateTime activeFromDate;
        private DateTime activeToDate;

        private Builder(String code) {
            setCode(code);
        }

        public static Builder create(String code) {
            return new Builder(code);
        }

        public static Builder create(EntityExternalIdentifierTypeHistoryContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create(contract.getCode());
            builder.setName(contract.getName());
            builder.setSortCode(contract.getSortCode());
            builder.setEncryptionRequired(contract.isEncryptionRequired());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setActive(contract.isActive());
            builder.setHistoryId(contract.getHistoryId());
            builder.setActiveFromDate(contract.getActiveFromDate());
            builder.setActiveToDate(contract.getActiveToDate());
            return builder;
        }

        public EntityExternalIdentifierTypeHistory build() {
            return new EntityExternalIdentifierTypeHistory(this);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getCode() {
            return this.code;
        }

        @Override
        public String getSortCode() {
            return this.sortCode;
        }

        @Override
        public boolean isEncryptionRequired() {
            return this.encryptionRequired;
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

        public void setName(String name) {
            this.name = name;
        }

        public void setCode(String code) {
            if (StringUtils.isWhitespace(code)) {
                throw new IllegalArgumentException("code is empty");
            }
            this.code = code;
        }

        public void setSortCode(String sortCode) {
            this.sortCode = sortCode;
        }

        public void setEncryptionRequired(boolean encryptionRequired) {
            this.encryptionRequired = encryptionRequired;
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

        final static String ROOT_ELEMENT_NAME = "entityExternalIdentifierType";
        final static String TYPE_NAME = "entityExternalIdentifierTypeType";
    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {

        final static String NAME = "name";
        final static String CODE = "code";
        final static String SORT_CODE = "sortCode";
        final static String ACTIVE = "active";
        final static String ENCRYPTION_REQUIRED = "encryptionRequired";

    }

}
