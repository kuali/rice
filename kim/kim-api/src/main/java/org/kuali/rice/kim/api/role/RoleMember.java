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
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.core.api.util.jaxb.DateTimeAdapter;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
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
import java.util.List;
import java.util.Map;

@XmlRootElement(name = RoleMember.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RoleMember.Constants.TYPE_NAME, propOrder = {
        RoleMember.Elements.ROLE_MEMBER_ID,
        RoleMember.Elements.ROLE_ID,
        RoleMember.Elements.ATTRIBUTES,
        RoleMember.Elements.ROLE_RESPONSIBILITY_ACTIONS,
        RoleMember.Elements.MEMBER_ID,
        RoleMember.Elements.MEMBER_TYPE_CODE,
        CoreConstants.CommonElements.ACTIVE_FROM_DATE,
        CoreConstants.CommonElements.ACTIVE_TO_DATE,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class RoleMember extends AbstractDataTransferObject implements RoleMemberContract {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = Elements.ROLE_MEMBER_ID)
    private final String roleMemberId;

    @XmlElement(name = Elements.ROLE_ID)
    private final String roleId;

    @XmlElement(name = Elements.ATTRIBUTES, required = false)
    @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    private final Map<String, String> attributes;

    @XmlElement(name = Elements.ROLE_RESPONSIBILITY_ACTIONS)
    private final List<RoleResponsibilityAction> roleResponsibilityActions;

    @XmlElement(name = Elements.MEMBER_ID)
    private final String memberId;

    @XmlElement(name = Elements.MEMBER_TYPE_CODE)
    private final String memberTypeCode;

    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlElement(name = CoreConstants.CommonElements.ACTIVE_FROM_DATE)
    private final DateTime activeFromDate;

    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlElement(name = CoreConstants.CommonElements.ACTIVE_TO_DATE)
    private final DateTime activeToDate;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor for JAXB
     */
    @SuppressWarnings("unused")
    private RoleMember() {
        roleMemberId = null;
        roleId = null;
        attributes = null;
        roleResponsibilityActions = null;
        memberId = null;
        memberTypeCode = null;
        activeFromDate = null;
        activeToDate = null;
    }

    private RoleMember(Builder b) {
        roleMemberId = b.getRoleMemberId();
        roleId = b.getRoleId();
        attributes = b.getAttributes();

        List<RoleResponsibilityAction> roleResponsibilityActions = new ArrayList<RoleResponsibilityAction>();
        if (!CollectionUtils.isEmpty(b.getRoleRspActions())) {
            for (RoleResponsibilityAction.Builder rraBuilder : b.getRoleRspActions()) {
                roleResponsibilityActions.add(rraBuilder.build());
            }
        }
        this.roleResponsibilityActions = roleResponsibilityActions;

        memberId = b.getMemberId();
        memberTypeCode = b.getMemberTypeCode();
        activeFromDate = b.getActiveFromDate();
        activeToDate = b.getActiveToDate();
    }


    public String getMemberId() {
        return this.memberId;
    }

    public String getMemberTypeCode() {
        return this.memberTypeCode;
    }


    public String getRoleMemberId() {
        return this.roleMemberId;
    }

    public String getRoleId() {
        return this.roleId;
    }

    /**
     * @return the attributes
     */
    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    /**
     * @return the roleResponsibilityActions
     */
    public List<RoleResponsibilityAction> getRoleRspActions() {
        return this.roleResponsibilityActions;
    }

    public DateTime getActiveFromDate() {
        return activeFromDate;
    }

    public DateTime getActiveToDate() {
        return activeToDate;
    }

    @Override
    public boolean isActive(DateTime activeAsOfDate) {
        return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, activeAsOfDate);
    }

    public static final class Builder implements ModelBuilder, RoleMemberContract, ModelObjectComplete {

        private String roleMemberId;
        private String roleId;
        private Map<String, String> attributes;
        private List<RoleResponsibilityAction.Builder> roleRspActions;
        private String memberId;
        private String memberTypeCode;
        private DateTime activeFromDate;
        private DateTime activeToDate;

        public static Builder create(String roleId, String roleMemberId, String memberId,
                                     String memberTypeCode, DateTime activeFromDate, DateTime activeToDate, Map<String, String> attributes) {
            Builder b = new Builder();
            b.setRoleId(roleId);
            b.setRoleMemberId(roleMemberId);
            b.setMemberId(memberId);
            b.setMemberTypeCode(memberTypeCode);
            b.setActiveFromDate(activeFromDate);
            b.setActiveToDate(activeToDate);
            b.setAttributes(attributes);
            return b;
        }

        public static Builder create(RoleMemberContract contract) {
            Builder b = new Builder();
            b.setRoleMemberId(contract.getRoleMemberId());
            b.setRoleId(contract.getRoleId());
            b.setAttributes(contract.getAttributes());

            List<RoleResponsibilityAction.Builder> rraBuilders = new ArrayList<RoleResponsibilityAction.Builder>();
            if (!CollectionUtils.isEmpty(contract.getRoleRspActions())) {
                for (RoleResponsibilityActionContract rrac : contract.getRoleRspActions()) {
                    rraBuilders.add(RoleResponsibilityAction.Builder.create(rrac));
                }
            }
            b.setRoleRspActions(rraBuilders);

            b.setMemberId(contract.getMemberId());
            b.setMemberTypeCode(contract.getMemberTypeCode());
            b.setActiveFromDate(contract.getActiveFromDate());
            b.setActiveToDate(contract.getActiveToDate());
            return b;
        }

        public RoleMember build() {
            return new RoleMember(this);
        }

        public String getRoleMemberId() {
            return roleMemberId;
        }

        public void setRoleMemberId(String roleMemberId) {
            this.roleMemberId = roleMemberId;
        }

        public String getRoleId() {
            return roleId;
        }

        public void setRoleId(String roleId) {
            this.roleId = roleId;
        }

        public Map<String, String> getAttributes() {
            return attributes;
        }

        public void setAttributes(Map<String, String> attributes) {
            this.attributes = attributes;
        }

        public List<RoleResponsibilityAction.Builder> getRoleRspActions() {
            return roleRspActions;
        }

        public void setRoleRspActions(List<RoleResponsibilityAction.Builder> roleRspActions) {
            this.roleRspActions = roleRspActions;
        }

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            if (StringUtils.isBlank(memberId)) {
                throw new IllegalArgumentException("memberId may not be null");
            }
            this.memberId = memberId;
        }

        public String getMemberTypeCode() {
            return memberTypeCode;
        }

        public void setMemberTypeCode(String memberTypeCode) {
            if (StringUtils.isBlank(memberTypeCode)) {
                throw new IllegalArgumentException("memberTypeCode may not be null");
            }
            this.memberTypeCode = memberTypeCode;
        }

        public DateTime getActiveFromDate() {
            return activeFromDate;
        }

        public void setActiveFromDate(DateTime activeFromDate) {
            this.activeFromDate = activeFromDate;
        }

        public DateTime getActiveToDate() {
            return activeToDate;
        }

        public void setActiveToDate(DateTime activeToDate) {
            this.activeToDate = activeToDate;
        }

        @Override
        public boolean isActive(DateTime activeAsOfDate) {
            return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, activeAsOfDate);
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
        final static String ROLE_MEMBER_ID = "roleMemberId";
        final static String ROLE_ID = "roleId";
        final static String ATTRIBUTES = "attributes";
        final static String ROLE_RESPONSIBILITY_ACTIONS = "roleResponsibilityActions";
        final static String MEMBER_ID = "memberId";
        final static String MEMBER_TYPE_CODE = "memberTypeCode";
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "roleMember";
        final static String TYPE_NAME = "RoleMemberType";
    }
}
