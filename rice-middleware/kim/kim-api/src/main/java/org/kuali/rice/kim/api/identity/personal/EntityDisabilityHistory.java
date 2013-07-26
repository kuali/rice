package org.kuali.rice.kim.api.identity.personal;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.core.api.util.jaxb.DateTimeAdapter;
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

@XmlRootElement(name = EntityDisabilityHistory.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityDisabilityHistory.Constants.TYPE_NAME, propOrder = {
        EntityDisabilityHistory.Elements.ID,
        EntityDisabilityHistory.Elements.ENTITY_ID,
        EntityDisabilityHistory.Elements.STATUS_CODE,
        EntityDisabilityHistory.Elements.DETERMINATION_SOURCE_TYPE,
        EntityDisabilityHistory.Elements.ACCOMMODATIONS_NEEDED,
        EntityDisabilityHistory.Elements.CONDITION_TYPE,
        CoreConstants.CommonElements.ACTIVE,
        CoreConstants.CommonElements.HISTORY_ID,
        CoreConstants.CommonElements.ACTIVE_FROM_DATE,
        CoreConstants.CommonElements.ACTIVE_TO_DATE,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.OBJECT_ID,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityDisabilityHistory
        extends AbstractDataTransferObject
        implements EntityDisabilityHistoryContract
{

    @XmlElement(name = Elements.STATUS_CODE, required = false)
    private final String statusCode;
    @XmlElement(name = Elements.DETERMINATION_SOURCE_TYPE, required = false)
    private final CodedAttributeHistory determinationSourceType;
    @XmlElementWrapper(name = Elements.ACCOMMODATIONS_NEEDED, required = false)
    @XmlElement(name = Elements.ACCOMMODATION_NEEDED, required = false)
    private final List<CodedAttributeHistory> accommodationsNeeded;
    @XmlElement(name = Elements.CONDITION_TYPE, required = false)
    private final CodedAttributeHistory conditionType;
    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
    @XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
    private final String objectId;
    @XmlElement(name = Elements.ACTIVE, required = false)
    private final boolean active;
    @XmlElement(name = CoreConstants.CommonElements.HISTORY_ID, required = false)
    private final Long historyId;
    @XmlElement(name = CoreConstants.CommonElements.ACTIVE_FROM_DATE, required = false)
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private final DateTime activeFromDate;
    @XmlElement(name = CoreConstants.CommonElements.ACTIVE_TO_DATE, required = false)
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private final DateTime activeToDate;
    @XmlElement(name = Elements.ID, required = false)
    private final String id;
    @XmlElement(name = Elements.ENTITY_ID, required = false)
    private final String entityId;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     *
     */
    private EntityDisabilityHistory() {
        this.statusCode = null;
        this.determinationSourceType = null;
        this.accommodationsNeeded = null;
        this.conditionType = null;
        this.versionNumber = null;
        this.objectId = null;
        this.active = false;
        this.id = null;
        this.historyId = null;
        this.activeFromDate = null;
        this.activeToDate = null;
        this.entityId = null;
    }

    private EntityDisabilityHistory(Builder builder) {
        this.statusCode = builder.getStatusCode();
        this.determinationSourceType = builder.getDeterminationSourceType() != null ? builder.getDeterminationSourceType().build() : null;
        this.accommodationsNeeded = new ArrayList<CodedAttributeHistory>();
        if (CollectionUtils.isNotEmpty(builder.getAccommodationsNeeded())) {
            for (CodedAttributeHistory.Builder accommodations : builder.getAccommodationsNeeded()) {
                this.accommodationsNeeded.add(accommodations.build());
            }
        }
        this.conditionType = builder.getConditionType() != null ? builder.getConditionType().build() : null;
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.active = builder.isActive();
        this.id = builder.getId();
        this.historyId = builder.getHistoryId();
        this.activeFromDate = builder.getActiveFromDate();
        this.activeToDate = builder.getActiveToDate();
        this.entityId = builder.getEntityId();
    }

    @Override
    public String getStatusCode() {
        return this.statusCode;
    }

    @Override
    public CodedAttributeHistory getDeterminationSourceType() {
        return this.determinationSourceType;
    }

    @Override
    public List<CodedAttributeHistory> getAccommodationsNeeded() {
        return Collections.unmodifiableList(this.accommodationsNeeded);
    }

    @Override
    public CodedAttributeHistory getConditionType() {
        return this.conditionType;
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

    @Override
    public String getEntityId() {
        return this.entityId;
    }


    /**
     * A builder which can be used to construct {@link org.kuali.rice.kim.api.identity.personal.EntityDisabilityHistory} instances.  Enforces the constraints of the {@link org.kuali.rice.kim.api.identity.personal.EntityDisabilityContract}.
     *
     */
    public final static class Builder
            implements Serializable, ModelBuilder, EntityDisabilityHistoryContract
    {

        private String statusCode;
        private CodedAttributeHistory.Builder determinationSourceType;
        private List<CodedAttributeHistory.Builder> accommodationsNeeded;
        private CodedAttributeHistory.Builder conditionType;
        private Long versionNumber;
        private String objectId;
        private boolean active;
        private String id;
        private Long historyId;
        private DateTime activeFromDate;
        private DateTime activeToDate;
        private String entityId;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(EntityDisabilityHistoryContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setStatusCode(contract.getStatusCode());
            if (contract.getDeterminationSourceType() != null) {
                builder.setDeterminationSourceType(CodedAttributeHistory.Builder.create(contract.getDeterminationSourceType()));
            }
            if (contract.getAccommodationsNeeded() != null) {
                List<CodedAttributeHistory.Builder> accommodations = new ArrayList<CodedAttributeHistory.Builder>();
                for (CodedAttributeHistoryContract accommodation : contract.getAccommodationsNeeded()) {
                    accommodations.add(CodedAttributeHistory.Builder.create(accommodation));
                }
                builder.setAccommodationsNeeded(accommodations);
            }
            if (contract.getConditionType() != null) {
                builder.setConditionType(CodedAttributeHistory.Builder.create(contract.getConditionType()));
            }
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setActive(contract.isActive());
            builder.setId(contract.getId());
            builder.setHistoryId(contract.getHistoryId());
            builder.setActiveFromDate(contract.getActiveFromDate());
            builder.setActiveToDate(contract.getActiveToDate());
            builder.setEntityId(contract.getEntityId());
            return builder;
        }

        public EntityDisabilityHistory build() {
            return new EntityDisabilityHistory(this);
        }

        @Override
        public String getStatusCode() {
            return this.statusCode;
        }

        @Override
        public CodedAttributeHistory.Builder getDeterminationSourceType() {
            return this.determinationSourceType;
        }

        @Override
        public List<CodedAttributeHistory.Builder> getAccommodationsNeeded() {
            return this.accommodationsNeeded;
        }

        @Override
        public CodedAttributeHistory.Builder getConditionType() {
            return this.conditionType;
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
        public String getEntityId() {
            return this.entityId;
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

        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }

        public void setDeterminationSourceType(CodedAttributeHistory.Builder determinationSourceType) {
            this.determinationSourceType = determinationSourceType;
        }

        public void setAccommodationsNeeded(List<CodedAttributeHistory.Builder> accommodationsNeeded) {
            this.accommodationsNeeded = Collections.unmodifiableList(accommodationsNeeded);
        }

        public void setConditionType(CodedAttributeHistory.Builder conditionType) {
            this.conditionType = conditionType;
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

        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }

    }


    /**
     * Defines some internal constants used on this class.
     *
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "entityDisability";
        final static String TYPE_NAME = "EntityDisabilityType";

    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     *
     */
    static class Elements {

        final static String STATUS_CODE = "statusCode";
        final static String DETERMINATION_SOURCE_TYPE = "determinationSourceType";
        final static String ACCOMMODATIONS_NEEDED = "accommodationsNeeded";
        final static String ACCOMMODATION_NEEDED = "accommodationNeeded";
        final static String CONDITION_TYPE = "conditionType";
        final static String ACTIVE = "active";
        final static String ID = "id";
        final static String ENTITY_ID = "entityId";
    }

}