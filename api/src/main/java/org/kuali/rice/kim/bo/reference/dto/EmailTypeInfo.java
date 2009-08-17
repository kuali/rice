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

import org.kuali.rice.kim.bo.reference.EmailType;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EmailTypeInfo extends KimCodeInfoBase implements EmailType {

	private static final long serialVersionUID = 1L;

	/**
	 * @see org.kuali.rice.kim.bo.reference.EmailType#getEmailTypeCode()
	 */
	public String getEmailTypeCode() {
		return getCode();
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.EmailType#getEmailTypeName()
	 */
	public String getEmailTypeName() {
		return getName();
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.EmailType#setEmailTypeCode(java.lang.String)
	 */
	public void setEmailTypeCode(String emailTypeCode) {
		setCode(emailTypeCode);
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.EmailType#setEmailTypeName(java.lang.String)
	 */
	public void setEmailTypeName(String emailTypeName) {
		setName(emailTypeName);
	}
	
}
