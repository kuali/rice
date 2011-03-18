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
package org.kuali.rice.kim.bo.ui;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.kim.bo.reference.EntityNameType;
import org.kuali.rice.kim.bo.reference.impl.EntityNameTypeImpl;

import javax.persistence.*;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@IdClass(PersonDocumentNameId.class)
@Entity
@Table(name = "KRIM_PND_NM_MT")
public class PersonDocumentName extends PersonDocumentBoDefaultBase {

	@Id
	@GeneratedValue(generator="KRIM_ENTITY_NM_ID_S")
	@GenericGenerator(name="KRIM_ENTITY_NM_ID_S",strategy="org.kuali.rice.core.jpa.spring.RiceNumericStringSequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KRIM_ENTITY_NM_ID_S"),
			@Parameter(name="value_column",value="id")
		})
	@Column(name = "ENTITY_NM_ID")
	protected String entityNameId;

	//@Column(name = "ENTITY_ID")
	@Transient
	protected String entityId;

	@Column(name = "NM_TYP_CD")
	protected String nameTypeCode ;

	@Column(name = "FIRST_NM")
	protected String firstName;

	@Column(name = "MIDDLE_NM")
	protected String middleName;

	@Column(name = "LAST_NM")
	protected String lastName;

	@Column(name = "TITLE_NM")
	protected String title;

	@Column(name = "SUFFIX_NM")
	protected String suffix;
	
	@ManyToOne(targetEntity=EntityNameTypeImpl.class, fetch = FetchType.EAGER, cascade = {})
	@JoinColumn(name = "NM_TYP_CD", insertable = false, updatable = false)
	protected EntityNameType entityNameType;

	public PersonDocumentName() {
		this.active = true;
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
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public void setNameTypeCode(String nameTypeCode) {
		this.nameTypeCode = nameTypeCode;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * This default implementation formats the name as LAST, FIRST MIDDLE.
	 * 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getFormattedName()
	 */
	public String getFormattedName() {
		return getLastName() + ", " + getFirstName() + " " + getMiddleName();
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public EntityNameType getEntityNameType() {
		return this.entityNameType;
	}

	public void setEntityNameType(EntityNameType entityNameType) {
		this.entityNameType = entityNameType;
	}

	public void setEntityNameId(String entityNameId) {
		this.entityNameId = entityNameId;
	}
}
