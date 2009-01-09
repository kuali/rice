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
package org.kuali.rice.kew.actiontaken.service;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.user.WorkflowUser;


/**
 * Responsible for the data access for {@link ActionTakenValue} objects.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface ActionTakenService {

    public ActionTakenValue load(Long id);

    public ActionTakenValue findByActionTakenId(Long actionTakenId);

    public Collection getActionsTaken(Long routeHeaderId);

    public void saveActionTaken(ActionTakenValue actionTaken);

    public ActionTakenValue getPreviousAction(ActionRequestValue actionRequest);

    public ActionTakenValue getPreviousAction(ActionRequestValue actionRequest, List<ActionTakenValue> simulatedActionsTaken);

    public Collection findByRouteHeaderId(Long routeHeaderId);

    public Collection findByDocIdAndAction(Long docId, String action);

    public List findByRouteHeaderIdWorkflowId(Long routeHeaderId, String workflowId);

    public void delete(ActionTakenValue actionTaken);

    public List findByRouteHeaderIdIgnoreCurrentInd(Long routeHeaderId);

    public void deleteByRouteHeaderId(Long routeHeaderId);

    public void validateActionTaken(ActionTakenValue actionTaken);

    public boolean hasUserTakenAction(WorkflowUser user, Long documentId);
    
    public Timestamp getLastApprovedDate(Long routeHeaderId);
}