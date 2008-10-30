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

import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.kuali.rice.kim.bo.role.KimDelegation;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googleroles.com)
 *
 */
@Entity
@Table(name="KRIM_DLGN_T")
public class KimDelegationImpl extends PersistableBusinessObjectBase implements KimDelegation {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="DLGN_ID")
	protected String delegationId;
	
	@Column(name="ROLE_ID")
	protected String roleId;

	@Column(name="ACTV_IND")
	protected boolean active;

	@Column(name="KIM_TYP_ID")
	protected String kimTypeId;

	@Column(name="DLGN_TYP_CD")
	protected String delegationTypeCode;
	
	@OneToMany(targetEntity=KimDelegationGroupImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="DLGN_ID", insertable=false, updatable=false)
	protected List<KimDelegationGroupImpl> memberGroups;

	@OneToMany(targetEntity=KimDelegationPrincipalImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="DLGN_ID", insertable=false, updatable=false)
	protected List<KimDelegationPrincipalImpl> memberPrincipals;

	@OneToMany(targetEntity=KimDelegationRoleImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="DLGN_ID", insertable=false, updatable=false)
	protected List<KimDelegationRoleImpl> memberRoles;
	
	@OneToMany(targetEntity=KimDelegationAttributeDataImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="DLGN_ID", insertable=false, updatable=false)
	protected List<KimDelegationAttributeDataImpl> attributes;
	
	@ManyToOne(targetEntity=KimTypeImpl.class,fetch=FetchType.LAZY)
	@JoinColumn(name="KIM_TYP_ID", insertable=false, updatable=false)
	protected KimTypeImpl kimType; 
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "delegationId", delegationId );
		m.put( "roleId", roleId );
		m.put( "delegationTypeCode", delegationTypeCode );
		return m;
	}

	public String getRoleId() {
		return this.roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getKimTypeId() {
		return this.kimTypeId;
	}

	public void setKimTypeId(String typeId) {
		this.kimTypeId = typeId;
	}

	public KimTypeImpl getKimType() {
		return this.kimType;
	}

	public void setKimType(KimTypeImpl kimType) {
		this.kimType = kimType;
	}

	public String getDelegationTypeCode() {
		return this.delegationTypeCode;
	}

	public void setDelegationTypeCode(String delegationTypeCode) {
		this.delegationTypeCode = delegationTypeCode;
	}

	public AttributeSet getQualifier() {
		AttributeSet attribs = new AttributeSet();
		
		if ( attributes == null ) {
			return attribs;
		}
		for ( KimDelegationAttributeDataImpl attr : attributes ) {
			attribs.put( attr.getKimAttribute().getAttributeName(), attr.getAttributeValue() );
		}
		return attribs;
	}

	public String getDelegationId() {
		return this.delegationId;
	}

	public void setDelegationId(String delegationId) {
		this.delegationId = delegationId;
	}

	public List<KimDelegationGroupImpl> getMemberGroups() {
		return this.memberGroups;
	}

	public void setMemberGroups(List<KimDelegationGroupImpl> memberGroups) {
		this.memberGroups = memberGroups;
	}

	public List<KimDelegationPrincipalImpl> getMemberPrincipals() {
		return this.memberPrincipals;
	}

	public void setMemberPrincipals(List<KimDelegationPrincipalImpl> memberPrincipals) {
		this.memberPrincipals = memberPrincipals;
	}

	public List<KimDelegationRoleImpl> getMemberRoles() {
		return this.memberRoles;
	}

	public void setMemberRoles(List<KimDelegationRoleImpl> memberRoles) {
		this.memberRoles = memberRoles;
	}

	public List<KimDelegationAttributeDataImpl> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(List<KimDelegationAttributeDataImpl> attributes) {
		this.attributes = attributes;
	}
	
	
//	public KimRoleInfo toSimpleInfo() {
//		KimRoleInfo dto = new KimRoleInfo();
//		
//		dto.setRoleId( getRoleId() );
//		dto.setRoleName( getRoleName() );
//		dto.setNamespaceCode( getNamespaceCode() );
//		dto.setRoleDescription( getRoleDescription() );
//		dto.setKimTypeId( getKimTypeId() );
//		dto.setActive( isActive() );
//		
//		return dto;
//	}
	
}
