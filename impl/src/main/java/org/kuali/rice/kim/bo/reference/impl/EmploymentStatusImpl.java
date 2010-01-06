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

import org.kuali.rice.kim.bo.reference.EmploymentStatus;
import org.kuali.rice.kim.bo.reference.dto.EmploymentStatusInfo;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRIM_EMP_STAT_T")
@AttributeOverrides({
	@AttributeOverride(name="code",column=@Column(name="EMP_STAT_CD")),
	@AttributeOverride(name="name",column=@Column(name="NM"))
})
public class EmploymentStatusImpl extends KimCodeBase implements EmploymentStatus {

	private static final long serialVersionUID = 1L;

	/**
	 * @see org.kuali.rice.kim.bo.reference.EmploymentStatus#getEmploymentStatusCode()
	 */
	public String getEmploymentStatusCode() {
		return getCode();
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.EmploymentStatus#getEmploymentStatusName()
	 */
	public String getEmploymentStatusName() {
		return getName();
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.EmploymentStatus#setEmploymentStatusCode(java.lang.String)
	 */
	public void setEmploymentStatusCode(String employmentStatusCode) {
		setCode(employmentStatusCode);
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.EmploymentStatus#setEmploymentStatusName(java.lang.String)
	 */
	public void setEmploymentStatusName(String employmentStatusName) {
		setName(employmentStatusName);
	}
	
	public EmploymentStatusInfo toInfo() {
		EmploymentStatusInfo info = new EmploymentStatusInfo();
		info.setCode(code);
		info.setName(name);
		info.setDisplaySortCode(displaySortCode);
		info.setActive(active);
		return info;
	}

}
