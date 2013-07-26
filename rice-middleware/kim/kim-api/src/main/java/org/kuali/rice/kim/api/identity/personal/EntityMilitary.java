package org.kuali.rice.kim.api.identity.personal;

import java.io.Serializable;
import java.util.Collection;
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
import org.kuali.rice.core.api.util.jaxb.DateTimeAdapter;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.w3c.dom.Element;

@XmlRootElement(name = EntityMilitary.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityMilitary.Constants.TYPE_NAME, propOrder = {
        EntityMilitary.Elements.ID,
        EntityMilitary.Elements.ENTITY_ID,
        EntityMilitary.Elements.SELECTIVE_SERVICE,
        EntityMilitary.Elements.SELECTIVE_SERVICE_NUMBER,
        EntityMilitary.Elements.DISCHARGE_DATE,
        EntityMilitary.Elements.RELATIONSHIP_STATUS,
        CoreConstants.CommonElements.ACTIVE,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.OBJECT_ID,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityMilitary
        extends AbstractDataTransferObject
        implements EntityMilitaryContract
{

    @XmlElement(name = Elements.SELECTIVE_SERVICE, required = false)
    private final boolean selectiveService;
    @XmlElement(name = Elements.SELECTIVE_SERVICE_NUMBER, required = false)
    private final String selectiveServiceNumber;
    @XmlElement(name = Elements.DISCHARGE_DATE, required = false)
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private final DateTime dischargeDate;
    @XmlElement(name = Elements.RELATIONSHIP_STATUS, required = false)
    private final CodedAttribute relationshipStatus;
    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
    @XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
    private final String objectId;
    @XmlElement(name = Elements.ACTIVE, required = false)
    private final boolean active;
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
    private EntityMilitary() {
        this.selectiveService = false;
        this.selectiveServiceNumber = null;
        this.dischargeDate = null;
        this.relationshipStatus = null;
        this.versionNumber = null;
        this.objectId = null;
        this.active = false;
        this.id = null;
        this.entityId = null;
    }

    private EntityMilitary(Builder builder) {
        this.selectiveService = builder.isSelectiveService();
        this.selectiveServiceNumber = builder.getSelectiveServiceNumber();
        this.dischargeDate = builder.getDischargeDate();
        this.relationshipStatus = builder.getRelationshipStatus() != null ? builder.getRelationshipStatus().build() : null;
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.active = builder.isActive();
        this.id = builder.getId();
        this.entityId = builder.getEntityId();
    }

    @Override
    public boolean isSelectiveService() {
        return this.selectiveService;
    }

    @Override
    public String getSelectiveServiceNumber() {
        return this.selectiveServiceNumber;
    }

    @Override
    public DateTime getDischargeDate() {
        return this.dischargeDate;
    }

    @Override
    public CodedAttribute getRelationshipStatus() {
        return this.relationshipStatus;
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
    public String getEntityId() {
        return this.entityId;
    }


    /**
     * A builder which can be used to construct {@link EntityMilitary} instances.  Enforces the constraints of the {@link EntityMilitaryContract}.
     *
     */
    public final static class Builder
            implements Serializable, ModelBuilder, EntityMilitaryContract
    {

        private boolean selectiveService;
        private String selectiveServiceNumber;
        private DateTime dischargeDate;
        private CodedAttribute.Builder relationshipStatus;
        private Long versionNumber;
        private String objectId;
        private boolean active;
        private String id;
        private String entityId;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(EntityMilitaryContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setSelectiveService(contract.isSelectiveService());
            builder.setSelectiveServiceNumber(contract.getSelectiveServiceNumber());
            builder.setDischargeDate(contract.getDischargeDate());
            if (contract.getRelationshipStatus() != null) {
                builder.setRelationshipStatus(CodedAttribute.Builder.create(contract.getRelationshipStatus()));
            }
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setActive(contract.isActive());
            builder.setId(contract.getId());
            builder.setEntityId(contract.getEntityId());
            return builder;
        }

        public EntityMilitary build() {
            return new EntityMilitary(this);
        }

        @Override
        public boolean isSelectiveService() {
            return this.selectiveService;
        }

        @Override
        public String getSelectiveServiceNumber() {
            return this.selectiveServiceNumber;
        }

        @Override
        public DateTime getDischargeDate() {
            return this.dischargeDate;
        }

        @Override
        public CodedAttribute.Builder getRelationshipStatus() {
            return this.relationshipStatus;
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
        public String getEntityId() {
            return this.entityId;
        }

        public void setSelectiveService(boolean selectiveService) {
            this.selectiveService = selectiveService;
        }

        public void setSelectiveServiceNumber(String selectiveServiceNumber) {
            this.selectiveServiceNumber = selectiveServiceNumber;
        }

        public void setDischargeDate(DateTime dischargeDate) {
            this.dischargeDate = dischargeDate;
        }

        public void setRelationshipStatus(CodedAttribute.Builder relationshipStatus) {
            this.relationshipStatus = relationshipStatus;
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

        final static String ROOT_ELEMENT_NAME = "entityMilitary";
        final static String TYPE_NAME = "EntityMilitaryType";

    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     *
     */
    static class Elements {

        final static String SELECTIVE_SERVICE = "selectiveService";
        final static String SELECTIVE_SERVICE_NUMBER = "selectiveServiceNumber";
        final static String DISCHARGE_DATE = "dischargeDate";
        final static String RELATIONSHIP_STATUS = "relationshipStatus";
        final static String ACTIVE = "active";
        final static String ID = "id";
        final static String ENTITY_ID = "entityId";

    }

}