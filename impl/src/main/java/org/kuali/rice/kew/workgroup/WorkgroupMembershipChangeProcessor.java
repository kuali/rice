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
package org.kuali.rice.kew.workgroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionlist.service.ActionListService;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.messaging.ParameterTranslator;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
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
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkgroupMembershipChangeProcessor implements KSBXMLService {

	private static final String ADDED_OPERATION = "ADDED";
	private static final String REMOVED_OPERATION = "REMOVED";

    public ActionRequestService getActionRequestService() {
        return (ActionRequestService) KEWServiceLocator.getActionRequestService();
    }
    
    public ActionListService getActionListService() {
    	return (ActionListService) KEWServiceLocator.getActionListService();
    }
    
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
		Group group = KIMServiceLocator.getIdentityManagementService().getGroup(groupId);
		if (group == null) {
			throw new RiceRuntimeException("Could not locate the group with the given id '" + groupId + "'");
		}
		if (ADDED_OPERATION.equals(operation)) {
			updateActionListForUserAddedToGroup(principalId, groupId);
		} else if (REMOVED_OPERATION.equals(operation)) {
			updateActionListForUserRemovedFromGroup(principalId, groupId);
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

    	
    
    /**
     * Update the user's Action List to reflect their addition to the given Workgroup.
     */
    private void updateActionListForUserAddedToGroup(String principalId, String groupId) {
		List<ActionRequestValue> actionRequests = new ArrayList<ActionRequestValue>();
		List<String> allGroupsToCheck = KIMServiceLocator.getIdentityManagementService().getParentGroupIds(groupId);
        allGroupsToCheck.add(0, groupId);
        for (String groupToCheckId : allGroupsToCheck) {
            actionRequests.addAll(getActionRequestService().findActivatedByGroup(groupToCheckId));
        }
        for (Iterator requestIt = actionRequests.iterator(); requestIt.hasNext();) {
            ActionRequestValue request = (ActionRequestValue) requestIt.next();
            ActionItem item = getActionListService().createActionItemForActionRequest(request);
            item.setPrincipalId(principalId);
            getActionListService().saveActionItem(item);
        }
    }
    
    private void updateActionListForUserRemovedFromGroup(String principalId, String groupId) {
		List<String> allGroupsToCheck = KIMServiceLocator.getIdentityManagementService().getParentGroupIds(groupId);
        allGroupsToCheck.add(0, groupId);
        Collection<ActionItem> actionItems = getActionListService().findByPrincipalId(principalId);
		for (Iterator<ActionItem> itemIt = actionItems.iterator(); itemIt.hasNext();) {
        	ActionItem item = itemIt.next();
        	if (item.isWorkgroupItem()) {
        		for (String groupIdToCheck : allGroupsToCheck) {
        			if (item.getGroupId().equals(groupIdToCheck)) {
        				getActionListService().deleteActionItem(item);
        			}
        		}
        	}
        }
    }    
}
