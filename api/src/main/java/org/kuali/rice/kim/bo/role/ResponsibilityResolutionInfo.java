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
package org.kuali.rice.kim.bo.role;


/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ResponsibilityResolutionInfo implements ResponsibilityResolution {

	String principalId;
	String responsibilityId;
	String actionTypeCode;
	Integer priorityNumber;

	public String getResponsibilityId() {
		return this.responsibilityId;
	}

	public void setResponsibilityId(String responsibilityId) {
		this.responsibilityId = responsibilityId;
	}

	public String getActionTypeCode() {
		return this.actionTypeCode;
	}

	public void setActionTypeCode(String actionTypeCode) {
		this.actionTypeCode = actionTypeCode;
	}

	public Integer getPriorityNumber() {
		return this.priorityNumber;
	}

	public void setPriorityNumber(Integer priorityNumber) {
		this.priorityNumber = priorityNumber;
	}

	public String getPrincipalId() {
		return this.principalId;
	}

	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}
}
