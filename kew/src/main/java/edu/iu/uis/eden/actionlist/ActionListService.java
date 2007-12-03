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
package edu.iu.uis.eden.actionlist;

import java.util.Collection;
import java.util.List;

import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Main service for doing action list data access work
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface ActionListService {

    public Collection getActionList(WorkflowUser workflowUser, ActionListFilter filter);

    public Collection findUserDelegators(WorkflowUser workflowUser, String delegationType) throws EdenUserNotFoundException;

    public boolean refreshActionList(WorkflowUser user);

    public void saveActionItem(ActionItem actionItem) throws EdenUserNotFoundException;

    public void deleteActionItem(ActionItem actionItem);

    public void deleteByRouteHeaderId(Long routeHeaderId);

    public void deleteActionItems(Long actionRequestId);

    public List generateActionItems(ActionRequestValue actionRequest, boolean simulate) throws EdenUserNotFoundException;

    public Collection findByWorkflowUser(WorkflowUser workflowUser);

//    public Collection findByWorkgroupId(Long workgroupId);

    public Collection findByWorkflowUserRouteHeaderId(String workflowUserId, Long routeHeaderId);

    public Collection findByRouteHeaderId(Long routeHeaderId);

    /**
     * Updates ActionItems for workgroup members according to membership differences between the
     * twho workgroups.  Since the changeset of such an operation could potentially be quite large,
     * this method should schedule the changes to occur asynchronously to mitigate transaction
     * and concurent document modification issues.
     */
    public void updateActionItemsForWorkgroupChange(Workgroup oldWorkgroup, Workgroup newWorkgroup) throws EdenUserNotFoundException;

    /**
     * Updates the action list for a the given document for a user who was added to a workgroup.  This method will generate
     * new action items for the requests on the document which are for the workgroup.  This method will also verify that
     * the user is, in fact, still a member of the workgroup at the time of the invocation of this method before
     * generating the action items.
     */
    public void updateActionListForUserAddedToWorkgroup(WorkflowUser user, Workgroup workgroup) throws EdenUserNotFoundException;

    /**
     * Updates the action list for a the given document for a user who was removed from a workgroup.  This will delete
     * any action items for the given user on the document which were sent to that user because they were a
     * member of the workgroup.  This method will also verify that the user is still no longer a member of the workgroup
     * at the time of the method invocation before removing the action items.
     */
    public void updateActionListForUserRemovedFromWorkgroup(WorkflowUser user, Workgroup workgroup) throws EdenUserNotFoundException;

    public void updateActionItemsForTitleChange(Long routeHeaderId, String newTitle) throws EdenUserNotFoundException;

    public Collection findDelegators(WorkflowUser user, String delegationType) throws EdenUserNotFoundException;

    public void validateActionItem(ActionItem actionItem);

    public ActionItem findByActionItemId(Long actionItemId);

    /**
     * Retrieves the number of Action List items in the given user's primary Action List (does not include secondary delegations)
     */
    public int getCount(WorkflowUser user);

    public void saveRefreshUserOption(WorkflowUser user);

}
