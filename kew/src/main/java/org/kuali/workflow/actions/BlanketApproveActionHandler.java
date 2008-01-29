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
import edu.iu.uis.eden.actions.BlanketApproveAction;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Implements "blanketapprove" action
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BlanketApproveActionHandler implements DeferrableActionHandler {
    private static final Logger LOG = Logger.getLogger(BlanketApproveActionHandler.class);

    /**
     * @see org.kuali.workflow.actions.ActionHandler#invoke(edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue, edu.iu.uis.eden.user.WorkflowUser, java.lang.String, java.lang.Object[])
     */
    public void invoke(DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation, Object... args) throws InvalidActionTakenException, EdenUserNotFoundException {
        BlanketApproveAction action = new BlanketApproveAction(routeHeader, user, annotation, (Set) args[0]);
        action.performAction();
    }

    /**
     * @see org.kuali.workflow.actions.DeferrableActionHandler#performDeferredWork(Long, WorkflowUser, Object...)
     * @param args Long actionTakenId, Set<String> nodeNames
     */
    public void performDeferredWork(Long routeHeaderId, WorkflowUser user, Object... args) {
        Long actionTakenId = (Long) args[0];
        Set<String> nodeNames = (Set<String>) args[1];

        KEWServiceLocator.getRouteHeaderService().lockRouteHeader(routeHeaderId, true);
        DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(routeHeaderId);
        ActionTakenValue actionTaken = KEWServiceLocator.getActionTakenService().findByActionTakenId(actionTakenId);
        BlanketApproveAction blanketApprove = new BlanketApproveAction(document, user, "", nodeNames);
        LOG.debug("Doing blanket approve work document " + document.getRouteHeaderId());
        try {
            blanketApprove.performDeferredBlanketApproveWork(actionTaken);
        } catch (Exception e) {
            throw new WorkflowRuntimeException(e);
        }
        LOG.debug("Work done and document requeued, document " + document.getRouteHeaderId());
    }
}