/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.dao.ojb;

import java.util.Arrays;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.bo.user.KualiModuleUserProperty;
import org.kuali.core.dao.KualiModuleUserDao;

public class KualiModuleUserDaoOjb implements KualiModuleUserDao {

	public Object getActiveUserQueryCriteria(String moduleId) {
		// build the criteria for the sub query
		Criteria isActiveCriteria = new Criteria();
		// ensure users equal
		isActiveCriteria.addEqualToField("personUniversalIdentifier", Criteria.PARENT_QUERY_PREFIX + "personUniversalIdentifier");
		isActiveCriteria.addEqualTo("moduleId", moduleId);
		Criteria activePropertyNameCriteria = new Criteria();
		activePropertyNameCriteria.addEqualTo("name", "active");
		isActiveCriteria.addAndCriteria(activePropertyNameCriteria);
		Criteria activeIsYesCriteria = new Criteria();
		activeIsYesCriteria.addEqualTo("value", "Y");
		isActiveCriteria.addAndCriteria(activeIsYesCriteria);
		// create the query object and insert into an exists sub-query
		ReportQueryByCriteria moduleUserPropertySubQuery = QueryFactory.newReportQuery(KualiModuleUserProperty.class, isActiveCriteria);
		moduleUserPropertySubQuery.setAttributes(new String[] { "personUniversalIdentifier" }); // don't need to select all attributes (the default) 
		Criteria criteria = new Criteria();
		criteria.addExists(moduleUserPropertySubQuery);
		return criteria; 
	}
	
	public Object getActiveFacultyStaffAffiliateCriteria( String[] allowedEmployeeStatusValues ) {
		Criteria criteria = new Criteria();
		criteria.addEqualTo("staff", "Y");
		Criteria isFacultyCriteria = new Criteria();
		isFacultyCriteria.addEqualTo("faculty", "Y");
		Criteria isAffiliateCriteria = new Criteria();
		isAffiliateCriteria.addEqualTo("affiliate", "Y");
		criteria.addOrCriteria(isFacultyCriteria);
		criteria.addOrCriteria(isAffiliateCriteria);
		criteria.addIn("employeeStatusCode", Arrays.asList(allowedEmployeeStatusValues));
		return criteria; 
	}

}
