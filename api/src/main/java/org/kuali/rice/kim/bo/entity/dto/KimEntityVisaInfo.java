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

import org.kuali.rice.kim.bo.entity.KimEntityVisa;

/**
 * This is a description of what this class does - jimt don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimEntityVisaInfo extends KimInfoBase implements KimEntityVisa {

	private static final long serialVersionUID = 469708778377293171L;

	private String id = "";
	private String entityId = "";
	private String visaTypeKey = "";
	private String visaEntry = "";
	private String visaId = "";

	public KimEntityVisaInfo() {
		super();
	}

	public KimEntityVisaInfo(KimEntityVisa kimEntityVisa) {
		this();
		if ( kimEntityVisa != null ) {
			id = unNullify(kimEntityVisa.getId());
			entityId = unNullify(kimEntityVisa.getEntityId());
			visaTypeKey = unNullify(kimEntityVisa.getVisaTypeKey());
			visaEntry = unNullify(kimEntityVisa.getVisaEntry());
			visaId = unNullify(kimEntityVisa.getVisaId());
		}
	}

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
