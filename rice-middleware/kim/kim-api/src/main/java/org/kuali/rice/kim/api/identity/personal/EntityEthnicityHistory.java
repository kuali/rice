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
package org.kuali.rice.kim.api.identity.personal;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.core.api.util.jaxb.DateTimeAdapter;
import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.CodedAttributeContract;
import org.kuali.rice.kim.api.identity.CodedAttributeHistory;
import org.kuali.rice.kim.api.identity.CodedAttributeHistoryContract;
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
import java.util.Collections;
import java.util.List;

@XmlRootElement(name = EntityEthnicityHistory.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityEthnicityHistory.Constants.TYPE_NAME, propOrder = {
    EntityEthnicityHistory.Elements.ID,
    EntityEthnicityHistory.Elements.ENTITY_ID,
    EntityEthnicityHistory.Elements.ETHNICITY_CODE,
    EntityEthnicityHistory.Elements.ETHNICITY_CODE_UNMASKED,
    EntityEthnicityHistory.Elements.SUB_ETHNICITY_CODE,
    EntityEthnicityHistory.Elements.SUB_ETHNICITY_CODE_UNMASKED,
    EntityEthnicityHistory.Elements.SUPPRESS_PERSONAL,
    EntityEthnicityHistory.Elements.HISPANIC_OR_LATINO,
    EntityEthnicityHistory.Elements.RACE_ETHNICITY_CODE,
    EntityEthnicityHistory.Elements.RACE_ETHNICITY_CODE_UNMASKED,
    EntityEthnicityHistory.Elements.LOCAL_RACE_ETHNICITY_CODE,
    EntityEthnicityHistory.Elements.LOCAL_RACE_ETHNICITY_CODE_UNMASKED,
    CoreConstants.CommonElements.HISTORY_ID,
    CoreConstants.CommonElements.ACTIVE_FROM_DATE,
    CoreConstants.CommonElements.ACTIVE_TO_DATE,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.OBJECT_ID,
    EntityEthnicityHistory.Elements.PERCENTAGE,
    EntityEthnicityHistory.Elements.PERCENTAGE_UNMASKED,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityEthnicityHistory extends AbstractDataTransferObject
    implements EntityEthnicityHistoryContract
{

    @XmlElement(name = Elements.ENTITY_ID, required = false)
    private final String entityId;
    @XmlElement(name = Elements.ETHNICITY_CODE, required = false)
    private final String ethnicityCode;
    @XmlElement(name = Elements.ETHNICITY_CODE_UNMASKED, required = false)
    private final String ethnicityCodeUnmasked;
    @XmlElement(name = Elements.SUB_ETHNICITY_CODE, required = false)
    private final String subEthnicityCode;
    @XmlElement(name = Elements.SUB_ETHNICITY_CODE_UNMASKED, required = false)
    private final String subEthnicityCodeUnmasked;
    @XmlElement(name = Elements.SUPPRESS_PERSONAL, required = false)
    private final boolean suppressPersonal;
    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
    @XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
    private final String objectId;
    @XmlElement(name = Elements.ID, required = false)
    private final String id;
    @XmlElement(name = Elements.HISPANIC_OR_LATINO, required = false)
    private final boolean hispanicOrLatino;
    @XmlElementWrapper(name = Elements.RACE_ETHNICITY_CODE, required = false)
    @XmlElement(name = Elements.RACE_ETHNICITY_CODE, required = false)
    private final CodedAttributeHistory raceEthnicityCode;
    @XmlElement(name = Elements.RACE_ETHNICITY_CODE_UNMASKED, required = false)
    private final CodedAttributeHistory raceEthnicityCodeUnmasked;
    @XmlElement(name = Elements.LOCAL_RACE_ETHNICITY_CODE, required = false)
    private final String localRaceEthnicityCode;
    @XmlElement(name = Elements.LOCAL_RACE_ETHNICITY_CODE_UNMASKED, required = false)
    private final String localRaceEthnicityCodeUnmasked;
    @XmlElement(name = CoreConstants.CommonElements.HISTORY_ID, required = false)
    private final Long historyId;
    @XmlElement(name = CoreConstants.CommonElements.ACTIVE_FROM_DATE, required = false)
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private final DateTime activeFromDate;
    @XmlElement(name = CoreConstants.CommonElements.ACTIVE_TO_DATE, required = false)
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private final DateTime activeToDate;
    @XmlElement(name = Elements.PERCENTAGE, required = false)
    private final Double percentage;
    @XmlElement(name = Elements.PERCENTAGE_UNMASKED, required = false)
    private final Double percentageUnmasked;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     *
     */
    private EntityEthnicityHistory() {
        this.entityId = null;
        this.ethnicityCode = null;
        this.ethnicityCodeUnmasked = null;
        this.subEthnicityCode = null;
        this.subEthnicityCodeUnmasked = null;
        this.suppressPersonal = false;
        this.versionNumber = null;
        this.objectId = null;
        this.id = null;
        this.hispanicOrLatino = false;
        this.raceEthnicityCode = null;
        this.raceEthnicityCodeUnmasked = null;
        this.localRaceEthnicityCode = null;
        this.localRaceEthnicityCodeUnmasked = null;
        this.historyId = null;
        this.activeFromDate = null;
        this.activeToDate = null;
        this.percentage = null;
        this.percentageUnmasked = null;
    }

    private EntityEthnicityHistory(Builder builder) {
        this.entityId = builder.getEntityId();
        this.ethnicityCode = builder.getEthnicityCode();
        this.ethnicityCodeUnmasked = builder.getEthnicityCodeUnmasked();
        this.subEthnicityCode = builder.getSubEthnicityCode();
        this.subEthnicityCodeUnmasked = builder.getSubEthnicityCodeUnmasked();
        this.suppressPersonal = builder.isSuppressPersonal();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.id = builder.getId();
        this.hispanicOrLatino = builder.isHispanicOrLatino();
        this.raceEthnicityCode = builder.getRaceEthnicityCode() != null ? builder.getRaceEthnicityCode().build() : null;
        this.raceEthnicityCodeUnmasked = builder.getRaceEthnicityCodeUnmasked() != null ? builder.getRaceEthnicityCodeUnmasked().build() : null;
        this.localRaceEthnicityCode = builder.getLocalRaceEthnicityCode();
        this.localRaceEthnicityCodeUnmasked = builder.getLocalRaceEthnicityCodeUnmasked();
        this.historyId = builder.getHistoryId();
        this.activeFromDate = builder.getActiveFromDate();
        this.activeToDate = builder.getActiveToDate();
        this.percentage = builder.getPercentage();
        this.percentageUnmasked = builder.getPercentageUnmasked();
    }

    @Override
    public String getEntityId() {
        return this.entityId;
    }

    @Override
    public String getEthnicityCode() {
        return this.ethnicityCode;
    }

    @Override
    public String getEthnicityCodeUnmasked() {
        return this.ethnicityCodeUnmasked;
    }

    @Override
    public String getSubEthnicityCode() {
        return this.subEthnicityCode;
    }

    @Override
    public String getSubEthnicityCodeUnmasked() {
        return this.subEthnicityCodeUnmasked;
    }

    @Override
    public boolean isSuppressPersonal() {
        return this.suppressPersonal;
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
    public boolean isHispanicOrLatino() {
        return this.hispanicOrLatino;
    }

    @Override
    public CodedAttributeHistory getRaceEthnicityCode() {
        return this.raceEthnicityCode;
    }

    @Override
    public CodedAttributeHistory getRaceEthnicityCodeUnmasked() {
        return this.raceEthnicityCodeUnmasked;
    }

    @Override
    public String getLocalRaceEthnicityCode() {
        return this.localRaceEthnicityCode;
    }

    @Override
    public String getLocalRaceEthnicityCodeUnmasked() {
        return this.localRaceEthnicityCodeUnmasked;
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

    @Override
    public Double getPercentage() {
        return this.percentage;
    }

    @Override
    public Double getPercentageUnmasked() {
        return this.percentageUnmasked;
    }


    /**
     * A builder which can be used to construct {@link org.kuali.rice.kim.api.identity.personal.EntityEthnicityHistory} instances.  Enforces the constraints of the {@link org.kuali.rice.kim.api.identity.personal.EntityEthnicityContract}.
     * 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, EntityEthnicityHistoryContract
    {

        private String entityId;
        private String ethnicityCode;
        private String subEthnicityCode;
        private boolean suppressPersonal;
        private Long versionNumber;
        private String objectId;
        private String id;
        private boolean hispanicOrLatino;
        private CodedAttributeHistory.Builder raceEthnicityCode;
        private String localRaceEthnicityCode;
        private Long historyId;
        private DateTime activeFromDate;
        private DateTime activeToDate;
        private Double percentage;

        private Builder() { }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(EntityEthnicityHistoryContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setEntityId(contract.getEntityId());
            builder.setEthnicityCode(contract.getEthnicityCodeUnmasked());
            builder.setSubEthnicityCode(contract.getSubEthnicityCodeUnmasked());
            builder.setSuppressPersonal(contract.isSuppressPersonal());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setId(contract.getId());
            builder.setHispanicOrLatino(contract.isHispanicOrLatino());
            if (contract.getRaceEthnicityCode() != null) {
                builder.setRaceEthnicityCodes(CodedAttributeHistory.Builder.create(contract.getRaceEthnicityCode()));
            }
            builder.setLocalRaceEthnicityCode(contract.getLocalRaceEthnicityCodeUnmasked());
            builder.setHistoryId(contract.getHistoryId());
            builder.setActiveFromDate(contract.getActiveFromDate());
            builder.setActiveToDate(contract.getActiveToDate());
            builder.setPercentage(contract.getPercentageUnmasked());
            return builder;
        }

        @Override
        public EntityEthnicityHistory build() {
            return new EntityEthnicityHistory(this);
        }

        @Override
        public String getEntityId() {
            return this.entityId;
        }

        @Override
        public String getEthnicityCode() {
            if (isSuppressPersonal()) {
                return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
            }
            return this.ethnicityCode;
        }

        @Override
        public String getEthnicityCodeUnmasked() {
            return this.ethnicityCode;
        }

        @Override
        public String getSubEthnicityCode() {
            if (isSuppressPersonal()) {
                return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
            }
            return this.subEthnicityCode;
        }

        @Override
        public String getSubEthnicityCodeUnmasked() {
            return this.subEthnicityCode;
        }

        @Override
        public boolean isSuppressPersonal() {
            return this.suppressPersonal;
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
        public boolean isHispanicOrLatino() {
            return this.hispanicOrLatino;
        }

        @Override
        public CodedAttributeHistory.Builder getRaceEthnicityCode() {
            if (isSuppressPersonal()) {
                return null;
            }
            return this.raceEthnicityCode;
        }

        @Override
        public CodedAttributeHistory.Builder getRaceEthnicityCodeUnmasked() {
            return this.raceEthnicityCode;
        }

        @Override
        public String getLocalRaceEthnicityCode() {
            if (isSuppressPersonal()) {
                return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
            }
            return this.localRaceEthnicityCode;
        }

        @Override
        public String getLocalRaceEthnicityCodeUnmasked() {
            return this.localRaceEthnicityCode;
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
        public boolean isActive() {
            return isActiveNow();
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
        public DateTime getActiveToDate() {
            return this.activeToDate;
        }

        @Override
        public Double getPercentage() {
            if (isSuppressPersonal()) {
                return null;
            }
            return this.percentage;
        }

        @Override
        public Double getPercentageUnmasked() {
            return this.percentage;
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

        public void setHispanicOrLatino(boolean hispanicOrLatino) {
            this.hispanicOrLatino = hispanicOrLatino;
        }

        public void setRaceEthnicityCodes(CodedAttributeHistory.Builder raceEthnicityCode) {
            this.raceEthnicityCode = raceEthnicityCode;
        }

        public void setLocalRaceEthnicityCode(String localRaceEthnicityCode) {
            this.localRaceEthnicityCode = localRaceEthnicityCode;
        }

        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }

        public void setEthnicityCode(String ethnicityCode) {
            this.ethnicityCode = ethnicityCode;
        }

        public void setSubEthnicityCode(String subEthnicityCode) {
            this.subEthnicityCode = subEthnicityCode;
        }

        private void setSuppressPersonal(boolean suppressPersonal) {
            this.suppressPersonal = suppressPersonal;
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

        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }

    }


    /**
     * Defines some internal constants used on this class.
     * 
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "entityEthnicity";
        final static String TYPE_NAME = "EntityEthnicityType";
    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {

        final static String ENTITY_ID = "entityId";
        final static String ETHNICITY_CODE = "ethnicityCode";
        final static String ETHNICITY_CODE_UNMASKED = "ethnicityCodeUnmasked";
        final static String SUB_ETHNICITY_CODE = "subEthnicityCode";
        final static String SUB_ETHNICITY_CODE_UNMASKED = "subEthnicityCodeUnmasked";
        final static String SUPPRESS_PERSONAL = "suppressPersonal";
        final static String ID = "id";
        final static String HISPANIC_OR_LATINO = "hispanicOrLatino";
        final static String RACE_ETHNICITY_CODE_UNMASKED = "raceEthnicityCodeUnmasked";
        final static String RACE_ETHNICITY_CODE = "raceEthnicityCode";
        final static String LOCAL_RACE_ETHNICITY_CODE = "localRaceEthnicityCode";
        final static String LOCAL_RACE_ETHNICITY_CODE_UNMASKED = "localRaceEthnicityCodeUnmasked";
        final static String PERCENTAGE = "percentage";
        final static String PERCENTAGE_UNMASKED = "percentageUnmasked";
    }

}