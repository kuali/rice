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
package org.kuali.rice.kew.dto;

import org.kuali.rice.kim.bo.group.dto.GroupInfo;

/**
 * Represents a revocation of an AdHoc request.<br><br>
 * 
 * Each of the fields represents criteria for revoking requests.  If necessary, they can be
 * combined.  For example, if you specify a userId and a nodeName it will indicate that the
 * requests for the user at that node will need to be revoked.<br><br>
 * 
 * <ol>
 *   <li><b>actionRequestId</b> - the ID of the action request to revoke</li>
 *   <li><b>userId</b> - the ID of the user whose request(s) should be revoked</li>
 * 	 <li><b>groupId</b> - the ID of the group whose requests(s) should be revoked</li>
 *   <li><b>nodeName</b> - the name of the node to revoke requests at</li>
 * </ol>
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class AdHocRevokeDTO implements java.io.Serializable {

	private static final long serialVersionUID = 7650456194747488114L;

	private Long actionRequestId;
	private String nodeName;
	private UserIdDTO userId;
	private GroupIdDTO groupId;
	
	public AdHocRevokeDTO() {}
	
	public AdHocRevokeDTO(Long actionRequestId) {
		this.actionRequestId = actionRequestId;
	}
	
	public AdHocRevokeDTO(UserIdDTO userId) {
		this.userId = userId;
	}
	
	public AdHocRevokeDTO(String nodeName) {
		this.nodeName = nodeName;
	}

	public AdHocRevokeDTO(GroupIdDTO groupId)
	{
		this.groupId = groupId;
	}
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

	public UserIdDTO getUserId() {
		return userId;
	}

	public void setUserId(UserIdDTO user) {
		this.userId = user;
	}

	/**
	 * @return the groupinfo
	 */
	public GroupIdDTO getGroupId() {
		return this.groupId;
	}

	/**
	 * @param groupinfo the groupinfo to set
	 */
	public void setGroupId(GroupIdDTO groupId) {
		this.groupId = groupId;
	}

}
