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
package edu.iu.uis.eden.messaging.exceptionhandling;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.MDC;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.util.ExceptionUtils;

import edu.iu.uis.eden.DocumentRouteStatusChange;
import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionrequests.ActionRequestFactory;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.RouteManagerException;
import edu.iu.uis.eden.exception.WorkflowDocumentExceptionRoutingService;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.messaging.PersistedMessage;
import edu.iu.uis.eden.postprocessor.PostProcessor;
import edu.iu.uis.eden.postprocessor.ProcessDocReport;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.util.PerformanceLogger;

public class ExceptionRoutingServiceImpl implements WorkflowDocumentExceptionRoutingService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExceptionRoutingServiceImpl.class);

    public void placeInExceptionRouting(Throwable throwable, PersistedMessage persistedMessage, Long routeHeaderId) {
        RouteNodeInstance nodeInstance = null;
        KEWServiceLocator.getRouteHeaderService().lockRouteHeader(routeHeaderId, true);
        DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(routeHeaderId);
        throwable = unwrapRouteManagerExceptionIfPossible(throwable);
        if (throwable instanceof RouteManagerException) {
            RouteManagerException rmException = (RouteManagerException) throwable;
            nodeInstance = rmException.getRouteContext().getNodeInstance();
        } else {
            List activeNodeInstances = KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(routeHeaderId);
            if (activeNodeInstances.isEmpty()) {
                // get the initial node instance
                nodeInstance = (RouteNodeInstance) document.getInitialRouteNodeInstances().get(0);
            } else {
                // take the first active nodeInstance found.
                nodeInstance = (RouteNodeInstance) activeNodeInstances.get(0);
            }
        }

        MDC.put("docID", routeHeaderId);
        PerformanceLogger performanceLogger = new PerformanceLogger(routeHeaderId);
        try {

            // mark all active requests to initialized and delete the action items
            List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(routeHeaderId);
            for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
                ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
                if (actionRequest.isActive()) {
                    actionRequest.setStatus(EdenConstants.ACTION_REQUEST_INITIALIZED);
                    for (Iterator iterator = actionRequest.getActionItems().iterator(); iterator.hasNext();) {
                        KEWServiceLocator.getActionListService().deleteActionItem((ActionItem) iterator.next());
                    }
                    KEWServiceLocator.getActionRequestService().saveActionRequest(actionRequest);
                }
            }

            LOG.debug("Generating exception request for doc : " + routeHeaderId);
            Throwable cause = determineActualCause(throwable, 0);

            String message = (cause != null && cause.getMessage() != null) ? cause.getMessage() : "";
            if (message.length() > EdenConstants.MAX_ANNOTATION_LENGTH) {
                message = message.substring(0, EdenConstants.MAX_ANNOTATION_LENGTH);
            }
            ActionRequestFactory arFactory = new ActionRequestFactory(document, nodeInstance);
            ActionRequestValue exceptionRequest = arFactory.createActionRequest(EdenConstants.ACTION_REQUEST_COMPLETE_REQ, new Integer(0), nodeInstance.getRouteNode().getExceptionWorkgroup(), "Exception Workgroup for route node " + nodeInstance.getName(), EdenConstants.EXCEPTION_REQUEST_RESPONSIBILITY_ID, Boolean.TRUE, message);
            DocumentRouteHeaderValue rh = KEWServiceLocator.getRouteHeaderService().getRouteHeader(routeHeaderId);
            String oldStatus = rh.getDocRouteStatus();
            rh.setDocRouteStatus(EdenConstants.ROUTE_HEADER_EXCEPTION_CD);
            notifyStatusChange(rh, EdenConstants.ROUTE_HEADER_EXCEPTION_CD, oldStatus);
            KEWServiceLocator.getRouteHeaderService().saveRouteHeader(rh);
            KEWServiceLocator.getActionRequestService().activateRequest(exceptionRequest);
            KSBServiceLocator.getRouteQueueService().delete(persistedMessage);
        } catch (Exception e) {
            throw new WorkflowRuntimeException("Caught exception generating exception request", e);
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