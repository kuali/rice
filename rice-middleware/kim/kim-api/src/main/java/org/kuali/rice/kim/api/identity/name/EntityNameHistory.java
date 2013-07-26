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
package org.kuali.rice.kim.api.identity.name;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.core.api.util.jaxb.DateTimeAdapter;
import org.kuali.rice.kim.api.KimConstants;
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

@XmlRootElement(name = EntityNameHistory.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityNameHistory.Constants.TYPE_NAME, propOrder = {
    EntityNameHistory.Elements.ID,
    EntityNameHistory.Elements.ENTITY_ID,
    EntityNameHistory.Elements.NAME_TYPE,
    EntityNameHistory.Elements.NAME_PREFIX,
    EntityNameHistory.Elements.NAME_TITLE,
    EntityNameHistory.Elements.FIRST_NAME,
    EntityNameHistory.Elements.MIDDLE_NAME,
    EntityNameHistory.Elements.LAST_NAME,
    EntityNameHistory.Elements.NAME_SUFFIX,
    EntityNameHistory.Elements.COMPOSITE_NAME,
    EntityNameHistory.Elements.NAME_PREFIX_UNMASKED,
    EntityNameHistory.Elements.NAME_TITLE_UNMASKED,
    EntityNameHistory.Elements.FIRST_NAME_UNMASKED,
    EntityNameHistory.Elements.MIDDLE_NAME_UNMASKED,
    EntityNameHistory.Elements.LAST_NAME_UNMASKED,
    EntityNameHistory.Elements.NAME_SUFFIX_UNMASKED,
    EntityNameHistory.Elements.COMPOSITE_NAME_UNMASKED,
    EntityNameHistory.Elements.NOTE_MESSAGE,
    EntityNameHistory.Elements.NAME_CHANGED_DATE,
    EntityNameHistory.Elements.SUPPRESS_NAME,
    EntityNameHistory.Elements.DEFAULT_VALUE,
    EntityNameHistory.Elements.ACTIVE,
    CoreConstants.CommonElements.HISTORY_ID,
    CoreConstants.CommonElements.ACTIVE_FROM_DATE,
    CoreConstants.CommonElements.ACTIVE_TO_DATE,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.OBJECT_ID,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityNameHistory extends AbstractDataTransferObject
    implements EntityNameHistoryContract
{

    @XmlElement(name = Elements.NAME_SUFFIX, required = false)
    private final String nameSuffix;
    @XmlElement(name = Elements.ENTITY_ID, required = false)
    private final String entityId;
    @XmlElement(name = Elements.NAME_TYPE, required = false)
    private final CodedAttributeHistory nameType;
    @XmlElement(name = Elements.FIRST_NAME, required = false)
    private final String firstName;
    @XmlElement(name = Elements.FIRST_NAME_UNMASKED, required = false)
    private final String firstNameUnmasked;
    @XmlElement(name = Elements.MIDDLE_NAME, required = false)
    private final String middleName;
    @XmlElement(name = Elements.MIDDLE_NAME_UNMASKED, required = false)
    private final String middleNameUnmasked;
    @XmlElement(name = Elements.LAST_NAME, required = false)
    private final String lastName;
    @XmlElement(name = Elements.LAST_NAME_UNMASKED, required = false)
    private final String lastNameUnmasked;
    @XmlElement(name = Elements.NAME_PREFIX, required = false)
    private final String namePrefix;
    @XmlElement(name = Elements.NAME_PREFIX_UNMASKED, required = false)
    private final String namePrefixUnmasked;
    @XmlElement(name = Elements.NAME_TITLE, required = false)
    private final String nameTitle;
    @XmlElement(name = Elements.NAME_TITLE_UNMASKED, required = false)
    private final String nameTitleUnmasked;
    @XmlElement(name = Elements.NAME_SUFFIX_UNMASKED, required = false)
    private final String nameSuffixUnmasked;
    @XmlElement(name = Elements.COMPOSITE_NAME, required = false)
    private final String compositeName;
    @XmlElement(name = Elements.COMPOSITE_NAME_UNMASKED, required = false)
    private final String compositeNameUnmasked;
    @XmlElement(name = Elements.NOTE_MESSAGE, required = false)
    private final String noteMessage;
    @XmlElement(name = Elements.NAME_CHANGED_DATE, required = false)
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private final DateTime nameChangedDate;
    @XmlElement(name = Elements.SUPPRESS_NAME, required = false)
    private final boolean suppressName;
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
    @XmlElement(name = Elements.DEFAULT_VALUE, required = false)
    private final boolean defaultValue;
    @XmlElement(name = Elements.ACTIVE, required = false)
    private final boolean active;
    @XmlElement(name = Elements.ID, required = false)
    private final String id;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     *
     */
    private EntityNameHistory() {
        this.nameSuffix = null;
        this.entityId = null;
        this.nameType = null;
        this.firstName = null;
        this.firstNameUnmasked = null;
        this.middleName = null;
        this.middleNameUnmasked = null;
        this.lastName = null;
        this.lastNameUnmasked = null;
        this.namePrefix = null;
        this.namePrefixUnmasked = null;
        this.nameTitle = null;
        this.nameTitleUnmasked = null;
        this.nameSuffixUnmasked = null;
        this.compositeName = null;
        this.compositeNameUnmasked = null;
        this.noteMessage = null;
        this.nameChangedDate = null;
        this.suppressName = false;
        this.versionNumber = null;
        this.objectId = null;
        this.defaultValue = false;
        this.active = false;
        this.id = null;
        this.historyId = null;
        this.activeFromDate = null;
        this.activeToDate = null;
    }

    private EntityNameHistory(Builder builder) {
        this.nameSuffix = builder.getNameSuffix();
        this.entityId = builder.getEntityId();
        this.nameType = builder.getNameType() != null ? builder.getNameType().build() : null;
        this.firstName = builder.getFirstName();
        this.firstNameUnmasked = builder.getFirstNameUnmasked();
        this.middleName = builder.getMiddleName();
        this.middleNameUnmasked = builder.getMiddleNameUnmasked();
        this.lastName = builder.getLastName();
        this.lastNameUnmasked = builder.getLastNameUnmasked();
        this.namePrefix = builder.getNamePrefix();
        this.namePrefixUnmasked = builder.getNamePrefixUnmasked();
        this.nameTitle = builder.getNameTitle();
        this.nameTitleUnmasked = builder.getNameTitleUnmasked();
        this.nameSuffixUnmasked = builder.getNameSuffixUnmasked();
        this.compositeName = builder.getCompositeName();
        this.compositeNameUnmasked = builder.getCompositeNameUnmasked();
        this.noteMessage = builder.getNoteMessage();
        this.nameChangedDate = builder.getNameChangedDate();
        this.suppressName = builder.isSuppressName();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.defaultValue = builder.isDefaultValue();
        this.active = builder.isActive();
        this.id = builder.getId();
        this.historyId = builder.getHistoryId();
        this.activeFromDate = builder.getActiveFromDate();
        this.activeToDate = builder.getActiveToDate();
    }

    @Override
    public String getNameSuffix() {
        return this.nameSuffix;
    }

    @Override
    public String getEntityId() {
        return this.entityId;
    }

    @Override
    public CodedAttributeHistory getNameType() {
        return this.nameType;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public String getFirstNameUnmasked() {
        return this.firstNameUnmasked;
    }

    @Override
    public String getMiddleName() {
        return this.middleName;
    }

    @Override
    public String getMiddleNameUnmasked() {
        return this.middleNameUnmasked;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public String getLastNameUnmasked() {
        return this.lastNameUnmasked;
    }

    @Override
    public String getNamePrefix() {
        return this.namePrefix;
    }

    @Override
    public String getNamePrefixUnmasked() {
        return this.namePrefixUnmasked;
    }

    @Override
    public String getNameTitle() {
        return this.nameTitle;
    }

    @Override
    public String getNameTitleUnmasked() {
        return this.nameTitleUnmasked;
    }

    @Override
    public String getNameSuffixUnmasked() {
        return this.nameSuffixUnmasked;
    }

    @Override
    public String getCompositeName() {
        return this.compositeName;
    }

    @Override
    public String getCompositeNameUnmasked() {
        return this.compositeNameUnmasked;
    }

    @Override
    public String getNoteMessage() {
        return this.noteMessage;
    }

    @Override
    public DateTime getNameChangedDate() {
        return this.nameChangedDate;
    }

    @Override
    public boolean isSuppressName() {
        return this.suppressName;
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
    public boolean isDefaultValue() {
        return this.defaultValue;
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
     * A builder which can be used to construct {@link org.kuali.rice.kim.api.identity.name.EntityNameHistory} instances.  Enforces the constraints of the {@link org.kuali.rice.kim.api.identity.name.EntityNameContract}.
     * 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, EntityNameHistoryContract
    {

        private String nameSuffix;
        private String entityId;
        private CodedAttributeHistory.Builder nameType;
        private String firstName;
        private String middleName;
        private String lastName;
        private String namePrefix;
        private String nameTitle;
        private String compositeName;
        private String noteMessage;
        private DateTime nameChangedDate;
        private boolean suppressName;
        private Long versionNumber;
        private String objectId;
        private boolean defaultValue;
        private boolean active;
        private String id;
        private Long historyId;
        private DateTime activeFromDate;
        private DateTime activeToDate;

        private Builder() { }

        private Builder(String id, String entityId, String firstName, String lastName, boolean suppressName) {
            setId(id);
            setEntityId(entityId);
            setFirstName(firstName);
            setLastName(lastName);
            setSuppressName(suppressName);
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(String id, String entityId, String firstName,
        						     String lastName, boolean suppressName) {
			return new Builder(id, entityId, firstName, lastName, suppressName);
        }

        public static Builder create(EntityNameHistoryContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setNameSuffix(contract.getNameSuffix());
            builder.setEntityId(contract.getEntityId());
            if (contract.getNameType() != null) {
                builder.setNameType(CodedAttributeHistory.Builder.create(contract.getNameType()));
            }
            builder.setFirstName(contract.getFirstName());
            builder.setMiddleName(contract.getMiddleName());
            builder.setLastName(contract.getLastName());
            builder.setNamePrefix(contract.getNamePrefix());
            builder.setNameTitle(contract.getNameTitle());
            builder.setNoteMessage(contract.getNoteMessage());
            builder.setNameChangedDate(contract.getNameChangedDate());
            builder.setSuppressName(contract.isSuppressName());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setDefaultValue(contract.isDefaultValue());
            builder.setActive(contract.isActive());
            builder.setId(contract.getId());
            builder.setCompositeName(contract.getCompositeName());
            builder.setHistoryId(contract.getHistoryId());
            builder.setActiveFromDate(contract.getActiveFromDate());
            builder.setActiveToDate(contract.getActiveToDate());
            return builder;
        }

        public EntityNameHistory build() {
            return new EntityNameHistory(this);
        }

        @Override
        public String getNameSuffix() {
            if (isSuppressName()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.nameSuffix;
        }

        @Override
        public String getEntityId() {
            return this.entityId;
        }

        @Override
        public CodedAttributeHistory.Builder getNameType() {
            return this.nameType;
        }

        @Override
        public String getFirstName() {
            if (isSuppressName()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.firstName;
        }

        @Override
        public String getFirstNameUnmasked() {
            return this.firstName;
        }

        @Override
        public String getMiddleName() {
            if (isSuppressName()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.middleName;
        }

        @Override
        public String getMiddleNameUnmasked() {
            return this.middleName;
        }

        @Override
        public String getLastName() {
            if (isSuppressName()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.lastName;
        }

        @Override
        public String getLastNameUnmasked() {
            return this.lastName;
        }

        @Override
        public String getNamePrefix() {
            if (isSuppressName()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.namePrefix;
        }

        @Override
        public String getNamePrefixUnmasked() {
            return this.namePrefix;
        }

        @Override
        public String getNameTitle() {
            if (isSuppressName()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.nameTitle;
        }

        @Override
        public String getNameTitleUnmasked() {
            return this.nameTitle;
        }

        @Override
        public String getNameSuffixUnmasked() {
            return this.nameSuffix;
        }

        @Override
        public String getCompositeName() {
            if (isSuppressName()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return getCompositeNameUnmasked();
        }

        @Override
        public String getCompositeNameUnmasked() {
        	if(this.compositeName == null) {
        		setCompositeName((getLastName() + ", " + getFirstName() + (getMiddleName()==null?"":" " + getMiddleName())).trim());
        	}
            return this.compositeName;
        }

        @Override
        public String getNoteMessage() {
            return this.noteMessage;
        }

        @Override
        public DateTime getNameChangedDate() {
            return this.nameChangedDate;
        }

        @Override
        public boolean isSuppressName() {
            return this.suppressName;
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
        public boolean isDefaultValue() {
            return this.defaultValue;
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

        @Override
        public String getId() {
            return this.id;
        }

        public void setNameSuffix(String nameSuffix) {
            this.nameSuffix = nameSuffix;
        }

        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }

        public void setNameType(CodedAttributeHistory.Builder nameType) {
            this.nameType = nameType;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setMiddleName(String middleName) {

            this.middleName = middleName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public void setNamePrefix(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        public void setNameTitle(String nameTitle) {
            this.nameTitle = nameTitle;
        }

        public void setCompositeName(String compositeName) {
        	this.compositeName = compositeName;
        }
        
        public void setNoteMessage(String noteMessage) {
            this.noteMessage = noteMessage;
        }

        public void setNameChangedDate(DateTime nameChangedDate) {
            this.nameChangedDate = nameChangedDate;
        }

        private void setSuppressName(boolean suppressName) {
            this.suppressName = suppressName;
        }

        public void setVersionNumber(Long versionNumber) {
            this.versionNumber = versionNumber;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public void setDefaultValue(boolean defaultValue) {
            this.defaultValue = defaultValue;
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

    }


    /**
     * Defines some internal constants used on this class.
     * 
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "entityName";
        final static String TYPE_NAME = "EntityNameType";
    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {

        final static String NAME_SUFFIX = "nameSuffix";
        final static String ENTITY_ID = "entityId";
        final static String NAME_TYPE = "nameType";
        final static String FIRST_NAME = "firstName";
        final static String FIRST_NAME_UNMASKED = "firstNameUnmasked";
        final static String MIDDLE_NAME = "middleName";
        final static String MIDDLE_NAME_UNMASKED = "middleNameUnmasked";
        final static String LAST_NAME = "lastName";
        final static String LAST_NAME_UNMASKED = "lastNameUnmasked";
        final static String NAME_PREFIX = "namePrefix";
        final static String NAME_PREFIX_UNMASKED = "namePrefixUnmasked";
        final static String NAME_TITLE = "nameTitle";
        final static String NAME_TITLE_UNMASKED = "nameTitleUnmasked";
        final static String NAME_SUFFIX_UNMASKED = "nameSuffixUnmasked";
        final static String COMPOSITE_NAME = "compositeName";
        final static String COMPOSITE_NAME_UNMASKED = "compositeNameUnmasked";
        final static String NOTE_MESSAGE = "noteMessage";
        final static String NAME_CHANGED_DATE= "nameChangedDate";
        final static String SUPPRESS_NAME = "suppressName";
        final static String DEFAULT_VALUE = "defaultValue";
        final static String ACTIVE = "active";
        final static String ID = "id";

    }

}
