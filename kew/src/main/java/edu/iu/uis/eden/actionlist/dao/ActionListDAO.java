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
package edu.iu.uis.eden.actionlist.dao;

import java.util.Collection;
import java.util.List;

import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionitem.OutboxItemActionListExtension;
import edu.iu.uis.eden.actionlist.ActionListFilter;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Data Access object for the Action List.
 *
 * @see ActionItem
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface ActionListDAO {
    public Collection<ActionItem> getActionList(WorkflowUser workflowUser, ActionListFilter filter);
    public Collection<ActionItem> getActionList(Long routeHeaderId, ActionListFilter filter);
    public int getCount(String workflowId);
    
    /**
     * 
     * Retrieves {@link OutboxItemActionListExtension} items for the given user
     * 
     * @param workflowUser
     * @param filter
     * @return
     */
    public Collection<ActionItem> getOutbox(WorkflowUser workflowUser, ActionListFilter filter);
    public void removeOutboxItems(WorkflowUser workflowUser, List<Long> outboxItems);
    public void saveOutboxItem(OutboxItemActionListExtension outboxItem);
    public OutboxItemActionListExtension getOutboxByDocumentId(Long documentId);
}
