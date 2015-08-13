/*
 * Copyright 2012 The Kuali Foundation.
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
package edu.arizona.kim.eds;

public class UaEdsAffiliation {

	private String affiliatonString;
	private String activeIndicator;
	private String deptCode;
	private String deptName;
	private String employeeType;
	private boolean isActive;

	public UaEdsAffiliation(String affiliationString, String deptCode, String deptName, String activeIndicator, String employeeType) {
		this.affiliatonString = affiliationString;
		this.activeIndicator = activeIndicator;
		this.deptCode = deptCode;
		this.employeeType = employeeType;
		this.isActive = false;
	}

	public String getAffiliatonString() {
		return affiliatonString;
	}

	public String getStatusCode() {
		return activeIndicator;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public String getDeptName() {
		return deptName;
	}

	public String getEmployeeType() {
		return employeeType;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
