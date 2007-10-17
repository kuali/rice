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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.MDC;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestFactory;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Disapproves a document. This deactivates all requests on the document and sends
 * acknowlegde requests to anybody who had already completed or approved the document.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DisapproveAction extends ActionTakenEvent {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisapproveAction.class);

    /**
     * @param rh RouteHeader for the document upon which the action is taken.
     * @param user User taking the action.
     */
    public DisapproveAction(DocumentRouteHeaderValue rh, WorkflowUser user) {
        super(rh, user);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_DENIED_CD);
    }

    /**
     * @param rh RouteHeader for the document upon which the action is taken.
     * @param user User taking the action.
     * @param annotation User comment on the action taken
     */
    public DisapproveAction(DocumentRouteHeaderValue rh, WorkflowUser user, String annotation) {
        super(rh, user, annotation);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_DENIED_CD);
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
        if (!getRouteHeader().isValidActionToTake(getActionPerformedCode())) {
            return "Document is not in a state to be disapproved";
        }
        if (!isActionCompatibleRequest(actionRequests)) {
            return "No request for the user is compatible " + "with the DISAPPROVE or DENY action";
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

            // APPROVE request matches all but FYI and ACK
            if ( (EdenConstants.ACTION_REQUEST_APPROVE_REQ.equals(request)) ||
                 (EdenConstants.ACTION_REQUEST_COMPLETE_REQ.equals(request)) ) {
                actionCompatible = true;
                break;
            }
        }

        return actionCompatible;
    }

    /**
     * Records the disapprove action. - Checks to make sure the document status allows the action. - Checks that the user has not taken a previous action. - Deactivates the pending requests for this user - Records the action
     *
     * @throws InvalidActionTakenException
     * @throws EdenUserNotFoundException
     */
    public void recordAction() throws InvalidActionTakenException, EdenUserNotFoundException {
        MDC.put("docId", getRouteHeader().getRouteHeaderId());
        checkLocking();
        updateSearchableAttributesIfPossible();

        LOG.debug("Disapproving document : " + annotation);

        List actionRequests = getActionRequestService().findAllValidRequests(getUser(), getRouteHeaderId(), EdenConstants.ACTION_REQUEST_COMPLETE_REQ);
        LOG.debug("Checking to see if the action is legal");
        String errorMessage = validateActionRules(actionRequests);
        if (!Utilities.isEmpty(errorMessage)) {
            throw new InvalidActionTakenException(errorMessage);
        }

//        if (!getRouteHeader().isValidActionToTake(getActionTakenCode())) {
//            LOG.warn("Document not in state to be disapproved.");
//            throw new InvalidActionTakenException("Document is not in a state to be disapproved");
//        }
//
//        List actionRequests = getActionRequestService().findAllValidRequests(getUser(), getRouteHeaderId(), EdenConstants.ACTION_REQUEST_COMPLETE_REQ);
//        if (!isActionCompatibleRequest(actionRequests, getActionTakenCode())) {
//            throw new InvalidActionTakenException("No request for the user is compatible " + "with the DISAPPROVE or DENY action");
//        }

        LOG.debug("Record the disapproval action");
        Recipient delegator = findDelegatorForActionRequests(actionRequests);
        saveActionTaken(delegator);

//        actionRequests = getActionRequestService().findByStatusAndDocId(EdenConstants.ACTION_REQUEST_DONE_STATE, getRouteHeaderId());
//        List actionRequestsToNotify = new ArrayList();
//        for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
//            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
//            //action request must be a complete and not to initiator (initiator will get specific request because they are initiator)
//            if (actionRequest.isApproveOrCompleteRequest() && ! actionRequest.isRecipientRoutedRequest(getRouteHeader().getInitiatorUser())) {
//                actionRequestsToNotify.add(actionRequest);
//            }
//        }

        LOG.debug("Deactivate all pending action requests");
        actionRequests = getActionRequestService().findPendingByDoc(getRouteHeaderId());
        getActionRequestService().deactivateRequests(actionTaken, actionRequests);
        notifyActionTaken(this.actionTaken);

        LOG.debug("Sending Acknowledgements to all previous approvers/completers");
   	 	// Generate the notification requests in the first node we find that the current user has an approve request
        RouteNodeInstance notificationNodeInstance = null;
//        if (actionRequests.size() > 0) { //I don't see why this matters let me know if it does rk
        	notificationNodeInstance = ((ActionRequestValue)actionRequests.get(0)).getNodeInstance();
//        }
        generateNotifications(notificationNodeInstance);

        LOG.debug("Disapproving document");
        try {
            String oldStatus = getRouteHeader().getDocRouteStatus();
            routeHeader.markDocumentDisapproved();
            String newStatus = getRouteHeader().getDocRouteStatus();
            getRouteHeaderService().saveRouteHeader(routeHeader);
            notifyStatusChange(newStatus, oldStatus);
        } catch (WorkflowException ex) {
            LOG.warn(ex, ex);
            throw new InvalidActionTakenException(ex.getMessage());
        }
    }

    //generate notifications to all people that have approved the document including the initiator
    private void generateNotifications(RouteNodeInstance notificationNodeInstance) throws EdenUserNotFoundException {
        Workgroup systemUserWorkgroup = getWorkgroupService().getWorkgroup(new GroupNameId(Utilities.getApplicationConstant(EdenConstants.NOTIFICATION_EXCLUDED_USERS_WORKGROUP_NAME)));
        Set<WorkflowUser> systemUserWorkflowIds = new HashSet<WorkflowUser>();
        if (systemUserWorkgroup != null) {
            systemUserWorkflowIds = new HashSet<WorkflowUser>(systemUserWorkgroup.getUsers());
        }
    	ActionRequestFactory arFactory = new ActionRequestFactory(getRouteHeader(), notificationNodeInstance);
    	Collection actions = KEWServiceLocator.getActionTakenService().findByRouteHeaderId(getRouteHeaderId());
    	//one notification per person
    	Set usersNotified = new HashSet();
    	for (Iterator iter = actions.iterator(); iter.hasNext();) {
			ActionTakenValue     action = (ActionTakenValue) iter.next();
			if ((action.isApproval() || action.isCompletion()) && ! usersNotified.contains(action.getWorkflowId())) {
                if (!systemUserWorkflowIds.contains(action.getWorkflowUser())) {
                    ActionRequestValue request = arFactory.createNotificationRequest(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, action.getWorkflowUser(), getActionTakenCode(), getUser(), getActionTakenCode());
                    KEWServiceLocator.getActionRequestService().activateRequest(request);
                    usersNotified.add(request.getWorkflowId());
                }
			}
		}
    }
}