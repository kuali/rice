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
package org.kuali.rice.kim.api.identity.employment;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.core.api.util.jaxb.DateTimeAdapter;
import org.kuali.rice.core.api.util.jaxb.KualiDecimalAdapter;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.CodedAttributeHistory;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationHistory;
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

@XmlRootElement(name = EntityEmploymentHistory.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityEmploymentHistory.Constants.TYPE_NAME, propOrder = {
    EntityEmploymentHistory.Elements.ID,
    EntityEmploymentHistory.Elements.ENTITY_ID,
    EntityEmploymentHistory.Elements.EMPLOYEE_ID,
    EntityEmploymentHistory.Elements.EMPLOYMENT_RECORD_ID,
    EntityEmploymentHistory.Elements.ENTITY_AFFILIATION,
    EntityEmploymentHistory.Elements.EMPLOYEE_STATUS,
    EntityEmploymentHistory.Elements.EMPLOYEE_TYPE,
    EntityEmploymentHistory.Elements.PRIMARY_DEPARTMENT_CODE,
    EntityEmploymentHistory.Elements.BASE_SALARY_AMOUNT,
    EntityEmploymentHistory.Elements.PRIMARY,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.OBJECT_ID,
    EntityEmploymentHistory.Elements.TENURED,
    EntityEmploymentHistory.Elements.ACTIVE,

    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityEmploymentHistory extends AbstractDataTransferObject
    implements EntityEmploymentHistoryContract
{
    @XmlElement(name = Elements.ID, required = false)
    private final String id;
    @XmlElement(name = Elements.ENTITY_ID, required = false)
    private final String entityId;
    @XmlElement(name = Elements.ENTITY_AFFILIATION, required = false)
    private final EntityAffiliationHistory entityAffiliation;
    @XmlElement(name = Elements.EMPLOYEE_STATUS, required = false)
    private final CodedAttributeHistory employeeStatus;
    @XmlElement(name = Elements.EMPLOYEE_TYPE, required = false)
    private final CodedAttributeHistory employeeType;
    @XmlElement(name = Elements.PRIMARY_DEPARTMENT_CODE, required = false)
    private final String primaryDepartmentCode;
    @XmlElement(name = Elements.EMPLOYEE_ID, required = false)
    private final String employeeId;
    @XmlElement(name = Elements.EMPLOYMENT_RECORD_ID, required = false)
    private final String employmentRecordId;
    @XmlElement(name = Elements.BASE_SALARY_AMOUNT, required = false)
    @XmlJavaTypeAdapter(KualiDecimalAdapter.class)
    private final KualiDecimal baseSalaryAmount;
    @XmlElement(name = Elements.PRIMARY, required = false)
    private final boolean primary;
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
    @XmlElement(name = Elements.TENURED, required = false)
    private final boolean tenured;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     *
     */
    private EntityEmploymentHistory() {
        this.entityAffiliation = null;
        this.employeeStatus = null;
        this.employeeType = null;
        this.primaryDepartmentCode = null;
        this.employeeId = null;
        this.employmentRecordId = null;
        this.baseSalaryAmount = null;
        this.primary = false;
        this.versionNumber = null;
        this.objectId = null;
        this.active = false;
        this.entityId = null;
        this.id = null;
        this.historyId = null;
        this.activeFromDate = null;
        this.activeToDate = null;
        this.tenured = false;
    }

    private EntityEmploymentHistory(Builder builder) {
        this.entityAffiliation = builder.getEntityAffiliation() != null ? builder.getEntityAffiliation().build() : null;
        this.employeeStatus = builder.getEmployeeStatus() != null ? builder.getEmployeeStatus().build() : null;
        this.employeeType = builder.getEmployeeType() != null ? builder.getEmployeeType().build() : null;
        this.primaryDepartmentCode = builder.getPrimaryDepartmentCode();
        this.employeeId = builder.getEmployeeId();
        this.employmentRecordId = builder.getEmploymentRecordId();
        this.baseSalaryAmount = builder.getBaseSalaryAmount();
        this.primary = builder.isPrimary();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.active = builder.isActive();
        this.id = builder.getId();
        this.entityId = builder.getEntityId();
        this.historyId = builder.getHistoryId();
        this.activeFromDate = builder.getActiveFromDate();
        this.activeToDate = builder.getActiveToDate();
        this.tenured = builder.isTenured();
    }

    @Override
    public String getEntityId() {
        return this.entityId;
    }
    @Override
    public EntityAffiliationHistory getEntityAffiliation() {
        return this.entityAffiliation;
    }

    @Override
    public CodedAttributeHistory getEmployeeStatus() {
        return this.employeeStatus;
    }

    @Override
    public CodedAttributeHistory getEmployeeType() {
        return this.employeeType;
    }

    @Override
    public String getPrimaryDepartmentCode() {
        return this.primaryDepartmentCode;
    }

    @Override
    public String getEmployeeId() {
        return this.employeeId;
    }

    @Override
    public String getEmploymentRecordId() {
        return this.employmentRecordId;
    }

    @Override
    public KualiDecimal getBaseSalaryAmount() {
        return this.baseSalaryAmount;
    }

    @Override
    public boolean isPrimary() {
        return this.primary;
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
    public boolean isTenured() {
        return this.tenured;
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
     * A builder which can be used to construct {@link org.kuali.rice.kim.api.identity.employment.EntityEmploymentHistory} instances.  Enforces the constraints of the {@link org.kuali.rice.kim.api.identity.employment.EntityEmploymentContract}.
     * 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, EntityEmploymentHistoryContract
    {
        private String entityId;
        private EntityAffiliationHistory.Builder entityAffiliation;
        private CodedAttributeHistory.Builder employeeStatus;
        private CodedAttributeHistory.Builder employeeType;
        private String primaryDepartmentCode;
        private String employeeId;
        private String employmentRecordId;
        private KualiDecimal baseSalaryAmount;
        private boolean primary;
        private Long versionNumber;
        private String objectId;
        private boolean active;
        private String id;
        private Long historyId;
        private DateTime activeFromDate;
        private DateTime activeToDate;
        private boolean tenured;

        private Builder() { }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(EntityEmploymentHistoryContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setEntityId(contract.getEntityId());
            if (contract.getEntityAffiliation() != null) {
                builder.setEntityAffiliation(EntityAffiliationHistory.Builder.create(contract.getEntityAffiliation()));
            }
            if (contract.getEmployeeStatus() != null) {
                builder.setEmployeeStatus(CodedAttributeHistory.Builder.create(contract.getEmployeeStatus()));
            }
            if (contract.getEmployeeType() != null) {
                builder.setEmployeeType(CodedAttributeHistory.Builder.create(contract.getEmployeeType()));
            }
            builder.setPrimaryDepartmentCode(contract.getPrimaryDepartmentCode());
            builder.setEmployeeId(contract.getEmployeeId());
            builder.setEmploymentRecordId(contract.getEmploymentRecordId());
            builder.setBaseSalaryAmount(contract.getBaseSalaryAmount());
            builder.setPrimary(contract.isPrimary());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setActive(contract.isActive());
            builder.setId(contract.getId());
            builder.setHistoryId(contract.getHistoryId());
            builder.setActiveFromDate(contract.getActiveFromDate());
            builder.setActiveToDate(contract.getActiveToDate());
            builder.setTenured(contract.isTenured());
            return builder;
        }

        public EntityEmploymentHistory build() {
            return new EntityEmploymentHistory(this);
        }

        @Override
        public String getEntityId() {
            return this.entityId;
        }
        @Override
        public EntityAffiliationHistory.Builder getEntityAffiliation() {
            return this.entityAffiliation;
        }

        @Override
        public CodedAttributeHistory.Builder getEmployeeStatus() {
            return this.employeeStatus;
        }

        @Override
        public CodedAttributeHistory.Builder getEmployeeType() {
            return this.employeeType;
        }

        @Override
        public String getPrimaryDepartmentCode() {
            return this.primaryDepartmentCode;
        }

        @Override
        public String getEmployeeId() {
            return this.employeeId;
        }

        @Override
        public String getEmploymentRecordId() {
            return this.employmentRecordId;
        }

        @Override
        public KualiDecimal getBaseSalaryAmount() {
            return this.baseSalaryAmount;
        }

        @Override
        public boolean isPrimary() {
            return this.primary;
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

        @Override
        public boolean isTenured() {
            return this.tenured;
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

        public void setEntityAffiliation(EntityAffiliationHistory.Builder entityAffiliation) {
            this.entityAffiliation = entityAffiliation;
        }

        public void setEmployeeStatus(CodedAttributeHistory.Builder employeeStatus) {
            this.employeeStatus = employeeStatus;
        }

        public void setEmployeeType(CodedAttributeHistory.Builder employeeType) {
            this.employeeType = employeeType;
        }

        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }

        public void setPrimaryDepartmentCode(String primaryDepartmentCode) {
            this.primaryDepartmentCode = primaryDepartmentCode;
        }

        public void setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
        }

        public void setEmploymentRecordId(String employmentRecordId) {
            this.employmentRecordId = employmentRecordId;
        }

        public void setBaseSalaryAmount(KualiDecimal baseSalaryAmount) {
            this.baseSalaryAmount = baseSalaryAmount;
        }

        public void setPrimary(boolean primary) {
            this.primary = primary;
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

        public void setTenured(boolean tenured) {
            this.tenured = tenured;
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

        final static String ROOT_ELEMENT_NAME = "entityEmployment";
        final static String TYPE_NAME = "EntityEmploymentType";
    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {

        final static String ENTITY_AFFILIATION = "entityAffiliation";
        final static String EMPLOYEE_STATUS = "employeeStatus";
        final static String EMPLOYEE_TYPE = "employeeType";
        final static String PRIMARY_DEPARTMENT_CODE = "primaryDepartmentCode";
        final static String EMPLOYEE_ID = "employeeId";
        final static String EMPLOYMENT_RECORD_ID = "employmentRecordId";
        final static String BASE_SALARY_AMOUNT = "baseSalaryAmount";
        final static String PRIMARY = "primary";
        final static String ACTIVE = "active";
        final static String ENTITY_ID = "entityId";
        final static String ID = "id";
        final static String TENURED = "tenured";
    }

}
