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
package org.kuali.rice.kew.actionlist.service;

import java.util.Collection;
import java.util.List;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionitem.OutboxItemActionListExtension;
import org.kuali.rice.kew.actionlist.ActionListFilter;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.user.Recipient;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.workgroup.Workgroup;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.KimGroup;


/**
 * Main service for doing action list data access work
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface ActionListService {
    public ActionItem createActionItemForActionRequest(ActionRequestValue actionRequest);

    public Collection getActionList(String principalId, ActionListFilter filter);

    public Collection getActionListForSingleDocument(Long routeHeaderId);

    public Collection<Recipient> findUserSecondaryDelegators(WorkflowUser workflowUser) throws KEWUserNotFoundException;

    public Collection<Recipient> findUserPrimaryDelegations(WorkflowUser workflowUser) throws KEWUserNotFoundException;

    public boolean refreshActionList(String principalId);

    public void saveActionItem(ActionItem actionItem) throws KEWUserNotFoundException;

    public void deleteActionItem(ActionItem actionItem);

    public void deleteByRouteHeaderId(Long routeHeaderId);

    public Collection<ActionItem> findByPrincipalId(String principalId);

    public Collection findByWorkflowUser(WorkflowUser workflowUser);

    public Collection findByWorkflowUserRouteHeaderId(String workflowUserId, Long routeHeaderId);

    public Collection findByRouteHeaderId(Long routeHeaderId);

    /**
     * Updates ActionItems for workgroup members according to membership differences between the
     * twho workgroups.  Since the changeset of such an operation could potentially be quite large,
     * this method should schedule the changes to occur asynchronously to mitigate transaction
     * and concurent document modification issues.
     */
    public void updateActionItemsForWorkgroupChange(Workgroup oldWorkgroup, Workgroup newWorkgroup) throws KEWUserNotFoundException;

    /**
     * Updates the action list for a the given document for a user who was added to a workgroup.  This method will generate
     * new action items for the requests on the document which are for the workgroup.  This method will also verify that
     * the user is, in fact, still a member of the workgroup at the time of the invocation of this method before
     * generating the action items.
     */
    public void updateActionListForUserAddedToGroup(WorkflowUser user, KimGroup group) throws KEWUserNotFoundException;

    /**
     * Updates the action list for a the given document for a user who was removed from a workgroup.  This will delete
     * any action items for the given user on the document which were sent to that user because they were a
     * member of the workgroup.  This method will also verify that the user is still no longer a member of the workgroup
     * at the time of the method invocation before removing the action items.
     */
    public void updateActionListForUserRemovedFromGroup(WorkflowUser user,KimGroup group) throws KEWUserNotFoundException;

    public void updateActionItemsForTitleChange(Long routeHeaderId, String newTitle) throws KEWUserNotFoundException;

    public void validateActionItem(ActionItem actionItem);

    public ActionItem findByActionItemId(Long actionItemId);

    /**
     * Retrieves the number of Action List items in the given user's primary Action List (does not include secondary delegations)
     */
    public int getCount(WorkflowUser user);

    public void saveRefreshUserOption(String principalId);


    /**
     *
     * Retrieves {@link OutboxItemActionListExtension} items for the given user
     *
     * @param workflowUser
     * @param filter
     * @return
     */
    public Collection getOutbox(String principalId, ActionListFilter filter);
    public void removeOutboxItems(WorkflowUser workflowUser, List<Long> outboxItems);
    public void saveOutboxItem(ActionItem actionItem);
}
