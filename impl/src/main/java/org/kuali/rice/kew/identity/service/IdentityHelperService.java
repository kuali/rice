/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kew.identity.service;

import org.kuali.rice.kew.dto.GroupIdDTO;
import org.kuali.rice.kew.dto.UserIdDTO;
import org.kuali.rice.kew.user.Recipient;
import org.kuali.rice.kew.user.UserId;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.workgroup.GroupId;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.KimGroup;

/**
 * A simple helper service in KEW for interacting with the KIM identity
 * management services.  Some of the methods on here exist solely for
 * the purpose of assisting with the piece-by-piece migration of
 * KEW to use the KIM services.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface IdentityHelperService {

	public KimGroup getGroup(GroupIdDTO groupId);
	
	public String getIdForPrincipalName(String principalName);
	
	public String getIdForGroupName(String namespace, String groupName);
	
	/**
	 * Returns the KimPrincipal for the given principalId.  Throws an exception
	 * if the principalId cannot be resolved to a principal.
	 */
	public KimPrincipal getPrincipal(String principalId);
	
	public Recipient getPrincipalRecipient(String principalId);
	
	/**
	 * @deprecated Please refrain from using the GroupId object, instead just call the
	 * appropriate KIM services passing either the groupId or the namespace+groupName
	 */
	public KimGroup getGroup(GroupId groupId);
	
	/**
	 * @deprecated
	 */
	public String getGroupId(GroupId groupId);
	
	/**
	 * @deprecated
	 */
	public KimPrincipal getPrincipal(UserId userId);
	
	/**
	 * @deprecated
	 */
	public KimPrincipal getPrincipal(UserIdDTO userId);
	
	/**
	 * @deprecated
	 */
	public String getPrincipalId(UserIdDTO userId);
		
	/**
	 * @deprecated
	 */
	public Recipient getGroupRecipient(String groupId);
	
	public WorkflowUser convertPersonToWorkflowUser(Person person);

}
