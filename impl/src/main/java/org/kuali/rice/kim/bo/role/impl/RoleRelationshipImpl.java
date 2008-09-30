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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.kim.bo.role.RoleRelationship;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name="KR_KIM_ROLE_REL_T")
public class RoleRelationshipImpl extends PersistableBusinessObjectBase implements RoleRelationship {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ROLE_REL_ID")
	protected String roleRelationshipId;
	
	@Column(name="ROLE_ID")
	protected String roleId;
	
	@Column(name="CONTAINED_ROLE_ID")
	protected String containedRoleId;
	
	public String getRoleRelationshipId() {
		return this.roleRelationshipId;
	}
	public void setRoleRelationshipId(String roleRelationshipId) {
		this.roleRelationshipId = roleRelationshipId;
	}
	public String getRoleId() {
		return this.roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getContainedRoleId() {
		return this.containedRoleId;
	}
	public void setContainedRoleId(String containedRoleId) {
		this.containedRoleId = containedRoleId;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
		m.put("roleRelationshipId", roleRelationshipId);
		m.put("roleId", roleId);
		m.put("containedRoleId", containedRoleId);
		return null;
	}
}
