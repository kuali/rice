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

import java.util.Date;

import org.kuali.rice.kim.bo.entity.KimEntityCitizenship;

/**
 * A DTO that contains entity citizenship info.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimEntityCitizenshipInfo extends KimInactivatableInfo implements KimEntityCitizenship {

	private static final long serialVersionUID = 1L;
	
	private String citizenshipStatusCode;
	private String countryCode;
	private Date endDate;
	private Date startDate;
	private String entityCitizenshipId;
	
	public KimEntityCitizenshipInfo() {
		super();
		active = true;
	}

	public KimEntityCitizenshipInfo(KimEntityCitizenship kimEntityCitizenship) {
		this();
		if ( kimEntityCitizenship != null ) {
			citizenshipStatusCode = kimEntityCitizenship.getCitizenshipStatusCode();
			countryCode = kimEntityCitizenship.getCountryCode();
			endDate = kimEntityCitizenship.getEndDate();
			startDate = kimEntityCitizenship.getStartDate();
			entityCitizenshipId = kimEntityCitizenship.getEntityCitizenshipId();
		}
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityCitizenship#getCitizenshipStatusCode()
	 */
	public String getCitizenshipStatusCode() {
		return citizenshipStatusCode;
	}

	/**
	 * @param citizenshipStatusCode the citizenshipStatusCode to set
	 */
	public void setCitizenshipStatusCode(String citizenshipStatusCode) {
		this.citizenshipStatusCode = citizenshipStatusCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityCitizenship#getCountryCode()
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityCitizenship#getEndDate()
	 */
	public Date getEndDate() {
		return this.endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityCitizenship#getStartDate()
	 */
	public Date getStartDate() {
		return this.startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityCitizenship#getEntityCitizenshipId()
	 */
	public String getEntityCitizenshipId() {
		return entityCitizenshipId;
	}

	/**
	 * @param entityCitizenshipId the entityCitizenshipId to set
	 */
	public void setEntityCitizenshipId(String entityCitizenshipId) {
		this.entityCitizenshipId = entityCitizenshipId;
	}

}
