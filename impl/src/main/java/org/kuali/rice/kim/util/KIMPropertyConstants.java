/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kim.util;

import org.kuali.rice.core.util.JSTLConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KIMPropertyConstants extends JSTLConstants {

	public static class Person {
		public static final String ENTITY_ID = "entityId";
		public static final String PRINCIPAL_ID = "principalId";
		public static final String PRINCIPAL_NAME = "principalName";
		public static final String FIRST_NAME = "firstName";
		public static final String MIDDLE_NAME = "middleName";
		public static final String LAST_NAME = "lastName";
		public static final String NAME = "name";
		public static final String EMAIL_ADDRESS = KNSPropertyConstants.EMAIL_ADDRESS;
		public static final String PHONE_NUMBER = "phoneNumber";
		public static final String ACTIVE = KNSPropertyConstants.ACTIVE;
		public static final String EMPLOYEE_ID = "employeeId";
		public static final String EMPLOYEE_STATUS_CODE = "employeeStatusCode";
		public static final String EMPLOYEE_TYPE_CODE = "employeeTypeCode";
		public static final String EXTERNAL_ID = "externalId";
		public static final String EXTERNAL_IDENTIFIER_TYPE_CODE = "externalIdentifierTypeCode";
		public static final String ADDRESS_LINE_1 = "line1";
		public static final String ADDRESS_LINE_2 = "line2";
		public static final String ADDRESS_LINE_3 = "line3";
		public static final String CITY_NAME = "cityName";
		public static final String STATE_CODE = KNSPropertyConstants.STATE_CODE;
		public static final String POSTAL_CODE = KNSPropertyConstants.POSTAL_CODE;
		public static final String COUNTRY_CODE = KNSPropertyConstants.COUNTY_CODE;
		public static final String CAMPUS_CODE = KNSPropertyConstants.CAMPUS_CODE;
		public static final String AFFILIATION_TYPE_CODE = "affiliationTypeCode";
		public static final String PRIMARY_DEPARTMENT_CODE = "primaryDepartmentCode";
		public static final String BASE_SALARY_AMOUNT = "baseSalaryAmount";
	}
	
	public static class Group {
	    public static final String GROUP_ID = "groupId";
	}
}
