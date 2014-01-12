/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kew.actiontaken.service;

import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;


/**
 * Responsible for the data access for {@link ActionTakenValue} objects.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ActionTakenService {

    ActionTakenValue findByActionTakenId(String actionTakenId);

    ActionTakenValue saveActionTaken(ActionTakenValue actionTaken);

    ActionTakenValue getPreviousAction(ActionRequestValue actionRequest);

    ActionTakenValue getPreviousAction(ActionRequestValue actionRequest, List<ActionTakenValue> simulatedActionsTaken);

    Collection<ActionTakenValue> findByDocumentId(String documentId);

    List<ActionTakenValue> findByDocumentIdPrincipalId(String documentId, String workflowId);

    void delete(ActionTakenValue actionTaken);

    List<ActionTakenValue> findByDocumentIdIgnoreCurrentInd(String documentId);

    boolean hasUserTakenAction(String principalId, String documentId);

    Timestamp getLastApprovedDate(String documentId);

}
