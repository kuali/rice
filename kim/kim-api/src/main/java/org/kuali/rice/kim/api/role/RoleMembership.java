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

package org.kuali.rice.kim.api.role;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractJaxbModelObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.kim.api.common.delegate.DelegateTypeContract;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = RoleMembership.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RoleMembership.Constants.TYPE_NAME, propOrder = {
        RoleMembership.Elements.ROLE_ID,
        RoleMembership.Elements.ROLE_MEMBER_ID,
        RoleMembership.Elements.EMBEDDED_ROLE_ID,
        RoleMembership.Elements.MEMBER_ID,
        RoleMembership.Elements.MEMBER_TYPE_CODE,
        RoleMembership.Elements.ROLE_SORTING_CODE,
        RoleMembership.Elements.QUALIFIER,
        RoleMembership.Elements.DELEGATES,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class RoleMembership extends AbstractJaxbModelObject implements RoleMembershipContract {
    private static final long serialVersionUID = 1L;

    @XmlElement(name=Elements.ROLE_ID)
    private final String roleId;

    @XmlElement(name=Elements.ROLE_MEMBER_ID)
    private final String roleMemberId;

    @XmlElement(name=Elements.EMBEDDED_ROLE_ID)
    private final String embeddedRoleId;

    @XmlElement(name=Elements.MEMBER_ID, required = true)
    private final String memberId;

    @XmlElement(name=Elements.MEMBER_TYPE_CODE, required = true)
    private final String memberTypeCode;

    @XmlElement(name=Elements.ROLE_SORTING_CODE)
    private final String roleSortingCode;

    @XmlElement(name=Elements.QUALIFIER)
    @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    private final Map<String, String> qualifier;

    @XmlElement(name=Elements.DELEGATES)
    private final List<DelegateType> delegates;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor for JAXB only
     */
    @SuppressWarnings("unused")
    private RoleMembership() {
        roleId = null;
        roleMemberId = null;
        embeddedRoleId = null;
        memberId = null;
        memberTypeCode = null;
        roleSortingCode = null;
        qualifier = null;
        delegates = null;
    }

    private RoleMembership(Builder b) {
        roleId = b.getRoleId();
        roleMemberId = b.getRoleMemberId();
        embeddedRoleId = b.getEmbeddedRoleId();
        memberId = b.getMemberId();
        memberTypeCode = b.getMemberTypeCode();
        roleSortingCode = b.getRoleSortingCode();
        qualifier = b.getQualifier();

        delegates = new ArrayList<DelegateType>();
        if (!CollectionUtils.isEmpty(b.getDelegates())) {
            for (DelegateType.Builder delegateBuilder : b.getDelegates()) {
                delegates.add(delegateBuilder.build());
            }
        }
    }

    public String getRoleId() {
        return roleId;
    }

    public String getRoleMemberId() {
        return roleMemberId;
    }

    public String getEmbeddedRoleId() {
        return embeddedRoleId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getMemberTypeCode() {
        return memberTypeCode;
    }

    public String getRoleSortingCode() {
        return roleSortingCode;
    }

    public Map<String, String> getQualifier() {
        return qualifier;
    }

    public List<DelegateType> getDelegates() {
        return Collections.unmodifiableList(delegates);
    }


    public static final class Builder implements ModelBuilder, RoleMembershipContract, ModelObjectComplete {
        private String roleId;
        private String roleMemberId;
        private String embeddedRoleId;
        private String memberId;
        private String memberTypeCode;
        private String roleSortingCode;
        private Map<String, String> qualifier;
        private List<DelegateType.Builder> delegates;

        private Builder() {
        }

        public static Builder create(String roleId, String roleMemberId, String memberId, String memberTypeCode,
                                     Map<String, String> qualifier) {

            Builder b = new Builder();
            b.setRoleId(roleId);
            b.setRoleMemberId(roleMemberId);
            b.setMemberId(memberId);
            b.setMemberTypeCode(memberTypeCode);
            b.setQualifier(qualifier);
            return b;
        }

        public static Builder create(RoleMembershipContract contract) {
            Builder b = new Builder();
            b.setRoleId(contract.getRoleId());
            b.setRoleMemberId(contract.getRoleMemberId());
            b.setEmbeddedRoleId(contract.getEmbeddedRoleId());
            b.setMemberId(contract.getMemberId());
            b.setMemberTypeCode(contract.getMemberTypeCode());
            b.setRoleSortingCode(contract.getRoleSortingCode());
            b.setQualifier(contract.getQualifier());

            List<DelegateType.Builder> delegateBuilders = new ArrayList<DelegateType.Builder>();
            if (!CollectionUtils.isEmpty(contract.getDelegates())) {
                for (DelegateTypeContract delegateContract : contract.getDelegates()) {
                    delegateBuilders.add(DelegateType.Builder.create(delegateContract));
                }
            }
            b.setDelegates(delegateBuilders);

            return b;
        }

        @Override
        public RoleMembership build() {
            return new RoleMembership(this);
        }

        public String getRoleId() {
            return this.roleId;
        }

        public void setRoleId(String roleId) {
            this.roleId = roleId;
        }

        public Map<String, String> getQualifier() {
            return this.qualifier;
        }

        public void setQualifier(Map<String, String> qualifier) {
            this.qualifier = qualifier;
        }

        public List<DelegateType.Builder> getDelegates() {
            return this.delegates;
        }

        public void setDelegates(List<DelegateType.Builder> delegates) {
            this.delegates = delegates;
        }

        public String getRoleMemberId() {
            return this.roleMemberId;
        }

        public void setRoleMemberId(String roleMemberId) {
            this.roleMemberId = roleMemberId;
        }

        public String getMemberId() {
            return this.memberId;
        }

        public void setMemberId(String memberId) {
            if (StringUtils.isEmpty(memberId)) {
                throw new IllegalArgumentException("memberId cannot be empty or null");
            }
            this.memberId = memberId;
        }

        public String getMemberTypeCode() {
            return this.memberTypeCode;
        }

        public void setMemberTypeCode(String memberTypeCode) {
            if (StringUtils.isEmpty(memberTypeCode)) {
                throw new IllegalArgumentException("memberTypeCode cannot be empty or null");
            }
            this.memberTypeCode = memberTypeCode;
        }

        public String getEmbeddedRoleId() {
            return this.embeddedRoleId;
        }

        public void setEmbeddedRoleId(String embeddedRoleId) {
            this.embeddedRoleId = embeddedRoleId;
        }

        public String getRoleSortingCode() {
            return this.roleSortingCode;
        }

        public void setRoleSortingCode(String roleSortingCode) {
            this.roleSortingCode = roleSortingCode;
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object obj) {
            return EqualsBuilder.reflectionEquals(obj, this);
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
        final static String ROLE_ID = "roleId";
        final static String ROLE_MEMBER_ID = "roleMemberId";
        final static String EMBEDDED_ROLE_ID = "embeddedRoleId";
        final static String MEMBER_ID = "memberId";
        final static String MEMBER_TYPE_CODE = "memberTypeCode";
        final static String ROLE_SORTING_CODE = "roleSortingCode";
        final static String QUALIFIER = "qualifier";
        final static String DELEGATES = "delegates";
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "roleMembership";
        final static String TYPE_NAME = "RoleMembershipType";
    }
}
