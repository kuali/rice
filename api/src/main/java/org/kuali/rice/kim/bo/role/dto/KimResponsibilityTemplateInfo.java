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
 * See the License for the specific language governing responsibilitys and
 * limitations under the License.
 */
package org.kuali.rice.kim.bo.role.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.kim.bo.role.KimResponsibilityTemplate;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimResponsibilityTemplateInfo implements KimResponsibilityTemplate, Serializable {

	private static final long serialVersionUID = -282533234389371097L;
	
	protected String responsibilityTemplateId;
	protected String namespaceCode;
	protected String name;
	protected String kimTypeId;
	protected String description;
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

	@Override
	public String toString() {
		return new ToStringBuilder( this )
		.append( "responsibilityTemplateId", this.responsibilityTemplateId )
		.append( "name", this.name )
		.append( "kimTypeId", this.kimTypeId )
		.toString();
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
}
