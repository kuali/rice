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
import edu.iu.uis.eden.actions.asyncservices.MoveDocumentService;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.engine.BlanketApproveEngine;
import edu.iu.uis.eden.engine.OrchestrationConfig;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.messaging.MessageServiceNames;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;

/**
 * Returns a document to a previous node in the route.
 *
 * Current implementation only supports returning to a node on the main branch of the
 * document.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class MoveDocumentAction extends ActionTakenEvent {

    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());

    private MovePoint movePoint;

    public MoveDocumentAction(DocumentRouteHeaderValue routeHeader, WorkflowUser user) {
        super(routeHeader, user);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_MOVE_CD);
    }

    public MoveDocumentAction(DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation, MovePoint movePoint) {
        super(routeHeader, user, annotation);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_MOVE_CD);
        this.movePoint = movePoint;
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.actions.ActionTakenEvent#isActionCompatibleRequest(java.util.List)
     */
    @Override
    public String validateActionRules() throws EdenUserNotFoundException {
        return validateActionRules(getActionRequestService().findAllValidRequests(getUser(), routeHeader.getRouteHeaderId(),
                EdenConstants.ACTION_REQUEST_COMPLETE_REQ), KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(getRouteHeader().getRouteHeaderId()));
    }

    private String validateActionRules(List actionRequests, Collection activeNodes) throws EdenUserNotFoundException {
        String superError = super.validateActionTakenRules();
        if (!Utilities.isEmpty(superError)) {
            return superError;
        }
        if (!getRouteHeader().isValidActionToTake(getActionPerformedCode())) {
            return "Document is not in a state to be moved";
        }
        if (activeNodes.isEmpty()) {
            return "Document has no active nodes.";
        }
        if (!isActionCompatibleRequest(actionRequests)) {
            return "No request for the user is compatible with the MOVE action";
        }
        return "";
    }


    /* (non-Javadoc)
     * @see edu.iu.uis.eden.actions.ActionTakenEvent#isActionCompatibleRequest(java.util.List)
     */
    public boolean isActionCompatibleRequest(List requests) throws EdenUserNotFoundException {
        //Move is always correct because the client application has authorized it
        return true;
    }

    public void recordAction() throws InvalidActionTakenException, EdenUserNotFoundException {
        MDC.put("docId", getRouteHeader().getRouteHeaderId());
        checkLocking();
        updateSearchableAttributesIfPossible();
        LOG.debug("Moving document " + getRouteHeader().getRouteHeaderId() + " to point: " + displayMovePoint(movePoint) + ", annotation: " + annotation);

        List actionRequests = getActionRequestService().findAllValidRequests(getUser(), getRouteHeaderId(), EdenConstants.ACTION_REQUEST_COMPLETE_REQ);
        Collection activeNodes = KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(getRouteHeader().getRouteHeaderId());
        String errorMessage = validateActionRules(actionRequests,activeNodes);
        if (!Utilities.isEmpty(errorMessage)) {
            throw new InvalidActionTakenException(errorMessage);
        }

//        if (getRouteHeader().isValidActionToTake(getActionTakenCode())) {


            RouteNodeInstance startNodeInstance = determineStartNode(activeNodes, movePoint);

//            List actionRequests = getActionRequestService().findAllValidRequests(getUser(), getRouteHeaderId(), EdenConstants.ACTION_REQUEST_COMPLETE_REQ);
//            if (! isActionCompatibleRequest(actionRequests, getActionTakenCode())) {
//                throw new InvalidActionTakenException("No request for the user is compatible with the MOVE action");
//            }
            LOG.debug("Record the move action");
            Recipient delegator = findDelegatorForActionRequests(actionRequests);
            saveActionTaken(delegator);
            getActionRequestService().deactivateRequests(actionTaken, actionRequests);
            notifyActionTaken(this.actionTaken);

            // TODO this whole bit is a bit hacky at the moment
            if (movePoint.getStepsToMove() > 0) {
                Set<String> targetNodeNames = new HashSet<String>();
                targetNodeNames.add(determineFutureNodeName(startNodeInstance, movePoint));

                MoveDocumentService moveDocumentProcessor = MessageServiceNames.getMoveDocumentProcessorService(getRouteHeader());
                moveDocumentProcessor.moveDocument(getUser(), getRouteHeader(), getActionTaken(), targetNodeNames);

//                SpringServiceLocator.getRouteQueueService().requeueDocument(routeHeader.getRouteHeaderId(),
//                		EdenConstants.ROUTE_QUEUE_BLANKET_APPROVE_PRIORITY, new Long(0), MoveDocumentProcessor.class.getName(),
//                		MoveDocumentProcessor.getMoveDocumentProcessorValue(getUser(), getActionTaken(), targetNodeNames));
                //BlanketApproveAction blanketAction = new BlanketApproveAction(getRouteHeader(), getUser(), annotation, targetNodeName);
                //blanketAction.actionTaken = actionTaken;
                //blanketAction.recordAction();
            } else {
                String targetNodeName = determineReturnNodeName(startNodeInstance, movePoint);
                ReturnToPreviousNodeAction returnAction = new ReturnToPreviousNodeAction(getRouteHeader(), getUser(), annotation, targetNodeName, false);
                returnAction.actionTaken = actionTaken;
                returnAction.setActionTakenCode(EdenConstants.ACTION_TAKEN_MOVE_CD);
                returnAction.recordAction();
            }
//        }
    }

    public void doMoveDocumentWork(Set nodeNames) throws Exception {

        if (getRouteHeader().isInException()) {
            LOG.debug("Moving document back to Enroute from Exception");

            String oldStatus = getRouteHeader().getDocRouteStatus();
            getRouteHeader().markDocumentEnroute();

            String newStatus = getRouteHeader().getDocRouteStatus();
            notifyStatusChange(newStatus, oldStatus);
        }
        OrchestrationConfig config = new OrchestrationConfig();
        config.setCause(actionTaken);
        config.setDestinationNodeNames(nodeNames);
        config.setSendNotifications(false);
        new BlanketApproveEngine(config).process(getRouteHeader().getRouteHeaderId(), null);
    }

    public void setActionTaken(ActionTakenValue actionTaken) {
    	this.actionTaken = actionTaken;
    }

    private RouteNodeInstance determineStartNode(Collection activeNodes, MovePoint movePoint) throws InvalidActionTakenException {
        RouteNodeInstance startNodeInstance = null;
        for (Iterator iterator = activeNodes.iterator(); iterator.hasNext();) {
            RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
            if (nodeInstance.getName().equals(movePoint.getStartNodeName())) {
                if (startNodeInstance != null) {
                    throw new InvalidActionTakenException("More than one active node exists with the given name:  " + movePoint.getStartNodeName());
                }
                startNodeInstance = nodeInstance;
            }
        }
        if (startNodeInstance == null) {
            throw new InvalidActionTakenException("Could not locate an active node with the given name: " + movePoint.getStartNodeName());
        }
        return startNodeInstance;
    }

    private String determineFutureNodeName(RouteNodeInstance startNodeInstance, MovePoint movePoint) throws InvalidActionTakenException {
        return determineFutureNodeName(startNodeInstance.getRouteNode(), movePoint, 0, new HashSet());
    }

    private String determineFutureNodeName(RouteNode node, MovePoint movePoint, int currentStep, Set nodesProcessed) throws InvalidActionTakenException {
        if (nodesProcessed.contains(node.getRouteNodeId())) {
            throw new InvalidActionTakenException("Detected a cycle at node " + node.getRouteNodeName() + " when attempting to move document.");
        }
        nodesProcessed.add(node.getRouteNodeId());
        if (currentStep == movePoint.getStepsToMove()) {
            return node.getRouteNodeName();
        }
        List nextNodes = node.getNextNodes();
        if (nextNodes.size() == 0) {
            throw new InvalidActionTakenException("Could not proceed forward, there are no more nodes in the route.  Halted on step " + currentStep);
        }
        if (nextNodes.size() != 1) {
            throw new InvalidActionTakenException("Cannot move forward in a multi-branch path.  Located "+nextNodes.size()+" branches.  Halted on step " + currentStep);
        }
        return determineFutureNodeName((RouteNode)nextNodes.get(0), movePoint, currentStep+1, nodesProcessed);
    }

    private String determineReturnNodeName(RouteNodeInstance startNodeInstance, MovePoint movePoint) throws InvalidActionTakenException {
        return determineReturnNodeName(startNodeInstance.getRouteNode(), movePoint, 0);
    }

    private String determineReturnNodeName(RouteNode node, MovePoint movePoint, int currentStep) throws InvalidActionTakenException {
        if (currentStep == movePoint.getStepsToMove()) {
            return node.getRouteNodeName();
        }
        List previousNodes = node.getPreviousNodes();
        if (previousNodes.size() == 0) {
            throw new InvalidActionTakenException("Could not locate the named target node in the document's past route.  Halted on step " + currentStep);
        }
        if (previousNodes.size() != 1) {
            throw new InvalidActionTakenException("Located a multi-branch path, could not proceed backward past this point.  Halted on step " + currentStep);
        }
        return determineReturnNodeName((RouteNode)previousNodes.get(0), movePoint, currentStep-1);
    }

    private String displayMovePoint(MovePoint movePoint) {
        return "fromNode="+movePoint.getStartNodeName()+", stepsToMove="+movePoint.getStepsToMove();
    }

}