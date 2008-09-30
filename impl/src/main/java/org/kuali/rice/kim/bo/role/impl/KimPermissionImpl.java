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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.role.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.KimType;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KR_KIM_PERMISSION_T")
public class KimPermissionImpl extends PersistableBusinessObjectBase implements KimPermission {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="PERM_ID")
	protected String permissionId;
	@Column(name="NMSPC_CD")
	protected String namespaceCode;
	@Column(name="PERM_NM")
	protected String permissionName;
	@Column(name="KIM_TYPE_ID")
	protected String kimTypeId;
	@Column(name="PERM_DESC", length=400)
	protected String permissionDescription;
	@Column(name="ACTV_IND")
	protected boolean active;

	@OneToOne(targetEntity=KimTypeImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "KIM_TYPE_ID", insertable = false, updatable = false)
	protected KimType kimPermissionType;
	
	/**
	 * @see org.kuali.rice.kim.bo.types.KimType#getKimTypeId()
	 */
	public String getKimTypeId() {
		return kimTypeId;
	}

	/**
	 * @see org.kuali.rice.kns.bo.Inactivateable#isActive()
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @see org.kuali.rice.kns.bo.Inactivateable#setActive(boolean)
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	public KimType getKimPermissionType() {
		return kimPermissionType;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimPermission#getNamespaceCode()
	 */
	public String getNamespaceCode() {
		return namespaceCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimPermission#getPermissionDescription()
	 */
	public String getPermissionDescription() {
		return permissionDescription;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimPermission#getPermissionId()
	 */
	public String getPermissionId() {
		return permissionId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimPermission#getPermissionName()
	 */
	public String getPermissionName() {
		return permissionName;
	}

	public void setKimTypeId(String kimTypeId) {
		this.kimTypeId = kimTypeId;
	}

	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	public void setPermissionDescription(String permissionDescription) {
		this.permissionDescription = permissionDescription;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "permissionId", permissionId );
		m.put( "namespaceCode", namespaceCode );
		m.put( "permissionName", permissionName );
		m.put( "kimTypeId", kimTypeId );
		return m;
	}

	public KimPermissionInfo toSimpleInfo() {
		KimPermissionInfo dto = new KimPermissionInfo();
		
		dto.setPermissionId( getPermissionId() );
		dto.setPermissionName( getPermissionName() );
		dto.setNamespaceCode( getNamespaceCode() );
		dto.setPermissionDescription( getPermissionDescription() );
		dto.setKimTypeId( getKimTypeId() );
		dto.setActive( isActive() );
		
		return dto;
	}
	
	public void fromInfo( KimPermissionInfo info ) {
		permissionId = info.getPermissionId();
		permissionName = info.getPermissionName();
		permissionDescription = info.getPermissionDescription();
		namespaceCode = info.getNamespaceCode();
		kimTypeId = info.getKimTypeId();
		active = info.isActive();
	}
}
