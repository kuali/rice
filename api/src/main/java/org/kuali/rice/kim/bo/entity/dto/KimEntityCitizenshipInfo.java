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

import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.unNullify;

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
			citizenshipStatusCode = unNullify(kimEntityCitizenship.getCitizenshipStatusCode());
			countryCode = unNullify(kimEntityCitizenship.getCountryCode());
			endDate = unNullify(kimEntityCitizenship.getEndDate());
			startDate = unNullify(kimEntityCitizenship.getStartDate());
			entityCitizenshipId = unNullify(kimEntityCitizenship.getEntityCitizenshipId());
		}
	}

	/**
	 * @return the citizenshipStatusCode
	 */
	public String getCitizenshipStatusCode() {
		return unNullify(this.citizenshipStatusCode);
	}

	/**
	 * @param citizenshipStatusCode the citizenshipStatusCode to set
	 */
	public void setCitizenshipStatusCode(String citizenshipStatusCode) {
		this.citizenshipStatusCode = citizenshipStatusCode;
	}

	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return unNullify(this.countryCode);
	}

	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return unNullify(this.endDate);
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return unNullify(this.startDate);
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the entityCitizenshipId
	 */
	public String getEntityCitizenshipId() {
		return unNullify( this.entityCitizenshipId);
	}

	/**
	 * @param entityCitizenshipId the entityCitizenshipId to set
	 */
	public void setEntityCitizenshipId(String entityCitizenshipId) {
		this.entityCitizenshipId = entityCitizenshipId;
	}

}
