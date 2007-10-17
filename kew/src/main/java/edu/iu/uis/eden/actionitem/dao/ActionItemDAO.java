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
package edu.iu.uis.eden.actionitem.dao;

import java.util.Collection;

import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.user.WorkflowUser;

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

    public Collection findByWorkflowUser(WorkflowUser workflowUser);

//    public Collection findByWorkgroupId(Long workgroupId);

    public Collection findByRouteHeaderId(Long routeHeaderId);
    
    public Collection findByActionRequestId(Long actionRequestId);

    public Collection findByWorkflowUserRouteHeaderId(String workflowUserId, Long routeHeaderId);

    //  public void deleteWorkgroupActionItems(Long routeHeaderId, Long
    // workGroupId);
    //  public void deleteUserActionItems(Long routeHeaderId, WorkflowUser
    // workflowUser);
    public void deleteActionItems(Long actionRequestId);
    
    public Collection findDelegators(WorkflowUser user, String delegationType) throws EdenUserNotFoundException;
    
}