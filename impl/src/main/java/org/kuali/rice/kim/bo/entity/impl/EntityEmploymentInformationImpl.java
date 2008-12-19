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
package org.kuali.rice.kim.bo.entity.impl;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.kuali.rice.kim.bo.entity.EntityEmploymentInformation;
import org.kuali.rice.kim.bo.reference.EmploymentStatus;
import org.kuali.rice.kim.bo.reference.EmploymentType;
import org.kuali.rice.kim.bo.reference.impl.EmploymentStatusImpl;
import org.kuali.rice.kim.bo.reference.impl.EmploymentTypeImpl;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name = "KRIM_ENTITY_EMP_INFO_T")
public class EntityEmploymentInformationImpl extends InactivatableEntityDataBase implements EntityEmploymentInformation {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_EMP_ID")
	protected String entityEmploymentId;

	@Column(name = "ENTITY_ID")
	protected String entityId;

	@Column(name = "EMP_ID")
	protected String employeeId;

	@Column(name = "EMP_REC_ID")
	protected String employmentRecordId;

	@Column(name = "ENTITY_AFLTN_ID")
	protected String entityAffiliationId;

	@Column(name = "EMP_STAT_CD")
	protected String employeeStatusCode;

	@Column(name = "EMP_TYP_CD")
	protected String employeeTypeCode;

	@Column(name = "PRMRY_DEPT_CD")
	protected String primaryDepartmentCode;
	
	@Column(name = "BASE_SLRY_AMT")
	protected KualiDecimal baseSalaryAmount;

	@Type(type="yes_no")
	@Column(name="PRMRY_IND")
	protected boolean primary;

	@ManyToOne(targetEntity=EmploymentTypeImpl.class, fetch = FetchType.EAGER, cascade = {})
	@JoinColumn(name = "EMP_TYP_CD", insertable = false, updatable = false)
	protected EmploymentTypeImpl employmentType;

	@ManyToOne(targetEntity=EmploymentStatusImpl.class, fetch = FetchType.EAGER, cascade = {})
	@JoinColumn(name = "EMP_STAT_CD", insertable = false, updatable = false)
	protected EmploymentStatusImpl employmentStatus;
	
	@ManyToOne(targetEntity=EntityAffiliationImpl.class, fetch = FetchType.EAGER, cascade = {})
	@JoinColumn(name = "ENTITY_AFLTN_ID", insertable = false, updatable = false)
	protected EntityAffiliationImpl affiliation;
	
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

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "entityEmploymentId", entityEmploymentId );
		m.put( "entityAffiliationId", entityAffiliationId );
		m.put( "employeeStatusCode", employeeStatusCode );
		m.put( "employeeTypeCode", employeeTypeCode );
		m.put( "employeeId", employeeId );
		m.put( "baseSalaryAmount", baseSalaryAmount );
		m.put( "primary", primary );
		return m;
	}

	public void setEntityEmploymentId(String entityEmploymentId) {
		this.entityEmploymentId = entityEmploymentId;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public EmploymentTypeImpl getEmploymentType() {
		return this.employmentType;
	}

	public void setEmploymentType(EmploymentTypeImpl employmentType) {
		this.employmentType = employmentType;
	}

	public EmploymentStatusImpl getEmploymentStatus() {
		return this.employmentStatus;
	}

	public void setEmploymentStatus(EmploymentStatusImpl employmentStatus) {
		this.employmentStatus = employmentStatus;
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

	public EntityAffiliationImpl getAffiliation() {
		return this.affiliation;
	}

	public void setAffiliation(EntityAffiliationImpl affiliation) {
		this.affiliation = affiliation;
	}

}
