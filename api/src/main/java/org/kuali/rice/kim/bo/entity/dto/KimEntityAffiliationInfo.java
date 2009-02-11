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
package org.kuali.rice.kim.bo.entity.dto;

import org.kuali.rice.kim.bo.entity.EntityAffiliation;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityAffiliationInfo extends KimDefaultableInfo implements EntityAffiliation {

	private static final long serialVersionUID = 1L;

	protected String entityAffiliationId = "";
	protected String affiliationTypeCode = "";
	protected String campusCode = "";

	
	/**
	 * 
	 */
	public KimEntityAffiliationInfo() {
	}
	/**
	 * 
	 */
	public KimEntityAffiliationInfo( EntityAffiliation aff ) {
		if ( aff != null ) {
			entityAffiliationId = unNullify( aff.getEntityAffiliationId() );
			affiliationTypeCode = unNullify( aff.getAffiliationTypeCode() );
			campusCode = unNullify( aff.getCampusCode() );
		}
	}
	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAffiliation#getAffiliationTypeCode()
	 */
	public String getAffiliationTypeCode() {
		return affiliationTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAffiliation#getCampusCode()
	 */
	public String getCampusCode() {
		return campusCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAffiliation#getEntityAffiliationId()
	 */
	public String getEntityAffiliationId() {
		return entityAffiliationId;
	}

	public void setAffiliationTypeCode(String affiliationTypeCode) {
		this.affiliationTypeCode = affiliationTypeCode;
	}

	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}

	public void setEntityAffiliationId(String entityAffiliationId) {
		this.entityAffiliationId = entityAffiliationId;
	}

}
