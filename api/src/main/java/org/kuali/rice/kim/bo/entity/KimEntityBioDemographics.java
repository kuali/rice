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
package org.kuali.rice.kim.bo.entity;

import java.util.Date;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KimEntityBioDemographics {

	String getEntityId();
	Date getBirthDate();
	Date getDeceasedDate();

	String getGenderCode();
	String getMaritalStatusCode();
	String getPrimaryLanguageCode();
	String getSecondaryLanguageCode();
	String getCountryOfBirthCode();
	String getBirthStateCode();
	String getCityOfBirth();
	String getGeographicOrigin();

	String getGenderCodeUnmasked();
	String getMaritalStatusCodeUnmasked();
	String getPrimaryLanguageCodeUnmasked();
	String getSecondaryLanguageCodeUnmasked();
	String getCountryOfBirthCodeUnmasked();
	String getBirthStateCodeUnmasked();
	String getCityOfBirthUnmasked();
	String getGeographicOriginUnmasked();

	boolean isSuppressPersonal();

}
