/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kew.actionlist.service;

import java.util.Collection;
import java.util.List;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionitem.OutboxItem;
import org.kuali.rice.kew.actionlist.ActionListFilter;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;




/**
 * Main service for doing action list data access work
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface ActionListService {
    public ActionItem createActionItemForActionRequest(ActionRequestValue actionRequest);

    public Collection<ActionItem> getActionList(String principalId, ActionListFilter filter);

    public Collection<ActionItem> getActionListForSingleDocument(String documentId);

    /**
     * Returns a list of recipients <i>which secondary-delegate to</i> the target principalId
     * @param principalId the target principalId/delegate
     * @return a list of recipients <i>which secondary-delegate to</i> the target principalId
     */
    public Collection<Recipient> findUserSecondaryDelegators(String principalId);

    /**
     * Retruns a list of recipients <i>which are primary-delegated to by</i> the source principalId
     * @param principalId the source principalId to query for primary delegates
     * @return a list of recipients <i>which are primary-delegated to by</i> the source principalId
     */
    public Collection<Recipient> findUserPrimaryDelegations(String principalId);

    public ActionItem saveActionItem(ActionItem actionItem);

    public void deleteActionItemNoOutbox(ActionItem actionItem);

    public void deleteActionItem(ActionItem actionItem);

    public void deleteActionItem(ActionItem actionItem, boolean forceIntoOutbox);

    public void deleteByDocumentId(String documentId);

    public Collection<ActionItem> findByPrincipalId(String principalId);

    public Collection<ActionItem> findByWorkflowUserDocumentId(String workflowUserId, String documentId);

    public Collection<ActionItem> findByDocumentId(String documentId);

    public Collection<ActionItem> findByDocumentTypeName(String documentTypeName);

    public void updateActionItemsForTitleChange(String documentId, String newTitle);

    public ActionItem findByActionItemId(String actionItemId);

    /**
     * Retrieves the number of Action List items in the given user's primary Action List (does not include secondary delegations)
     */
    public int getCount(String principalId);

    /**
     * Retrieves the max action item Id  and the total number of action items for the given user's primary Action List
     * (does not include secondary delegations)
     * @param principalId
     */
    public List<Object> getMaxActionItemDateAssignedAndCountForUser(String principalId);

    public Collection<ActionItem> findByActionRequestId(String actionRequestId);

    /**
     *
     * Retrieves {@link OutboxItem} items for the given user
     *
     * @param principalId
     * @param filter
     * @return
     */
    public Collection<OutboxItem> getOutbox(String principalId, ActionListFilter filter);
    public Collection<OutboxItem> getOutboxItemsByDocumentType(String documentTypeName);
    public void removeOutboxItems(String principalId, List<String> outboxItems);
    public OutboxItem saveOutboxItem(ActionItem actionItem);
    public OutboxItem saveOutboxItem(ActionItem actionItem, boolean forceIntoOutbox);
    public OutboxItem saveOutboxItem(OutboxItem outboxItem);

    /**
     * Pulls a proxied version of the document route header with only the properties needed by the
     * action list display.
     */
    DocumentRouteHeaderValue getMinimalRouteHeader( String documentId );
}
