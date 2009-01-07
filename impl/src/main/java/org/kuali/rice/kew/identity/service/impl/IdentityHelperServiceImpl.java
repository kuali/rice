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
package org.kuali.rice.kew.identity.service.impl;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.kew.actionrequest.KimGroupRecipient;
import org.kuali.rice.kew.dto.GroupIdDTO;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.dto.UserIdDTO;
import org.kuali.rice.kew.dto.WorkflowIdDTO;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.identity.service.IdentityHelperService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.BaseWorkflowUser;
import org.kuali.rice.kew.user.EmplId;
import org.kuali.rice.kew.user.Recipient;
import org.kuali.rice.kew.user.UuId;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.user.WorkflowUserId;
import org.kuali.rice.kew.workgroup.GroupId;
import org.kuali.rice.kew.workgroup.GroupNameId;
import org.kuali.rice.kew.workgroup.WorkflowGroupId;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityHelperServiceImpl implements IdentityHelperService {

	private static final Log logger = LogFactory.getLog(IdentityHelperServiceImpl.class);

		public String getIdForPrincipalName(String principalName) {
		if (principalName == null) {
			throw new IllegalArgumentException("Can't lookup a principal ID for a null principal name.");
		}
		KimPrincipal principal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(principalName);
		if (principal == null) {
			throw new RiceRuntimeException("Given principal name of '" + principalName + "' was invalid.  Failed to lookup a corresponding principal ID.");
		}
		return principal.getPrincipalId();
	}
	
	public String getIdForGroupName(String namespace, String groupName) {
		KimGroup group = KIMServiceLocator.getIdentityManagementService().getGroupByName(namespace, groupName);
		if (group == null) {
			throw new RiceRuntimeException("Given namespace of '" + namespace + "' and name of '" + groupName + "' was invalid.  Failed to lookup a corresponding group ID.");
		}
		return group.getGroupId();
	}

	
	public KimGroup getGroup(GroupIdDTO groupId) {
		if (groupId.getGroupId() != null) {
			return KIMServiceLocator.getIdentityManagementService().getGroup(groupId.getGroupId());
		} else {
			return KIMServiceLocator.getIdentityManagementService().getGroupByName(groupId.getNamespace(), groupId.getGroupName());
		}
	}

	public KimGroup getGroup(GroupId groupId) {
		if (groupId == null || groupId.isEmpty()) {
			return null;
		} else if (groupId instanceof WorkflowGroupId) {
			return KIMServiceLocator.getIdentityManagementService().getGroup(""+((WorkflowGroupId)groupId).getGroupId());
		} else if (groupId instanceof GroupNameId) {
			return KIMServiceLocator.getIdentityManagementService().getGroupByName(KimConstants.TEMP_GROUP_NAMESPACE, ((GroupNameId)groupId).getNameId());
		}
		throw new IllegalArgumentException("Invalid GroupId type was passed: " + groupId);
	}
	
	public String getGroupId(GroupId groupId) {
		if (groupId == null || groupId.isEmpty()) {
			return null;
		} else if (groupId instanceof WorkflowGroupId) {
			return ((WorkflowGroupId)groupId).getGroupId().toString();
		} else if (groupId instanceof GroupNameId) {
			KimGroup group = getGroup(groupId);
			return group.getGroupId();
		}
		throw new IllegalArgumentException("Invalid GroupId type was passed: " + groupId);
	}
	
	public KimPrincipal getPrincipal(UserIdDTO userId) {
		if (userId == null) {
			return null;
		} else if (userId instanceof WorkflowIdDTO) {
			String principalId = ((WorkflowIdDTO)userId).getWorkflowId();
			return KIMServiceLocator.getIdentityManagementService().getPrincipal(principalId);
		} else if (userId instanceof NetworkIdDTO) {
			String principalName = ((NetworkIdDTO)userId).getNetworkId();
			return KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(principalName);
		}
		throw new IllegalArgumentException("Invalid UserIdDTO type was passed: " + userId);
	}
	
	public String getPrincipalId(UserIdDTO userId) {
		if (userId == null) {
			return null;
		} else if (userId instanceof WorkflowIdDTO) {
			return ((WorkflowIdDTO)userId).getWorkflowId();
		} else if (userId instanceof NetworkIdDTO) {
			String principalName = ((NetworkIdDTO)userId).getNetworkId();
			KimPrincipal principal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(principalName);
			return principal.getPrincipalId();
		}
		throw new IllegalArgumentException("Invalid UserIdDTO type was passed: " + userId);
	}
	
	public Recipient getPrincipalRecipient(String principalId) throws KEWUserNotFoundException {
		// for now, until WorkflowUser is converted, let's continue using WorkflowUser
		return KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(principalId));
	}
	
	public Recipient getGroupRecipient(String groupId) {
		KimGroup group = KIMServiceLocator.getIdentityManagementService().getGroup(groupId);
		return new KimGroupRecipient(group);
	}

	public WorkflowUser convertPersonToWorkflowUser(Person person) {
		if (person == null) {
			logger.error("KimUserService.convertPersonToWorkflowUser() was passed a null Person object");
			return null;
		}
		BaseWorkflowUser user = new org.kuali.rice.kns.workflow.bo.WorkflowUser();
		user.setWorkflowUserId(new WorkflowUserId(person.getPrincipalId()));
		user.setLockVerNbr(1);
		user.setAuthenticationUserId(new org.kuali.rice.kew.user.AuthenticationUserId(person.getPrincipalName()));
		user.setDisplayName(person.getName());
		user.setEmailAddress(person.getEmailAddress());
		user.setEmplId(new EmplId(person.getExternalId("EMPLOYEE")));
		user.setGivenName(person.getFirstName());
		user.setLastName(person.getLastName());
		user.setUuId(new UuId(person.getPrincipalId()));
		user.setCreateDate(new Timestamp(new Date().getTime()));
		user.setLastUpdateDate(new Timestamp(new Date().getTime()));
		return user;
	}

}
