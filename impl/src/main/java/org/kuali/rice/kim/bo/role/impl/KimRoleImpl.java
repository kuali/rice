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

import java.util.ArrayList;
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
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kuali.rice.kim.bo.role.KimRole;
import org.kuali.rice.kim.bo.role.RoleGroup;
import org.kuali.rice.kim.bo.role.RoleMember;
import org.kuali.rice.kim.bo.role.RolePrincipal;
import org.kuali.rice.kim.bo.role.RoleRelationship;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.types.KimType;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googleroles.com)
 *
 */
@Entity
@Table(name="KR_KIM_ROLE_T")
public class KimRoleImpl extends PersistableBusinessObjectBase implements KimRole {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="ROLE_ID")
	protected String roleId;
	@Column(name="ROLE_NM")
	protected String roleName;
	@Column(name="ROLE_DESC",length=4000)
	protected String roleDescription;
	@Column(name="ACTV_IND")
	protected boolean active;
	@Column(name="TYP_ID")
	protected String kimTypeId;
	@Column(name="NMSPC_CD")
	protected String namespaceCode;

	@OneToMany(targetEntity=RoleRelationshipImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="ROLE_ID", insertable=false, updatable=false)
	protected List<RoleRelationship> containedRoles;

	@OneToMany(targetEntity=RoleGroupImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="ROLE_ID", insertable=false, updatable=false)
	protected List<RoleGroup> memberGroups;

	@OneToMany(targetEntity=RolePrincipalImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="ROLE_ID", insertable=false, updatable=false)
	protected List<RolePrincipal> memberPrincipals;

	@ManyToOne(targetEntity=KimTypeImpl.class,fetch=FetchType.LAZY)
	@Transient
	protected KimType kimRoleType; 
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "roleId", roleId );
		m.put( "namespaceCode", namespaceCode );
		m.put( "roleName", roleName );
		return m;
	}

	public String getRoleId() {
		return this.roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleDescription() {
		return this.roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<RoleMember> getMembers() {
		ArrayList<RoleMember> m = new ArrayList<RoleMember>();
		m.addAll( getMemberGroups() );
		m.addAll( getMemberPrincipals() );
		return m;
	}

	public List<RoleRelationship> getContainedRoles() {
		return this.containedRoles;
	}

	public void setContainedRoles(List<RoleRelationship> containedRoles) {
		this.containedRoles = containedRoles;
	}

	public List<RoleGroup> getMemberGroups() {
		return this.memberGroups;
	}

	public void setMemberGroups(List<RoleGroup> memberGroups) {
		this.memberGroups = memberGroups;
	}


	public List<RolePrincipal> getMemberPrincipals() {
		return this.memberPrincipals;
	}

	public void setMemberPrincipals(List<RolePrincipal> memberPrincipals) {
		this.memberPrincipals = memberPrincipals;
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
		if ( !(object instanceof KimRole) ) {
			return false;
		}
		KimRole rhs = (KimRole)object;
		return new EqualsBuilder().append( this.roleId, rhs.getRoleId() ).isEquals();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder( -460627871, 746615189 ).append( this.roleId ).toHashCode();
	}

	public KimType getKimRoleType() {
		return this.kimRoleType;
	}

	public void setKimRoleType(KimType kimRoleType) {
		this.kimRoleType = kimRoleType;
	}
	
	public KimRoleInfo toSimpleInfo() {
		KimRoleInfo dto = new KimRoleInfo();
		
		dto.setRoleId( getRoleId() );
		dto.setRoleName( getRoleName() );
		dto.setNamespaceCode( getNamespaceCode() );
		dto.setRoleDescription( getRoleDescription() );
		dto.setKimTypeId( getKimTypeId() );
		dto.setActive( isActive() );
		
		return dto;
	}
	
}
