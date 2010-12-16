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

import org.kuali.rice.core.util.type.KualiDecimal;
import org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimEntityEmploymentInformationInfo extends KimInactivatableInfo implements KimEntityEmploymentInformation {

	private static final long serialVersionUID = 1L;

	protected String entityEmploymentId;
	protected String employeeId;
	protected String employmentRecordId;
	protected String entityAffiliationId;
	protected String employeeStatusCode;
	protected String employeeTypeCode;
	protected String primaryDepartmentCode;
	protected KualiDecimal baseSalaryAmount;
	protected boolean primary = true;

	/**
	 * 
	 */
	public KimEntityEmploymentInformationInfo() {
		super();
		active = true;
	}
	
	/**
	 * 
	 */
	public KimEntityEmploymentInformationInfo( KimEntityEmploymentInformation eei ) {
		this();
		if ( eei != null ) {
			entityEmploymentId = eei.getEntityEmploymentId();
			employeeId = eei.getEmployeeId();
			employmentRecordId = eei.getEmploymentRecordId();
			entityAffiliationId = eei.getEntityAffiliationId();
			employeeStatusCode = eei.getEmployeeStatusCode();
			employeeTypeCode = eei.getEmployeeTypeCode();
			primaryDepartmentCode = eei.getPrimaryDepartmentCode();
			baseSalaryAmount = eei.getBaseSalaryAmount();
			primary = eei.isPrimary();
			active = eei.isActive();
		}
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation#getEntityEmploymentId()
	 */
	public String getEntityEmploymentId() {
		return entityEmploymentId;
	}

	/**
	 * @param entityEmploymentId the entityEmploymentId to set
	 */
	public void setEntityEmploymentId(String entityEmploymentId) {
		this.entityEmploymentId = entityEmploymentId;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation#getEmployeeId()
	 */
	public String getEmployeeId() {
		return employeeId;
	}

	/**
	 * @param employeeId the employeeId to set
	 */
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation#getEmploymentRecordId()
	 */
	public String getEmploymentRecordId() {
		return employmentRecordId;
	}

	/**
	 * @param employmentRecordId the employmentRecordId to set
	 */
	public void setEmploymentRecordId(String employmentRecordId) {
		this.employmentRecordId = employmentRecordId;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation#getEntityAffiliationId()
	 */
	public String getEntityAffiliationId() {
		return entityAffiliationId;
	}

	/**
	 * @param entityAffiliationId the entityAffiliationId to set
	 */
	public void setEntityAffiliationId(String entityAffiliationId) {
		this.entityAffiliationId = entityAffiliationId;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation#getEmployeeStatusCode()
	 */
	public String getEmployeeStatusCode() {
		return employeeStatusCode;
	}

	/**
	 * @param employeeStatusCode the employeeStatusCode to set
	 */
	public void setEmployeeStatusCode(String employeeStatusCode) {
		this.employeeStatusCode = employeeStatusCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation#getEmployeeTypeCode()
	 */
	public String getEmployeeTypeCode() {
		return employeeTypeCode;
	}

	/**
	 * @param employeeTypeCode the employeeTypeCode to set
	 */
	public void setEmployeeTypeCode(String employeeTypeCode) {
		this.employeeTypeCode = employeeTypeCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation#getPrimaryDepartmentCode()
	 */
	public String getPrimaryDepartmentCode() {
		return primaryDepartmentCode;
	}

	/**
	 * @param primaryDepartmentCode the primaryDepartmentCode to set
	 */
	public void setPrimaryDepartmentCode(String primaryDepartmentCode) {
		this.primaryDepartmentCode = primaryDepartmentCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation#getBaseSalaryAmount()
	 */
	public KualiDecimal getBaseSalaryAmount() {
		return baseSalaryAmount;
	}

	/**
	 * @param baseSalaryAmount the baseSalaryAmount to set
	 */
	public void setBaseSalaryAmount(KualiDecimal baseSalaryAmount) {
		this.baseSalaryAmount = baseSalaryAmount;
	}

	/**
	 * @return the primary
	 */
	public boolean isPrimary() {
		return this.primary;
	}

	/**
	 * @param primary the primary to set
	 */
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
	
}
