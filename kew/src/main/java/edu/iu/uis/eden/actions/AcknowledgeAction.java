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
import edu.iu.uis.eden.exception.ResourceUnavailableException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;

/**
 * <p>
 * AcknowledegeAction records the users acknowledgement of a document
 * </p>
 * The routeheader is first checked to make sure the action is valid for the document.
 * Next the user is checked to make sure he/she has not taken a previous action on this
 * document at the actions responsibility or below. The action is recorded. Any requests
 * related to this user are deactivated.
 * </p>
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class AcknowledgeAction extends ActionTakenEvent {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AcknowledgeAction.class);

    /**
     * @param rh
     *            RouteHeader for the document upon which the action is taken.
     * @param user
     *            User taking the action.
     */
    public AcknowledgeAction(DocumentRouteHeaderValue rh, WorkflowUser user) {
        super(rh, user);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_ACKNOWLEDGED_CD);
    }

    /**
     * @param rh
     *            RouteHeader for the document upon which the action is taken.
     * @param user
     *            User taking the action.
     * @param delegator
     *            Delegator who delegated this action authority to the user.
     * @param annotation
     *            User comment on the action taken
     */
    public AcknowledgeAction(DocumentRouteHeaderValue rh, WorkflowUser user, String annotation) {
        super(rh, user, annotation);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_ACKNOWLEDGED_CD);
    }

    /**
     * Method to check if the Action is currently valid on the given document
     * @return  returns an error message to give system better identifier for problem
     */
    public String validateActionRules() throws EdenUserNotFoundException {
        return validateActionRules(getActionRequestService().findAllValidRequests(getUser(), routeHeader.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));
    }

    private String validateActionRules(List actionRequests) throws EdenUserNotFoundException {
        String superError = super.validateActionTakenRules();
        if (!Utilities.isEmpty(superError)) {
            return superError;
        }
        if (!getRouteHeader().isValidActionToTake(getActionPerformedCode())) {
            return "Document is not in a state to be acknowledged";
        }
        if (!isActionCompatibleRequest(actionRequests)) {
            return "No request for the user is compatible " + "with the ACKNOWLEDGE action";
        }
        return "";
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.actions.ActionTakenEvent#isActionCompatibleRequest(java.util.List, java.lang.String)
     */
    @Override
    public boolean isActionCompatibleRequest(List requests) throws EdenUserNotFoundException {

        // we allow pre-approval
        if (requests.isEmpty()) {
            return true;
        }

        // can always cancel saved or initiated document
        if (routeHeader.isStateInitiated() || routeHeader.isStateSaved()) {
            return true;
        }

        boolean actionCompatible = false;
        Iterator ars = requests.iterator();
        ActionRequestValue actionRequest = null;

        while (ars.hasNext()) {
            actionRequest = (ActionRequestValue) ars.next();
            String request = actionRequest.getActionRequested();

            // Acknowledge Taken Code matches Fyi and Ack
            if ( (EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ.equals(request)) || (EdenConstants.ACTION_REQUEST_FYI_REQ.equals(request)) ) {
                actionCompatible = true;
                break;
            }
        }

        return actionCompatible;
    }

    /**
     * Records the Acknowldege action. - Checks to make sure the document status allows the action. - Checks that the user has not taken a previous action. - Deactivates the pending requests for this user - Records the action
     *
     * @throws InvalidActionTakenException
     * @throws ResourceUnavailableException
     */
    public void recordAction() throws InvalidActionTakenException, EdenUserNotFoundException {
        MDC.put("docId", getRouteHeader().getRouteHeaderId());
        checkLocking();
        updateSearchableAttributesIfPossible();

        if (annotation == null) {
            annotation = "";
        }
        LOG.debug("Acknowledging document : " + annotation);

        LOG.debug("Checking to see if the action is legal");
        List actionRequests = getActionRequestService().findAllValidRequests(getUser(), routeHeader.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ);
        String errorMessage = validateActionRules(actionRequests);
        if (!Utilities.isEmpty(errorMessage)) {
            throw new InvalidActionTakenException(errorMessage);
        }

//        if (!getRouteHeader().isValidActionToTake(getActionTakenCode())) {
//            LOG.warn("Document not in state to be acknowledged.");
//            throw new InvalidActionTakenException("Document is not in a state to be acknowledged");
//        }
//
//        List actionRequests = getActionRequestService().findAllValidRequests(getUser(), routeHeader.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ);
//        if (!isActionCompatibleRequest(actionRequests, getActionTakenCode())) {
//            throw new InvalidActionTakenException("No request for the user is compatible " + "with the DISAPPROVE or DENY action");
//        }

        LOG.debug("Record the acknowledge action");
        Recipient delegator = findDelegatorForActionRequests(actionRequests);
        saveActionTaken(delegator);
        LOG.debug("Deactivate all pending action requests");
        getActionRequestService().deactivateRequests(actionTaken, actionRequests);
        notifyActionTaken(this.actionTaken);
    }
}