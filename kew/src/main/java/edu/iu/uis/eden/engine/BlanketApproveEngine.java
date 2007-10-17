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
package edu.iu.uis.eden.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.MDC;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestFactory;
import edu.iu.uis.eden.actionrequests.ActionRequestService;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.actions.NotificationContext;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.engine.node.Process;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.util.Utilities;

/**
 * A WorkflowEngine implementation which orchestrates the document through the blanket approval process.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BlanketApproveEngine extends StandardWorkflowEngine {

    // private Set nodeNames;
    // private ActionTakenValue actionTaken;
//    private ActionRequestNotificationGenerator notifier = new ActionRequestNotificationGenerator();
    private OrchestrationConfig config;

    public BlanketApproveEngine(OrchestrationConfig config) {
        this.config = config;
    }

    public BlanketApproveEngine(String nodeName, ActionTakenValue actionTaken) {
        this(wrapInSet(nodeName), actionTaken);
    }

    public BlanketApproveEngine(Set nodeNames, ActionTakenValue actionTaken) {
        this(createBlanketApproveConfig(nodeNames, actionTaken));
    }

    private static Set wrapInSet(String nodeName) {
        Set nodeNames = new HashSet();
        if (!Utilities.isEmpty(nodeName)) {
            nodeNames.add(nodeName);
        }
        return nodeNames;
    }

    private static OrchestrationConfig createBlanketApproveConfig(Set nodeNames, ActionTakenValue actionTaken) {
        OrchestrationConfig config = new OrchestrationConfig();
        config.setCause(actionTaken);
        config.setDestinationNodeNames(nodeNames);
        config.setNotificationType(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ);
        config.setSendNotifications(true);
        return config;
    }

    /**
     * Orchestrates the document through the blanket approval process. The termination of the process is keyed off of the Set of node names. If there are no node names, then the document will be blanket approved past the terminal node(s) in the document.
     */
    public void process(Long documentId, Long nodeInstanceId) throws Exception {
        if (documentId == null) {
            throw new IllegalArgumentException("Cannot process a null document id.");
        }
        MDC.put("docID", documentId);
        RouteContext context = RouteContext.getCurrentRouteContext();
        try {
            KEWServiceLocator.getRouteHeaderService().lockRouteHeader(documentId, true);
            LOG.info("Processing document for Blanket Approval: " + documentId + " : " + nodeInstanceId);
            DocumentRouteHeaderValue document = getRouteHeaderService().getRouteHeader(documentId);
            if (!document.isRoutable()) {
                LOG.debug("Document not routable so returning with doing no action");
                return;
            }
            List activeNodeInstances = new ArrayList();
            if (nodeInstanceId == null) {
                activeNodeInstances.addAll(getRouteNodeService().getActiveNodeInstances(documentId));
            } else {
                RouteNodeInstance instanceNode = getRouteNodeService().findRouteNodeInstanceById(nodeInstanceId);
                if (instanceNode == null) {
                    throw new IllegalArgumentException("Invalid node instance id: " + nodeInstanceId);
                }
                activeNodeInstances.add(instanceNode);
            }
            List nodeInstancesToProcess = determineNodeInstancesToProcess(activeNodeInstances, config.getDestinationNodeNames());


            context.setDoNotSendApproveNotificationEmails(true);
            context.setDocument(document);
            context.setEngineState(new EngineState());
            NotificationContext notifyContext = null;
            if (config.isSendNotifications()) {
                notifyContext = new NotificationContext(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, config.getCause().getWorkflowUser(), config.getCause().getActionTaken());
            }
            try {
                List processingQueue = new LinkedList();
                for (Iterator iterator = nodeInstancesToProcess.iterator(); iterator.hasNext();) {
                    processingQueue.add(new ProcessEntry((RouteNodeInstance) iterator.next()));
                }
                Set nodesCompleted = new HashSet();
                // check the processingQueue for cases where there are no dest. nodes otherwise check if we've reached
                // the dest. nodes
                while (!processingQueue.isEmpty() && !isReachedDestinationNodes(config.getDestinationNodeNames(), nodesCompleted)) {
                    ProcessEntry entry = (ProcessEntry) processingQueue.remove(0);
                    // TODO document magical join node workage (ask Eric)
                    // TODO this has been set arbitrarily high because the implemented processing model here will probably not work for
                    // large parallel object graphs. This needs to be re-evaluated, see KULWF-459.
                    if (entry.getTimesProcessed() > 20) {
                        throw new WorkflowException("Could not process document through to blanket approval." + "  Document failed to progress past node " + entry.getNodeInstance().getRouteNode().getRouteNodeName());
                    }
                    RouteNodeInstance nodeInstance = entry.getNodeInstance();
                    context.setNodeInstance(nodeInstance);
                    if (config.getDestinationNodeNames().contains(nodeInstance.getName())) {
                        nodesCompleted.add(nodeInstance.getName());
                        continue;
                    }
                    ProcessContext resultProcessContext = processNodeInstance(context, helper);
                    invokeBlanketApproval(config.getCause(), nodeInstance, notifyContext);
                    if (!resultProcessContext.getNextNodeInstances().isEmpty() || resultProcessContext.isComplete()) {
                        for (Iterator nodeIt = resultProcessContext.getNextNodeInstances().iterator(); nodeIt.hasNext();) {
                            addToProcessingQueue(processingQueue, (RouteNodeInstance) nodeIt.next());
                        }
                    } else {
                        entry.increment();
                        processingQueue.add(processingQueue.size(), entry);
                    }
                }
                //clear the context so the standard engine can begin routing normally
                RouteContext.clearCurrentRouteContext();
                // continue with normal routing after blanket approve brings us to the correct place
                // if there is an active approve request this is no-op.
                super.process(documentId, null);
            } catch (Exception e) {
            	if (e instanceof RuntimeException) {
        		throw (RuntimeException)e;
        	} else {
        		throw new WorkflowRuntimeException(e.toString(), e);
        	}
            }
        } finally {
        	RouteContext.clearCurrentRouteContext();
            MDC.remove("docID");
        }
    }

    /**
     * @return true if all destination node are active but not yet complete - ready for the standard engine to take over the activation process for requests
     */
    private boolean isReachedDestinationNodes(Set destinationNodesNames, Set nodeNamesCompleted) {
        return !destinationNodesNames.isEmpty() && nodeNamesCompleted.equals(destinationNodesNames);
    }

    private void addToProcessingQueue(List processingQueue, RouteNodeInstance nodeInstance) {
        // first, detect if it's already there
        for (Iterator iterator = processingQueue.iterator(); iterator.hasNext();) {
            ProcessEntry entry = (ProcessEntry) iterator.next();
            if (entry.getNodeInstance().getRouteNodeInstanceId().equals(nodeInstance.getRouteNodeInstanceId())) {
                entry.setNodeInstance(nodeInstance);
                return;
            }
        }
        processingQueue.add(processingQueue.size(), new ProcessEntry(nodeInstance));
    }

    /**
     * If there are multiple paths, we need to figure out which ones we need to follow for blanket approval. This method will throw an exception if a node with the given name could not be located in the routing path. This method is written in such a way that it should be impossible for there to be an infinite loop, even if there is extensive looping in the node graph.
     */
    private List determineNodeInstancesToProcess(List activeNodeInstances, Set nodeNames) throws Exception {
        if (nodeNames.isEmpty()) {
            return activeNodeInstances;
        }
        List nodeInstancesToProcess = new ArrayList();
        for (Iterator iterator = activeNodeInstances.iterator(); iterator.hasNext();) {
            RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
            if (isNodeNameInPath(nodeNames, nodeInstance)) {
                nodeInstancesToProcess.add(nodeInstance);
            }
        }
        if (nodeInstancesToProcess.size() == 0) {
            throw new InvalidActionTakenException("Could not locate nodes with the given names in the blanket approval path '" + printNodeNames(nodeNames) + "'.  " + "The document is probably already passed the specified nodes or does not contain the nodes.");
        }
        return nodeInstancesToProcess;
    }

    private boolean isNodeNameInPath(Set nodeNames, RouteNodeInstance nodeInstance) throws Exception {
        boolean isInPath = false;
        for (Iterator nodeNameIt = nodeNames.iterator(); nodeNameIt.hasNext();) {
            String nodeName = (String) nodeNameIt.next();
            for (Iterator iterator = nodeInstance.getRouteNode().getNextNodes().iterator(); iterator.hasNext();) {
                RouteNode nextNode = (RouteNode) iterator.next();
                isInPath = isInPath || isNodeNameInPath(nodeName, nextNode, new HashSet());
            }
        }
        return isInPath;
    }

    private boolean isNodeNameInPath(String nodeName, RouteNode node, Set inspected) throws Exception {
        boolean isInPath = !inspected.contains(node.getRouteNodeId()) && node.getRouteNodeName().equals(nodeName);
        inspected.add(node.getRouteNodeId());
        if (helper.isSubProcessNode(node)) {
            Process subProcess = node.getDocumentType().getNamedProcess(node.getRouteNodeName());
            RouteNode subNode = subProcess.getInitialRouteNode();
            isInPath = isInPath || isNodeNameInPath(nodeName, subNode, inspected);
        }
        for (Iterator iterator = node.getNextNodes().iterator(); iterator.hasNext();) {
            RouteNode nextNode = (RouteNode) iterator.next();
            isInPath = isInPath || isNodeNameInPath(nodeName, nextNode, inspected);
        }
        return isInPath;
    }

    private String printNodeNames(Set nodesNames) {
        StringBuffer buffer = new StringBuffer();
        for (Iterator iterator = nodesNames.iterator(); iterator.hasNext();) {
            String nodeName = (String) iterator.next();
            buffer.append(nodeName);
            buffer.append((iterator.hasNext() ? ", " : ""));
        }
        return buffer.toString();
    }

    /**
     * Invokes the blanket approval for the given node instance. This deactivates all pending approve or complete requests at the node and sends out notifications to the individuals who's requests were trumped by the blanket approve.
     */
    private void invokeBlanketApproval(ActionTakenValue actionTaken, RouteNodeInstance nodeInstance, NotificationContext notifyContext) throws EdenUserNotFoundException {
        List actionRequests = getActionRequestService().findPendingRootRequestsByDocIdAtRouteNode(nodeInstance.getDocumentId(), nodeInstance.getRouteNodeInstanceId());
        actionRequests = getActionRequestService().getRootRequests(actionRequests);
        List requestsToNotify = new ArrayList();
        for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue request = (ActionRequestValue) iterator.next();
            if (request.isApproveOrCompleteRequest()) {
                getActionRequestService().deactivateRequest(actionTaken, request);
                requestsToNotify.add(request);
            }
        }
        if (notifyContext != null) {
        	ActionRequestFactory arFactory = new ActionRequestFactory(RouteContext.getCurrentRouteContext().getDocument(), nodeInstance);
        	List notificationRequests = arFactory.generateNotifications(requestsToNotify, notifyContext.getUserTakingAction(), actionTaken.getDelegatorUser(), notifyContext.getNotificationRequestCode(), notifyContext.getActionTakenCode());
        	getActionRequestService().activateRequests(notificationRequests);
        }
    }

    private ActionRequestService getActionRequestService() {
        return KEWServiceLocator.getActionRequestService();
    }

    private class ProcessEntry {

        private RouteNodeInstance nodeInstance;
        private int timesProcessed = 0;

        public ProcessEntry(RouteNodeInstance nodeInstance) {
            this.nodeInstance = nodeInstance;
        }

        public RouteNodeInstance getNodeInstance() {
            return nodeInstance;
        }

        public void setNodeInstance(RouteNodeInstance nodeInstance) {
            this.nodeInstance = nodeInstance;
        }

        public void increment() {
            timesProcessed++;
        }

        public int getTimesProcessed() {
            return timesProcessed;
        }

    }

}
