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
package org.kuali.rice.kew.actions;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kim.bo.group.KimGroup;


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
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class AdHocRevoke implements java.io.Serializable {

	private static final long serialVersionUID = 8536540010313763068L;

	private Long actionRequestId;
	private String nodeName;
	private WorkflowUser user;
	private KimGroup group;
	
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
	public WorkflowUser getUser() {
		return user;
	}
	public void setUser(WorkflowUser user) {
		this.user = user;
	}
		
	/**
	 * Determines if the given action request is an ad hoc request which matches this set of criteria.
	 */
	public boolean matchesActionRequest(ActionRequestValue actionRequest) throws KEWUserNotFoundException {
		if (!actionRequest.isAdHocRequest()) {
			return false;
		}
		if (getActionRequestId() != null && !getActionRequestId().equals(actionRequest.getActionRequestId()) ){
			return false;
		}
		if (!StringUtils.isEmpty(getNodeName()) && !getNodeName().equals(actionRequest.getNodeInstance().getName())) {
			return false;
		}
		WorkflowUser user = getUser();
		if (user != null && (!actionRequest.isUserRequest() || !actionRequest.getWorkflowId().equals(user.getWorkflowId()))) {
			return false;
		}
		KimGroup group = getGroup();
		
		if (group != null && (!actionRequest.isGroupRequest() || !actionRequest.getGroupId().equals(group.getGroupId()))) {
			return false;
		}
		return true;
	}

	/**
	 * @return the group
	 */
	public KimGroup getGroup() {
		return this.group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(KimGroup group) {
		this.group = group;
	}
	
}
