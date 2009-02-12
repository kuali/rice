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

import java.io.Serializable;

import org.kuali.rice.kim.bo.entity.KimEntityName;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityNameInfo extends KimDefaultableInfo implements KimEntityName, Serializable {

	private static final long serialVersionUID = 1L;

	protected String entityNameId = "";
	protected String nameTypeCode = "";
	protected String firstName = "";
	protected String middleName = "";
	protected String lastName = "";
	protected String title = "";
	protected String suffix = "";

	/**
	 * 
	 */
	public KimEntityNameInfo() {
	}
	
	/**
	 * 
	 */
	public KimEntityNameInfo( KimEntityName name ) {
		if ( name != null ) {
			entityNameId = name.getEntityNameId();
			nameTypeCode = name.getNameTypeCode();
			firstName = name.getFirstName();
			middleName = name.getMiddleName();
			lastName = name.getLastName();
			title = name.getTitle();
			suffix = name.getSuffix();
			active = name.isActive();
			dflt = name.isDefault();
		}
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getEntityNameId()
	 */
	public String getEntityNameId() {
		return entityNameId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getFirstName()
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getLastName()
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getMiddleName()
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getNameTypeCode()
	 */
	public String getNameTypeCode() {
		return nameTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getSuffix()
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getTitle()
	 */
	public String getTitle() {
		return title;
	}

	public void setFirstName(String firstName) {
		this.firstName = unNullify( firstName );
	}

	public void setLastName(String lastName) {
		this.lastName = unNullify( lastName );
	}

	public void setMiddleName(String middleName) {
		this.middleName = unNullify( middleName );
	}

	public void setNameTypeCode(String nameTypeCode) {
		this.nameTypeCode = unNullify( nameTypeCode );
	}

	public void setSuffix(String suffix) {
		this.suffix = unNullify( suffix );
	}

	public void setTitle(String title) {
		this.title = unNullify( title );
	}

	/**
	 * This default implementation formats the name as LAST, FIRST MIDDLE.
	 * 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getFormattedName()
	 */
	public String getFormattedName() {
		return getLastName() + ", " + getFirstName() + (getMiddleName()==null?"":" " + getMiddleName());
	}

	public void setEntityNameId(String entityNameId) {
		this.entityNameId = unNullify( entityNameId );
	}
	
}
