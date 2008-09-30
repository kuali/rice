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
 * See the License for the specific language governing Responsibilitys and
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

import org.kuali.rice.kim.bo.role.KimResponsibility;
import org.kuali.rice.kim.bo.role.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.types.KimType;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KR_KIM_RESPONSIBILITY_T")
public class KimResponsibilityImpl extends PersistableBusinessObjectBase implements KimResponsibility {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="RESP_ID")
	protected String responsibilityId;
	@Column(name="NMSPC_CD")
	protected String namespaceCode;
	@Column(name="RESP_NM")
	protected String responsibilityName;
	@Column(name="KIM_TYPE_ID")
	protected String kimTypeId;
	@Column(name="RESP_DESC", length=400)
	protected String responsibilityDescription;
	@Column(name="ACTV_IND")
	protected boolean active;

	@OneToOne(targetEntity=KimTypeImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "KIM_TYPE_ID", insertable = false, updatable = false)
	protected KimType kimResponsibilityType;
	
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

	public KimType getKimResponsibilityType() {
		return kimResponsibilityType;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimResponsibility#getNamespaceCode()
	 */
	public String getNamespaceCode() {
		return namespaceCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimResponsibility#getResponsibilityDescription()
	 */
	public String getResponsibilityDescription() {
		return responsibilityDescription;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimResponsibility#getResponsibilityId()
	 */
	public String getResponsibilityId() {
		return responsibilityId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimResponsibility#getResponsibilityName()
	 */
	public String getResponsibilityName() {
		return responsibilityName;
	}

	public void setKimTypeId(String kimTypeId) {
		this.kimTypeId = kimTypeId;
	}

	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	public void setResponsibilityDescription(String responsibilityDescription) {
		this.responsibilityDescription = responsibilityDescription;
	}

	public void setResponsibilityName(String responsibilityName) {
		this.responsibilityName = responsibilityName;
	}

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "responsibilityId", responsibilityId );
		m.put( "namespaceCode", namespaceCode );
		m.put( "responsibilityName", responsibilityName );
		m.put( "kimTypeId", kimTypeId );
		return m;
	}

	public KimResponsibilityInfo toSimpleInfo() {
		KimResponsibilityInfo dto = new KimResponsibilityInfo();
		
		dto.setResponsibilityId( getResponsibilityId() );
		dto.setResponsibilityName( getResponsibilityName() );
		dto.setNamespaceCode( getNamespaceCode() );
		dto.setResponsibilityDescription( getResponsibilityDescription() );
		dto.setKimTypeId( getKimTypeId() );
		dto.setActive( isActive() );
		
		return dto;
	}
	
	public void fromInfo( KimResponsibilityInfo info ) {
		responsibilityId = info.getResponsibilityId();
		responsibilityName = info.getResponsibilityName();
		responsibilityDescription = info.getResponsibilityDescription();
		namespaceCode = info.getNamespaceCode();
		kimTypeId = info.getKimTypeId();
		active = info.isActive();
	}
}
