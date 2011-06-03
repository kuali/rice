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

import org.kuali.rice.kim.bo.entity.KimEntityResidency;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityResidencyInfo extends KimInfoBase implements KimEntityResidency {

	private static final long serialVersionUID = -3939718576000597749L;

	private String id;
	private String entityId;
	private String determinationMethod;
	private String inStateFlag;

	public KimEntityResidencyInfo() {
		super();
	}

	public KimEntityResidencyInfo(KimEntityResidency kimEntityResidency) {
		this();
		if ( kimEntityResidency != null ) {
			id = kimEntityResidency.getId();
			entityId = kimEntityResidency.getEntityId();
			determinationMethod = kimEntityResidency.getDeterminationMethod();
			inStateFlag = kimEntityResidency.getInState();
		}
	}

	/**
	 * @param inStateFlag the inStateFlag to set
	 */
	public void setInStateFlag(String inStateFlag) {
		this.inStateFlag = inStateFlag;
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
	 * @param determinationMethod the determinationMethod to set
	 */
	public void setDeterminationMethod(String determinationMethod) {
		this.determinationMethod = determinationMethod;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityResidency#getDeterminationMethod()
	 */
	public String getDeterminationMethod() {
		return determinationMethod;
	}

	/**
	 * {@inheritDoc}
	 * @see org.kuali.rice.kim.bo.entity.KimEntityResidency#getEntityId()
	 */
	public String getEntityId() {
		return entityId;
	}

	/**
	 * {@inheritDoc}
	 * @see org.kuali.rice.kim.bo.entity.KimEntityResidency#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 * @see org.kuali.rice.kim.bo.entity.KimEntityResidency#getInState()
	 */
	public String getInState() {
		return inStateFlag;
	}
}
