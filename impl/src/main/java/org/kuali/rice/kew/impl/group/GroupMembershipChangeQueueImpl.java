/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.kew.impl.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionlist.service.ActionListService;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.group.GroupMembershipChangeQueue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

/**
 * Executes the updating of {@link ActionItem}s for a {@link Workgroup} when
 * the membership of a group changes.  This keeps users' Action Lists
 * in-sync with their group membership.  Allowing their Action List to
 * be updated for requests routed to groups that they are either added to
 * or removed from.
 *
 * @see ActionItem
 * @see Group 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class GroupMembershipChangeQueueImpl implements GroupMembershipChangeQueue {
    
    /**
     * @see org.kuali.rice.kew.api.group.GroupMembershipChangeQueue#notifyMembershipChange(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void notifyMembershipChange(String operation, String groupId, String principalId) {
        Principal principal = KimApiServiceLocator.getIdentityService().getPrincipal(principalId);
        if (principal == null) {
            throw new RiceRuntimeException("Could not locate the user for the given principal id '" + principalId + "'");
        }
        Group group = KimApiServiceLocator.getGroupService().getGroup(groupId);
        if (group == null) {
            throw new RiceRuntimeException("Could not locate the group with the given id '" + groupId + "'");
        }
        if (KEWConstants.GroupMembershipChangeOperations.ADDED.equalsIgnoreCase(operation)) {
            updateActionListForUserAddedToGroup(principalId, groupId);
        } else if (KEWConstants.GroupMembershipChangeOperations.REMOVED.equalsIgnoreCase(operation)) {
            updateActionListForUserRemovedFromGroup(principalId, groupId);
        } else {
            throw new WorkflowRuntimeException("Did not understand requested group membership change operation '" + operation + "'");
        }
    }

    
    /**
     * Update the user's Action List to reflect their addition to the given Workgroup.
     */
    private void updateActionListForUserAddedToGroup(String principalId, String groupId) {
        List<ActionRequestValue> actionRequests = new ArrayList<ActionRequestValue>();
        List<String> allGroupsToCheck = KimApiServiceLocator.getGroupService().getParentGroupIds(groupId);
        allGroupsToCheck.add(0, groupId);
        for (String groupToCheckId : allGroupsToCheck) {
            actionRequests.addAll(getActionRequestService().findActivatedByGroup(groupToCheckId));
        }
        for (ActionRequestValue request : actionRequests) {
            ActionItem item = getActionListService().createActionItemForActionRequest(request);
            item.setPrincipalId(principalId);
            getActionListService().saveActionItem(item);
        }
    }
    
    private void updateActionListForUserRemovedFromGroup(String principalId, String groupId) {
        List<String> allGroupsToCheck = KimApiServiceLocator.getGroupService().getParentGroupIds(groupId);
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
    
    public ActionRequestService getActionRequestService() {
        return (ActionRequestService) KEWServiceLocator.getActionRequestService();
    }
    
    public ActionListService getActionListService() {
        return (ActionListService) KEWServiceLocator.getActionListService();
    }
}
