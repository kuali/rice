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
package edu.arizona.kim.document;

import org.kuali.rice.kim.document.IdentityManagementPersonDocument;

/**
 * This is a description of what this class does - shyu don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class UaIdentityManagementPersonDocument extends IdentityManagementPersonDocument {
	protected static final long serialVersionUID = -534993712085516925L;

	protected String employeeId = "";

	public UaIdentityManagementPersonDocument() {
		super();
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
}
