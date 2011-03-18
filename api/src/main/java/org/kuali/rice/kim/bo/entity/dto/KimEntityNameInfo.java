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

import java.io.Serializable;

import org.kuali.rice.kim.bo.entity.KimEntityName;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimEntityNameInfo extends KimDefaultableInfo implements KimEntityName, Serializable {

	private static final long serialVersionUID = 1L;

	protected String entityNameId;
	protected String nameTypeCode;
	protected String firstName;
	protected String firstNameUnmasked;
	protected String middleName;
	protected String middleNameUnmasked;
	protected String lastName;
	protected String lastNameUnmasked;
	protected String title;
	protected String titleUnmasked;
	protected String suffix;
	protected String suffixUnmasked;
	
	protected boolean suppressName = false;
	
	protected String formattedName;
	protected String formattedNameUnmasked;

	/**
	 * construct an empty {@link KimEntityNameInfo}
	 */
	public KimEntityNameInfo() {
		super();
		active = true;
	}
	
	/**
	 * construct a {@link KimEntityNameInfo} derived from the given {@link KimEntityName}
	 */
	public KimEntityNameInfo( KimEntityName name ) {
		this();
		if ( name != null ) {
		    this.entityNameId = name.getEntityNameId();
		    this.nameTypeCode = name.getNameTypeCode();
		    this.firstName = name.getFirstName();
		    this.firstNameUnmasked = name.getFirstNameUnmasked();
		    this.middleName = name.getMiddleName();
		    this.middleNameUnmasked = name.getMiddleNameUnmasked();
		    this.lastName = name.getLastName();
		    this.lastNameUnmasked = name.getLastNameUnmasked();
		    this.title = name.getTitle();
		    this.titleUnmasked = name.getTitleUnmasked();
		    this.suffix = name.getSuffix();
		    this.suffixUnmasked = name.getSuffixUnmasked();
		    this.active = name.isActive();
		    this.defaultValue = name.isDefaultValue();
		    this.suppressName = name.isSuppressName();
		    
		    this.formattedName = name.getFormattedName();
		    this.formattedNameUnmasked = name.getFormattedNameUnmasked();
		}
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getEntityNameId()
	 */
	public String getEntityNameId() {
		return entityNameId;
	}

	/**
	 * @param entityNameId the entityNameId to set
	 */
	public void setEntityNameId(String entityNameId) {
		this.entityNameId = entityNameId;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getNameTypeCode()
	 */
	public String getNameTypeCode() {
		return nameTypeCode;
	}

	/**
	 * @param nameTypeCode the nameTypeCode to set
	 */
	public void setNameTypeCode(String nameTypeCode) {
		this.nameTypeCode = nameTypeCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getFirstName()
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getFirstNameUnmasked()
	 */
	public String getFirstNameUnmasked() {
		return firstNameUnmasked;
	}

	/**
	 * @param firstNameUnmasked the firstNameUnmasked to set
	 */
	public void setFirstNameUnmasked(String firstNameUnmasked) {
		this.firstNameUnmasked = firstNameUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getMiddleName()
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * @param middleName the middleName to set
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getMiddleNameUnmasked()
	 */
	public String getMiddleNameUnmasked() {
		return middleNameUnmasked;
	}

	/**
	 * @param middleNameUnmasked the middleNameUnmasked to set
	 */
	public void setMiddleNameUnmasked(String middleNameUnmasked) {
		this.middleNameUnmasked = middleNameUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getLastName()
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getLastNameUnmasked()
	 */
	public String getLastNameUnmasked() {
		return lastNameUnmasked;
	}

	/**
	 * @param lastNameUnmasked the lastNameUnmasked to set
	 */
	public void setLastNameUnmasked(String lastNameUnmasked) {
		this.lastNameUnmasked = lastNameUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getTitle()
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getTitleUnmasked()
	 */
	public String getTitleUnmasked() {
		return titleUnmasked;
	}

	/**
	 * @param titleUnmasked the titleUnmasked to set
	 */
	public void setTitleUnmasked(String titleUnmasked) {
		this.titleUnmasked = titleUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getSuffix()
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * @param suffix the suffix to set
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getSuffixUnmasked()
	 */
	public String getSuffixUnmasked() {
		return suffixUnmasked;
	}

	/**
	 * @param suffixUnmasked the suffixUnmasked to set
	 */
	public void setSuffixUnmasked(String suffixUnmasked) {
		this.suffixUnmasked = suffixUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#isSuppressName()
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
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getFormattedName()
	 */
	public String getFormattedName() {
		return formattedName;
	}

	/**
	 * @param formattedName the formattedName to set
	 */
	public void setFormattedName(String formattedName) {
		this.formattedName = formattedName;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getFormattedNameUnmasked()
	 */
	public String getFormattedNameUnmasked() {
		return formattedNameUnmasked;
	}

	/**
	 * @param formattedNameUnmasked the formattedNameUnmasked to set
	 */
	public void setFormattedNameUnmasked(String formattedNameUnmasked) {
		this.formattedNameUnmasked = formattedNameUnmasked;
	}

}
