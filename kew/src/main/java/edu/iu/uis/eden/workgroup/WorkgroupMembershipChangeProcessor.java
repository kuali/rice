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
package edu.iu.uis.eden.workgroup;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.messaging.KEWXMLService;
import edu.iu.uis.eden.messaging.ParameterTranslator;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Executes the updating of {@link ActionItem}s for a {@link Workgroup} when
 * the membership of a Workgroup changes.
 *
 * @see ActionItem
 * @see Workgroup
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupMembershipChangeProcessor implements KEWXMLService {

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
			throw new EdenUserNotFoundException("Could not locate the user for the given authentication id '" + parameters[1] + "'");
		}
		Long versionNumber = new Long(parameters[3]);
		GroupNameId workgroupName = new GroupNameId(parameters[2]);
		Workgroup workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(workgroupName);
		if (workgroup != null && !workgroup.getLockVerNbr().equals(versionNumber)) {
			// if the lock version numbers don't match, then refresh the workgroup from the cache
			KEWServiceLocator.getWorkgroupService().removeNameFromCache(workgroupName);
			workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(workgroupName);
		}
		if (workgroup == null) {
			throw new WorkflowException("Could not locate the workgroup with the given name '" + workgroupName + "'");
		}
		if (ADDED_OPERATION.equals(operation)) {
			KEWServiceLocator.getActionListService().updateActionListForUserAddedToWorkgroup(user, workgroup);
		} else if (REMOVED_OPERATION.equals(operation)) {
			KEWServiceLocator.getActionListService().updateActionListForUserRemovedFromWorkgroup(user, workgroup);
		} else {
			throw new WorkflowException("Did not understand requested workgroup membership change operation '" + operation + "'");
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
		//SpringServiceLocator.getRouteQueueService().requeueDocument(new Long(-1), EdenConstants.ROUTE_QUEUE_DEFAULT_PRIORITY, new Long(10*1000), WorkgroupMembershipChangeProcessor.class.getName(), translator.getUntranslatedString());
		return translator.getUntranslatedString();
	}

}
