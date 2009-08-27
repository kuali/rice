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

import java.io.Serializable;
import java.util.Date;

import org.kuali.rice.kim.bo.entity.KimEntityBioDemographics;
import org.kuali.rice.kim.bo.entity.KimEntityCitizenship;

/**
 * Represents an entity's Bio Demographics info.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityBioDemographicsInfo implements KimEntityBioDemographics, Serializable {

	private static final long serialVersionUID = 1L;
	
	private Date birthDate;
	private String ethnicityCode;
	private String genderCode;
	
	/** empty ctor. */
	public KimEntityBioDemographicsInfo() {
		super();
	}
	
	/**
	 * Copy ctor.
	 * @param o the object to copy.
	 */
	public KimEntityBioDemographicsInfo(KimEntityBioDemographics o) {
		if (o != null) {
			this.birthDate = new Date(o.getBirthDate() != null ? o.getBirthDate().getTime(): 0L);
			this.ethnicityCode = o.getEthnicityCode();
			this.genderCode = o.getGenderCode();
		}
	}
	
	/**
	 * Gets the birth date.
	 * 
	 * @return the birth date
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getBirthDate()
	 */
	public Date getBirthDate() {
		return unNullify(this.birthDate);
	}
	
	/**
	 * Sets the birth date.
	 * 
	 * @param birthDate the birth date
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * Gets the ethnicity code.
	 * 
	 * @return the ethnicity code
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getEthnicityCode()
	 */
	public String getEthnicityCode() {
		return unNullify(this.ethnicityCode);
	}
	
	/**
	 * Sets the ethnicity code.
	 * 
	 * @param ethnicityCode the Ethnicity Code
	 */
	public void setEthnicityCode(String ethnicityCode) {
		this.ethnicityCode = ethnicityCode;
	}

	/**
	 * Gets the gender code.
	 * 
	 * @return the gender code
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getGenderCode()
	 */
	public String getGenderCode() {
		return unNullify(this.genderCode);
	}
	
	/**
	 * Sets the gender code.
	 * 
	 * @param genderCode the gender code
	 */
	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}
}
