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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.log4j.MDC;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.engine.BlanketApproveEngine;
import edu.iu.uis.eden.engine.CompatUtils;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.RouteNodeService;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.messaging.KEWXMLService;
import edu.iu.uis.eden.messaging.MessageServiceNames;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;

/**
 * Does the sync work for blanket approves requested by client apps.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BlanketApproveAction extends ActionTakenEvent {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BlanketApproveAction.class);
    private Set nodeNames;

    public BlanketApproveAction(DocumentRouteHeaderValue rh, WorkflowUser user) {
        super(rh, user);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD);
    }

    public BlanketApproveAction(DocumentRouteHeaderValue rh, WorkflowUser user, String annotation, Integer routeLevel) {
        this(rh, user, annotation, convertRouteLevel(rh.getDocumentType(), routeLevel));
    }

    public BlanketApproveAction(DocumentRouteHeaderValue rh, WorkflowUser user, String annotation, String nodeName) {
        this(rh, user, annotation, Utilities.asSet(nodeName));
    }

    public BlanketApproveAction(DocumentRouteHeaderValue rh, WorkflowUser user, String annotation, Set nodeNames) {
        super(rh, user, annotation);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD);
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
     * @see edu.iu.uis.eden.actions.ActionTakenEvent#validateActionRules()
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

    public void recordAction() throws InvalidActionTakenException, EdenUserNotFoundException {
        MDC.put("docId", getRouteHeader().getRouteHeaderId());
        checkLocking();
        updateSearchableAttributesIfPossible();

        List actionRequests = getActionRequestService().findAllValidRequests(getUser(), getRouteHeaderId(), EdenConstants.ACTION_REQUEST_COMPLETE_REQ);
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

//            List actionRequests = getActionRequestService().findAllValidRequests(getUser(), getRouteHeaderId(), EdenConstants.ACTION_REQUEST_COMPLETE_REQ);
//            if (!isActionCompatibleRequest(actionRequests, getActionTakenCode())) {
//                throw new InvalidActionTakenException("No request for the user is compatible with the BlanketApprove Action");
//            }

            LOG.debug("Record the blanket approval action");
            Recipient delegator = findDelegatorForActionRequests(actionRequests);
            saveActionTaken(delegator);

            LOG.debug("Deactivate pending action requests for user");
            getActionRequestService().deactivateRequests(actionTaken, actionRequests);
            notifyActionTaken(this.actionTaken);

            getRouteHeaderService().saveRouteHeader(getRouteHeader());

//        } else {
//            LOG.warn("Document not in state to be approved.");
//            throw new InvalidActionTakenException("Document is not in a state to be approved");
//        }
    }

    public void doBlanketApproveWork() throws Exception {

        if (getRouteHeader().isInException()) {
            LOG.debug("Moving document back to Enroute from Exception");

            String oldStatus = getRouteHeader().getDocRouteStatus();
            getRouteHeader().markDocumentEnroute();

            String newStatus = getRouteHeader().getDocRouteStatus();
            notifyStatusChange(newStatus, oldStatus);
        }
        new BlanketApproveEngine(nodeNames, actionTaken).process(getRouteHeader().getRouteHeaderId(), null);
   }

    protected void markDocumentEnroute(DocumentRouteHeaderValue routeHeader) throws InvalidActionTakenException {
        String oldStatus = getRouteHeader().getDocRouteStatus();
        getRouteHeader().markDocumentEnroute();

        String newStatus = getRouteHeader().getDocRouteStatus();
        notifyStatusChange(newStatus, oldStatus);
        getRouteHeaderService().saveRouteHeader(getRouteHeader());
    }

    public void queueDocument() {
    	QName documentServiceName = new QName(getRouteHeader().getDocumentType().getMessageEntity(), MessageServiceNames.DOCUMENT_ROUTING_SERVICE);
    	KEWXMLService documentRoutingService = (KEWXMLService)MessageServiceNames.getServiceAsynchronously(documentServiceName, getRouteHeader());
    	try {
			documentRoutingService.invoke(String.valueOf(getRouteHeaderId()));
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}

    }

    public void setActionTaken(ActionTakenValue actionTaken) {
    	this.actionTaken = actionTaken;
    }

    private RouteNodeService getRouteNodeService() {
        return KEWServiceLocator.getRouteNodeService();
    }
}