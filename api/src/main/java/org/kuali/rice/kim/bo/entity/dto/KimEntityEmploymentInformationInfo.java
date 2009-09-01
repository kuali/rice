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

import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.unNullify;

import org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimEntityEmploymentInformationInfo extends KimInactivatableInfo implements KimEntityEmploymentInformation {

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
		super();
		active = true;
	}
	
	/**
	 * 
	 */
	public KimEntityEmploymentInformationInfo( KimEntityEmploymentInformation eei ) {
		this();
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
	 * @return the entityEmploymentId
	 */
	public String getEntityEmploymentId() {
		return this.entityEmploymentId;
	}

	/**
	 * @param entityEmploymentId the entityEmploymentId to set
	 */
	public void setEntityEmploymentId(String entityEmploymentId) {
		this.entityEmploymentId = entityEmploymentId;
	}

	/**
	 * @return the employeeId
	 */
	public String getEmployeeId() {
		return this.employeeId;
	}

	/**
	 * @param employeeId the employeeId to set
	 */
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	/**
	 * @return the employmentRecordId
	 */
	public String getEmploymentRecordId() {
		return this.employmentRecordId;
	}

	/**
	 * @param employmentRecordId the employmentRecordId to set
	 */
	public void setEmploymentRecordId(String employmentRecordId) {
		this.employmentRecordId = employmentRecordId;
	}

	/**
	 * @return the entityAffiliationId
	 */
	public String getEntityAffiliationId() {
		return this.entityAffiliationId;
	}

	/**
	 * @param entityAffiliationId the entityAffiliationId to set
	 */
	public void setEntityAffiliationId(String entityAffiliationId) {
		this.entityAffiliationId = entityAffiliationId;
	}

	/**
	 * @return the employeeStatusCode
	 */
	public String getEmployeeStatusCode() {
		return this.employeeStatusCode;
	}

	/**
	 * @param employeeStatusCode the employeeStatusCode to set
	 */
	public void setEmployeeStatusCode(String employeeStatusCode) {
		this.employeeStatusCode = employeeStatusCode;
	}

	/**
	 * @return the employeeTypeCode
	 */
	public String getEmployeeTypeCode() {
		return this.employeeTypeCode;
	}

	/**
	 * @param employeeTypeCode the employeeTypeCode to set
	 */
	public void setEmployeeTypeCode(String employeeTypeCode) {
		this.employeeTypeCode = employeeTypeCode;
	}

	/**
	 * @return the primaryDepartmentCode
	 */
	public String getPrimaryDepartmentCode() {
		return this.primaryDepartmentCode;
	}

	/**
	 * @param primaryDepartmentCode the primaryDepartmentCode to set
	 */
	public void setPrimaryDepartmentCode(String primaryDepartmentCode) {
		this.primaryDepartmentCode = primaryDepartmentCode;
	}

	/**
	 * @return the baseSalaryAmount
	 */
	public KualiDecimal getBaseSalaryAmount() {
		return this.baseSalaryAmount;
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
