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
import org.kuali.rice.kew.dto.GroupIdDTO;
import org.kuali.rice.kew.identity.service.IdentityHelperService;
import org.kuali.rice.kew.user.BaseWorkflowUser;
import org.kuali.rice.kew.user.EmplId;
import org.kuali.rice.kew.user.UuId;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.user.WorkflowUserId;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.impl.PersonImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.impl.KimUserServiceImpl;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityHelperServiceImpl implements IdentityHelperService {

	private static final Log logger = LogFactory.getLog(IdentityHelperServiceImpl.class);

	public KimGroup getGroup(GroupIdDTO groupId) {
		if (groupId.getGroupId() != null) {
			return KIMServiceLocator.getIdentityManagementService().getGroup(groupId.getGroupId());
		} else {
			return KIMServiceLocator.getIdentityManagementService().getGroupByName(groupId.getNamespace(), groupId.getGroupName());
		}
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
