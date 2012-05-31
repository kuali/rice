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
package org.kuali.rice.kew.identity;

import org.kuali.rice.kew.user.EmplId;

/**
 * The Employee ID of a Person in KIM
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class EmployeeId extends EmplId {

	public EmployeeId() {
		super();
	}

	public EmployeeId(String employeeId) {
		super(employeeId);
	}
	
	public String getEmployeeId() {
		return getEmplId();
	}
	
	public void setEmployeeId(String employeeId) {
		setEmplId(employeeId);
	}

}
