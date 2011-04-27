/*
 * Copyright 2006-2011 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.kim.api.attribute.KimAttribute;
import org.kuali.rice.kim.api.type.KimType;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Collection;


@XmlRootElement(name = GroupAttribute.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = GroupAttribute.Constants.TYPE_NAME, propOrder = {
        GroupAttribute.Elements.ID,
        GroupAttribute.Elements.GROUP_ID,
        GroupAttribute.Elements.KIM_TYPE,
        GroupAttribute.Elements.KIM_ATTRIBUTE,
        GroupAttribute.Elements.ATTRIBUTE_VALUE,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.OBJECT_ID,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class GroupAttribute implements GroupAttributeContract, ModelObjectComplete {
    @XmlElement(name = Elements.ID, required = false)
    private final String id;

    @XmlElement(name = Elements.GROUP_ID, required = false)
    private final String groupId;

    @XmlElement(name = Elements.KIM_TYPE, required = true)
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

    private GroupAttribute() {
        this.id = null;
        this.groupId = null;
        this.kimType = null;
        this.kimAttribute = null;
        this.attributeValue = null;
        this.versionNumber = null;
        this.objectId = null;
    }

    public GroupAttribute(Builder builder) {
        this.id = builder.getId();
        this.groupId = builder.getGroupId();
        this.kimType = builder.getKimType().build();
        this.kimAttribute =
                builder.getKimAttribute() != null ? builder.getKimAttribute().build() : null;
        this.attributeValue = builder.getAttributeValue();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getGroupId() {
        return groupId;
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

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

        public static class Builder implements GroupAttributeContract, ModelBuilder, Serializable {
        private String id;
        private String groupId;
        private KimType.Builder kimType;
        private KimAttribute.Builder kimAttribute;
        private String attributeValue;
        private Long versionNumber;
        private String objectId;

        private Builder(KimType.Builder kimType) {
            setKimType(kimType);
        }

        /**
         * creates a Parameter with the required fields.
         */
        public static Builder create(KimType.Builder kimType) {
            return new Builder(kimType);
        }

        /**
         * creates a Parameter from an existing {@link org.kuali.rice.core.api.parameter.ParameterContract}.
         */
        public static Builder create(GroupAttributeContract contract) {
            Builder builder = new Builder(KimType.Builder.create(contract.getKimType()));
            builder.setGroupId(contract.getGroupId());

            builder.setId(contract.getId());
            if (contract.getKimAttribute() != null) {
                builder.setKimAttribute(KimAttribute.Builder.create(contract.getKimAttribute()));
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
                throw new IllegalArgumentException("id is blank");
            }
            this.id = id;
        }

        @Override
        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(final String groupId) {
            this.groupId = groupId;
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
        public GroupAttribute build() {
            return new GroupAttribute(this);
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "groupAttribute";
        final static String TYPE_NAME = "GroupAttributeType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    static class Elements {
        final static String ID = "id";
        final static String GROUP_ID = "groupId";
        final static String KIM_TYPE = "kimType";
        final static String KIM_ATTRIBUTE = "kimAttribute";
        final static String ATTRIBUTE_VALUE = "attributeValue";
    }
}
