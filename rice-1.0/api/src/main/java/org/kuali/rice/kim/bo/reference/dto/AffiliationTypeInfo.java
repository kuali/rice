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

import org.kuali.rice.kim.bo.reference.AffiliationType;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AffiliationTypeInfo extends KimCodeInfoBase implements AffiliationType {

	private static final long serialVersionUID = 1L;

    protected boolean employmentAffiliationType;


	/**
	 * @see org.kuali.rice.kim.bo.reference.AffiliationType#getAffiliationTypeCode()
	 */
	public String getAffiliationTypeCode() {
		return getCode();
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.AffiliationType#getAffiliationTypeName()
	 */
	public String getAffiliationTypeName() {
		return getName();
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.AffiliationType#setAffiliationTypeCode(java.lang.String)
	 */
	public void setAffiliationTypeCode(String affiliationTypeCode) {
		setCode(affiliationTypeCode);
	}

	/**
	 * @see org.kuali.rice.kim.bo.reference.AffiliationType#setAffiliationTypeName(java.lang.String)
	 */
	public void setAffiliationTypeName(String affiliationTypeName) {
		setName(affiliationTypeName);
	}

	public boolean isEmploymentAffiliationType() {
		return this.employmentAffiliationType;
	}

	public void setEmploymentAffiliationType(boolean employmentAffiliationType) {
		this.employmentAffiliationType = employmentAffiliationType;
	}
	
}
