/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.bo.group.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.util.KimConstants.KimGroupMemberTypes;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name="KRIM_GRP_T")
public class KimGroupImpl extends PersistableBusinessObjectBase implements KimGroup {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="GRP_ID")
	protected String groupId;
	@Column(name="GRP_NM")
	protected String groupName;
	@Column(name="GRP_DESC",length=4000)
	protected String groupDescription;

	@Column(name="ACTV_IND")
	@Type(type="yes_no")
	protected boolean active;

	@Column(name="KIM_TYP_ID")
	protected String kimTypeId;
	@Column(name="NMSPC_CD")
	protected String namespaceCode;

	@OneToMany(targetEntity=GroupMemberImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="GRP_ID", insertable=false, updatable=false)
	protected List<GroupMemberImpl> members;

	@OneToMany(targetEntity=GroupAttributeDataImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="TARGET_PRIMARY_KEY", insertable=false, updatable=false)
	protected List<GroupAttributeDataImpl> groupAttributes = new TypedArrayList(GroupAttributeDataImpl.class);

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "groupId", groupId );
		m.put( "namespaceCode", namespaceCode );
		m.put( "groupName", groupName );
		return m;
	}

	public AttributeSet getAttributes() {
		AttributeSet attributes = new AttributeSet( groupAttributes.size() );
        for ( GroupAttributeDataImpl attr : groupAttributes ) {
            attributes.put(attr.getKimAttribute().getAttributeName(), attr.getAttributeValue());
        }

        return attributes;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupDescription() {
		return this.groupDescription;
	}

	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}

	public List<GroupMemberImpl> getMembers() {
		return this.members;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<String> getMemberGroupIds() {
		List<String> groupMembers = new ArrayList<String>();
		if (getMembers() != null) {
    		for ( GroupMemberImpl groupMemberImpl : getMembers() ) {
    			if ( groupMemberImpl.getMemberTypeCode().equals ( KimGroupMemberTypes.GROUP_MEMBER_TYPE )
    					&& groupMemberImpl.isActive() ) {
    				groupMembers.add( groupMemberImpl.getMemberId() );
    			}
    		}
		}
		return groupMembers;
	}

	public List<String> getMemberPrincipalIds() {
		List<String> groupMembers = new ArrayList<String>();
		if (getMembers() != null) {
    		for ( GroupMemberImpl groupMemberImpl : getMembers() ) {
    			if ( groupMemberImpl.getMemberTypeCode().equals ( KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE )
    					&& groupMemberImpl.isActive() ) {
    				groupMembers.add( groupMemberImpl.getMemberId() );
    			}
    		}
		}
		return groupMembers;
	}

	public String getKimTypeId() {
		return this.kimTypeId;
	}

	public void setKimTypeId(String typeId) {
		this.kimTypeId = typeId;
	}

	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if ( !(object instanceof KimGroup) ) {
			return false;
		}
		KimGroup rhs = (KimGroup)object;
		return new EqualsBuilder().append( this.groupId, rhs.getGroupId() ).isEquals();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder( -460627871, 746615189 ).append( this.groupId ).toHashCode();
	}

	public List<GroupAttributeDataImpl> getGroupAttributes() {
		return this.groupAttributes;
	}

	public void setGroupAttributes(List<GroupAttributeDataImpl> groupAttributes) {
		this.groupAttributes = groupAttributes;
	}

	public void setMembers(List<GroupMemberImpl> members) {
		this.members = members;
	}
}
