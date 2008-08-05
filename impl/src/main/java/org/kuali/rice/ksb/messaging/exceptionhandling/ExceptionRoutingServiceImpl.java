/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.rice.ksb.messaging.exceptionhandling;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.MDC;
import org.kuali.rice.kew.DocumentRouteStatusChange;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionrequests.ActionRequestFactory;
import org.kuali.rice.kew.actionrequests.ActionRequestValue;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.exception.InvalidActionTakenException;
import org.kuali.rice.kew.exception.RouteManagerException;
import org.kuali.rice.kew.exception.WorkflowDocumentExceptionRoutingService;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.postprocessor.PostProcessor;
import org.kuali.rice.kew.postprocessor.ProcessDocReport;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.PerformanceLogger;
import org.kuali.rice.ksb.messaging.PersistedMessage;
import org.kuali.rice.ksb.services.KSBServiceLocator;
import org.kuali.rice.util.ExceptionUtils;


public class ExceptionRoutingServiceImpl implements WorkflowDocumentExceptionRoutingService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExceptionRoutingServiceImpl.class);

    public void placeInExceptionRouting(Throwable throwable, PersistedMessage persistedMessage, Long routeHeaderId) throws Exception {
        RouteNodeInstance nodeInstance = null;
        KEWServiceLocator.getRouteHeaderService().lockRouteHeader(routeHeaderId, true);
        DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(routeHeaderId);
        throwable = unwrapRouteManagerExceptionIfPossible(throwable);
        if (throwable instanceof RouteManagerException) {
            RouteManagerException rmException = (RouteManagerException) throwable;
            nodeInstance = rmException.getRouteContext().getNodeInstance();
        } else {
            List activeNodeInstances = KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(routeHeaderId);
            if (!activeNodeInstances.isEmpty()) {
                // take the first active nodeInstance found.
                nodeInstance = (RouteNodeInstance) activeNodeInstances.get(0);
            }
        }
        
        if (nodeInstance == null) {
            // get the initial node instance
            nodeInstance = (RouteNodeInstance) document.getInitialRouteNodeInstances().get(0);
        }

        MDC.put("docID", routeHeaderId);
        PerformanceLogger performanceLogger = new PerformanceLogger(routeHeaderId);
        try {

            // mark all active requests to initialized and delete the action items
            List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(routeHeaderId);
            for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
                ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
                if (actionRequest.isActive()) {
                    actionRequest.setStatus(KEWConstants.ACTION_REQUEST_INITIALIZED);
                    for (Iterator iterator = actionRequest.getActionItems().iterator(); iterator.hasNext();) {
                        KEWServiceLocator.getActionListService().deleteActionItem((ActionItem) iterator.next());
                    }
                    KEWServiceLocator.getActionRequestService().saveActionRequest(actionRequest);
                }
            }

            LOG.debug("Generating exception request for doc : " + routeHeaderId);
            Throwable cause = determineActualCause(throwable, 0);

            String message = (cause != null && cause.getMessage() != null) ? cause.getMessage() : "";
            if (message.length() > KEWConstants.MAX_ANNOTATION_LENGTH) {
                message = message.substring(0, KEWConstants.MAX_ANNOTATION_LENGTH);
            }
            ActionRequestFactory arFactory = new ActionRequestFactory(document, nodeInstance);
            ActionRequestValue exceptionRequest = arFactory.createActionRequest(KEWConstants.ACTION_REQUEST_COMPLETE_REQ, new Integer(0), nodeInstance.getRouteNode().getExceptionWorkgroup(), "Exception Workgroup for route node " + nodeInstance.getName(), KEWConstants.EXCEPTION_REQUEST_RESPONSIBILITY_ID, Boolean.TRUE, message);
            DocumentRouteHeaderValue rh = KEWServiceLocator.getRouteHeaderService().getRouteHeader(routeHeaderId);
            String oldStatus = rh.getDocRouteStatus();
            rh.setDocRouteStatus(KEWConstants.ROUTE_HEADER_EXCEPTION_CD);
            notifyStatusChange(rh, KEWConstants.ROUTE_HEADER_EXCEPTION_CD, oldStatus);
            KEWServiceLocator.getRouteHeaderService().saveRouteHeader(rh);
            KEWServiceLocator.getActionRequestService().activateRequest(exceptionRequest);
            KSBServiceLocator.getRouteQueueService().delete(persistedMessage);
        } finally {
            performanceLogger.log("Time to generate exception request.");
            MDC.remove("docID");
        }
    }

    protected void notifyStatusChange(DocumentRouteHeaderValue routeHeader, String newStatusCode, String oldStatusCode) throws InvalidActionTakenException {
        DocumentRouteStatusChange statusChangeEvent = new DocumentRouteStatusChange(routeHeader.getRouteHeaderId(), routeHeader.getAppDocId(), oldStatusCode, newStatusCode);
        try {
            LOG.debug("Notifying post processor of status change "+oldStatusCode+"->"+newStatusCode);
            PostProcessor postProcessor = routeHeader.getDocumentType().getPostProcessor();
            ProcessDocReport report = postProcessor.doRouteStatusChange(statusChangeEvent);
            if (!report.isSuccess()) {
                LOG.warn(report.getMessage(), report.getProcessException());
                throw new InvalidActionTakenException(report.getMessage());
            }
        } catch (Exception ex) {
            LOG.warn(ex, ex);
            throw new WorkflowRuntimeException(ex);
        }
    }

    private Throwable unwrapRouteManagerExceptionIfPossible(Throwable throwable) {
    	throwable = ExceptionUtils.unwrapActualCause(throwable);
    	if (throwable != null && (! (throwable instanceof RouteManagerException)) && throwable.getCause() instanceof RouteManagerException) {
    		throwable = throwable.getCause();
    	}
    	return throwable;
    }

    protected Throwable determineActualCause(Throwable throwable, int depth) {
    	if (depth >= 10) {
    		return throwable;
    	}
    	if ((throwable instanceof InvocationTargetException) || (throwable instanceof RouteManagerException)) {
    		if (throwable.getCause() != null) {
    			return determineActualCause(throwable.getCause(), ++depth);
    		}
    	}
    	return throwable;
    }
}