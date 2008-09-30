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

import java.io.Serializable;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimResponsibilityInfo implements KimResponsibility, Serializable {

	protected String responsibilityId;
	protected String namespaceCode;
	protected String responsibilityName;
	protected String kimTypeId;
	protected String responsibilityDescription;
	
	protected boolean active;
	
	public String getResponsibilityId() {
		return this.responsibilityId;
	}
	public void setResponsibilityId(String permissionId) {
		this.responsibilityId = permissionId;
	}
	public String getNamespaceCode() {
		return this.namespaceCode;
	}
	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}
	public String getResponsibilityName() {
		return this.responsibilityName;
	}
	public void setResponsibilityName(String permissionName) {
		this.responsibilityName = permissionName;
	}
	public String getKimTypeId() {
		return this.kimTypeId;
	}
	public void setKimTypeId(String kimTypeId) {
		this.kimTypeId = kimTypeId;
	}
	public String getResponsibilityDescription() {
		return this.responsibilityDescription;
	}
	public void setResponsibilityDescription(String permissionDescription) {
		this.responsibilityDescription = permissionDescription;
	}
	public boolean isActive() {
		return this.active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
}
