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

import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.unNullify;

import org.kuali.rice.kim.bo.entity.KimPrincipal;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimPrincipalInfo extends KimInactivatableInfo implements KimPrincipal {

	private static final long serialVersionUID = 4480581610252159266L;

	private String principalId = "";
	private String principalName = "";
	private String entityId = "";

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
			principalId = unNullify( p.getPrincipalId() );
			entityId = unNullify( p.getEntityId() );
			principalName = unNullify( p.getPrincipalName() );
			active = p.isActive();
		}
	}
	
	public String getPrincipalId() {
		return unNullify( this.principalId);
	}

	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}

	public String getPrincipalName() {
		return unNullify( this.principalName);
	}

	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}

	public String getEntityId() {
		return unNullify( this.entityId);
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

}
