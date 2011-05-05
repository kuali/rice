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

package org.kuali.rice.kim.api.permission;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.kim.bo.role.dto.DelegateInfo;
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


@XmlRootElement(name = PermissionAssignee.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = PermissionAssignee.Constants.TYPE_NAME, propOrder = {
        PermissionAssignee.Elements.PRINCIPAL_ID,
        PermissionAssignee.Elements.GROUP_ID,
        PermissionAssignee.Elements.DELEGATES,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class PermissionAssignee implements PermissionAssigneeContract, ModelObjectComplete {
    @XmlElement(name = Elements.PRINCIPAL_ID, required = false)
    private final String principalId;

    @XmlElement(name = Elements.GROUP_ID, required = true)
    private final String groupId;

    @XmlElementWrapper(name = Elements.DELEGATES, required = false)
    @XmlElement(name = Elements.DELEGATE, required = false)
    // TODO Need modelized DelegateInfo
    private final List<DelegateInfo> delegates;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
	 *  A constructor to be used only by JAXB unmarshalling.
	 *  
	 */
    private PermissionAssignee() {
        this.principalId = null;
        this.groupId = null;
        this.delegates = null;
    }
 
    /**
	 * A constructor using the Builder.
	 * 
	 * @param builder
	 */
    public PermissionAssignee(Builder builder) {
        this.principalId = builder.getPrincipalId();
        this.groupId = builder.getGroupId();
        this.delegates = new ArrayList<DelegateInfo>();
        if (!CollectionUtils.isEmpty(builder.getDelegates())) {
            for (DelegateInfo delegate: builder.getDelegates()) {
                delegates.add(delegate);
            }
        }
    }

	/**
	 * @see org.kuali.rice.kim.api.permission.PermissionAssigneeContract#getPrincipalId()
	 */
	@Override
	public String getPrincipalId() {
		return this.principalId;
	}

	/**
	 * @see org.kuali.rice.kim.api.permission.PermissionAssigneeContract#getGroupId()
	 */
	@Override
	public String getGroupId() {
		return this.groupId;
	}

	/**
	 * @see org.kuali.rice.kim.api.permission.PermissionAssigneeContract#getDelegates()
	 */
	@Override
	public List<DelegateInfo> getDelegates() {
		return this.delegates;
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
     * This builder constructs a PermissionAssignee enforcing the constraints of the {@link PermissionAssigneeContract}.
     */
    public static final class Builder implements PermissionAssigneeContract, ModelBuilder, Serializable {
        private String principalId;
        private String groupId;
        private List<DelegateInfo> delegates;

        private Builder(String principalId, String groupId, List<DelegateInfo> delegates) {
            setPrincipalId(principalId);
            setGroupId(groupId);
            setDelegates(delegates);
        }

        /**
         * Creates a PermissionAttribute with the required fields.
         */
        public static Builder create(String principalId, String groupId, List<DelegateInfo> delegates) {
            return new Builder(principalId, groupId, delegates);
        }

        /**
         * creates a PermissionAttribute from an existing {@link PermissionAttributeContract}.
         */
        public static Builder create(PermissionAssigneeContract contract) {
            Builder builder = new Builder(contract.getPrincipalId(), contract.getGroupId(), contract.getDelegates());
            return builder;
        }

        @Override
        public String getPrincipalId() {
            return principalId;
        }

        public void setPrincipalId(final String principalId) {
            if (StringUtils.isBlank(principalId)) {
                throw new IllegalArgumentException("principalId is blank");
            }
            this.principalId = principalId;
        }

        @Override
        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(final String groupId) {
        	if (StringUtils.isBlank(groupId)) {
                throw new IllegalArgumentException("groupId is blank");
            }
        	this.groupId = groupId;
        }

		@Override
		public List<DelegateInfo> getDelegates() {
			return delegates;
		}
		
        public void setDelegates(final List<DelegateInfo> delegates) {
        	if (delegates == null || delegates.isEmpty()) {
                throw new IllegalArgumentException("delegates is null or empty");
            }
        	this.delegates = delegates;
        }		
		
		@Override
		public PermissionAssignee build() {
			return new PermissionAssignee(this);
		}
       
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "permissionAssignee";
        final static String TYPE_NAME = "PermissionAssigneeType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    static class Elements {
        final static String PRINCIPAL_ID = "principalId";
        final static String GROUP_ID = "groupId";
        final static String DELEGATES = "delegates";
        final static String DELEGATE = "delegate";
    }
}
