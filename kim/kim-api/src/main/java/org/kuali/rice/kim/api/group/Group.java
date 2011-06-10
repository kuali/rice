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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.core.api.mo.common.Attributes;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.api.common.attribute.KimAttributeData;
import org.kuali.rice.kim.api.common.attribute.KimAttributeDataContract;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@XmlRootElement(name = Group.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Group.Constants.TYPE_NAME, propOrder = {
        Group.Elements.ID,
        Group.Elements.NAMESPACE_CODE,
        Group.Elements.NAME,
        Group.Elements.DESCRIPTION,
        Group.Elements.KIM_TYPE_ID,
        Group.Elements.ATTRIBUTES,
        Group.Elements.ACTIVE,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.OBJECT_ID,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class Group implements GroupContract, ModelObjectComplete {
    @XmlElement(name = Elements.ID, required = false)
    private final String id;

    @XmlElement(name = Elements.NAMESPACE_CODE, required = true)
    private final String namespaceCode;

    @XmlElement(name = Elements.NAME, required = true)
    private final String name;

    @XmlElement(name = Elements.DESCRIPTION, required = false)
    private final String description;

    @XmlElement(name = Elements.KIM_TYPE_ID, required = true)
    private final String kimTypeId;

    @XmlElement(name = Elements.ATTRIBUTES, required = false)
    private final Attributes attributes;

    @XmlElement(name = Elements.ACTIVE, required = false)
    private final boolean active;

    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;

    @XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
    private final String objectId;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    private Group() {
        this.id = null;
        this.namespaceCode = null;
        this.name = null;
        this.description = null;
        this.kimTypeId = null;
        this.attributes = null;
        this.versionNumber = null;
        this.objectId = null;
        this.active = false;
    }

    public Group(Builder builder) {
        id = builder.getId();
        namespaceCode = builder.getNamespaceCode();
        name = builder.getName();
        description = builder.getDescription();
        kimTypeId = builder.getKimTypeId();
        attributes = builder.getAttributes();
        versionNumber = builder.getVersionNumber();
        objectId = builder.getObjectId();
        active = builder.isActive();
    }


    /**
     * This builder constructs an Group enforcing the constraints of the {@link org.kuali.rice.kim.api.group.GroupContract}.
     */
    public static class Builder implements GroupContract, ModelBuilder, Serializable {
        private String id;
        private String namespaceCode;
        private String name;
        private String description;
        private String kimTypeId;
        private Attributes attributes;
        private boolean active;
        private Long versionNumber;
        private String objectId;

        private Builder(String namespaceCode, String name, String kimTypeId) {
            setNamespaceCode(namespaceCode);
            setName(name);
            setKimTypeId(kimTypeId);
        }

        /**
         * creates a Group with the required fields.
         */
        public static Builder create(String namespaceCode, String name, String kimTypeId) {
            return new Builder(namespaceCode, name, kimTypeId);
        }

        /**
         * creates a Group from an existing {@link org.kuali.rice.kim.api.group.GroupContract}.
         */
        public static Builder create(GroupContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("GroupContract is null");
            }
            Builder builder = new Builder(contract.getNamespaceCode(), contract.getName(), contract.getKimTypeId());
            builder.setId(contract.getId());
            builder.setDescription(contract.getDescription());

            builder.setAttributes(contract.getAttributes());

            builder.setActive(contract.isActive());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            return builder;
        }

        @Override
        public String getId() {
            return id;
        }

        public void setId(String id) {
            if (StringUtils.isWhitespace(id)) {
                throw new IllegalArgumentException("id is blank");
            }
            this.id = id;
        }

        @Override
        public String getNamespaceCode() {
            return namespaceCode;
        }

        public void setNamespaceCode(String namespaceCode) {
            if (StringUtils.isEmpty(namespaceCode)) {
                throw new IllegalArgumentException("namespaceCode is empty");
            }
            this.namespaceCode = namespaceCode;
        }

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            if (StringUtils.isEmpty(name)) {
                throw new IllegalArgumentException("name is empty");
            }
            this.name = name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String getKimTypeId() {
            return kimTypeId;
        }

        public void setKimTypeId(String kimTypeId) {
            if (StringUtils.isEmpty(kimTypeId)) {
                throw new IllegalArgumentException("kimTypeId is empty");
            }
            this.kimTypeId = kimTypeId;
        }

        @Override
        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }

        @Override
        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
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

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        @Override
        public Group build() {
            return new Group(this);
        }
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

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getNamespaceCode() {
        return namespaceCode;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getKimTypeId() {
        return kimTypeId;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    /*public AttributeSet getAttributeSet() {
        AttributeSet attributeSet = new AttributeSet( this.attributes.size() );
        for ( KimAttributeData attr : attributes ) {
        	if ( attr.getKimAttribute() != null ) {
        		attributeSet.put(attr.getKimAttribute().getAttributeName(), attr.getAttributeValue());
        	} else {
        		attributeSet.put("Unknown Attribute ID: " + attr.getKimAttribute().getId(), attr.getAttributeValue());
        	}
        }

        return attributeSet;
    }*/

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public Long getVersionNumber() {
        return versionNumber;
    }

    @Override
    public String getObjectId() {
        return objectId;
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "group";
        final static String TYPE_NAME = "GroupType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    static class Elements {
        final static String ID = "id";
        final static String NAMESPACE_CODE = "namespaceCode";
        final static String NAME = "name";
        final static String DESCRIPTION = "description";
        final static String KIM_TYPE_ID = "kimTypeId";
        final static String ATTRIBUTES = "attributes";
        final static String ACTIVE = "active";
    }
}
