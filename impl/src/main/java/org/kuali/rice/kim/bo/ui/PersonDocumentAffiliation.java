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
package org.kuali.rice.kim.bo.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.kim.bo.reference.impl.AffiliationTypeImpl;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name = "KRIM_PND_AFLTN_MT")
public class PersonDocumentAffiliation extends PersonDocumentBoDefaultBase {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_AFLTN_ID")
	protected String entityAffiliationId;

	@Column(name = "AFLTN_TYP_CD")
	protected String affiliationTypeCode;

	@Column(name = "CAMPUS_CD")
	protected String campusCode;

	protected AffiliationTypeImpl affiliationType;
	protected PersonDocumentEmploymentInfo newEmpInfo;
	protected List<PersonDocumentEmploymentInfo> empInfos;

	public PersonDocumentAffiliation() {
		empInfos = new ArrayList<PersonDocumentEmploymentInfo>();
		setNewEmpInfo(new PersonDocumentEmploymentInfo());
		this.active = true;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAffiliation#getAffiliationTypeCode()
	 */
	public String getAffiliationTypeCode() {
		return affiliationTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAffiliation#getCampusCode()
	 */
	public String getCampusCode() {
		return campusCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAffiliation#getEntityAffiliationId()
	 */
	public String getEntityAffiliationId() {
		return entityAffiliationId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAffiliation#setAffiliationTypeCode(java.lang.String)
	 */
	public void setAffiliationTypeCode(String affiliationTypeCode) {
		this.affiliationTypeCode = affiliationTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAffiliation#setCampusCode(java.lang.String)
	 */
	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = super.toStringMapper();
		m.put("entityAffiliationId", entityAffiliationId);
		m.put("affiliationTypeCode", affiliationTypeCode);
		return m;
	}

	public void setEntityAffiliationId(String entityAffiliationId) {
		this.entityAffiliationId = entityAffiliationId;
	}

	public PersonDocumentEmploymentInfo getNewEmpInfo() {
		return this.newEmpInfo;
	}

	public void setNewEmpInfo(PersonDocumentEmploymentInfo newEmpInfo) {
		this.newEmpInfo = newEmpInfo;
	}

	public List<PersonDocumentEmploymentInfo> getEmpInfos() {
		return this.empInfos;
	}

	public void setEmpInfos(List<PersonDocumentEmploymentInfo> empInfos) {
		this.empInfos = empInfos;
	}

	public AffiliationTypeImpl getAffiliationType() {
		return this.affiliationType;
	}

	public void setAffiliationType(AffiliationTypeImpl affiliationType) {
		this.affiliationType = affiliationType;
	}

	@Override
	public boolean isActive(){
		return this.active;
	}

}
