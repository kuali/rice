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
package org.kuali.rice.kew.messaging.exceptionhandling;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.MDC;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.core.util.ExceptionUtils;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionrequest.ActionRequestFactory;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.KimGroupRecipient;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.exception.InvalidActionTakenException;
import org.kuali.rice.kew.exception.RouteManagerException;
import org.kuali.rice.kew.exception.WorkflowDocumentExceptionRoutingService;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kew.postprocessor.PostProcessor;
import org.kuali.rice.kew.postprocessor.ProcessDocReport;
import org.kuali.rice.kew.role.RoleRouteModule;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.PerformanceLogger;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.ksb.messaging.PersistedMessage;
import org.kuali.rice.ksb.service.KSBServiceLocator;


public class ExceptionRoutingServiceImpl implements WorkflowDocumentExceptionRoutingService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExceptionRoutingServiceImpl.class);

    public void placeInExceptionRouting(Throwable throwable, PersistedMessage persistedMessage, Long routeHeaderId) throws Exception {
        KEWServiceLocator.getRouteHeaderService().lockRouteHeader(routeHeaderId, true);
        DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(routeHeaderId);
        throwable = unwrapRouteManagerExceptionIfPossible(throwable);
        RouteContext routeContext = establishRouteContext(document, throwable);
        RouteNodeInstance nodeInstance = routeContext.getNodeInstance();
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
            List<ActionRequestValue> exceptionRequests = new ArrayList<ActionRequestValue>();
            if (nodeInstance.getRouteNode().isExceptionGroupDefined()) {
            	exceptionRequests = generateExceptionGroupRequests(routeContext);
            } else {
            	exceptionRequests = generateKimExceptionRequests(routeContext);
            }
            if (exceptionRequests.isEmpty()) {
            	throw new RiceRuntimeException("Failed to generate exception requests for exception routing!");
            }
            activateExceptionRequests(routeContext, exceptionRequests, message);
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
    
    protected List<ActionRequestValue> generateExceptionGroupRequests(RouteContext routeContext) {
    	RouteNodeInstance nodeInstance = routeContext.getNodeInstance();
    	ActionRequestFactory arFactory = new ActionRequestFactory(routeContext.getDocument(), nodeInstance);
    	ActionRequestValue exceptionRequest = arFactory.createActionRequest(KEWConstants.ACTION_REQUEST_COMPLETE_REQ, new Integer(0), new KimGroupRecipient(nodeInstance.getRouteNode().getExceptionWorkgroup()), "Exception Workgroup for route node " + nodeInstance.getName(), KEWConstants.EXCEPTION_REQUEST_RESPONSIBILITY_ID, Boolean.TRUE, "");
    	return Collections.singletonList(exceptionRequest);
    }
    
    protected List<ActionRequestValue> generateKimExceptionRequests(RouteContext routeContext) throws Exception {
    	RoleRouteModule roleRouteModule = new RoleRouteModule();
    	roleRouteModule.setNamespace(KNSConstants.KUALI_RICE_SYSTEM_NAMESPACE);
    	roleRouteModule.setResponsibilityTemplateName(KEWConstants.EXCEPTION_ROUTING_RESPONSIBILITY_TEMPLATE_NAME);
    	return roleRouteModule.findActionRequests(routeContext);
    }
    
    protected void activateExceptionRequests(RouteContext routeContext, List<ActionRequestValue> exceptionRequests, String exceptionMessage) throws Exception {
    	setExceptionAnnotations(exceptionRequests, exceptionMessage);
    	// TODO is there a reason we reload the document here?
    	DocumentRouteHeaderValue rh = KEWServiceLocator.getRouteHeaderService().getRouteHeader(routeContext.getDocument().getRouteHeaderId());
    	String oldStatus = rh.getDocRouteStatus();
    	rh.setDocRouteStatus(KEWConstants.ROUTE_HEADER_EXCEPTION_CD);
    	notifyStatusChange(rh, KEWConstants.ROUTE_HEADER_EXCEPTION_CD, oldStatus);
    	KEWServiceLocator.getRouteHeaderService().saveRouteHeader(rh);
    	KEWServiceLocator.getActionRequestService().activateRequests(exceptionRequests);
    }
    
    /**
     * Sets the exception message as the annotation on the top-level Action Requests
     */
    protected void setExceptionAnnotations(List<ActionRequestValue> actionRequests, String exceptionMessage) {
    	for (ActionRequestValue actionRequest : actionRequests) {
    		actionRequest.setAnnotation(exceptionMessage);
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
    
    protected RouteContext establishRouteContext(DocumentRouteHeaderValue document, Throwable throwable) {
    	RouteContext routeContext = new RouteContext();
        if (throwable instanceof RouteManagerException) {
            RouteManagerException rmException = (RouteManagerException) throwable;
            routeContext = rmException.getRouteContext();
        } else {
        	routeContext.setDocument(document);
            List activeNodeInstances = KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(document.getRouteHeaderId());
            if (!activeNodeInstances.isEmpty()) {
                // take the first active nodeInstance found.
                RouteNodeInstance nodeInstance = (RouteNodeInstance) activeNodeInstances.get(0);
                routeContext.setNodeInstance(nodeInstance);
            }
        }
        if (routeContext.getNodeInstance() == null) {
            // get the initial node instance
            routeContext.setNodeInstance((RouteNodeInstance) document.getInitialRouteNodeInstances().get(0));
        }
        return routeContext;
    }
}