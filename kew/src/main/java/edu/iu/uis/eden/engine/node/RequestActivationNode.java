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
package edu.iu.uis.eden.engine.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.MDC;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.RouteHelper;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.ResourceUnavailableException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.util.PerformanceLogger;
import edu.iu.uis.eden.util.Utilities;

/**
 * A node which will activate any requests on it, returning true when there are no more requests 
 * which require activation.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RequestActivationNode implements SimpleNode {

    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());
    private static long generatedRequestPriority = 0;

    public SimpleResult process(RouteContext routeContext, RouteHelper routeHelper) throws Exception {
        DocumentRouteHeaderValue document = routeContext.getDocument();
        RouteNodeInstance nodeInstance = routeContext.getNodeInstance();
        if (routeContext.isSimulation()) {
            if (routeContext.getActivationContext().isActionsToPerform()) {
                activateRequests(routeContext, document, nodeInstance);
            }
            return new SimpleResult(true);
        } else if (!activateRequests(routeContext, document, nodeInstance) && shouldTransition(document, nodeInstance)) {
            return new SimpleResult(true);
        } else {
            return new SimpleResult(false);
        }            
    }
    
    public boolean shouldTransition(DocumentRouteHeaderValue document, RouteNodeInstance nodeInstance) {
        List requests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocIdAtRouteNode(document.getRouteHeaderId(), nodeInstance.getRouteNodeInstanceId());
        boolean shouldTransition = true;
        for (Iterator iterator = requests.iterator(); iterator.hasNext();) {
            ActionRequestValue request = (ActionRequestValue) iterator.next();
            if (request.isApproveOrCompleteRequest()) {
                shouldTransition = false;
                break;
            }
        }
        return shouldTransition;
    }

    /**
     * Activates the action requests that are pending at this routelevel of the document. The requests are processed by priority and then request ID. It is implicit in the access that the requests are activated according to the route level above all.
     * <p>
     * FYI and acknowledement requests do not cause the processing to stop. Only action requests for approval or completion cause the processing to stop and then only for route level with a serialized activation policy. Only requests at the current document's current route level are activated. Inactive requests at a lower level cause a routing exception.
     * <p>
     * Exception routing and adhoc routing are processed slightly differently.
     * 
     * @param rh
     *            RouteHeaderBean object for which requests are activated
     * @return True if the any approval actions were activated.
     * @throws ResourceUnavailableException
     * @throws WorkflowException
     */
    public boolean activateRequests(RouteContext context, DocumentRouteHeaderValue document, RouteNodeInstance nodeInstance) throws WorkflowException {
        MDC.put("docID", document.getRouteHeaderId());
        PerformanceLogger performanceLogger = new PerformanceLogger(document.getRouteHeaderId());
        List generatedActionItems = new ArrayList();
        List requests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocIdAtRouteNode(document.getRouteHeaderId(), nodeInstance.getRouteNodeInstanceId());
        if (context.isSimulation()) {
            requests.addAll(context.getEngineState().getGeneratedRequests());
        }
        Collections.sort(requests, new Utilities().new PrioritySorter());
        LOG.debug("Pending Root Requests " + requests.size());
        String activationType = nodeInstance.getRouteNode().getActivationType();
        boolean isParallel = EdenConstants.ROUTE_LEVEL_PARALLEL.equals(activationType);
        boolean activatedApproveRequest = false;
        for (Iterator iter = requests.iterator(); iter.hasNext();) {
            if (activatedApproveRequest && !isParallel) {
                break;
            }
            ActionRequestValue request = (ActionRequestValue) iter.next();
            if (request.getParentActionRequest() != null || request.getNodeInstance() == null) {
                // 1. disregard request if it's not a top-level request
                // 2. disregard request if it's a "future" request and hasn't been attached to a node instance yet
                continue; 
            }
            if (request.isActive()) {
                activatedApproveRequest = activatedApproveRequest || request.isApproveOrCompleteRequest();
                continue;
            }
            logProcessingMessage(request);   
            LOG.debug("Activating request.");
            activatedApproveRequest = activateRequest(context, request, nodeInstance, generatedActionItems) || activatedApproveRequest;
        }
        // now let's send notifications, since this code needs to be able to activate each request individually, we need
        // to collection all action items and then notify after all have been generated
        if (!context.isSimulation()) {
            KEWServiceLocator.getNotificationService().notify(generatedActionItems);
        }
        performanceLogger.log("Time to activate requests.");
        return activatedApproveRequest;
    }

    private boolean activateRequest(RouteContext context, ActionRequestValue actionRequest, RouteNodeInstance nodeInstance, List generatedActionItems) throws EdenUserNotFoundException {
        if (actionRequest.isRoleRequest()) {
            List actionRequests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocIdAtRouteNode(actionRequest.getRouteHeaderId(), nodeInstance.getRouteNodeInstanceId());
            for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
                ActionRequestValue siblingRequest = (ActionRequestValue) iterator.next();
                if (actionRequest.getRoleName().equals(siblingRequest.getRoleName())) {
                    generatedActionItems.addAll(KEWServiceLocator.getActionRequestService().activateRequestNoNotification(siblingRequest, context.getActivationContext()));
                }
            }
        }
        generatedActionItems.addAll(KEWServiceLocator.getActionRequestService().activateRequestNoNotification(actionRequest, context.getActivationContext()));
        return actionRequest.isApproveOrCompleteRequest() && ! actionRequest.isDone();
    }
    
    protected void saveActionRequest(RouteContext context, ActionRequestValue actionRequest) throws EdenUserNotFoundException {
        if (!context.isSimulation()) {
            KEWServiceLocator.getActionRequestService().saveActionRequest(actionRequest);
        } else {
            actionRequest.setActionRequestId(new Long(generatedRequestPriority++));
            context.getEngineState().getGeneratedRequests().add(actionRequest);    
        }
        
    }
    
    protected void saveDocument(RouteContext context, DocumentRouteHeaderValue document) {
        if (!context.isSimulation()) {
            KEWServiceLocator.getRouteHeaderService().saveRouteHeader(document);
        }
    }

    private void logProcessingMessage(ActionRequestValue request) {
        if (LOG.isDebugEnabled()) {
        	RouteNodeInstance nodeInstance = request.getNodeInstance();
            StringBuffer buffer = new StringBuffer();
            buffer.append("Processing AR: ").append(request.getActionRequestId()).append("\n");
            buffer.append("AR Node Name: ").append(nodeInstance != null ? nodeInstance.getName() : "null").append("\n");
            buffer.append("AR RouteLevel: ").append(request.getRouteLevel()).append("\n");
            buffer.append("AR Request Code: ").append(request.getActionRequested()).append("\n");
            buffer.append("AR Request priority: ").append(request.getPriority()).append("\n");
            LOG.debug(buffer);
        }
    }
            
}
