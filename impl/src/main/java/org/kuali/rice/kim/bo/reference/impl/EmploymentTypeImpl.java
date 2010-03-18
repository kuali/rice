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
import javax.persistence.Transient;

import org.kuali.rice.kim.bo.reference.EmploymentType;
import org.kuali.rice.kim.bo.reference.dto.EmploymentTypeInfo;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRIM_EMP_TYP_T")
@AttributeOverrides({
	@AttributeOverride(name="code",column=@Column(name="EMP_TYP_CD")),
	@AttributeOverride(name="name",column=@Column(name="NM"))
})
public class EmploymentTypeImpl extends KimCodeBase implements EmploymentType {

	private static final long serialVersionUID = 1L;

	@Transient
	private String employmentTypeCode;
	
	/**
	 * @see org.kuali.rice.kim.bo.reference.EmploymentType#getEmploymentTypeCode()
	 */
	public String getEmploymentTypeCode() {
		return getCode();
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.EmploymentType#getEmploymentTypeName()
	 */
	public String getEmploymentTypeName() {
		return getName();
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.EmploymentType#setEmploymentTypeCode(java.lang.String)
	 */
	public void setEmploymentTypeCode(String employmentTypeCode) {
		setCode(employmentTypeCode);
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.EmploymentType#setEmploymentTypeName(java.lang.String)
	 */
	public void setEmploymentTypeName(String employmentTypeName) {
		setName(employmentTypeName);
	}

	public EmploymentTypeInfo toInfo() {
		EmploymentTypeInfo info = new EmploymentTypeInfo();
		info.setCode(code);
		info.setName(name);
		info.setDisplaySortCode(displaySortCode);
		info.setActive(active);
		return info;
	}
	
}
