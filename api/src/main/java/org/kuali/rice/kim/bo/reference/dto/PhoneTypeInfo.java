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

import org.kuali.rice.kim.bo.reference.PhoneType;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PhoneTypeInfo extends KimCodeInfoBase implements PhoneType {

	private static final long serialVersionUID = 1L;

	/**
	 * @see org.kuali.rice.kim.bo.reference.PhoneType#getPhoneTypeCode()
	 */
	public String getPhoneTypeCode() {
		return getCode();
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.PhoneType#getPhoneTypeName()
	 */
	public String getPhoneTypeName() {
		return getName();
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.PhoneType#setPhoneTypeCode(java.lang.String)
	 */
	public void setPhoneTypeCode(String phoneTypeCode) {
		setCode(phoneTypeCode);
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.PhoneType#setPhoneTypeName(java.lang.String)
	 */
	public void setPhoneTypeName(String phoneTypeName) {
		setName(phoneTypeName);
	}
	
}
