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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
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
        GroupAttribute.Elements.KIM_TYPE_ID,
        GroupAttribute.Elements.ATTRIBUTE_ID,
        GroupAttribute.Elements.VALUE,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.OBJECT_ID,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class GroupAttribute implements GroupAttributeContract, ModelObjectComplete {
    @XmlElement(name = Elements.ID, required = true)
    private final String id;

    @XmlElement(name = Elements.GROUP_ID, required = false)
    private final String groupId;

    @XmlElement(name = Elements.KIM_TYPE_ID, required = true)
    private final String kimTypeId;

    @XmlElement(name = Elements.ATTRIBUTE_ID, required = false)
    private final String attributeId;

    @XmlElement(name = Elements.VALUE, required = false)
    private final String value;

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
        this.kimTypeId = null;
        this.attributeId = null;
        this.value = null;
        this.versionNumber = null;
        this.objectId = null;
    }

    public GroupAttribute(Builder builder) {
        this.id = builder.getId();
        this.groupId = builder.getGroupId();
        this.kimTypeId = builder.getKimTypeId();
        this.attributeId = builder.getAttributeId();
        this.value = builder.getValue();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
    }

    public static class Builder implements GroupAttributeContract, ModelBuilder, Serializable {
        private String id;
        private String groupId;
        private String kimTypeId;
        private String attributeId;
        private String value;
        private Long versionNumber;
        private String objectId;

        private Builder(String id, String kimTypeId) {
            setId(id);
            setKimTypeId(kimTypeId);
        }

        /**
         * creates a Parameter with the required fields.
         */
        public static Builder create(String id, String kimTypeId) {
            return new Builder(id, kimTypeId);
        }

        /**
         * creates a Parameter from an existing {@link org.kuali.rice.core.api.parameter.ParameterContract}.
         */
        public static Builder create(GroupAttributeContract contract) {
            Builder builder = new Builder(contract.getId(), contract.getKimTypeId());
            builder.setGroupId(contract.getGroupId());
            builder.setAttributeId(contract.getAttributeId());
            builder.setValue(contract.getValue());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            return builder;
        }

        @Override
        public String getId() {
            return id;
        }

        public void setId(final String id) {
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
        public String getKimTypeId() {
            return kimTypeId;
        }

        public void setKimTypeId(final String kimTypeId) {
            this.kimTypeId = kimTypeId;
        }

        @Override
        public String getAttributeId() {
            return attributeId;
        }

        public void setAttributeId(final String attributeId) {
            this.attributeId = attributeId;
        }

        @Override
        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
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

    public String getId() {
        return id;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getKimTypeId() {
        return kimTypeId;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public String getValue() {
        return value;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

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
        final static String KIM_TYPE_ID = "kimTypeId";
        final static String ATTRIBUTE_ID = "attributeId";
        final static String VALUE = "value";
    }
}
