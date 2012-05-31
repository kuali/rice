/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.kim.bo.role.dto;

import java.io.Serializable;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RoleResponsibilityInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String roleResponsibilityId;
	protected String roleId;
	protected String responsibilityId;
	protected KimResponsibilityInfo kimResponsibilityInfo;
	/**
	 * @return the kimResponsibility
	 */
	public KimResponsibilityInfo getKimResponsibilityInfo() {
		return this.kimResponsibilityInfo;
	}
	/**
	 * @param kimResponsibility the kimResponsibility to set
	 */
	public void setKimResponsibilityInfo(KimResponsibilityInfo kimResponsibilityInfo) {
		this.kimResponsibilityInfo = kimResponsibilityInfo;
	}
	/**
	 * @return the responsibilityId
	 */
	public String getResponsibilityId() {
		return this.responsibilityId;
	}
	/**
	 * @param responsibilityId the responsibilityId to set
	 */
	public void setResponsibilityId(String responsibilityId) {
		this.responsibilityId = responsibilityId;
	}
	/**
	 * @return the roleId
	 */
	public String getRoleId() {
		return this.roleId;
	}
	/**
	 * @param roleId the roleId to set
	 */
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	/**
	 * @return the roleResponsibilityId
	 */
	public String getRoleResponsibilityId() {
		return this.roleResponsibilityId;
	}
	/**
	 * @param roleResponsibilityId the roleResponsibilityId to set
	 */
	public void setRoleResponsibilityId(String roleResponsibilityId) {
		this.roleResponsibilityId = roleResponsibilityId;
	}

}
