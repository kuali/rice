/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.kim.api.common.attribute;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.core.api.util.jaxb.DateTimeAdapter;
import org.kuali.rice.kim.api.type.KimType;
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

@XmlRootElement(name = KimAttributeDataHistory.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = KimAttributeDataHistory.Constants.TYPE_NAME, propOrder = {
        //KimAttributeDataHistory.Elements.HISTORY_ID,
        KimAttributeDataHistory.Elements.ID,
        KimAttributeDataHistory.Elements.ASSIGNED_TO_HISTORY_ID,
        KimAttributeDataHistory.Elements.ASSIGNED_TO_ID,
        KimAttributeDataHistory.Elements.KIM_TYPE_ID,
        KimAttributeDataHistory.Elements.KIM_TYPE,
        KimAttributeDataHistory.Elements.KIM_ATTRIBUTE,
        KimAttributeDataHistory.Elements.ATTRIBUTE_VALUE,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.OBJECT_ID,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class KimAttributeDataHistory extends AbstractDataTransferObject implements KimAttributeDataHistoryContract {
    //@XmlElement(name = Elements.HISTORY_ID, required = false)
    //private final Long historyId;

    @XmlElement(name = Elements.ID, required = false)
    private final String id;

    @XmlElement(name = Elements.ASSIGNED_TO_HISTORY_ID, required = true)
    private final Long assignedToHistoryId;

    @XmlElement(name = Elements.ASSIGNED_TO_ID, required = false)
    private final String assignedToId;

    @XmlElement(name = Elements.KIM_TYPE_ID, required = true)
    private final String kimTypeId;

    @XmlElement(name = Elements.KIM_TYPE, required = false)
    private final KimType kimType;

    @XmlElement(name = Elements.KIM_ATTRIBUTE, required = false)
    private final KimAttribute kimAttribute;

    @XmlElement(name = Elements.ATTRIBUTE_VALUE, required = false)
    private final String attributeValue;

    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;

    @XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
    private final String objectId;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    @SuppressWarnings("unused")
    private KimAttributeDataHistory() {
        //this.historyId = null;
        this.id = null;
        this.assignedToHistoryId = null;
        this.assignedToId = null;
        this.kimTypeId = null;
        this.kimType = null;
        this.kimAttribute = null;
        this.attributeValue = null;
        this.versionNumber = null;
        this.objectId = null;
    }

    private KimAttributeDataHistory(Builder builder) {
        this.id = builder.getId();
        this.assignedToId = builder.getAssignedToId();
        this.kimTypeId = builder.getKimTypeId();
        this.kimType =
                builder.getKimType() != null ? builder.getKimType().build() : null;
        this.kimAttribute =
                builder.getKimAttribute() != null ? builder.getKimAttribute().build() : null;
        this.attributeValue = builder.getAttributeValue();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        //this.historyId = builder.getHistoryId();
        this.assignedToHistoryId = builder.getAssignedToHistoryId();
    }

    //@Override
    //public Long getHistoryId() {
    //    return historyId;
    //}

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getAssignedToId() {
        return assignedToId;
    }

    @Override
    public Long getAssignedToHistoryId() {
        return assignedToHistoryId;
    }

    @Override
    public String getKimTypeId() {
        return kimTypeId;
    }

    @Override
    public KimType getKimType() {
        return kimType;
    }

    @Override
    public KimAttribute getKimAttribute() {
        return kimAttribute;
    }

    @Override
    public String getAttributeValue() {
        return attributeValue;
    }

    @Override
    public Long getVersionNumber() {
        return versionNumber;
    }


    @Override
    public String getObjectId() {
        return objectId;
    }

    public static final class Builder implements KimAttributeDataHistoryContract, ModelBuilder, Serializable {
        private String id;
        private String assignedToId;
        private String kimTypeId;
        private KimType.Builder kimType;
        private KimAttribute.Builder kimAttribute;
        private String attributeValue;
        private Long versionNumber;
        private String objectId;
        //private Long historyId;
        private Long assignedToHistoryId;

        private Builder(String kimTypeId) {
            setKimTypeId(kimTypeId);
        }

        /**
         * creates a Parameter with the required fields.
         */
        public static Builder create(String kimTypeId) {
            return new Builder(kimTypeId);
        }

        /**
         * creates a KimAttributeData from an existing {@link KimAttributeContract}
         */
        public static Builder create(KimAttributeDataContract contract) {
            Builder builder = new Builder(contract.getKimTypeId());
            builder.setAssignedToId(contract.getAssignedToId());

            builder.setId(contract.getId());
            if (contract.getKimAttribute() != null) {
                builder.setKimAttribute(KimAttribute.Builder.create(contract.getKimAttribute()));
            }
            if (contract.getKimType() != null) {
                builder.setKimType(KimType.Builder.create(contract.getKimType()));
            }
            builder.setValue(contract.getAttributeValue());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            return builder;
        }

        @Override
        public String getId() {
            return id;
        }

        public void setId(final String id) {
            if (StringUtils.isWhitespace(id)) {
                throw new IllegalArgumentException("id is whitespace");
            }
            this.id = id;
        }

        @Override
        public String getAssignedToId() {
            return assignedToId;
        }

        public void setAssignedToId(final String assignedToId) {
            this.assignedToId = assignedToId;
        }

        @Override
        public String getKimTypeId(){
            return kimTypeId;
        }

        public void setKimTypeId(String kimTypeId) {
            this.kimTypeId = kimTypeId;
        }

        @Override
        public KimType.Builder getKimType() {
            return kimType;
        }

        public void setKimType(final KimType.Builder kimType) {
            this.kimType = kimType;
        }

        @Override
        public KimAttribute.Builder getKimAttribute() {
            return kimAttribute;
        }

        public void setKimAttribute(final KimAttribute.Builder kimAttribute) {
            this.kimAttribute = kimAttribute;
        }

        @Override
        public String getAttributeValue() {
            return attributeValue;
        }

        public void setValue(final String attributeValue) {
            this.attributeValue = attributeValue;
        }

        @Override
        public Long getVersionNumber() {
            return versionNumber;
        }

        public void setVersionNumber(Long versionNumber) {
            this.versionNumber = versionNumber;
        }

        @Override
        public String getObjectId() {
            return objectId;
        }

        public void setObjectId(final String objectId) {
            this.objectId = objectId;
        }

        @Override
        public KimAttributeDataHistory build() {
            return new KimAttributeDataHistory(this);
        }

        //@Override
        //public Long getHistoryId() {
        //    return historyId;
        //}

        //public void setHistoryId(Long historyId) {
        //    this.historyId = historyId;
        //}

        @Override
        public Long getAssignedToHistoryId() {
            return assignedToHistoryId;
        }

        public void setAssignedToHistoryId(Long assignedToHistoryId) {
            this.assignedToHistoryId = assignedToHistoryId;
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "kimAttributeDataHistory";
        final static String TYPE_NAME = "KimAttributeDataHistoryType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    static class Elements {
        final static String HISTORY_ID = "historyId";
        final static String ID = "id";
        final static String ASSIGNED_TO_HISTORY_ID = "assignedToHistoryId";
        final static String ASSIGNED_TO_ID = "assignedToId";
        final static String KIM_TYPE_ID = "kimTypeId";
        final static String KIM_TYPE = "kimType";
        final static String KIM_ATTRIBUTE = "kimAttribute";
        final static String ATTRIBUTE_VALUE = "attributeValue";
    }
}
