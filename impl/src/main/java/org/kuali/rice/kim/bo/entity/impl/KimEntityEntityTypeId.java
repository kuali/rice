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
package org.kuali.rice.kim.bo.entity.impl;

import javax.persistence.Column;
import javax.persistence.Id;

import org.kuali.rice.core.persistence.CompositePrimaryKeyBase;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimEntityEntityTypeId extends CompositePrimaryKeyBase {
	@Id
	@Column(name = "ENT_TYP_CD")
    protected String entityTypeCode;
	@Id
	@Column(name = "ENTITY_ID")
	protected String entityId;
	
	/**
	 * @return the roleId
	 */
	public String getEntityId() {
		return this.entityId;
	}
	/**
	 * @return the documentNumber
	 */
	public String getEntityTypeCode() {
		return this.entityTypeCode;
	}
}
