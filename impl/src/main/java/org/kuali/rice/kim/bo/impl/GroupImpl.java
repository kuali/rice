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
package org.kuali.rice.kim.bo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.group.impl.GroupAttributeDataImpl;
import org.kuali.rice.kim.bo.group.impl.GroupMemberImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants.KimGroupMemberTypes;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This class is the implementation of a Kim Group 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name="KRIM_GRP_T")
public class GroupImpl extends PersistableBusinessObjectBase implements Group {

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
	@JoinColumn(name="GRP_ID", insertable=false, updatable=false)
	protected List<GroupAttributeDataImpl> groupAttributes = new TypedArrayList(GroupAttributeDataImpl.class);

	@Transient
	private List<Person> memberPersons;
	@Transient
	private List<GroupInfo> memberGroups;
	@Transient
	private KimTypeImpl kimTypeImpl;

	protected KimTypeImpl kimGroupType; 
	protected AttributeSet attributes;
	
	/**
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

	public String getKimTypeId() {
		return this.kimTypeId;
	}

	public void setKimTypeId(String kimTypeId) {
		this.kimTypeId = kimTypeId;
	}

	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	public KimTypeImpl getKimGroupType() {
		if (kimGroupType == null) {
			Map<String,String> pkMap = new HashMap<String,String>();
			pkMap.put("kimTypeId", kimTypeId);
			setKimGroupType((KimTypeImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, pkMap));			
		}
		return this.kimGroupType;
	}

	public void setKimGroupType(KimTypeImpl kimGroupType) {
		this.kimGroupType = kimGroupType;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return this.groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return this.groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return the groupDescription
	 */
	public String getGroupDescription() {
		return this.groupDescription;
	}

	/**
	 * @param groupDescription the groupDescription to set
	 */
	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	public AttributeSet getAttributes() {
		AttributeSet attributes = new AttributeSet( groupAttributes.size() );
        for ( GroupAttributeDataImpl attr : groupAttributes ) {
        	if ( attr.getKimAttribute() != null ) {
        		attributes.put(attr.getKimAttribute().getAttributeName(), attr.getAttributeValue());
        	} else {
        		attributes.put("Unknown Attribute ID: " + attr.getKimAttributeId(), attr.getAttributeValue());
        	}
        }

        return attributes;
	}
	
	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(AttributeSet attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the groupAttributes
	 */
	public List<GroupAttributeDataImpl> getGroupAttributes() {
		return this.groupAttributes;
	}

	/**
	 * @param groupAttributes the groupAttributes to set
	 */
	public void setGroupAttributes(List<GroupAttributeDataImpl> groupAttributes) {
		this.groupAttributes = groupAttributes;
	}

	/**
	 * @param kimTypeImpl the kimTypeImpl to set
	 */
	public void setKimTypeImpl(KimTypeImpl kimTypeImpl) {
		this.kimTypeImpl = kimTypeImpl;
	}

	/**
	 * @return the memberGroups
	 */
	public List<GroupInfo> getMemberGroups() {
		return this.memberGroups;
	}

	/**
	 * @param memberGroups the memberGroups to set
	 */
	protected void setMemberGroups(List<GroupInfo> memberGroups) {
		this.memberGroups = memberGroups;
	}

	/**
	 * @return the memberPersons
	 */
	public List<Person> getMemberPersons() {
		return this.memberPersons;
	}

	/**
	 * @param memberPersons the memberPersons to set
	 */
	protected void setMemberPersons(List<Person> memberPersons) {
		this.memberPersons = memberPersons;
	}

	/**
	 * @return the members
	 */
	public List<GroupMemberImpl> getMembers() {
		return this.members;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(List<GroupMemberImpl> members) {
		this.members = members;
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

    public void setMemberPersonsAndGroups() {
        List<Person> personMembers = new ArrayList<Person>();
        List<GroupInfo> groupMembers = new ArrayList<GroupInfo>();
        if (getMembers() != null) {
            for ( GroupMemberImpl groupMemberImpl : getMembers() ) {
                if ( groupMemberImpl.getMemberTypeCode().equals ( KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE )
                        && groupMemberImpl.isActive() ) {
                    personMembers.add( KIMServiceLocator.getPersonService().getPerson(groupMemberImpl.getMemberId()) );
                } else if (groupMemberImpl.getMemberTypeCode().equals ( KimGroupMemberTypes.GROUP_MEMBER_TYPE )
                        && groupMemberImpl.isActive() ) {
                    groupMembers.add( 
                    		KIMServiceLocator.getIdentityManagementService().getGroup(groupMemberImpl.getMemberId()) );
                }
            }
        }
        setMemberPersons(personMembers);
        setMemberGroups(groupMembers);
    }
    
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if ( !(object instanceof Group) ) {
			return false;
		}
		Group rhs = (Group)object;
		return new EqualsBuilder().append( this.groupId, rhs.getGroupId() ).isEquals();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder( -460627871, 746615189 ).append( this.groupId ).toHashCode();
	}

	public GroupAttributeDataImpl getGroupAttributeById(String attributeId) {
	    for (GroupAttributeDataImpl gad : getGroupAttributes()) {
	        if (gad.getAttributeValue().equals(attributeId.trim())) {
	            return gad;
	        }
	    }
	    return null;
	}

    public KimTypeImpl getKimTypeImpl() {
        return KIMServiceLocator.getTypeInternalService().getKimType(this.kimTypeId);
    }

}
