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
package org.kuali.rice.kim.bo.reference.impl;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.kuali.rice.kim.bo.reference.ExternalIdentifierType;
import org.kuali.rice.kim.bo.reference.dto.ExternalIdentifierTypeInfo;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRIM_EXT_ID_TYP_T")
@AttributeOverrides({
	@AttributeOverride(name="code",column=@Column(name="EXT_ID_TYP_CD")),
	@AttributeOverride(name="name",column=@Column(name="NM"))
})
public class ExternalIdentifierTypeImpl extends KimCodeBase implements ExternalIdentifierType {

	private static final long serialVersionUID = 1L;

    @Type(type="yes_no")
	@Column(name="ENCR_REQ_IND")
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
	
	public ExternalIdentifierTypeInfo toInfo() {
		ExternalIdentifierTypeInfo info = new ExternalIdentifierTypeInfo();
		info.setCode(code);
		info.setName(name);
		info.setActive(active);
		info.setEncryptionRequired(encryptionRequired);
		info.setDisplaySortCode(displaySortCode);
		return info;
	}
}
