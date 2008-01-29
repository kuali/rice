/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.workflow.actions;

import java.util.Set;

import org.apache.log4j.Logger;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actions.MoveDocumentAction;
import edu.iu.uis.eden.actions.MovePoint;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Implements "movedocument" action 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class MoveDocumentActionHandler implements DeferrableActionHandler {
    private static final Logger LOG = Logger.getLogger(MoveDocumentActionHandler.class);

    /**
     * @see org.kuali.workflow.actions.ActionHandler#invoke(edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue, edu.iu.uis.eden.user.WorkflowUser, java.lang.String, java.lang.Object[])
     * @param args MovePoint
     */
    public void invoke(DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation, Object... args) throws InvalidActionTakenException, EdenUserNotFoundException {
        MoveDocumentAction action = new MoveDocumentAction(routeHeader, user, annotation,(MovePoint) args[0]);
        action.performAction();
    }

    /**
     * @see org.kuali.workflow.actions.DeferrableActionHandler#performDeferredWork(java.lang.Long, edu.iu.uis.eden.user.WorkflowUser, java.lang.Object[])
     * @param args ActionTakenValue actionTakenId, Set<String> nodeNames 
     */
    public void performDeferredWork(Long routeHeaderId, WorkflowUser user, Object... args) {
        ActionTakenValue actionTaken = (ActionTakenValue) args[0];
        Set<String> nodeNames = (Set<String>) args[1];
        
        KEWServiceLocator.getRouteHeaderService().lockRouteHeader(routeHeaderId, true);
        DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(routeHeaderId);

        MoveDocumentAction moveAction = new MoveDocumentAction(document, user, "", null);
        LOG.debug("Doing move document work " + document.getRouteHeaderId());
        try {
            moveAction.performDeferredMoveDocumentWork(actionTaken, nodeNames);
        } catch (Exception e) {
            throw new WorkflowRuntimeException(e);
        }
        LOG.debug("Work done and document requeued, document " + document.getRouteHeaderId());
    }   
}