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
 * Cancels a document at the request of a client app.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class CancelAction extends ActionTakenEvent {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CancelAction.class);

    public CancelAction(DocumentRouteHeaderValue rh, WorkflowUser user) {
        super(rh, user);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_CANCELED_CD);
    }

    public CancelAction(DocumentRouteHeaderValue rh, WorkflowUser user, String annotation) {
        super(rh, user, annotation);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_CANCELED_CD);
    }

    @Override
    protected boolean requireInitiatorCheck() {
    	return routeHeader.getDocumentType().getInitiatorMustCancelPolicy().getPolicyValue().booleanValue();
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.actions.ActionTakenEvent#isActionCompatibleRequest(java.util.List)
     */
    @Override
    public String validateActionRules() throws EdenUserNotFoundException {
        return validateActionRules(getActionRequestService().findAllValidRequests(getUser(), routeHeader.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_COMPLETE_REQ));
    }

    private String validateActionRules(List actionRequests) throws EdenUserNotFoundException {
        String superError = super.validateActionTakenRules();
        if (!Utilities.isEmpty(superError)) {
            return superError;
        }
        // FYI delyea:  This is new validation check... was not being checked previously
        if (!getRouteHeader().isValidActionToTake(getActionPerformedCode())) {
            return "Document is not in a state to be cancelled";
        }
        if (!isActionCompatibleRequest(actionRequests)) {
            return "No request for the user is compatible with the Cancel Action";
        }
        return "";
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.actions.ActionTakenEvent#isActionCompatibleRequest(java.util.List)
     */
    @Override
    public boolean isActionCompatibleRequest(List requests) throws EdenUserNotFoundException {

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

            // APPROVE and COMPLETE request matches CANCEL Taken code
            if ( (EdenConstants.ACTION_REQUEST_APPROVE_REQ.equals(request)) ||
                 (EdenConstants.ACTION_REQUEST_COMPLETE_REQ.equals(request)) ) {
                actionCompatible = true;
                break;
            }
        }

        return actionCompatible;
    }

    public void recordAction() throws InvalidActionTakenException, EdenUserNotFoundException {
        MDC.put("docId", getRouteHeader().getRouteHeaderId());
        checkLocking();
        updateSearchableAttributesIfPossible();

        LOG.debug("Canceling document : " + annotation);

        List actionRequests = getActionRequestService().findAllValidRequests(getUser(), getRouteHeaderId(), EdenConstants.ACTION_REQUEST_COMPLETE_REQ);
        LOG.debug("Checking to see if the action is legal");
        String errorMessage = validateActionRules(actionRequests);
        if (!Utilities.isEmpty(errorMessage)) {
            throw new InvalidActionTakenException(errorMessage);
        }

//        List actionRequests = getActionRequestService().findAllValidRequests(getUser(), getRouteHeaderId(), EdenConstants.ACTION_REQUEST_COMPLETE_REQ);
//
//        LOG.debug("Checking to see if the action is legal");
//        if (!isActionCompatibleRequest(actionRequests, getActionTakenCode())) {
//            throw new InvalidActionTakenException("No request for the user is compatible with the DISAPPROVE or DENY action");
//        }

        LOG.debug("Record the cancel action");
        saveActionTaken(findDelegatorForActionRequests(actionRequests));

        LOG.debug("Deactivate all pending action requests");
        actionRequests = getActionRequestService().findPendingByDoc(getRouteHeaderId());

        getActionRequestService().deactivateRequests(actionTaken, actionRequests);
        notifyActionTaken(this.actionTaken);

        LOG.debug("Canceling document");

        try {
            String oldStatus = getRouteHeader().getDocRouteStatus();
            getRouteHeader().markDocumentCanceled();

            String newStatus = getRouteHeader().getDocRouteStatus();
            notifyStatusChange(newStatus, oldStatus);
        } catch (WorkflowException ex) {
            LOG.warn(ex, ex);
            throw new InvalidActionTakenException(ex.getMessage());
        }

        getRouteHeaderService().saveRouteHeader(getRouteHeader());
    }
}