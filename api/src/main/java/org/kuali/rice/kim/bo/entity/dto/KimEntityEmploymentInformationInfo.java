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

import org.kuali.rice.kim.bo.entity.EntityEmploymentInformation;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityEmploymentInformationInfo extends KimInactivatableInfo implements EntityEmploymentInformation {

	private static final long serialVersionUID = 1L;

	protected String entityEmploymentId = "";
	protected String employeeId = "";
	protected String employmentRecordId = "";
	protected String entityAffiliationId = "";
	protected String employeeStatusCode = "";
	protected String employeeTypeCode = "";
	protected String primaryDepartmentCode = "";
	protected KualiDecimal baseSalaryAmount = KualiDecimal.ZERO;
	protected boolean primary = true;

	/**
	 * 
	 */
	public KimEntityEmploymentInformationInfo() {
	}
	
	/**
	 * 
	 */
	public KimEntityEmploymentInformationInfo( EntityEmploymentInformation eei ) {
		if ( eei != null ) {
			entityEmploymentId = unNullify( eei.getEntityEmploymentId() );
			employeeId = unNullify( eei.getEmployeeId() );
			employmentRecordId = unNullify( eei.getEmploymentRecordId() );
			entityAffiliationId = unNullify( eei.getEntityAffiliationId() );
			employeeStatusCode = unNullify( eei.getEmployeeStatusCode() );
			employeeTypeCode = unNullify( eei.getEmployeeTypeCode() );
			primaryDepartmentCode = unNullify( eei.getPrimaryDepartmentCode() );
			baseSalaryAmount = eei.getBaseSalaryAmount();
			primary = eei.isPrimary();
			active = eei.isActive();
		}
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityEmploymentInformation#getBaseSalaryAmount()
	 */
	public KualiDecimal getBaseSalaryAmount() {
		return baseSalaryAmount;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityEmploymentInformation#getEmployeeStatusCode()
	 */
	public String getEmployeeStatusCode() {
		return employeeStatusCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityEmploymentInformation#getEmployeeTypeCode()
	 */
	public String getEmployeeTypeCode() {
		return employeeTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityEmploymentInformation#getEntityAffiliationId()
	 */
	public String getEntityAffiliationId() {
		return entityAffiliationId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityEmploymentInformation#getEntityEmploymentId()
	 */
	public String getEntityEmploymentId() {
		return entityEmploymentId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityEmploymentInformation#isPrimary()
	 */
	public boolean isPrimary() {
		return primary;
	}

	public void setEntityAffiliationId(String entityAffiliationId) {
		this.entityAffiliationId = entityAffiliationId;
	}

	public void setBaseSalaryAmount(KualiDecimal baseSalaryAmount) {
		this.baseSalaryAmount = baseSalaryAmount;
	}

	public void setEmployeeStatusCode(String employeeStatusCode) {
		this.employeeStatusCode = employeeStatusCode;
	}

	public void setEmployeeTypeCode(String employeeTypeCode) {
		this.employeeTypeCode = employeeTypeCode;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public void setEntityEmploymentId(String entityEmploymentId) {
		this.entityEmploymentId = entityEmploymentId;
	}

	public String getPrimaryDepartmentCode() {
		return this.primaryDepartmentCode;
	}

	public void setPrimaryDepartmentCode(String primaryDepartmentCode) {
		this.primaryDepartmentCode = primaryDepartmentCode;
	}

	public String getEmployeeId() {
		return this.employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmploymentRecordId() {
		return this.employmentRecordId;
	}

	public void setEmploymentRecordId(String employmentRecordId) {
		this.employmentRecordId = employmentRecordId;
	}

}
