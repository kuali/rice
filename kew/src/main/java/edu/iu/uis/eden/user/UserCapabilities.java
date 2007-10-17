/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.user;

import java.io.Serializable;

/**
 * Identifies the capabilities of a particular {@link UserService} implementation.
 * 
 * @see UserService
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class UserCapabilities implements Serializable {

	private static final long serialVersionUID = 2306224360420470187L;

	private boolean lookupSupported = false;
	private boolean reportSupported = false;
	private boolean createSupported = false;
	private boolean editSupported = false;
	
	public boolean isCreateSupported() {
		return createSupported;
	}
	public void setCreateSupported(boolean createSupported) {
		this.createSupported = createSupported;
	}
	public boolean isEditSupported() {
		return editSupported;
	}
	public void setEditSupported(boolean editSupported) {
		this.editSupported = editSupported;
	}
	public boolean isLookupSupported() {
		return lookupSupported;
	}
	public void setLookupSupported(boolean lookupSupported) {
		this.lookupSupported = lookupSupported;
	}
	public boolean isReportSupported() {
		return reportSupported;
	}
	public void setReportSupported(boolean reportSupported) {
		this.reportSupported = reportSupported;
	}
	
	public static UserCapabilities getAll() {
		UserCapabilities capabilities = new UserCapabilities();
		capabilities.setCreateSupported(true);
		capabilities.setEditSupported(true);
		capabilities.setLookupSupported(true);
		capabilities.setReportSupported(true);
		return capabilities;
	}
	
	public static UserCapabilities getReadOnly() {
		UserCapabilities capabilities = new UserCapabilities();
		capabilities.setReportSupported(true);
		capabilities.setLookupSupported(true);
		return capabilities;
	}

}
