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
package org.kuali.rice.kew.workgroup;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.messaging.ParameterTranslator;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.AuthenticationUserId;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.ksb.messaging.service.KSBXMLService;


/**
 * Executes the updating of {@link ActionItem}s for a {@link Workgroup} when
 * the membership of a Workgroup changes.
 *
 * @see ActionItem
 * @see Workgroup
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupMembershipChangeProcessor implements KSBXMLService {

	private static final String ADDED_OPERATION = "ADDED";
	private static final String REMOVED_OPERATION = "REMOVED";

	public void invoke(String contents) throws Exception {
		ParameterTranslator translator = new ParameterTranslator(contents);
		String[] parameters = translator.getParameters();
		if (parameters.length != 4) {
			throw new IllegalArgumentException("The Workgroup Membership Change Processor requires four parameters.");
		}
		String operation = parameters[0];
		WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(parameters[1]));
		if (user == null) {
			throw new KEWUserNotFoundException("Could not locate the user for the given authentication id '" + parameters[1] + "'");
		}
		Long versionNumber = new Long(parameters[3]);
		String groupName = new String(parameters[2]);
		GroupInfo group = KIMServiceLocator.getGroupService().getGroupInfoByName("KFS", groupName);
		if (group!=null)
			KIMServiceLocator.getGroupService().removePrincipalFromGroup(user.getWorkflowId(),group.getGroupId());
		if (group == null) {
			throw new WorkflowException("Could not locate the group with the given name '" + groupName + "'");
		}
		if (ADDED_OPERATION.equals(operation)) {
			KEWServiceLocator.getActionListService().updateActionListForUserAddedToGroup(user.getWorkflowId(), group);
		} else if (REMOVED_OPERATION.equals(operation)) {
			KEWServiceLocator.getActionListService().updateActionListForUserRemovedFromGroup(user.getWorkflowId(), group);
		} else {
			throw new WorkflowException("Did not understand requested group membership change operation '" + operation + "'");
		}
	}

	public static String getMemberAddedMessageContents(WorkflowUser user, Workgroup workgroup) {
		return getMessageContents(user, workgroup, ADDED_OPERATION);
    }

	public static String getMemberRemovedMessageContents(WorkflowUser user, Workgroup workgroup) {
		return getMessageContents(user, workgroup, REMOVED_OPERATION);
	}

	public static String getMessageContents(WorkflowUser user, Workgroup workgroup, String operation) {
		ParameterTranslator translator = new ParameterTranslator();
		translator.addParameter(operation);
		translator.addParameter(user.getAuthenticationUserId().getAuthenticationId());
		translator.addParameter(workgroup.getGroupNameId().getNameId());
		translator.addParameter(workgroup.getLockVerNbr().toString());
		// delay for about 10 seconds to allow a reasonable amount of time for the workgroup cache to be updated
		// (and hopefully prevent thrashing of the workgroup cache), regardless we will fall back on the version
		// number of the workgroup and check it against the machine this processor is processed on to ensure that
		// it's cache is up to date
		//SpringServiceLocator.getRouteQueueService().requeueDocument(new Long(-1), KEWConstants.ROUTE_QUEUE_DEFAULT_PRIORITY, new Long(10*1000), WorkgroupMembershipChangeProcessor.class.getName(), translator.getUntranslatedString());
		return translator.getUntranslatedString();
	}

}
