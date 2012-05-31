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
package org.kuali.rice.kew.dto;


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
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AdHocRevokeDTO implements java.io.Serializable {

	private static final long serialVersionUID = 7650456194747488114L;

	private Long actionRequestId;
	private String nodeName;
	private String principalId;
	private String groupId;
	
	public AdHocRevokeDTO() {}
	
	public AdHocRevokeDTO(Long actionRequestId) {
		this.actionRequestId = actionRequestId;
	}
		
	public AdHocRevokeDTO(String nodeName) {
		this.nodeName = nodeName;
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

}
