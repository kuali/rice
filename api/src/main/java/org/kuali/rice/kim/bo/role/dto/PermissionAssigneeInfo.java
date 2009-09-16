/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.List;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PermissionAssigneeInfo {
	protected String principalId;
	protected String groupId;
	protected List<DelegateInfo> delegates = new ArrayList<DelegateInfo>();
	
	// for jax-ws service construction
	@SuppressWarnings("unused")
	private PermissionAssigneeInfo() {}
	
	public PermissionAssigneeInfo(String principalId, String groupId, List<DelegateInfo> delegates) {
		this.principalId = principalId;
		this.groupId = groupId;
		this.delegates = delegates;
	}
	
	public String getPrincipalId() {
		return this.principalId;
	}
	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}
	public String getGroupId() {
		return this.groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public List<DelegateInfo> getDelegates() {
		return this.delegates;
	}
	public void setDelegates(List<DelegateInfo> delegates) {
		this.delegates = delegates;
	}
}
