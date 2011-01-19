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
package org.kuali.rice.kim.bo.entity.impl;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.kim.bo.entity.KimEntityVisa;

import javax.persistence.*;

/**
 * This is a description of what this class does - jimt don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name = "KRIM_ENTITY_VISA_T")
public class KimEntityVisaImpl extends KimEntityDataBase implements KimEntityVisa {
	
	private static final long serialVersionUID = 3067809653175495621L;

	@Id
	@GeneratedValue(generator="KRIM_ENTITY_VISA_ID_S")
	@GenericGenerator(name="KRIM_ENTITY_VISA_ID_S",strategy="org.kuali.rice.core.jpa.spring.RiceNumericStringSequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KRIM_ENTITY_VISA_ID_S"),
			@Parameter(name="value_column",value="id")
		})
	@Column(name = "ID")
	private String id;

	@Column(name = "ENTITY_ID")
	private String entityId;
	
	@Column(name = "VISA_TYPE_KEY")
	private String visaTypeKey;
	
	@Column(name = "VISA_ENTRY")
	private String visaEntry;
	
	@Column(name = "VISA_ID")
	private String visaId;
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityVisa#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityVisa#getEntityId()
	 */
	public String getEntityId() {
		return entityId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityVisa#getVisaEntry()
	 */
	public String getVisaEntry() {
		return visaEntry;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityVisa#getVisaId()
	 */
	public String getVisaId() {
		return visaId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityVisa#getVisaTypeKey()
	 */
	public String getVisaTypeKey() {
		return visaTypeKey;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	/**
	 * @param visaTypeKey the visaTypeKey to set
	 */
	public void setVisaTypeKey(String visaTypeKey) {
		this.visaTypeKey = visaTypeKey;
	}

	/**
	 * @param visaEntry the visaEntry to set
	 */
	public void setVisaEntry(String visaEntry) {
		this.visaEntry = visaEntry;
	}

	/**
	 * @param visaId the visaId to set
	 */
	public void setVisaId(String visaId) {
		this.visaId = visaId;
	}
}
