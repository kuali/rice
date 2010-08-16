/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kew.actionlist.dao;

import java.util.Collection;
import java.util.List;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionitem.OutboxItemActionListExtension;
import org.kuali.rice.kew.actionlist.ActionListFilter;


/**
 * Data Access object for the Action List.
 *
 * @see ActionItem
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ActionListDAO {
    public Collection<ActionItem> getActionList(String principalId, ActionListFilter filter);
    public Collection<ActionItem> getActionListForSingleDocument(Long routeHeaderId);
    public int getCount(String workflowId);

    /**
     *
     * Retrieves {@link OutboxItemActionListExtension} items for the given user
     *
     * @param principalId
     * @param filter
     * @return
     */
    public Collection<ActionItem> getOutbox(String principalId, ActionListFilter filter);
    public void removeOutboxItems(String principalId, List<String> outboxItems);
    public void saveOutboxItem(OutboxItemActionListExtension outboxItem);
    public OutboxItemActionListExtension getOutboxByDocumentId(Long documentId);
    public OutboxItemActionListExtension getOutboxByDocumentIdUserId(Long documentId, String userId);
}
