/*
 * Copyright 2007-2008 The Kuali Foundation
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
 * See the License for the specific language governing responsibilitys and
 * limitations under the License.
 */
package org.kuali.rice.kim.bo.role.impl;

import org.hibernate.annotations.Type;
import org.kuali.rice.kim.bo.role.RoleResponsibility;
import org.kuali.rice.kim.bo.role.dto.RoleResponsibilityInfo;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

import javax.persistence.*;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRIM_ROLE_RSP_T")
public class RoleResponsibilityImpl extends PersistableBusinessObjectBase implements RoleResponsibility {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="ROLE_RSP_ID")
	protected String roleResponsibilityId;
	@Column(name="ROLE_ID")
	protected String roleId;
	@Column(name="RSP_ID")
	protected String responsibilityId;
	@Type(type="yes_no")
	@Column(name="ACTV_IND")
	protected boolean active;
	
	@ManyToOne(targetEntity=KimResponsibilityImpl.class, fetch = FetchType.EAGER, cascade = { })
	@JoinColumn(name = "RSP_ID", insertable = false, updatable = false)
	protected KimResponsibilityImpl kimResponsibility;

	/**
	 * @see org.kuali.rice.kim.bo.role.RoleResponsibility#getResponsibilityId()
	 */
	public String getResponsibilityId() {
		return responsibilityId;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.role.RoleResponsibility#getRoleId()
	 */
	public String getRoleId() {
		return roleId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.RoleResponsibility#getRoleResponsibilityId()
	 */
	public String getRoleResponsibilityId() {
		return roleResponsibilityId;
	}

	public void setResponsibilityId(String responsibilityId) {
		this.responsibilityId = responsibilityId;
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

	public void setRoleResponsibilityId(String roleResponsibilityId) {
		this.roleResponsibilityId = roleResponsibilityId;
	}

	public KimResponsibilityImpl getKimResponsibility() {
		return this.kimResponsibility;
	}

	public void setKimResponsibility(KimResponsibilityImpl kimResponsibility) {
		this.kimResponsibility = kimResponsibility;
	}

	public RoleResponsibilityInfo toSimpleInfo() {
		RoleResponsibilityInfo roleResponsibilityInfo = new RoleResponsibilityInfo();
		roleResponsibilityInfo.setRoleId(getRoleId());
		roleResponsibilityInfo.setResponsibilityId(getResponsibilityId());
		roleResponsibilityInfo.setRoleResponsibilityId(getRoleResponsibilityId());
		roleResponsibilityInfo.setKimResponsibilityInfo(getKimResponsibility().toSimpleInfo());
		return roleResponsibilityInfo;
	}

}
