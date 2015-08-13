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

import org.apache.commons.lang.StringUtils;

/**
 * 
 * This class is the Java object for the EDS field dccRelation, as described here:
 * http://sia.uits.arizona.edu/eds_attributes
 * 
 * The EDS value is a colon separated String, with the following fields:
 * title:type:department:department name:status:action date:end date
 * 
 * And as an array:
 * dccRelation[0]: title
 * dccRelation[1]: type
 * dccRelation[2]: department code
 * dccRelation[3]: department name
 * dccRelation[4]: status
 * dccRelation[5]: action date
 * dccRelation[6]: end date
 * 
 * 
 * And some actual values as found in the 'EDS_DCC_ACTIVE_AFFILIATIONS'
 * KFS param (The presence of these in params make theof
 * type valid, i.e. auto-grantable for role 32):
  * Type    Title
 * ----    ------------------------------------
 * 00910   Affiliate
 * 00920   Associate
 * 00950   Pre-Hire
 * 00970   UA Foundation Members
 * 00972   Government Agency Staff
 * 00974   Health Care Provider
 * 
 */
public class UaEdsDccRelation {

	private static final String DCC_RELATION_DELIM = ":";

	private String title;
	private String type;
	private String deptCode;
	private String departmentName;
	private String status;
	private String actionDate;
	private String endDate;

	public UaEdsDccRelation(final String dccRelationRecordString) {
		String[] tokens = dccRelationRecordString.split(DCC_RELATION_DELIM);
		setTitle(tokens[0]);
		setType(tokens[1]);
		setDeptCode(tokens[2]);
		setDepartmentName(tokens[3]);
		setStatus(tokens[4]);
		setActionDate(tokens[5]);
		setEndDate(tokens[6]);
	}

	/**
	 * The title of this DCC, in plain english
	 * 
	 * @return String at dccRelation[0], the title of this DCC
	 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * This method returns type of this relation, of the form '00NNN'.
	 * 
	 * @return String at dccRelation[1], the type code of this DCC as '00NNN'
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * The primary department code for this DCC, should be of the form 'XXXX' --
	 * no longer than four characters
	 * 
	 * @return String at dccRelation[2], this DCC's department code
	 */
	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = StringUtils.isBlank(deptCode) ? "????" : deptCode;
	}

	/**
	 * Primary department name for a DCC
	 * 
	 * @return String at dccRelation[3], the department name
	 */
	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	/**
	 * This method returns the primary status of the DCC
	 * 
	 * Values are: A = Active F = Future Active I = Inactive
	 * 
	 * @return @return String at dccRelation[4], the status
	 */
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 
	 * 
	 * @return @return String at dccRelation[5]
	 */
	public String getActionDate() {
		return actionDate;
	}

	public void setActionDate(String actionDate) {
		this.actionDate = actionDate;
	}

	/**
	 * 
	 * 
	 * @return String at dccRelation[6]
	 */
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}
