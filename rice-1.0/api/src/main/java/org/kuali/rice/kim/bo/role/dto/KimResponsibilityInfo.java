/*
 * Copyright 2008 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.kim.bo.role.KimResponsibility;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimResponsibilityInfo extends ResponsibilityDetailsInfo implements KimResponsibility, Serializable {

	private static final long serialVersionUID = 7887896860986162310L;
	protected String namespaceCode;
	protected String name;
	protected String description;
	
	protected boolean active;
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isActive() {
		return this.active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getNamespaceCode() {
		return this.namespaceCode;
	}
	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder( this )
				.append( "responsibilityId", this.responsibilityId )
				.append( "namespaceCode", this.namespaceCode )
				.append( "details", this.details )
				.toString();
	}

	public boolean equals(Object object) {
		if (!(object instanceof KimResponsibilityInfo)) {
			return false;
		}
		return StringUtils.equals( responsibilityId, ((KimResponsibilityInfo)object).responsibilityId );
	}
	
}
