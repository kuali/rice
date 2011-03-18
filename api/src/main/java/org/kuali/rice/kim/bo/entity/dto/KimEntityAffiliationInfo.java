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

import org.kuali.rice.kim.bo.entity.KimEntityAffiliation;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimEntityAffiliationInfo extends KimDefaultableInfo implements KimEntityAffiliation {

	private static final long serialVersionUID = 1L;

	protected String entityAffiliationId;
	protected String affiliationTypeCode;
	protected String campusCode;

	
	/**
	 * constructs an empty {@link KimEntityAffiliationInfo}
	 */
	public KimEntityAffiliationInfo() {
		super();
		active = true;
	}
	
	/**
	 * constructs a {@link KimEntityAffiliationInfo} derived from the given {@link KimEntityAffiliation} 
	 */
	public KimEntityAffiliationInfo( KimEntityAffiliation aff ) {
		this();
		if ( aff != null ) {
			entityAffiliationId = aff.getEntityAffiliationId();
			affiliationTypeCode = aff.getAffiliationTypeCode();
			campusCode = aff.getCampusCode();
			defaultValue = aff.isDefaultValue();
			active = aff.isActive();
		}
	}
	
	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAffiliation#getAffiliationTypeCode()
	 */
	public String getAffiliationTypeCode() {
		return affiliationTypeCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAffiliation#getCampusCode()
	 */
	public String getCampusCode() {
		return campusCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAffiliation#getEntityAffiliationId()
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
