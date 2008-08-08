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
package org.kuali.rice.kew.actions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.MDC;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actions.asyncservices.BlanketApproveProcessorService;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.doctype.DocumentType;
import org.kuali.rice.kew.engine.BlanketApproveEngine;
import org.kuali.rice.kew.engine.CompatUtils;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.engine.node.service.RouteNodeService;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.exception.InvalidActionTakenException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.messaging.MessageServiceNames;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.user.Recipient;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;


/**
 * Does the sync work for blanket approves requested by client apps.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BlanketApproveAction extends ActionTakenEvent {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BlanketApproveAction.class);
    private Set nodeNames;

    public BlanketApproveAction(DocumentRouteHeaderValue rh, WorkflowUser user) {
        super(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD, rh, user);
    }

    public BlanketApproveAction(DocumentRouteHeaderValue rh, WorkflowUser user, String annotation, Integer routeLevel) {
        this(rh, user, annotation, convertRouteLevel(rh.getDocumentType(), routeLevel));
    }

    public BlanketApproveAction(DocumentRouteHeaderValue rh, WorkflowUser user, String annotation, String nodeName) {
        this(rh, user, annotation, Utilities.asSet(nodeName));
    }

    public BlanketApproveAction(DocumentRouteHeaderValue rh, WorkflowUser user, String annotation, Set nodeNames) {
        super(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD, rh, user, annotation);
        this.nodeNames = (nodeNames == null ? new HashSet() : nodeNames);
    }

    private static Set convertRouteLevel(DocumentType documentType, Integer routeLevel) {
        Set<String> nodeNames = new HashSet<String>();
        if (routeLevel == null) {
            return nodeNames;
        }
        RouteNode node = CompatUtils.getNodeForLevel(documentType, routeLevel);
        if (node == null) {
            throw new WorkflowRuntimeException("Could not locate a valid node for the given route level: " + routeLevel);
        }
        nodeNames.add(node.getRouteNodeName());
        return nodeNames;
    }

    @Override
    protected boolean requireInitiatorCheck() {
    	return routeHeader.getDocumentType().getInitiatorMustBlanketApprovePolicy().getPolicyValue().booleanValue();
    }

    /* (non-Javadoc)
     * @see org.kuali.rice.kew.actions.ActionTakenEvent#validateActionRules()
     */
    @Override
    public String validateActionRules() throws KEWUserNotFoundException {
        return validateActionRules(getActionRequestService().findAllValidRequests(getUser(), routeHeader.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_COMPLETE_REQ));
    }

    private String validateActionRules(List<ActionRequestValue> actionRequests) throws KEWUserNotFoundException {
        String superError = super.validateActionTakenRules();
        if (!Utilities.isEmpty(superError)) {
            return superError;
        }
        if ( (getRouteHeader().getDocumentType() != null) && (! getRouteHeader().getDocumentType().isUserBlanketApprover(getUser())) ) {
            return "User is not authorized to BlanketApprove document";
        }
        if ( (nodeNames != null) && (!nodeNames.isEmpty()) ) {
            String nodeName = isGivenNodeListValid();
            if (!Utilities.isEmpty(nodeName)) {
                return "Document already at or beyond route node " + nodeName;
            }
        }
        if (!getRouteHeader().isValidActionToTake(getActionPerformedCode())) {
            return "Document is not in a state to be approved";
        }
        if (!isActionCompatibleRequest(actionRequests)) {
            return "No request for the user is compatible with the BlanketApprove Action";
        }
        return "";
    }

    private String isGivenNodeListValid() {
        for (Iterator iterator = nodeNames.iterator(); iterator.hasNext();) {
            String nodeName = (String) iterator.next();
            if (nodeName == null) {
                iterator.remove();
                continue;
            }
            if (!getRouteNodeService().isNodeInPath(getRouteHeader(), nodeName)) {
                return nodeName;
            }
        }
        return "";
    }

    public void recordAction() throws InvalidActionTakenException, KEWUserNotFoundException {
        MDC.put("docId", getRouteHeader().getRouteHeaderId());
   //     checkLocking();
        updateSearchableAttributesIfPossible();

        List<ActionRequestValue> actionRequests = getActionRequestService().findAllValidRequests(getUser(), getRouteHeaderId(), KEWConstants.ACTION_REQUEST_COMPLETE_REQ);
        String errorMessage = validateActionRules(actionRequests);
        if (!Utilities.isEmpty(errorMessage)) {
            throw new InvalidActionTakenException(errorMessage);
        }

//        for (Iterator iterator = nodeNames.iterator(); iterator.hasNext();) {
//            String nodeName = (String) iterator.next();
//            if (nodeName == null) {
//                iterator.remove();
//                continue;
//            }
//            if (!getRouteNodeService().isNodeInPath(getRouteHeader(), nodeName)) {
//                throw new InvalidActionTakenException("Document already at or beyond route node " + nodeName);
//            }
//        }
        LOG.debug("Checking to see if the action is legal");

//        //find all current activated/initialized action requests
//        if (! getRouteHeader().getDocumentType().getBlanketApproveWorkgroup().hasMember(getUser())) {
//        	throw new InvalidActionTakenException("User is not authorized to BlanketApprove document");
//        } else if (getRouteHeader().isValidActionToTake(getActionTakenCode())) {

            LOG.debug("Blanket approving document : " + annotation);

            if (getRouteHeader().isStateInitiated() || getRouteHeader().isStateSaved()) {
                markDocumentEnroute(getRouteHeader());
            }

//            List actionRequests = getActionRequestService().findAllValidRequests(getUser(), getRouteHeaderId(), KEWConstants.ACTION_REQUEST_COMPLETE_REQ);
//            if (!isActionCompatibleRequest(actionRequests, getActionTakenCode())) {
//                throw new InvalidActionTakenException("No request for the user is compatible with the BlanketApprove Action");
//            }

            LOG.debug("Record the blanket approval action");
            Recipient delegator = findDelegatorForActionRequests(actionRequests);
            ActionTakenValue actionTaken = saveActionTaken(delegator);

            LOG.debug("Deactivate pending action requests for user");
            getActionRequestService().deactivateRequests(actionTaken, actionRequests);
            notifyActionTaken(actionTaken);

            KEWServiceLocator.getRouteHeaderService().saveRouteHeader(getRouteHeader());

//        } else {
//            LOG.warn("Document not in state to be approved.");
//            throw new InvalidActionTakenException("Document is not in a state to be approved");
//        }
            
          queueDeferredWork(actionTaken);
    }

    protected void queueDeferredWork(ActionTakenValue actionTaken) {
        try {

            BlanketApproveProcessorService blanketApprove = MessageServiceNames.getBlanketApproveProcessorService(routeHeader);
            blanketApprove.doBlanketApproveWork(routeHeader.getRouteHeaderId(), getUser(), actionTaken.getActionTakenId(), nodeNames);
//

//          KEWAsyncronousJavaService blanketApproveProcessor = (KEWAsyncronousJavaService)SpringServiceLocator.getMessageHelper().getServiceAsynchronously(
//                  MessageServiceNames.BLANKET_APPROVE_PROCESSING_SERVICE, routeHeader);
//          blanketApproveProcessor.invoke(BlanketApproveProcessor.getPayLoad(user, action.getActionTaken(), nodeNames, routeHeader));

//          SpringServiceLocator.getMessageHelper().sendMessage(MessageServiceNames.BLANKET_APPROVE_PROCESSING_SERVICE,
//                  BlanketApproveProcessor.getPayLoad(user, action.getActionTaken(), nodeNames, routeHeader), routeHeader);
        } catch (Exception e) {
            LOG.error(e);
            throw new WorkflowRuntimeException(e);
        }
//      SpringServiceLocator.getRouteQueueService().requeueDocument(routeHeader.getRouteHeaderId(), KEWConstants.ROUTE_QUEUE_BLANKET_APPROVE_PRIORITY, new Long(0),
//              BlanketApproveProcessor.class.getName(), BlanketApproveProcessor.getBlanketApproveProcessorValue(user, action.getActionTaken(), nodeNames));
    }
    
    public void performDeferredBlanketApproveWork(ActionTakenValue actionTaken) throws Exception {

        if (getRouteHeader().isInException()) {
            LOG.debug("Moving document back to Enroute from Exception");

            String oldStatus = getRouteHeader().getDocRouteStatus();
            getRouteHeader().markDocumentEnroute();

            String newStatus = getRouteHeader().getDocRouteStatus();
            notifyStatusChange(newStatus, oldStatus);
        }
        new BlanketApproveEngine(nodeNames, actionTaken).process(getRouteHeader().getRouteHeaderId(), null);
        
        queueDocumentProcessing();
   }

    protected void markDocumentEnroute(DocumentRouteHeaderValue routeHeader) throws InvalidActionTakenException {
        String oldStatus = getRouteHeader().getDocRouteStatus();
        getRouteHeader().markDocumentEnroute();

        String newStatus = getRouteHeader().getDocRouteStatus();
        notifyStatusChange(newStatus, oldStatus);
        KEWServiceLocator.getRouteHeaderService().saveRouteHeader(getRouteHeader());
    }

    private RouteNodeService getRouteNodeService() {
        return KEWServiceLocator.getRouteNodeService();
    }
}