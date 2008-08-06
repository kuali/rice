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
package org.kuali.rice.kew.actionitem.dao;

import java.util.Collection;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.user.Recipient;
import org.kuali.rice.kew.user.WorkflowUser;


/**
 * Data Access Object for {@link ActionItem}s.
 * 
 * @see ActionItem
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface ActionItemDAO {

    public ActionItem findByActionItemId(Long actionItemId);
    
    public void deleteActionItem(ActionItem actionItem);

    public void deleteByRouteHeaderId(Long routeHeaderId);

    public void deleteByRouteHeaderIdWorkflowUserId(Long routeHeaderId, String workflowUserId);

    public void saveActionItem(ActionItem actionItem);

    public Collection<ActionItem> findByWorkflowUser(WorkflowUser workflowUser);

    public Collection<ActionItem> findByRouteHeaderId(Long routeHeaderId);
    
    public Collection<ActionItem> findByActionRequestId(Long actionRequestId);

    public Collection<ActionItem> findByWorkflowUserRouteHeaderId(String workflowUserId, Long routeHeaderId);
    
    public Collection<Recipient> findDelegators(WorkflowUser user, String delegationType) throws KEWUserNotFoundException;
    
}