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
package org.kuali.rice.kim.api.identity.residency;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.util.jaxb.DateTimeAdapter;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.CodedAttributeContract;
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

@XmlRootElement(name = EntityResidency.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityResidency.Constants.TYPE_NAME, propOrder = {
    EntityResidency.Elements.ID,
    EntityResidency.Elements.ENTITY_ID,
    EntityResidency.Elements.DETERMINATION_METHOD,
    EntityResidency.Elements.IN_STATE,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.OBJECT_ID,
    EntityResidency.Elements.ESTABLISHED_DATE,
    EntityResidency.Elements.CHANGE_DATE,
    EntityResidency.Elements.COUNTRY_CODE,
    EntityResidency.Elements.COUNTY_CODE,
    EntityResidency.Elements.STATE_PROVINCE_CODE,
    EntityResidency.Elements.RESIDENCY_STATUS,
    EntityResidency.Elements.RESIDENCY_TYPE,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityResidency extends AbstractDataTransferObject
    implements EntityResidencyContract
{

    @XmlElement(name = Elements.ENTITY_ID, required = false)
    private final String entityId;
    @XmlElement(name = Elements.DETERMINATION_METHOD, required = false)
    private final String determinationMethod;
    @XmlElement(name = Elements.IN_STATE, required = false)
    private final String inState;
    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
    @XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
    private final String objectId;
    @XmlElement(name = Elements.ID, required = false)
    private final String id;
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlElement(name = Elements.ESTABLISHED_DATE, required = false)
    private final DateTime establishedDate;
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlElement(name = Elements.CHANGE_DATE, required = false)
    private final DateTime changeDate;
    @XmlElement(name = Elements.COUNTRY_CODE, required = false)
    private final String countryCode;
    @XmlElement(name = Elements.COUNTY_CODE, required = false)
    private final String countyCode;
    @XmlElement(name = Elements.STATE_PROVINCE_CODE, required = false)
    private final String stateProvinceCode;
    @XmlElement(name = Elements.RESIDENCY_STATUS, required = false)
    private final CodedAttribute residencyStatus;
    @XmlElement(name = Elements.RESIDENCY_TYPE, required = false)
    private final CodedAttribute residencyType;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     * 
     */
    private EntityResidency() {
        this.entityId = null;
        this.determinationMethod = null;
        this.inState = null;
        this.versionNumber = null;
        this.objectId = null;
        this.id = null;
        this.establishedDate = null;
        this.changeDate = null;
        this.countryCode = null;
        this.countyCode = null;
        this.stateProvinceCode = null;
        this.residencyStatus = null;
        this.residencyType = null;
    }

    private EntityResidency(Builder builder) {
        this.entityId = builder.getEntityId();
        this.determinationMethod = builder.getDeterminationMethod();
        this.inState = builder.getInState();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.id = builder.getId();
        this.establishedDate = builder.getEstablishedDate();
        this.changeDate = builder.getChangeDate();
        this.countryCode = builder.getCountryCode();
        this.countyCode = builder.getCountyCode();
        this.stateProvinceCode = builder.getStateProvinceCode();
        this.residencyStatus = builder.getResidencyStatus() == null ? null : builder.getResidencyStatus().build();
        this.residencyType = builder.getResidencyType() == null ? null : builder.getResidencyType().build();
    }

    @Override
    public String getEntityId() {
        return this.entityId;
    }

    @Override
    public String getDeterminationMethod() {
        return this.determinationMethod;
    }

    @Override
    public String getInState() {
        return this.inState;
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
    public DateTime getEstablishedDate() {
        return this.establishedDate;
    }

    @Override
    public DateTime getChangeDate() {
        return this.changeDate;
    }

    @Override
    public String getCountryCode() {
        return this.countryCode;
    }

    @Override
    public String getCountyCode() {
        return this.countyCode;
    }

    @Override
    public String getStateProvinceCode() {
        return this.stateProvinceCode;
    }

    @Override
    public CodedAttribute getResidencyStatus() {
        return this.residencyStatus;
    }

    @Override
    public CodedAttribute getResidencyType() {
        return this.residencyType;
    }



    /**
     * A builder which can be used to construct {@link EntityResidency} instances.  Enforces the constraints of the {@link EntityResidencyContract}.
     * 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, EntityResidencyContract
    {
        private DateTime establishedDate;
        private DateTime changeDate;
        private String countryCode;
        private String countyCode;
        private String stateProvinceCode;
        private CodedAttribute.Builder residencyStatus;
        private CodedAttribute.Builder residencyType;
        private String entityId;
        private String determinationMethod;
        private String inState;
        private Long versionNumber;
        private String objectId;
        private String id;

        private Builder() { }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(EntityResidencyContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setEntityId(contract.getEntityId());
            builder.setDeterminationMethod(contract.getDeterminationMethod());
            builder.setInState(contract.getInState());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setId(contract.getId());
            builder.setEstablishedDate(contract.getEstablishedDate());
            builder.setChangeDate(contract.getChangeDate());
            builder.setCountryCode(contract.getCountryCode());
            builder.setCountyCode(contract.getCountyCode());
            builder.setStateProvinceCode(contract.getStateProvinceCode());
            builder.setResidencyStatus(contract.getResidencyStatus() == null ? null : CodedAttribute.Builder.create(contract.getResidencyStatus()));
            builder.setResidencyType(contract.getResidencyType() == null ? null : CodedAttribute.Builder.create(contract.getResidencyType()));
            return builder;
        }

        public EntityResidency build() {
            return new EntityResidency(this);
        }

        @Override
        public String getEntityId() {
            return this.entityId;
        }

        @Override
        public String getDeterminationMethod() {
            return this.determinationMethod;
        }

        @Override
        public String getInState() {
            return this.inState;
        }

        @Override
        public DateTime getEstablishedDate() {
            return this.establishedDate;
        }

        @Override
        public DateTime getChangeDate() {
            return this.changeDate;
        }

        @Override
        public String getCountryCode() {
            return this.countryCode;
        }

        @Override
        public String getCountyCode() {
            return this.countyCode;
        }

        @Override
        public String getStateProvinceCode() {
            return this.stateProvinceCode;
        }

        @Override
        public CodedAttribute.Builder getResidencyStatus() {
            return this.residencyStatus;
        }

        @Override
        public CodedAttribute.Builder getResidencyType() {
            return this.residencyType;
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

        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }

        public void setDeterminationMethod(String determinationMethod) {
            this.determinationMethod = determinationMethod;
        }

        public void setInState(String inState) {
            this.inState = inState;
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

        public void setEstablishedDate(DateTime establishedDate) {
            this.establishedDate = establishedDate;
        }

        public void setChangeDate(DateTime changeDate) {
            this.changeDate = changeDate;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public void setCountyCode(String countyCode) {
            this.countyCode = countyCode;
        }

        public void setStateProvinceCode(String stateProvinceCode) {
            this.stateProvinceCode = stateProvinceCode;
        }

        public void setResidencyStatus(CodedAttribute.Builder residencyStatus) {
            this.residencyStatus = residencyStatus;
        }

        public void setResidencyType(CodedAttribute.Builder residencyType) {
            this.residencyType = residencyType;
        }

    }


    /**
     * Defines some internal constants used on this class.
     * 
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "entityResidency";
        final static String TYPE_NAME = "entityResidencyType";
    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {

        final static String ENTITY_ID = "entityId";
        final static String DETERMINATION_METHOD = "determinationMethod";
        final static String IN_STATE = "inState";
        final static String ID = "id";
        final static String ESTABLISHED_DATE = "establishedDate";
        final static String CHANGE_DATE = "changeDate";
        final static String COUNTRY_CODE = "countryCode";
        final static String COUNTY_CODE = "countyCode";
        final static String STATE_PROVINCE_CODE = "stateProvinceCode";
        final static String RESIDENCY_STATUS = "residencyStatus";
        final static String RESIDENCY_TYPE = "residencyType";

    }

}