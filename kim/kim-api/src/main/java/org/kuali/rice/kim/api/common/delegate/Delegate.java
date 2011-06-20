/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.kim.api.common.delegate;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.jaxb.SqlTimestampAdapter;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.Timestamp;
import java.util.Collection;

/**
 * An immutable and JAXB annotated DTO of a DelegateContract
 */
@XmlRootElement(name = Delegate.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Delegate.Constants.TYPE_NAME, propOrder = {
        Delegate.Elements.DELEGATION_ID,
        Delegate.Elements.DELEGATION_TYPE_CODE,
        Delegate.Elements.MEMBER_ID,
        Delegate.Elements.MEMBER_TYPE_CODE,
        Delegate.Elements.QUALIFIER,
        Delegate.Elements.ROLE_MEMBER_ID,
        Delegate.Elements.ROLE_ID,
        Delegate.Elements.DELEGATION_MEMBER_ID,
        Delegate.Elements.MEMBER_NAME,
        Delegate.Elements.MEMBER_NAMESPACE_CODE,
        CoreConstants.CommonElements.ACTIVE_FROM_DATE,
        CoreConstants.CommonElements.ACTIVE_TO_DATE,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class Delegate implements DelegateContract, ModelObjectComplete {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = Elements.DELEGATION_ID, required = true)
    private final String delegationId;

    @XmlElement(name = Elements.DELEGATION_TYPE_CODE, required = true)
    private final String delegationTypeCode;

    @XmlElement(name = Elements.MEMBER_ID, required = true)
    private final String memberId;

    @XmlElement(name = Elements.MEMBER_TYPE_CODE, required = true)
    private final String memberTypeCode;

    @XmlElement(name = Elements.QUALIFIER, required = true)
    private final AttributeSet qualifier;

    @XmlElement(name = Elements.ROLE_MEMBER_ID, required = true)
    private final String roleMemberId;

    @XmlElement(name = Elements.ROLE_ID)
    private final String roleId;

    @XmlElement(name = Elements.DELEGATION_MEMBER_ID)
    private final String delegationMemberId;

    @XmlElement(name = Elements.MEMBER_NAME)
    private final String memberName;

    @XmlElement(name = Elements.MEMBER_NAMESPACE_CODE)
    private final String memberNamespaceCode;

    @XmlElement(name =  CoreConstants.CommonElements.ACTIVE_FROM_DATE)
    @XmlJavaTypeAdapter(SqlTimestampAdapter.class)
    private final Timestamp activeFromDate;

    @XmlElement(name =  CoreConstants.CommonElements.ACTIVE_TO_DATE)
    @XmlJavaTypeAdapter(SqlTimestampAdapter.class)
	private final Timestamp activeToDate;

    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER)
    private final Long versionNumber;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;


    @SuppressWarnings("unused")
    private Delegate() {
        delegationId = null;
        delegationTypeCode = null;
        memberId = null;
        memberTypeCode = null;
        qualifier = null;
        roleMemberId = null;
        roleId = null;
        delegationMemberId = null;
        memberName = null;
        memberNamespaceCode = null;
        activeFromDate = null;
        activeToDate = null;
        versionNumber = null;
    }

    private Delegate(Builder b) {
        delegationId = b.getDelegationId();
        delegationTypeCode = b.getDelegationTypeCode();
        memberId = b.getMemberId();
        memberTypeCode = b.getMemberTypeCode();
        qualifier = b.getQualifier();
        roleMemberId = b.getRoleMemberId();
        roleId = b.getRoleId();
        delegationMemberId = b.getDelegationMemberId();
        memberName = b.getMemberName();
        memberNamespaceCode = b.getMemberNamespaceCode();
        activeFromDate = b.getActiveFromDate();
        activeToDate = b.getActiveToDate();
        versionNumber = b.getVersionNumber();
    }

    public String getDelegationTypeCode() {
        return this.delegationTypeCode;
    }

    public String getMemberTypeCode() {
        return this.memberTypeCode;
    }

    public String getMemberId() {
        return this.memberId;
    }

    public AttributeSet getQualifier() {
        return this.qualifier;
    }

    public String getDelegationId() {
        return this.delegationId;
    }

    public String getRoleMemberId() {
        return this.roleMemberId;
    }

    public String getMemberName() {
        return this.memberName;
    }

    public String getMemberNamespaceCode() {
        return this.memberNamespaceCode;
    }

    public String getDelegationMemberId() {
        return this.delegationMemberId;
    }

    public String getRoleId() {
        return this.roleId;
    }

    @Override
    public Timestamp getActiveFromDate() {
        return activeFromDate;
    }

    @Override
    public Timestamp getActiveToDate() {
        return activeToDate;
    }

    @Override
    public boolean isActive(Timestamp activeAsOfDate) {
        return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, activeAsOfDate);
    }

    public Long getVersionNumber() {
        return this.versionNumber;
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

    public static final class Builder implements DelegateContract, ModelBuilder, ModelObjectComplete {

        private String delegationId;
        private String delegationTypeCode;
        private String memberId;
        private String memberTypeCode;
        private AttributeSet qualifier;
        private String roleMemberId;
        private String roleId;
        private String delegationMemberId;
        private String memberName;
        private String memberNamespaceCode;
        private Timestamp activeFromDate;
    	private Timestamp activeToDate;
        private Long versionNumber;

        private Builder() {
        }

        public static Builder create(DelegateContract delegateContract) {
            Builder b = new Builder();
            b.setDelegationId(delegateContract.getDelegationId());
            b.setDelegationTypeCode(delegateContract.getDelegationTypeCode());
            b.setMemberId(delegateContract.getMemberId());
            b.setMemberTypeCode(delegateContract.getMemberTypeCode());
            b.setQualifier(delegateContract.getQualifier());
            b.setRoleMemberId(delegateContract.getRoleMemberId());
            b.setRoleId(delegateContract.getRoleId());
            b.setDelegationMemberId(delegateContract.getDelegationMemberId());
            b.setMemberName(delegateContract.getMemberName());
            b.setMemberNamespaceCode(delegateContract.getMemberNamespaceCode());
            b.setActiveFromDate(delegateContract.getActiveFromDate());
            b.setActiveToDate(delegateContract.getActiveToDate());
            b.setVersionNumber(delegateContract.getVersionNumber());

            return b;
        }

        public static Builder create(String delegationId, String delegationTypeCode, String memberId,
                                     String memberTypeCode, String roleMemberId, AttributeSet qualifier) {
            Builder b = new Builder();

            b.setDelegationId(delegationId);
            b.setDelegationTypeCode(delegationTypeCode);
            b.setMemberId(memberId);
            b.setMemberTypeCode(memberTypeCode);
            b.setRoleMemberId(roleMemberId);
            b.setQualifier(qualifier);

            return b;
        }

        public Delegate build() {
            return new Delegate(this);
        }

        public String getDelegationId() {
            return delegationId;
        }

        public String getDelegationTypeCode() {
            return delegationTypeCode;
        }

        public String getMemberId() {
            return memberId;
        }

        public String getMemberTypeCode() {
            return memberTypeCode;
        }

        public AttributeSet getQualifier() {
            return qualifier;
        }

        public String getRoleMemberId() {
            return roleMemberId;
        }

        public String getRoleId() {
            return roleId;
        }

        public String getDelegationMemberId() {
            return delegationMemberId;
        }

        public String getMemberName() {
            return memberName;
        }

        public String getMemberNamespaceCode() {
            return memberNamespaceCode;
        }

        public void setDelegationTypeCode(String delegationTypeCode) {
            if (StringUtils.isBlank(delegationTypeCode)) {
                throw new IllegalArgumentException("delegationTypeCode cannot be blank or null");
            }
            this.delegationTypeCode = delegationTypeCode;
        }

        public void setMemberId(String memberId) {
            if (StringUtils.isBlank(memberId)) {
                throw new IllegalArgumentException("memberId cannot be blank or null");
            }
            this.memberId = memberId;
        }

        public void setMemberTypeCode(String memberTypeCode) {
            if (StringUtils.isBlank(memberTypeCode)) {
                throw new IllegalArgumentException("memberTypeCode cannot be blank or null");
            }
            this.memberTypeCode = memberTypeCode;
        }

        public void setQualifier(AttributeSet qualifier) {
            if (qualifier == null) {
                this.qualifier = new AttributeSet();
            }
            this.qualifier = qualifier;
        }

        public void setDelegationId(String delegationId) {
            if (StringUtils.isBlank(delegationId)) {
                throw new IllegalArgumentException("delegationId cannot be blank or null");
            }
            this.delegationId = delegationId;
        }

        public void setRoleMemberId(String roleMemberId) {
            if (StringUtils.isBlank(roleMemberId)) {
                throw new IllegalArgumentException("roleMemberId cannot be blank or null");
            }
            this.roleMemberId = roleMemberId;
        }

        @SuppressWarnings("unused")
        public void setMemberName(String memberName) {
            this.memberName = memberName;
        }

        @SuppressWarnings("unused")
        public void setMemberNamespaceCode(String memberNamespaceCode) {
            this.memberNamespaceCode = memberNamespaceCode;
        }

        @SuppressWarnings("unused")
        public void setDelegationMemberId(String delegationMemberId) {
            this.delegationMemberId = delegationMemberId;
        }

        public void setRoleId(String roleId) {
            this.roleId = roleId;
        }

        public Timestamp getActiveFromDate() {
            return activeFromDate != null ? new Timestamp(activeFromDate.getTime()) : null;
        }

        public void setActiveFromDate(Timestamp activeFromDate) {
            this.activeFromDate = activeFromDate != null ? new Timestamp(activeFromDate.getTime()) : null;
        }

        public Timestamp getActiveToDate() {
            return activeToDate != null ? new Timestamp(activeToDate.getTime()) : null;
        }

        public void setActiveToDate(Timestamp activeToDate) {
            this.activeToDate = activeFromDate != null ?  new Timestamp(activeToDate.getTime()) : null;
        }

        @Override
        public boolean isActive(Timestamp activeAsOfDate) {
            return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, activeAsOfDate);
        }

        public Long getVersionNumber() {
            return versionNumber;
        }

        public void setVersionNumber(Long versionNumber) {
            this.versionNumber = versionNumber;
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
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    static class Elements {
        final static String DELEGATION_ID = "delegationId";
        final static String DELEGATION_TYPE_CODE = "delegationTypeCode";
        final static String MEMBER_ID = "memberId";
        final static String MEMBER_TYPE_CODE = "memberTypeCode";
        final static String QUALIFIER = "qualifier";
        final static String ROLE_MEMBER_ID = "roleMemberId";
        final static String ROLE_ID = "roleId";
        final static String DELEGATION_MEMBER_ID = "delegationMemberId";
        final static String MEMBER_NAME = "memberName";
        final static String MEMBER_NAMESPACE_CODE = "memberNamespaceCode";
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "delegate";
        final static String TYPE_NAME = "DelegateType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }
}
