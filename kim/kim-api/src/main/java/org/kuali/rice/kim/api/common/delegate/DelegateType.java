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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@XmlRootElement(name = DelegateType.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DelegateType.Constants.TYPE_NAME, propOrder = {
        DelegateType.Elements.ROLE_ID,
        DelegateType.Elements.DELEGATION_ID,
        DelegateType.Elements.DELEGATION_TYPE_CODE,
        DelegateType.Elements.KIM_TYPE_ID,
        DelegateType.Elements.MEMBERS,
        DelegateType.Elements.ACTIVE,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class DelegateType implements DelegateTypeContract, ModelObjectComplete {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = Elements.ROLE_ID)
    private final String roleId;

    @XmlElement(name = Elements.DELEGATION_ID)
    private final String delegationId;

    @XmlElement(name = Elements.DELEGATION_TYPE_CODE)
    private final String delegationTypeCode;

    @XmlElement(name = Elements.KIM_TYPE_ID)
    private final String kimTypeId;

    @XmlElement(name = Elements.MEMBERS)
    private final List<Delegate> members;

    @XmlElement(name = Elements.ACTIVE)
    private final boolean active;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Default constructor invoked by JAXB only
     */
    @SuppressWarnings("unused")
    private DelegateType() {
        roleId = null;
        delegationId = null;
        delegationTypeCode = null;
        kimTypeId = null;
        members = null;
        active = false;
    }

    private DelegateType(Builder b) {
        roleId = b.getRoleId();
        delegationId = b.getDelegationId();
        delegationTypeCode = b.getDelegationTypeCode();
        kimTypeId = b.getKimTypeId();
        active = b.isActive();

        List<Delegate> delegateMembers = new ArrayList<Delegate>();
        if (!CollectionUtils.isEmpty(b.getMembers())) {
            for (Delegate.Builder delgateBuilder : b.getMembers()) {
                delegateMembers.add(delgateBuilder.build());
            }
        }
        members = delegateMembers;
    }

    @Override
    public String getKimTypeId() {
        return this.kimTypeId;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public String getDelegationTypeCode() {
        return this.delegationTypeCode;
    }

    @Override
    public String getDelegationId() {
        return this.delegationId;
    }

    @Override
    public String getRoleId() {
        return this.roleId;
    }

    @Override
    public List<Delegate> getMembers() {
        return Collections.unmodifiableList(this.members);
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


    public static final class Builder implements DelegateTypeContract, ModelBuilder, ModelObjectComplete {
        private String roleId;
        private String delegationId;
        private String delegationTypeCode;
        private String kimTypeId;
        private List<Delegate.Builder> members;
        private boolean active;

        public static Builder create(DelegateTypeContract dtc) {
            Builder b = new Builder();
            b.setRoleId(dtc.getRoleId());
            b.setDelegationId(dtc.getDelegationId());
            b.setDelegationTypeCode(dtc.getDelegationTypeCode());
            b.setActive(dtc.isActive());

            ArrayList<Delegate.Builder> delegateBuilders = new ArrayList<Delegate.Builder>();
            for (DelegateContract delegate : dtc.getMembers()) {
                delegateBuilders.add(Delegate.Builder.create(delegate));
            }
            b.setMembers(delegateBuilders);

            return b;
        }

        public static Builder create(String roleId, String delegationId, String delegationTypeCode, List<Delegate.Builder> members) {
            Builder b = new Builder();
            b.setRoleId(roleId);
            b.setDelegationId(delegationId);
            b.setDelegationTypeCode(delegationTypeCode);
            b.setMembers(members);
            b.setActive(true);

            return b;
        }

        @Override
        public DelegateType build() {
            return new DelegateType(this);
        }

        @Override
        public String getRoleId() {
            return roleId;
        }

        public void setRoleId(String roleId) {
            if (StringUtils.isBlank(roleId)) {
                throw new IllegalArgumentException("roleId cannot be null or blank");
            }
            this.roleId = roleId;
        }

        @Override
        public String getDelegationId() {
            return delegationId;
        }

        public void setDelegationId(String delegationId) {
            if (StringUtils.isBlank(delegationId)) {
                throw new IllegalArgumentException("delegationId cannot be null or blank");
            }

            this.delegationId = delegationId;
        }

        @Override
        public String getDelegationTypeCode() {
            return delegationTypeCode;
        }

        public void setDelegationTypeCode(String delegationTypeCode) {
            if (StringUtils.isBlank(delegationTypeCode)) {
                throw new IllegalArgumentException("delegationTypeCode cannot be null or blank");
            }
            this.delegationTypeCode = delegationTypeCode;
        }

        @Override
        public String getKimTypeId() {
            return kimTypeId;
        }

        public void setKimTypeId(String kimTypeId) {
            this.kimTypeId = kimTypeId;
        }

        @Override
        public List<Delegate.Builder> getMembers() {
            return members;
        }

        public void setMembers(List<Delegate.Builder> members) {
            this.members = members;
        }

        @Override
        public boolean isActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
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
        static final String ROLE_ID = "roleId";
        static final String DELEGATION_ID = "delegationId";
        static final String DELEGATION_TYPE_CODE = "delegationTypeCode";
        static final String KIM_TYPE_ID = "kimTypeId";
        static final String MEMBERS = "members";
        static final String ACTIVE = "active";
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "delegateType";
        final static String TYPE_NAME = "DelegateTypeType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }
}
