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
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.core.api.util.jaxb.SqlTimestampAdapter;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;

@XmlRootElement(name = GroupMember.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = GroupMember.Constants.TYPE_NAME, propOrder = {
        GroupMember.Elements.ID,
        GroupMember.Elements.GROUP_ID,
        GroupMember.Elements.MEMBER_ID,
        GroupMember.Elements.TYPE_CODE,
        CoreConstants.CommonElements.ACTIVE_FROM_DATE,
        CoreConstants.CommonElements.ACTIVE_TO_DATE,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.OBJECT_ID,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class GroupMember implements GroupMemberContract, ModelObjectComplete  {

    @XmlElement(name = Elements.ID, required = false)
    private final String id;

    @XmlElement(name = Elements.GROUP_ID, required = true)
    private final String groupId;

    @XmlElement(name = Elements.MEMBER_ID, required = true)
    private final String memberId;

    @XmlElement(name = Elements.TYPE_CODE, required = true)
    private final String typeCode;

    @XmlElement(name = CoreConstants.CommonElements.ACTIVE_FROM_DATE, required = false)
    @XmlJavaTypeAdapter(SqlTimestampAdapter.class)
    private final Timestamp activeFromDate;

    @XmlElement(name = CoreConstants.CommonElements.ACTIVE_TO_DATE, required = false)
    @XmlJavaTypeAdapter(SqlTimestampAdapter.class)
	private final Timestamp activeToDate;

    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;

    @XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
    private final String objectId;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    private GroupMember() {
        this.id = null;
        this.groupId = null;
        this.memberId = null;
        this.typeCode = null;
        this.versionNumber = null;
        this.objectId = null;
        this.activeFromDate = null;
        this.activeToDate = null;
    }


    public GroupMember(Builder builder) {
        this.id = builder.getId();
        this.groupId = builder.getGroupId();
        this.memberId = builder.getMemberId();
        this.typeCode = builder.getTypeCode();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.activeFromDate = builder.getActiveFromDate();
        this.activeToDate = builder.getActiveToDate();
    }

    public static class Builder implements GroupMemberContract, ModelBuilder, Serializable {
        private String id;
        private String groupId;
        private String memberId;
        private String typeCode;
        private Timestamp activeFromDate;
        private Timestamp activeToDate;
        private Long versionNumber;
        private String objectId;

        private Builder(String groupId, String memberId, String typeCode) {
            setGroupId(groupId);
            setMemberId(memberId);
            setTypeCode(typeCode);
        }

        /**
         * creates a Parameter with the required fields.
         */
        public static Builder create(String groupId, String memberId, String typeCode) {
            return new Builder(groupId, memberId, typeCode);
        }

        /**
         * creates a GroupMember from an existing {@link org.kuali.rice.kim.api.group.GroupMemberContract}.
         */
        public static Builder create(GroupMemberContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = new Builder(contract.getGroupId(), contract.getMemberId(), contract.getTypeCode());
            builder.setId(contract.getId());
            builder.setActiveFromDate(contract.getActiveFromDate());
            builder.setActiveToDate(contract.getActiveToDate());
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
            if (StringUtils.isEmpty(groupId)) {
                throw new IllegalArgumentException("groupId is empty");
            }
            this.groupId = groupId;
        }

        @Override
        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(final String memberId) {
            if (StringUtils.isEmpty(memberId)) {
                throw new IllegalArgumentException("memberId is empty");
            }
            this.memberId = memberId;
        }

        @Override
        public String getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(final String typeCode) {
            if (StringUtils.isEmpty(typeCode)) {
                throw new IllegalArgumentException("typeCode is empty");
            }
            this.typeCode = typeCode;
        }

        @Override
        public Timestamp getActiveFromDate() {
            return activeFromDate;
        }

        public void setActiveFromDate(final Timestamp activeFromDate) {
            this.activeFromDate = activeFromDate;
        }

        @Override
        public Timestamp getActiveToDate() {
            return activeToDate;
        }

        public void setActiveToDate(final Timestamp activeToDate) {
            this.activeToDate = activeToDate;
        }

        @Override
        public Long getVersionNumber() {
            return versionNumber;
        }

        public void setVersionNumber(final Long versionNumber) {
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
        public boolean isActive(Timestamp activeAsOf) {
            return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, activeAsOf);
        }

        @Override
        public GroupMember build() {
            return new GroupMember(this);
        }
    }

    public String getId() {
        return id;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public Timestamp getActiveFromDate() {
        return activeFromDate;
    }

    public Timestamp getActiveToDate() {
        return activeToDate;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public String getObjectId() {
        return objectId;
    }

    @Override
    public boolean isActive(Timestamp activeAsOf) {
        return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, activeAsOf);
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
        final static String ROOT_ELEMENT_NAME = "groupMember";
        final static String TYPE_NAME = "GroupMemberType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    static class Elements {
        final static String ID = "id";
        final static String GROUP_ID = "groupId";
        final static String MEMBER_ID = "memberId";
        final static String TYPE_CODE = "typeCode";
    }
}
