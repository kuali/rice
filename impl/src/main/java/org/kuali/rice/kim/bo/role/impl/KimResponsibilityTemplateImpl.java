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
 * See the License for the specific language governing responsibilitys and
 * limitations under the License.
 */
package org.kuali.rice.kim.bo.role.impl;

import org.hibernate.annotations.Type;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.bo.role.KimResponsibilityTemplate;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityTemplateInfo;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRIM_RSP_TMPL_T")
public class KimResponsibilityTemplateImpl extends PersistableBusinessObjectBase implements KimResponsibilityTemplate {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="RSP_TMPL_ID")
	protected String responsibilityTemplateId;
	@Column(name="NMSPC_CD")
	protected String namespaceCode;
	@Column(name="NM")
	protected String name;
	@Column(name="KIM_TYP_ID")
	protected String kimTypeId;
	@Column(name="DESC_TXT", length=400)
	protected String description;
	@Type(type="yes_no")
	@Column(name="ACTV_IND")
	protected boolean active;
	
	public String getKimTypeId() {
		return kimTypeId;
	}

	/**
	 * @see org.kuali.rice.kns.bo.Inactivateable#isActive()
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @see org.kuali.rice.kns.bo.Inactivateable#setActive(boolean)
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	public KimType getKimType() {
		return KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId);
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimResponsibility#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimResponsibility#getResponsibilityId()
	 */
	public String getResponsibilityTemplateId() {
		return responsibilityTemplateId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimResponsibility#getName()
	 */
	public String getName() {
		return name;
	}

	public void setKimTypeId(String kimTypeId) {
		this.kimTypeId = kimTypeId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	public void setResponsibilityTemplateId(String responsibilityTemplateId) {
		this.responsibilityTemplateId = responsibilityTemplateId;
	}
	
	public KimResponsibilityTemplateInfo toInfo() {
		KimResponsibilityTemplateInfo info = new KimResponsibilityTemplateInfo();
		info.setResponsibilityTemplateId(responsibilityTemplateId);
		info.setNamespaceCode(namespaceCode);
		info.setName(name);
		info.setDescription(description);
		info.setKimTypeId(kimTypeId);
		info.setActive(active);
		return info;
	}
}
