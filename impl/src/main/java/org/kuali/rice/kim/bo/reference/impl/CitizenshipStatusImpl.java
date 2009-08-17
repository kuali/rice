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
package org.kuali.rice.kim.bo.reference.impl;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.rice.kim.bo.reference.CitizenshipStatus;
import org.kuali.rice.kim.bo.reference.dto.CitizenshipStatusInfo;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRIM_CTZNSHP_STAT_T")
@AttributeOverrides({
	@AttributeOverride(name="code",column=@Column(name="CTZNSHP_STAT_CD")),
	@AttributeOverride(name="name",column=@Column(name="NM"))
})
public class CitizenshipStatusImpl extends KimCodeBase implements CitizenshipStatus {

	private static final long serialVersionUID = 1L;

	/**
	 * @see org.kuali.rice.kim.bo.reference.CitizenshipStatus#getCitizenshipStatusCode()
	 */
	public String getCitizenshipStatusCode() {
		return getCode();
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.CitizenshipStatus#getCitizenshipStatusName()
	 */
	public String getCitizenshipStatusName() {
		return getName();
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.CitizenshipStatus#setCitizenshipStatusCode(java.lang.String)
	 */
	public void setCitizenshipStatusCode(String citizenshipStatusCode) {
		setCode(citizenshipStatusCode);
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.CitizenshipStatus#setCitizenshipStatusName(java.lang.String)
	 */
	public void setCitizenshipStatusName(String citizenshipStatusName) {
		setName(citizenshipStatusName);
	}
	
	public CitizenshipStatusInfo toInfo() {
		CitizenshipStatusInfo info = new CitizenshipStatusInfo();
		info.setCode(code);
		info.setName(name);
		info.setDisplaySortCode(displaySortCode);
		info.setActive(active);
		return info;
	}

}
