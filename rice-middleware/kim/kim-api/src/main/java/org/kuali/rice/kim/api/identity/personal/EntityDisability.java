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
package org.kuali.rice.kim.api.identity.personal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.CodedAttributeContract;
import org.w3c.dom.Element;

@XmlRootElement(name = EntityDisability.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityDisability.Constants.TYPE_NAME, propOrder = {
        EntityDisability.Elements.ID,
        EntityDisability.Elements.ENTITY_ID,
        EntityDisability.Elements.STATUS_CODE,
        EntityDisability.Elements.DETERMINATION_SOURCE_TYPE,
        EntityDisability.Elements.ACCOMMODATIONS_NEEDED,
        EntityDisability.Elements.CONDITION_TYPE,
        CoreConstants.CommonElements.ACTIVE,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.OBJECT_ID,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityDisability
        extends AbstractDataTransferObject
        implements EntityDisabilityContract
{

    @XmlElement(name = Elements.STATUS_CODE, required = false)
    private final String statusCode;
    @XmlElement(name = Elements.DETERMINATION_SOURCE_TYPE, required = false)
    private final CodedAttribute determinationSourceType;
    @XmlElementWrapper(name = Elements.ACCOMMODATIONS_NEEDED, required = false)
    @XmlElement(name = Elements.ACCOMMODATION_NEEDED, required = false)
    private final List<CodedAttribute> accommodationsNeeded;
    @XmlElement(name = Elements.CONDITION_TYPE, required = false)
    private final CodedAttribute conditionType;
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
    private EntityDisability() {
        this.statusCode = null;
        this.determinationSourceType = null;
        this.accommodationsNeeded = null;
        this.conditionType = null;
        this.versionNumber = null;
        this.objectId = null;
        this.active = false;
        this.id = null;
        this.entityId = null;
    }

    private EntityDisability(Builder builder) {
        this.statusCode = builder.getStatusCode();
        this.determinationSourceType = builder.getDeterminationSourceType() != null ? builder.getDeterminationSourceType().build() : null;
        this.accommodationsNeeded = new ArrayList<CodedAttribute>();
        if (CollectionUtils.isNotEmpty(builder.getAccommodationsNeeded())) {
            for (CodedAttribute.Builder accommodations : builder.getAccommodationsNeeded()) {
                this.accommodationsNeeded.add(accommodations.build());
            }
        }
        this.conditionType = builder.getConditionType() != null ? builder.getConditionType().build() : null;
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.active = builder.isActive();
        this.id = builder.getId();
        this.entityId = builder.getEntityId();
    }

    @Override
    public String getStatusCode() {
        return this.statusCode;
    }

    @Override
    public CodedAttribute getDeterminationSourceType() {
        return this.determinationSourceType;
    }

    @Override
    public List<CodedAttribute> getAccommodationsNeeded() {
        return Collections.unmodifiableList(this.accommodationsNeeded);
    }

    @Override
    public CodedAttribute getConditionType() {
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
    public String getEntityId() {
        return this.entityId;
    }


    /**
     * A builder which can be used to construct {@link EntityDisability} instances.  Enforces the constraints of the {@link EntityDisabilityContract}.
     *
     */
    public final static class Builder
            implements Serializable, ModelBuilder, EntityDisabilityContract
    {

        private String statusCode;
        private CodedAttribute.Builder determinationSourceType;
        private List<CodedAttribute.Builder> accommodationsNeeded;
        private CodedAttribute.Builder conditionType;
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

        public static Builder create(EntityDisabilityContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setEntityId(contract.getEntityId());
            builder.setStatusCode(contract.getStatusCode());
            if (contract.getDeterminationSourceType() != null) {
                builder.setDeterminationSourceType(CodedAttribute.Builder.create(contract.getDeterminationSourceType()));
            }
            if (contract.getAccommodationsNeeded() != null) {
                List<CodedAttribute.Builder> accommodations = new ArrayList<CodedAttribute.Builder>();
                for (CodedAttributeContract accommodation : contract.getAccommodationsNeeded()) {
                    accommodations.add(CodedAttribute.Builder.create(accommodation));
                }
                builder.setAccommodationsNeeded(accommodations);
            }
            if (contract.getConditionType() != null) {
                builder.setConditionType(CodedAttribute.Builder.create(contract.getConditionType()));
            }
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setActive(contract.isActive());
            builder.setId(contract.getId());
            return builder;
        }

        public EntityDisability build() {
            return new EntityDisability(this);
        }

        @Override
        public String getStatusCode() {
            return this.statusCode;
        }

        @Override
        public String getEntityId() {
            return this.entityId;
        }

        @Override
        public CodedAttribute.Builder getDeterminationSourceType() {
            return this.determinationSourceType;
        }

        @Override
        public List<CodedAttribute.Builder> getAccommodationsNeeded() {
            return this.accommodationsNeeded;
        }

        @Override
        public CodedAttribute.Builder getConditionType() {
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

        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }

        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }

        public void setDeterminationSourceType(CodedAttribute.Builder determinationSourceType) {
            this.determinationSourceType = determinationSourceType;
        }

        public void setAccommodationsNeeded(List<CodedAttribute.Builder> accommodationsNeeded) {
            this.accommodationsNeeded = Collections.unmodifiableList(accommodationsNeeded);
        }

        public void setConditionType(CodedAttribute.Builder conditionType) {
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