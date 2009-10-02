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

import java.io.Serializable;

import org.kuali.rice.kim.bo.entity.KimEntityName;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimEntityNameInfo extends KimDefaultableInfo implements KimEntityName, Serializable {

	private static final long serialVersionUID = 1L;

	protected String entityNameId = "";
	protected String nameTypeCode = "";
	protected String firstName = "";
	protected String firstNameUnmasked = "";
	protected String middleName = "";
	protected String middleNameUnmasked = "";
	protected String lastName = "";
	protected String lastNameUnmasked = "";
	protected String title = "";
	protected String titleUnmasked = "";
	protected String suffix = "";
	protected String suffixUnmasked = "";
	
	protected boolean suppressName = false;
	
	protected String formattedName = "";
	protected String formattedNameUnmasked = "";

	/**
	 * 
	 */
	public KimEntityNameInfo() {
		super();
		active = true;
	}
	
	/**
	 * 
	 */
	public KimEntityNameInfo( KimEntityName name ) {
		this();
		if ( name != null ) {
		    this.entityNameId = unNullify(name.getEntityNameId());
		    this.nameTypeCode = unNullify(name.getNameTypeCode());
		    this.firstName = unNullify(name.getFirstName());
		    this.firstNameUnmasked = unNullify(name.getFirstNameUnmasked());
		    this.middleName = unNullify(name.getMiddleName());
		    this.middleNameUnmasked = unNullify(name.getMiddleNameUnmasked());
		    this.lastName = unNullify(name.getLastName());
		    this.lastNameUnmasked = unNullify(name.getLastNameUnmasked());
		    this.title = unNullify(name.getTitle());
		    this.titleUnmasked = unNullify(name.getTitleUnmasked());
		    this.suffix = unNullify(name.getSuffix());
		    this.suffixUnmasked = unNullify(name.getSuffixUnmasked());
		    this.active = name.isActive();
		    this.dflt = name.isDefault();
		    this.suppressName = name.isSuppressName();
		    
		    this.formattedName = unNullify(name.getFormattedName());
		    this.formattedNameUnmasked = unNullify(name.getFormattedNameUnmasked());
		}
	}

	/**
	 * @return the entityNameId
	 */
	public String getEntityNameId() {
		return unNullify(this.entityNameId);
	}

	/**
	 * @param entityNameId the entityNameId to set
	 */
	public void setEntityNameId(String entityNameId) {
		this.entityNameId = entityNameId;
	}

	/**
	 * @return the nameTypeCode
	 */
	public String getNameTypeCode() {
		return unNullify(this.nameTypeCode);
	}

	/**
	 * @param nameTypeCode the nameTypeCode to set
	 */
	public void setNameTypeCode(String nameTypeCode) {
		this.nameTypeCode = nameTypeCode;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return unNullify(this.firstName);
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the firstNameUnmasked
	 */
	public String getFirstNameUnmasked() {
		return unNullify(this.firstNameUnmasked);
	}

	/**
	 * @param firstNameUnmasked the firstNameUnmasked to set
	 */
	public void setFirstNameUnmasked(String firstNameUnmasked) {
		this.firstNameUnmasked = firstNameUnmasked;
	}

	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return unNullify(this.middleName);
	}

	/**
	 * @param middleName the middleName to set
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * @return the middleNameUnmasked
	 */
	public String getMiddleNameUnmasked() {
		return unNullify(this.middleNameUnmasked);
	}

	/**
	 * @param middleNameUnmasked the middleNameUnmasked to set
	 */
	public void setMiddleNameUnmasked(String middleNameUnmasked) {
		this.middleNameUnmasked = middleNameUnmasked;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return unNullify(this.lastName);
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the lastNameUnmasked
	 */
	public String getLastNameUnmasked() {
		return unNullify(this.lastNameUnmasked);
	}

	/**
	 * @param lastNameUnmasked the lastNameUnmasked to set
	 */
	public void setLastNameUnmasked(String lastNameUnmasked) {
		this.lastNameUnmasked = lastNameUnmasked;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return unNullify(this.title);
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the titleUnmasked
	 */
	public String getTitleUnmasked() {
		return unNullify(this.titleUnmasked);
	}

	/**
	 * @param titleUnmasked the titleUnmasked to set
	 */
	public void setTitleUnmasked(String titleUnmasked) {
		this.titleUnmasked = titleUnmasked;
	}

	/**
	 * @return the suffix
	 */
	public String getSuffix() {
		return unNullify(this.suffix);
	}

	/**
	 * @param suffix the suffix to set
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * @return the suffixUnmasked
	 */
	public String getSuffixUnmasked() {
		return unNullify(this.suffixUnmasked);
	}

	/**
	 * @param suffixUnmasked the suffixUnmasked to set
	 */
	public void setSuffixUnmasked(String suffixUnmasked) {
		this.suffixUnmasked = suffixUnmasked;
	}

	/**
	 * @return the suppressName
	 */
	public boolean isSuppressName() {
		return this.suppressName;
	}

	/**
	 * @param suppressName the suppressName to set
	 */
	public void setSuppressName(boolean suppressName) {
		this.suppressName = suppressName;
	}

	/**
	 * @return the formattedName
	 */
	public String getFormattedName() {
		return unNullify(this.formattedName);
	}

	/**
	 * @param formattedName the formattedName to set
	 */
	public void setFormattedName(String formattedName) {
		this.formattedName = formattedName;
	}

	/**
	 * @return the formattedNameUnmasked
	 */
	public String getFormattedNameUnmasked() {
		return unNullify(this.formattedNameUnmasked);
	}

	/**
	 * @param formattedNameUnmasked the formattedNameUnmasked to set
	 */
	public void setFormattedNameUnmasked(String formattedNameUnmasked) {
		this.formattedNameUnmasked = formattedNameUnmasked;
	}

}
