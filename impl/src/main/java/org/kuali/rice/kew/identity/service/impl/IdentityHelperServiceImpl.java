/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kew.identity.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kew.actionrequest.KimPrincipalRecipient;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.identity.service.IdentityHelperService;
import org.kuali.rice.kew.identity.PrincipalName;
import org.kuali.rice.kew.user.EmplId;
import org.kuali.rice.kew.user.UserId;
import org.kuali.rice.kew.user.WorkflowUserId;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.workgroup.GroupId;
import org.kuali.rice.kew.workgroup.GroupNameId;
import org.kuali.rice.kew.workgroup.WorkflowGroupId;
import org.kuali.rice.kim.api.services.KIMServiceLocator;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.KimPrincipal;

/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
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

		public void validatePrincipalId(String principalId) {
			// the getPrincipal method attempts to load the principal with the id and throws an exception if it fails
			getPrincipal(principalId);
		}

	public String getIdForGroupName(String namespace, String groupName) {
		Group group = KIMServiceLocator.getIdentityManagementService().getGroupByName(namespace, groupName);
		if (group == null) {
			throw new RiceRuntimeException("Given namespace of '" + namespace + "' and name of '" + groupName + "' was invalid.  Failed to lookup a corresponding group ID.");
		}
		return group.getId();
	}


	public Recipient getPrincipalRecipient(String principalId) {
		KimPrincipal principal = getPrincipal(principalId);
		return new KimPrincipalRecipient(principal);
	}

	public KimPrincipal getPrincipal(String principalId) {
		KimPrincipal principal = KIMServiceLocator.getIdentityManagementService().getPrincipal(principalId);
		if (principal == null) {
			throw new RiceRuntimeException("Could not locate a principal with the given principalId of " + principalId);
		}
		return principal;
	}

	public KimPrincipal getPrincipalByPrincipalName(String principalName) {
		KimPrincipal principal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(principalName);
		if (principal == null) {
			throw new RiceRuntimeException("Could not locate a principal with the given principalName of " + principalName);
		}
		return principal;
	}

	public Group getGroupByName(String namespaceCode, String name) {
		Group group = KIMServiceLocator.getIdentityManagementService().getGroupByName(namespaceCode, name);
		if (group == null) {
			throw new RiceRuntimeException("Could not locate a group with the given namspace of '" + namespaceCode + "' and group name of '" + name + "'");
		}
		return group;
	}

	public Person getPerson(String principalId) {
		Person person = KIMServiceLocator.getPersonService().getPerson(principalId);
		if (person == null) {
			throw new RiceRuntimeException("Could not locate a person with the given principal id of " + principalId);
		}
		return person;
	}

	public Person getPersonByPrincipalName(String principalName) {
		Person person = KIMServiceLocator.getPersonService().getPersonByPrincipalName(principalName);
		if (person == null) {
			throw new RiceRuntimeException("Could not locate a person with the given principal name of " + principalName);
		}
		return person;
	}

	public Person getPersonByEmployeeId(String employeeId) {
		Person person = KIMServiceLocator.getPersonService().getPersonByEmployeeId(employeeId);
		if (person == null) {
			throw new RiceRuntimeException("Could not locate a person with the given employee id of " + employeeId);
		}
		return person;
	}


	public Group getGroup(String groupId) {
		Group group = KIMServiceLocator.getIdentityManagementService().getGroup(groupId);
		if (group == null) {
			throw new RiceRuntimeException("Could not locate a group with the given groupId of " + groupId);
		}
		return group;
	}

	public Group getGroup(GroupId groupId) {
		if (groupId == null || groupId.isEmpty()) {
			return null;
		} else if (groupId instanceof WorkflowGroupId) {
			return KIMServiceLocator.getIdentityManagementService().getGroup(""+((WorkflowGroupId)groupId).getGroupId());
		} else if (groupId instanceof GroupNameId) {
			return KIMServiceLocator.getIdentityManagementService().getGroupByName(((GroupNameId)groupId).getNamespace(), ((GroupNameId)groupId).getNameId());
		}
		throw new IllegalArgumentException("Invalid GroupId type was passed: " + groupId);
	}

	public KimPrincipal getPrincipal(UserId userId) {
		if (userId == null) {
			return null;
		} else if (userId instanceof WorkflowUserId) {
			String principalId = ((WorkflowUserId)userId).getWorkflowId();
			return KIMServiceLocator.getIdentityManagementService().getPrincipal(principalId);
		} else if (userId instanceof PrincipalName) {
			String principalName = ((PrincipalName)userId).getId();
			return KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(principalName);
		} else if (userId instanceof EmplId) {
			String employeeId = ((EmplId)userId).getEmplId();
			Person person = getPersonByEmployeeId(employeeId);
			return getPrincipal(person.getPrincipalId());
		}
		throw new IllegalArgumentException("Invalid UserIdDTO type was passed: " + userId);
	}
	
	public KimPrincipal getSystemPrincipal() {
		return getPrincipalByPrincipalName(KEWConstants.SYSTEM_USER);
	}

}
