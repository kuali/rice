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

import org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimEntityExternalIdentifierInfo extends KimInfoBase implements KimEntityExternalIdentifier {

	private static final long serialVersionUID = 1L;

	protected String entityExternalIdentifierId;
	protected String externalIdentifierTypeCode;
	protected String externalId;
	
	/**
	 * 
	 */
	public KimEntityExternalIdentifierInfo() {
		super();
	}
	
	/**
	 * 
	 */
	public KimEntityExternalIdentifierInfo( KimEntityExternalIdentifier eid ) {
		this();
		if ( eid != null ) {
			entityExternalIdentifierId = eid.getEntityExternalIdentifierId();
			externalIdentifierTypeCode = eid.getExternalIdentifierTypeCode();
			externalId = eid.getExternalId();
		}
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier#getEntityExternalIdentifierId()
	 */
	public String getEntityExternalIdentifierId() {
		return entityExternalIdentifierId;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier#getExternalId()
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier#getExternalIdentifierTypeCode()
	 */
	public String getExternalIdentifierTypeCode() {
		return externalIdentifierTypeCode;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public void setExternalIdentifierTypeCode(String externalIdentifierTypeCode) {
		this.externalIdentifierTypeCode = externalIdentifierTypeCode;
	}

	public void setEntityExternalIdentifierId(String entityExternalIdentifierId) {
		this.entityExternalIdentifierId = entityExternalIdentifierId;
	}

}
