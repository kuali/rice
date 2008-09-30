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
package org.kuali.rice.kim.bo.role.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.kuali.rice.kim.bo.role.RoleMember;
import org.kuali.rice.kim.bo.role.RoleMemberAttributeData;
import org.kuali.rice.kim.bo.types.KimAttributeContainer;
import org.kuali.rice.kim.bo.types.KimAttributeData;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googleroles.com)
 *
 */
@MappedSuperclass
public abstract class RoleMemberBase extends PersistableBusinessObjectBase implements RoleMember, KimAttributeContainer {

	@Id
	@Column(name="ROLE_MEMBER_ID")
	protected String roleMemberId;
	
	@Column(name="ROLE_ID")
	protected String roleId;
	
	@Column(name="MEMBER_ID")
	protected String memberId;
	
	// TODO: uncomment this when the class has been implemented
	//@OneToMany(targetEntity=RoleMemberAttributeDataImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
//	@JoinColumns({
//		@JoinColumn(name="ROLE_ID", insertable=false, updatable=false),
//		@JoinColumn(name="MEMBER_ID", insertable=false, updatable=false)
//	})	
	protected List<RoleMemberAttributeData> qualifier;
	
	public String getRoleMemberId() {
		return this.roleMemberId;
	}
	public void setRoleMemberId(String roleMemberId) {
		this.roleMemberId = roleMemberId;
	}
	public String getRoleId() {
		return this.roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getMemberId() {
		return this.memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	
	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "roleMemberId", roleMemberId );
		m.put( "roleId", roleId );
		m.put( "memberTypeCode", getRoleMemberTypeCode() );
		m.put( "memberId", memberId );
		return m;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.RoleMember#getQualifier()
	 */
	public List<RoleMemberAttributeData> getQualifier() {
		return this.qualifier;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.RoleMember#getQualifierAsMap()
	 */
	public Map<String, String> getQualifierAsMap() {
		return getAttributesAsMap();
	}
	public void setQualifier(List<RoleMemberAttributeData> qualifications) {
		this.qualifier = qualifications;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.types.KimAttributeContainer#getAttributes()
	 */
	public List<? extends KimAttributeData> getAttributes() {
		return qualifier;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.types.KimAttributeContainer#getAttributesAsMap()
	 */
	public Map<String,String> getAttributesAsMap() {
		Map<String,String> m = new HashMap<String,String>();
		for ( KimAttributeData data : getAttributes() ) {
			m.put( data.getKimAttribute().getAttributeName(), data.getAttributeValue() );
		}
		return m;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.role.RoleMember#hasQualifier()
	 */
	public boolean hasQualifier() {
		return !getAttributes().isEmpty();
	}
}
