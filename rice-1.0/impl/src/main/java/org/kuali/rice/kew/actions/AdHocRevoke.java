/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.actions;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;


/**
 * Represents a revocation of an AdHoc request.<br><br>
 * 
 * If the <code>nodeName</code> property on this object is set, then the system will only
 * examine pending app-specific requests at nodes with that particular name.  In addition to
 * this, one of the following 3 parameters is required:<br><br>
 * 
 * <ol>
 *   <li><b>actionRequestId</b> - the ID of the action request to revoke</li>
 *   <li><b>userId</b> - the ID of the user whose request(s) should be revoked</li>
 * 	 <li><b>workgroupId</b> - the ID of the workgroup whose requests(s) should be revoked</li>
 * </ol>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AdHocRevoke implements java.io.Serializable {

	private static final long serialVersionUID = 8536540010313763068L;

	private Long actionRequestId;
	private String nodeName;
	private String principalId;
	private String groupId;
	
	public AdHocRevoke() {}
	
	public Long getActionRequestId() {
		return actionRequestId;
	}
	public void setActionRequestId(Long actionRequestId) {
		this.actionRequestId = actionRequestId;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getPrincipalId() {
		return principalId;
	}
	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}
		
	/**
	 * Determines if the given action request is an ad hoc request which matches this set of criteria.
	 */
	public boolean matchesActionRequest(ActionRequestValue actionRequest) {
		if (!actionRequest.isAdHocRequest()) {
			return false;
		}
		if (getActionRequestId() != null && !getActionRequestId().equals(actionRequest.getActionRequestId()) ){
			return false;
		}
		if (!StringUtils.isEmpty(getNodeName()) && !getNodeName().equals(actionRequest.getNodeInstance().getName())) {
			return false;
		}
		if (getPrincipalId() != null && (!actionRequest.isUserRequest() || !actionRequest.getPrincipalId().equals(getPrincipalId()))) {
			return false;
		}
		if (getGroupId() != null && (!actionRequest.isGroupRequest() || !actionRequest.getGroupId().equals(getGroupId()))) {
			return false;
		}
		return true;
	}

	/**
	 * @return the group
	 */
	public String getGroupId() {
		return this.groupId;
	}

	/**
	 * @param groupId of the group to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
}
