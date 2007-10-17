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
package edu.iu.uis.eden.actions;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.MDC;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;

/**
 * Action that puts the document in routing.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RouteDocumentAction extends ActionTakenEvent {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RouteDocumentAction.class);

    public RouteDocumentAction(DocumentRouteHeaderValue rh, WorkflowUser user) {
        super(rh, user);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_COMPLETED_CD);
    }

    public RouteDocumentAction(DocumentRouteHeaderValue rh, WorkflowUser user, String annotation) {
        super(rh, user, annotation);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_COMPLETED_CD);
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.actions.ActionTakenEvent#getActionPerformedCode()
     */
    @Override
    public String getActionPerformedCode() {
        return EdenConstants.ACTION_TAKEN_ROUTED_CD;
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.actions.ActionTakenEvent#requireInitiatorCheck()
     */
    @Override
    protected boolean requireInitiatorCheck() {
        return routeHeader.getDocumentType().getInitiatorMustRoutePolicy().getPolicyValue().booleanValue();
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.actions.ActionTakenEvent#isActionCompatibleRequest(java.util.List)
     */
    @Override
    public String validateActionRules() throws EdenUserNotFoundException {
        String superError = super.validateActionTakenRules();
        if (!Utilities.isEmpty(superError)) {
            return superError;
        }
        if (!getRouteHeader().isValidActionToTake(getActionPerformedCode())) {
            return "Document is not in a state to be routed";
        }
        return "";
    }

    /**
     * Record the routing action. To route a document, it must be in the proper state. Previous requests and actions have no bearing on the outcome of this action, unless the
     * @throws edu.iu.uis.eden.exception.InvalidActionTakenException
     * @throws edu.iu.uis.eden.exception.EdenUserNotFoundException
     */
    public void recordAction() throws edu.iu.uis.eden.exception.InvalidActionTakenException, EdenUserNotFoundException {
        MDC.put("docId", getRouteHeader().getRouteHeaderId());
        checkLocking();
        updateSearchableAttributesIfPossible();
//        if (routeHeader.getDocumentType().getInitiatorMustRoutePolicy().getPolicyValue().booleanValue()) {
//            super.recordAction();
//        }

        if (annotation == null) {
            annotation = "";
        }

        LOG.debug("Routing document : " + annotation);

        LOG.debug("Checking to see if the action is legal");
        String errorMessage = validateActionRules();
        if (!Utilities.isEmpty(errorMessage)) {
            throw new InvalidActionTakenException(errorMessage);
        }


        // we want to check that the "RouteDocument" command is valid here, not the "Complete" command (which is in our Action's action taken code)
//        if (getRouteHeader().isValidActionToTake(EdenConstants.ACTION_TAKEN_ROUTED_CD)) {
            LOG.debug("Record the routing action");
            saveActionTaken();

            //TODO this will get all pending AR's even if they haven't been in an action list... This seems bad
            List actionRequests = getActionRequestService().findPendingByDoc(getRouteHeaderId());
            LOG.debug("Deactivate all pending action requests");
            // deactivate any requests for the user that routed the document.
            for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
                ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
                // requests generated to the user who is routing the document should be deactivated
                if ( (getUser().getWorkflowId().equals(actionRequest.getWorkflowId())) && (actionRequest.isActive()) ) {
                    getActionRequestService().deactivateRequest(actionTaken, actionRequest);
                }
                // requests generated by a save action should be deactivated
                else if (EdenConstants.SAVED_REQUEST_RESPONSIBILITY_ID.equals(actionRequest.getResponsibilityId())) {
                    getActionRequestService().deactivateRequest(actionTaken, actionRequest);
                }
            }

            notifyActionTaken(this.actionTaken);

            try {
                String oldStatus = getRouteHeader().getDocRouteStatus();
                getRouteHeader().markDocumentEnroute();
                getRouteHeader().setRoutedByUserWorkflowId(getUser().getWorkflowId());

                String newStatus = getRouteHeader().getDocRouteStatus();
                notifyStatusChange(newStatus, oldStatus);
                getRouteHeaderService().saveRouteHeader(getRouteHeader());
            } catch (WorkflowException ex) {
                LOG.warn(ex, ex);
	            throw new InvalidActionTakenException(ex.getMessage());
            }
//        } else {
//            LOG.warn("Document not in state to be routed.");
//            throw new InvalidActionTakenException("Document is not in a state to be routed");
//        }
    }
}