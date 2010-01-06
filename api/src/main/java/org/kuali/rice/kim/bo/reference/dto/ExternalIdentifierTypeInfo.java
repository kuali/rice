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
package org.kuali.rice.kim.bo.reference.dto;

import org.kuali.rice.kim.bo.reference.ExternalIdentifierType;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ExternalIdentifierTypeInfo extends KimCodeInfoBase implements ExternalIdentifierType {

	private static final long serialVersionUID = 1L;

	protected boolean encryptionRequired;
	
	/**
	 * @see org.kuali.rice.kim.bo.reference.ExternalIdentifierType#getExternalIdentifierTypeCode()
	 */
	public String getExternalIdentifierTypeCode() {
		return getCode();
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.ExternalIdentifierType#getExternalIdentifierTypeName()
	 */
	public String getExternalIdentifierTypeName() {
		return getName();
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.ExternalIdentifierType#setExternalIdentifierTypeCode(java.lang.String)
	 */
	public void setExternalIdentifierTypeCode(String externalIdentifierTypeCode) {
		setCode(externalIdentifierTypeCode);
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.ExternalIdentifierType#setExternalIdentifierTypeName(java.lang.String)
	 */
	public void setExternalIdentifierTypeName(String externalIdentifierTypeName) {
		setName(externalIdentifierTypeName);
	}

	public boolean isEncryptionRequired() {
		return this.encryptionRequired;
	}

	public void setEncryptionRequired(boolean encryptionRequired) {
		this.encryptionRequired = encryptionRequired;
	}

}
