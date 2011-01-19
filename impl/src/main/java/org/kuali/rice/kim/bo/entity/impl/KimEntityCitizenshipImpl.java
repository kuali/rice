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

import org.kuali.rice.kim.bo.entity.KimEntityCitizenship;
import org.kuali.rice.kim.bo.reference.CitizenshipStatus;
import org.kuali.rice.kim.bo.reference.impl.CitizenshipStatusImpl;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ENTITY_CTZNSHP_T")
public class KimEntityCitizenshipImpl extends KimInactivatableEntityDataBase implements KimEntityCitizenship {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_CTZNSHP_ID")
	protected String entityCitizenshipId;
	
	@Column(name = "ENTITY_ID")
	protected String entityId;
	
	@Column(name = "POSTAL_CNTRY_CD")
	protected String countryCode;

	@Column(name = "CTZNSHP_STAT_CD")
	protected String citizenshipStatusCode;

	@Column(name = "strt_dt")
	protected Date startDate;

	@Column(name = "end_dt")
	protected Date endDate;

	@ManyToOne(targetEntity=CitizenshipStatusImpl.class, fetch=FetchType.EAGER, cascade={})
	@JoinColumn(name = "CTZNSHP_STAT_CD", insertable = false, updatable = false)
	protected CitizenshipStatus citizenshipType;

	// Waiting until we pull in from KFS
	// protected Country country;
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityCitizenship#getCitizenshipStatusCode()
	 */
	public String getCitizenshipStatusCode() {
		return citizenshipStatusCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityCitizenship#getEndDate()
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityCitizenship#getEntityCitizenshipId()
	 */
	public String getEntityCitizenshipId() {
		return entityCitizenshipId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityCitizenship#getStartDate()
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityCitizenship#setCitizenshipStatusCode(java.lang.String)
	 */
	public void setCitizenshipStatusCode(String citizenshipStatusCode) {
		this.citizenshipStatusCode = citizenshipStatusCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityCitizenship#setEndDate(java.util.Date)
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityCitizenship#startDate(java.util.Date)
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getCountryCode() {
		return this.countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public CitizenshipStatus getCitizenshipType() {
		return this.citizenshipType;
	}

	public void setCitizenshipType(CitizenshipStatus citizenshipType) {
		this.citizenshipType = citizenshipType;
	}

	public void setEntityCitizenshipId(String entityCitizenshipId) {
		this.entityCitizenshipId = entityCitizenshipId;
	}

}
