/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.bo.entity.dto;

import org.kuali.rice.kim.bo.entity.KimPrincipal;

/**
 * DTO for a principal associated with a KIM entity 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimPrincipalInfo extends KimInactivatableInfo implements KimPrincipal {

	private static final long serialVersionUID = 4480581610252159266L;

	private String principalId;
	private String principalName;
	private String entityId;
	private String password;
	
	/**
	 * 
	 */
	public KimPrincipalInfo() {
		super();
		active = true;
	}
	
	/**
	 * 
	 */
	public KimPrincipalInfo( KimPrincipal p ) {
		this();
		if ( p != null ) {
			principalId = p.getPrincipalId();
			entityId = p.getEntityId();
			principalName = p.getPrincipalName();
			password = p.getPassword();
			active = p.isActive();
		}
	}
	
	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimPrincipal#getPrincipalId()
	 */
	public String getPrincipalId() {
		return principalId;
	}

	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimPrincipal#getPrincipalName()
	 */
	public String getPrincipalName() {
		return principalName;
	}

	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimPrincipal#getEntityId()
	 */
	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimPrincipal#getPassword()
	 */
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
