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

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.kuali.rice.kim.bo.entity.KimDefaultableEntityTypeData;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@MappedSuperclass
public abstract class KimDefaultableEntityTypeDataBase extends KimDefaultableEntityDataBase
		implements KimDefaultableEntityTypeData {

	private static final long serialVersionUID = 1L;

	@Column(name="ENT_TYP_CD")
	protected String entityTypeCode;

	/**
	 * @return the entityTypeCode
	 */
	public String getEntityTypeCode() {
		return this.entityTypeCode;
	}

	/**
	 * @param entityTypeCode the entityTypeCode to set
	 */
	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}
}
