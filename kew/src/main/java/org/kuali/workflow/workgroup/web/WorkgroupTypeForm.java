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
package org.kuali.workflow.workgroup.web;

import org.apache.struts.action.ActionForm;
import org.kuali.workflow.workgroup.WorkgroupType;

/**
 * A Struts ActionForm for the {@link WorkgroupTypeAction}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupTypeForm extends ActionForm {

	private static final long serialVersionUID = 2042175205384573992L;

	private WorkgroupType workgroupType;
	private Long workgroupTypeId;
	private String name = "";
	private String methodToCall = "";

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public WorkgroupType getWorkgroupType() {
		return workgroupType;
	}
	public void setWorkgroupType(WorkgroupType workgroupType) {
		this.workgroupType = workgroupType;
	}
	public Long getWorkgroupTypeId() {
		return workgroupTypeId;
	}
	public void setWorkgroupTypeId(Long workgroupTypeId) {
		this.workgroupTypeId = workgroupTypeId;
	}
	public String getMethodToCall() {
		return methodToCall;
	}
	public void setMethodToCall(String methodToCall) {
		this.methodToCall = methodToCall;
	}

}