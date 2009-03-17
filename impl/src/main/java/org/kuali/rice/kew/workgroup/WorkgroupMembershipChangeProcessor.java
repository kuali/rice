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

import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.messaging.ParameterTranslator;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.ksb.messaging.service.KSBXMLService;


/**
 * Executes the updating of {@link ActionItem}s for a {@link Workgroup} when
 * the membership of a Workgroup changes.  This keeps users' Action Lists
 * in-sync with their group membership.  Allowing their Action List to
 * be updated for requests routed to groups that they are either added to
 * or removed from.
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
		if (parameters.length != 3) {
			throw new IllegalArgumentException("The Workgroup Membership Change Processor requires four parameters.");
		}
		String operation = parameters[0];
		String principalId = parameters[1];
		String groupId = parameters[2];
		KimPrincipal principal = KIMServiceLocator.getIdentityManagementService().getPrincipal(principalId);
		if (principal == null) {
			throw new RiceRuntimeException("Could not locate the user for the given principal id '" + principalId + "'");
		}
		KimGroup group = KIMServiceLocator.getIdentityManagementService().getGroup(groupId);
		if (group == null) {
			throw new RiceRuntimeException("Could not locate the group with the given id '" + groupId + "'");
		}
		if (ADDED_OPERATION.equals(operation)) {
			KIMServiceLocator.getGroupInternalService().updateActionListForUserAddedToGroup(principalId, groupId);
		} else if (REMOVED_OPERATION.equals(operation)) {
			KIMServiceLocator.getGroupInternalService().updateActionListForUserRemovedFromGroup(principalId, groupId);
		} else {
			throw new WorkflowException("Did not understand requested group membership change operation '" + operation + "'");
		}
	}

	public static String getMemberAddedMessageContents(String principalId, String groupId) {
		return getMessageContents(principalId, groupId, ADDED_OPERATION);
    }

	public static String getMemberRemovedMessageContents(String principalId, String groupId) {
		return getMessageContents(principalId, groupId, REMOVED_OPERATION);
	}

	public static String getMessageContents(String principalId, String groupId, String operation) {
		ParameterTranslator translator = new ParameterTranslator();
		translator.addParameter(operation);
		translator.addParameter(principalId);
		translator.addParameter(groupId);
		return translator.getUntranslatedString();
	}

}
