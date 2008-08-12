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
package org.kuali.rice.kim.v2.bo.impl;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.kim.v2.bo.Principal;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KR_KIM_ENTITY_PRINCIPAL_T")
public class PrincipalImpl extends PersistableBusinessObjectBase implements Principal {

	private static final long serialVersionUID = 4480581610252159266L;

	@Id
	@Column(name="entity_prncpl_id")
	private String principalId;
	
	@Column(name="entity_prncpl_nm")
	private String principalName;

	@Column(name="entity_id")
	private String entityId;
	
	@Column(name="prncpl_pswd")
	private String password;
	
	@Column(name="actv_ind")
	private boolean active;

	// TODO: Add a FK 1:1 ref to Entity after it is created.
	
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "principalId", this.principalId );
		m.put( "principalName", this.principalName );
		m.put( "entityId", this.entityId );
		m.put( "password", this.password );
		m.put( "active", this.active );
		return m;
	}

	public String getPrincipalId() {
		return this.principalId;
	}

	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}

	public String getPrincipalName() {
		return this.principalName;
	}

	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
