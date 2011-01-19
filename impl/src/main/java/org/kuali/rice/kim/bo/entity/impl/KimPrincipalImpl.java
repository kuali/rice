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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.bo.entity.impl;

import org.hibernate.annotations.Type;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRIM_PRNCPL_T")
public class KimPrincipalImpl extends PersistableBusinessObjectBase implements KimPrincipal {

	private static final long serialVersionUID = 4480581610252159266L;

	@Id
	@Column(name="PRNCPL_ID", columnDefinition="VARCHAR(40)")
	private String principalId;

	@Column(name="PRNCPL_NM")
	private String principalName;

	@Column(name="ENTITY_ID")
	private String entityId;

	@Column(name="PRNCPL_PSWD")
	private String password;

	@Column(name="ACTV_IND")
	@Type(type="yes_no")
	private boolean active;

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
		this.principalName = principalName.toLowerCase();
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
