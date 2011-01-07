/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.bo;

import org.kuali.rice.core.persistence.CompositePrimaryKeyBase;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * PK of PostalCodeImpl 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PostalCodeImplId extends CompositePrimaryKeyBase {
	@Id
	@Column(name="POSTAL_CNTRY_CD")
    private String postalCountryCode;
	@Id
	@Column(name="POSTAL_CD")
    private String postalCode;
	
	public PostalCodeImplId() {}
	
	public PostalCodeImplId(String postalCountryCode, String postalCode) {
		this.postalCountryCode = postalCountryCode;
		this.postalCode = postalCode;
	}
	
	/**
	 * @return the postalCountryCode
	 */
	public String getPostalCountryCode() {
		return this.postalCountryCode;
	}
	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return this.postalCode;
	}
}
