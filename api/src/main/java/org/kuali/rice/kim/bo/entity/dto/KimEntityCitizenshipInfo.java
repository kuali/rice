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
	
	/**
	 * Gets the citizenship status code.
	 * 
	 * @return citizenship status code
	 * @see org.kuali.rice.kim.bo.entity.KimEntityCitizenship#getCitizenshipStatusCode()
	 */
	public String getCitizenshipStatusCode() {
		return unNullify(this.citizenshipStatusCode);
	}
	
	/**
	 * Sets the citizenship status code.
	 * 
	 * @param citizenshipStatusCode status code
	 */
	public void setCitizenshipStatusCode(String citizenshipStatusCode) {
		this.citizenshipStatusCode = citizenshipStatusCode;
	}

	/**
	 * Gets the citizenship country code.
	 * 
	 * @return citizenship country code
	 * @see org.kuali.rice.kim.bo.entity.KimEntityCitizenship#getCountryCode()
	 */
	public String getCountryCode() {
		return unNullify(this.countryCode);
	}
	
	/**
	 * Gets the citizenship country code.
	 * 
	 * @param countryCode country code
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * Gets the citizenship end date.
	 * 
	 * @return citizenship end date
	 * @see org.kuali.rice.kim.bo.entity.KimEntityCitizenship#getEndDate()
	 */
	public Date getEndDate() {
		return unNullify(this.endDate);
	}
	
	/**
	 * Sets the citizenship end date.
	 * 
	 * @param endDate end date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Gets the entity citizenship id.
	 * 
	 * @return entity citizenship id
	 * @see org.kuali.rice.kim.bo.entity.KimEntityCitizenship#getEntityCitizenshipId()
	 */
	public String getEntityCitizenshipId() {
		return unNullify(this.entityCitizenshipId);
	}
	
	/**
	 * Sets the entity citizenship id.
	 * 
	 * @param entityCitizenshipId citizenship id
	 */
	public void setEntityCitizenshipId(String entityCitizenshipId) {
		this.entityCitizenshipId = entityCitizenshipId;
	}

	/**
	 * Gets the citizenship start date.
	 * 
	 * @return citizenship start date
	 * @see org.kuali.rice.kim.bo.entity.KimEntityCitizenship#getStartDate()
	 */
	public Date getStartDate() {
		return unNullify(this.startDate);
	}

	/**
	 * Sets the citizenship start date.
	 * 
	 * @param startDate start date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
