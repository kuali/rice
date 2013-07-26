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
package org.kuali.rice.kim.api.group;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.core.api.util.jaxb.DateTimeAdapter;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.kim.api.KimConstants;
import org.w3c.dom.Element;

@XmlRootElement(name = GroupHistory.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = GroupHistory.Constants.TYPE_NAME, propOrder = {
        GroupHistory.Elements.NAME,
        GroupHistory.Elements.ATTRIBUTES,
        GroupHistory.Elements.NAMESPACE_CODE,
        GroupHistory.Elements.DESCRIPTION,
        GroupHistory.Elements.KIM_TYPE_ID,
        GroupHistory.Elements.ACTIVE,
        GroupHistory.Elements.ID,
        CoreConstants.CommonElements.HISTORY_ID,
        CoreConstants.CommonElements.ACTIVE_FROM_DATE,
        CoreConstants.CommonElements.ACTIVE_TO_DATE,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.OBJECT_ID,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class GroupHistory
        extends AbstractDataTransferObject
        implements GroupHistoryContract
{

    @XmlElement(name = Elements.NAME, required = false)
    private final String name;
    @XmlElement(name = Elements.ATTRIBUTES, required = false)
    @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    private final Map<String, String> attributes;
    @XmlElement(name = Elements.NAMESPACE_CODE, required = false)
    private final String namespaceCode;
    @XmlElement(name = Elements.DESCRIPTION, required = false)
    private final String description;
    @XmlElement(name = Elements.KIM_TYPE_ID, required = false)
    private final String kimTypeId;
    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
    @XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
    private final String objectId;
    @XmlElement(name = Elements.ACTIVE, required = false)
    private final boolean active;
    @XmlElement(name = Elements.ID, required = false)
    private final String id;
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
    private GroupHistory() {
        this.name = null;
        this.attributes = null;
        this.namespaceCode = null;
        this.description = null;
        this.kimTypeId = null;
        this.versionNumber = null;
        this.objectId = null;
        this.active = false;
        this.id = null;
        this.historyId = null;
        this.activeFromDate = null;
        this.activeToDate = null;
    }

    private GroupHistory(Builder builder) {
        this.name = builder.getName();
        this.attributes = builder.getAttributes() != null ? builder.getAttributes() : Collections.<String, String>emptyMap();
        this.namespaceCode = builder.getNamespaceCode();
        this.description = builder.getDescription();
        this.kimTypeId = builder.getKimTypeId();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.active = builder.isActive();
        this.id = builder.getId();
        this.historyId = builder.getHistoryId();
        this.activeFromDate = builder.getActiveFromDate();
        this.activeToDate = builder.getActiveToDate();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getNamespaceCode() {
        return this.namespaceCode;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getKimTypeId() {
        return this.kimTypeId;
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
    public boolean isActiveNow() {
        return isActive() && InactivatableFromToUtils.isActive(activeFromDate, activeToDate, null);
    }

    @Override
    public boolean isActive(DateTime activeAsOf) {
        return isActive() && InactivatableFromToUtils.isActive(activeFromDate, activeToDate, activeAsOf);
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


    /**
     * A builder which can be used to construct {@link GroupHistory} instances.  Enforces the constraints of the {@link GroupHistoryContract}.
     *
     */
    public final static class Builder
            implements Serializable, ModelBuilder, GroupHistoryContract
    {

        private String name;
        private Map<String, String> attributes;
        private String namespaceCode;
        private String description;
        private String kimTypeId;
        private Long versionNumber;
        private String objectId;
        private boolean active;
        private String id;
        private Long historyId;
        private DateTime activeFromDate;
        private DateTime activeToDate;

        private Builder(String namespaceCode, String name, String kimTypeId) {
            setNamespaceCode(namespaceCode);
            setName(name);
            setKimTypeId(kimTypeId);
        }

        /**
         * creates a Group with the required fields.
         */
        public static Builder create(String namespaceCode, String name, String kimTypeId) {
            return new Builder(namespaceCode, name, kimTypeId);
        }

        public static Builder create(GroupContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = new Builder(contract.getNamespaceCode(), contract.getName(), contract.getKimTypeId());
            builder.setAttributes(contract.getAttributes());
            builder.setDescription(contract.getDescription());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setActive(contract.isActive());
            builder.setId(contract.getId());
            return builder;
        }

        public static Builder create(GroupHistoryContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = new Builder(contract.getNamespaceCode(), contract.getName(), contract.getKimTypeId());
            builder.setAttributes(contract.getAttributes());
            builder.setDescription(contract.getDescription());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setActive(contract.isActive());
            builder.setId(contract.getId());
            builder.setHistoryId(contract.getHistoryId());
            builder.setActiveFromDate(contract.getActiveFromDate());
            builder.setActiveToDate(contract.getActiveToDate());
            return builder;
        }

        public GroupHistory build() {
            return new GroupHistory(this);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Map<String, String> getAttributes() {
            return this.attributes;
        }

        @Override
        public String getNamespaceCode() {
            return this.namespaceCode;
        }

        @Override
        public String getDescription() {
            return this.description;
        }

        @Override
        public String getKimTypeId() {
            return this.kimTypeId;
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

        public void setName(String name) {
            if (StringUtils.isEmpty(name)) {
                throw new IllegalArgumentException("name is empty");
            }
            this.name = name;
        }

        public void setAttributes(Map<String, String> attributes) {
            this.attributes = attributes;
        }

        public void setNamespaceCode(String namespaceCode) {
            if (StringUtils.isEmpty(namespaceCode)) {
                throw new IllegalArgumentException("namespaceCode is empty");
            }
            this.namespaceCode = namespaceCode;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setKimTypeId(String kimTypeId) {
            if (StringUtils.isEmpty(kimTypeId)) {
                throw new IllegalArgumentException("kimTypeId is empty");
            }
            this.kimTypeId = kimTypeId;
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

        public void setHistoryId(Long historyId) {
            this.historyId = historyId;
        }

        public void setActiveFromDate(DateTime activeFromDate) {
            this.activeFromDate = activeFromDate;
        }

        public void setActiveToDate(DateTime activeToDate) {
            this.activeToDate = activeToDate;
        }

    }


    /**
     * Defines some internal constants used on this class.
     *
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "groupHistory";
        final static String TYPE_NAME = "GroupHistoryType";

    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     *
     */
    static class Elements {

        final static String NAME = "name";
        final static String ATTRIBUTES = "attributes";
        final static String NAMESPACE_CODE = "namespaceCode";
        final static String DESCRIPTION = "description";
        final static String KIM_TYPE_ID = "kimTypeId";
        final static String ACTIVE = "active";
        final static String ID = "id";
    }

    public static class Cache {
        public static final String NAME = KimConstants.Namespaces.KIM_NAMESPACE_2_0 + "/" + GroupHistory.Constants.TYPE_NAME;
    }
}
