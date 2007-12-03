/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.workflow.tools.xml;


/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Responsibility {

	private Workgroup workgroup;
	private String user;
	private String actionRequested;

	public String getActionRequested() {
		return actionRequested;
	}
	public void setActionRequested(String actionRequested) {
		this.actionRequested = actionRequested;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public Workgroup getWorkgroup() {
		return workgroup;
	}
	public void setWorkgroup(Workgroup workgroup) {
		this.workgroup = workgroup;
	}

}
