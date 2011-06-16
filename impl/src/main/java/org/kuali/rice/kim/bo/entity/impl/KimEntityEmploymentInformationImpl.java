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

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.kuali.rice.core.framework.persistence.jpa.type.HibernateKualiDecimalFieldType;
import org.kuali.rice.core.util.type.KualiDecimal;
import org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation;
import org.kuali.rice.kim.bo.reference.impl.EmploymentStatusImpl;
import org.kuali.rice.kim.bo.reference.impl.EmploymentTypeImpl;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationBo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ENTITY_EMP_INFO_T")
@TypeDef(
		name="rice_decimal",
		typeClass=HibernateKualiDecimalFieldType.class
	)
public class KimEntityEmploymentInformationImpl extends KimInactivatableEntityDataBase implements KimEntityEmploymentInformation {

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
	
	@Type(type="rice_decimal")
	@Column(name = "BASE_SLRY_AMT")
	protected KualiDecimal baseSalaryAmount;

	@Type(type="yes_no")
	@Column(name="PRMRY_IND")
	protected boolean primary;

	@ManyToOne(targetEntity=EmploymentTypeImpl.class, fetch=FetchType.EAGER, cascade = {})
	//@Fetch(value = FetchMode.SELECT)
	@JoinColumn(name="EMP_TYP_CD", insertable = false, updatable = false)
	protected EmploymentTypeImpl employmentType;

	@ManyToOne(targetEntity=EmploymentStatusImpl.class, fetch = FetchType.EAGER, cascade = {})
	//@Fetch(value = FetchMode.SELECT)
	@JoinColumn(name="EMP_STAT_CD", insertable = false, updatable = false)
	protected EmploymentStatusImpl employmentStatus;
	
	@ManyToOne(targetEntity=EntityAffiliationBo.class, fetch = FetchType.EAGER, cascade = {})
	//@Fetch(value = FetchMode.SELECT)
	@JoinColumn(name="ENTITY_AFLTN_ID", insertable = false, updatable = false)
	protected EntityAffiliationBo affiliation; // = new KimEntityAffiliationImpl();
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation#getBaseSalaryAmount()
	 */
	public KualiDecimal getBaseSalaryAmount() {
		return baseSalaryAmount;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation#getEmployeeStatusCode()
	 */
	public String getEmployeeStatusCode() {
		return employeeStatusCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation#getEmployeeTypeCode()
	 */
	public String getEmployeeTypeCode() {
		return employeeTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation#getEntityAffiliationId()
	 */
	public String getEntityAffiliationId() {
		return entityAffiliationId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation#getEntityEmploymentId()
	 */
	public String getEntityEmploymentId() {
		return entityEmploymentId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation#isPrimary()
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

	public EntityAffiliationBo getAffiliation() {
		return this.affiliation;
	}

	public void setAffiliation(EntityAffiliationBo affiliation) {
		this.affiliation = affiliation;
	}

}
