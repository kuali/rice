/*
 * Copyright 2005-2007 The Kuali Foundation
 *
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.actions;

import org.apache.log4j.Logger;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.KimGroupRecipient;
import org.kuali.rice.kew.actionrequest.KimPrincipalRecipient;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.document.DocumentProcessingOptions;
import org.kuali.rice.kew.api.document.DocumentProcessingQueue;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeIndexingQueue;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.exception.InvalidActionTakenException;
import org.kuali.rice.kew.messaging.MessageServiceNames;
import org.kuali.rice.kew.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kew.postprocessor.PostProcessor;
import org.kuali.rice.kew.postprocessor.ProcessDocReport;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.api.identity.principal.PrincipalContract;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import javax.xml.namespace.QName;
import java.util.List;


/**
 * Super class containing mostly often used methods by all actions. Holds common
 * state as well, {@link DocumentRouteHeaderValue} document,
 * {@link ActionTakenValue} action taken (once saved), {@link PrincipalContract} principal
 * that has taken the action
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ActionTakenEvent {

	private static final Logger LOG = Logger.getLogger(ActionTakenEvent.class);

	/**
	 * Used when saving an ActionTakenValue, and for validation in validateActionRules
	 */
	private String actionTakenCode;

	protected final String annotation;

	protected DocumentRouteHeaderValue routeHeader;

	private final PrincipalContract principal;

    private final boolean runPostProcessorLogic;
    
    private List<String> groupIdsForPrincipal;
    

    private boolean queueDocumentAfterAction = true;


	public ActionTakenEvent(String actionTakenCode, DocumentRouteHeaderValue routeHeader, PrincipalContract principal) {
		this(actionTakenCode, routeHeader, principal, null, true);
	}

    public ActionTakenEvent(String actionTakenCode, DocumentRouteHeaderValue routeHeader, PrincipalContract principal, String annotation) {
        this(actionTakenCode, routeHeader, principal, annotation, true);
    }

	public ActionTakenEvent(String actionTakenCode, DocumentRouteHeaderValue routeHeader, PrincipalContract principal, String annotation, boolean runPostProcessorLogic) {
	    this.actionTakenCode = actionTakenCode;
	    this.routeHeader = routeHeader;
        this.principal = principal;
        this.annotation = annotation == null ? "" : annotation;
		this.runPostProcessorLogic = runPostProcessorLogic;
		this.queueDocumentAfterAction = true;
	}

	public ActionRequestService getActionRequestService() {
		return (ActionRequestService) KEWServiceLocator.getService(KEWServiceLocator.ACTION_REQUEST_SRV);
	}

	public DocumentRouteHeaderValue getRouteHeader() {
		return routeHeader;
	}

	public void setRouteHeader(DocumentRouteHeaderValue routeHeader) {
		this.routeHeader = routeHeader;
	}

	public PrincipalContract getPrincipal() {
		return principal;
	}

	/**
	 * Code of the action performed by the user
	 *
	 * Method may be overriden is action performed will be different than action
	 * taken
     * @return
     */
	protected String getActionPerformedCode() {
		return getActionTakenCode();
	}

	/**
	 * Validates whether or not this action is valid for the given principal
	 * and DocumentRouteHeaderValue.
	 */
	protected boolean isActionValid() {
		return org.apache.commons.lang.StringUtils.isEmpty(validateActionRules());
	}

	/**
	 * Placeholder for validation rules for each action
	 *
	 * @return error message string of specific error message
	 */
	public abstract String validateActionRules();
	public abstract String validateActionRules(List<ActionRequestValue> actionRequests);
	
	/**
	 * Filters action requests based on if they occur after the given requestCode, and if they relate to this
	 * event's principal
	 * @param actionRequests the List of ActionRequestValues to filter
	 * @param requestCode the request code for all ActionRequestValues to be after
	 * @return the filtered List of ActionRequestValues
	 */
	public List<ActionRequestValue> filterActionRequestsByCode(List<ActionRequestValue> actionRequests, String requestCode) {
		return getActionRequestService().filterActionRequestsByCode(actionRequests, getPrincipal().getPrincipalId(), getGroupIdsForPrincipal(), requestCode);
	}

	protected boolean isActionCompatibleRequest(List<ActionRequestValue> requests) {
		LOG.debug("isActionCompatibleRequest() Default method = returning true");
		return true;
	}

	public void performAction() throws InvalidActionTakenException {
	    recordAction();
	    if (queueDocumentAfterAction) {
	    	queueDocumentProcessing();
	    }

	}

	protected abstract void recordAction() throws InvalidActionTakenException;

	public void performDeferredAction() {

	}

	protected void updateSearchableAttributesIfPossible() {
		// queue the document up so that it can be indexed for searching if it
		// has searchable attributes
		RouteContext routeContext = RouteContext.getCurrentRouteContext();
		if (routeHeader.getDocumentType().hasSearchableAttributes() && !routeContext.isSearchIndexingRequestedForContext()) {
			routeContext.requestSearchIndexingForContext();
            DocumentAttributeIndexingQueue queue = KewApiServiceLocator.getDocumentAttributeIndexingQueue(routeHeader.getDocumentType().getApplicationId());
            queue.indexDocument(getDocumentId());
		}
	}

	protected void notifyActionTaken(ActionTakenValue actionTaken) {
	    if (!isRunPostProcessorLogic()) {
	        return;
	    }
		if (actionTaken == null) {
			return;
		}
		try {
			LOG.debug("Notifying post processor of action taken");
			PostProcessor postProcessor = routeHeader.getDocumentType().getPostProcessor();
			ProcessDocReport report = postProcessor.doActionTaken(new org.kuali.rice.kew.postprocessor.ActionTakenEvent(routeHeader.getDocumentId(), routeHeader.getAppDocId(), actionTaken));
			if (!report.isSuccess()) {
				LOG.warn(report.getMessage(), report.getProcessException());
				throw new InvalidActionTakenException(report.getMessage());
			}

		} catch (Exception ex) {
		    processPostProcessorException(ex);
		}
	}

	protected void notifyStatusChange(String newStatusCode, String oldStatusCode) throws InvalidActionTakenException {
        if (!isRunPostProcessorLogic()) {
            return;
        }
		DocumentRouteStatusChange statusChangeEvent = new DocumentRouteStatusChange(routeHeader.getDocumentId(), routeHeader.getAppDocId(), oldStatusCode, newStatusCode);
		try {
			LOG.debug("Notifying post processor of status change " + oldStatusCode + "->" + newStatusCode);
			PostProcessor postProcessor = routeHeader.getDocumentType().getPostProcessor();
			ProcessDocReport report = postProcessor.doRouteStatusChange(statusChangeEvent);
			if (!report.isSuccess()) {
				LOG.warn(report.getMessage(), report.getProcessException());
				throw new InvalidActionTakenException(report.getMessage());
			}
		} catch (Exception ex) {
		    processPostProcessorException(ex);
		}
	}

	/**
	 * Asynchronously queues the documented to be processed by the workflow engine.
	 */
	protected void queueDocumentProcessing() {
		DocumentProcessingQueue documentProcessingQueue = (DocumentProcessingQueue) MessageServiceNames.getDocumentProcessingQueue(getRouteHeader());
        DocumentProcessingOptions options = DocumentProcessingOptions.create(isRunPostProcessorLogic(), RouteContext.getCurrentRouteContext().isSearchIndexingRequestedForContext());
        documentProcessingQueue.processWithOptions(getDocumentId(), options);
	}

	protected ActionTakenValue saveActionTaken() {
	    return saveActionTaken(Boolean.TRUE);
	}

	protected ActionTakenValue saveActionTaken(Boolean currentInd) {
		return saveActionTaken(currentInd, null);
	}

	protected ActionTakenValue saveActionTaken(Recipient delegator) {
	    return saveActionTaken(Boolean.TRUE, delegator);
	}

	protected ActionTakenValue saveActionTaken(Boolean currentInd, Recipient delegator) {
		ActionTakenValue val = new ActionTakenValue();
		val.setActionTaken(getActionTakenCode());
		val.setAnnotation(annotation);
		val.setDocVersion(routeHeader.getDocVersion());
		val.setDocumentId(routeHeader.getDocumentId());
		val.setPrincipalId(principal.getPrincipalId());
		if (delegator instanceof KimPrincipalRecipient) {
			val.setDelegatorPrincipalId(((KimPrincipalRecipient)delegator).getPrincipalId());
		} else if (delegator instanceof KimGroupRecipient) {
			val.setDelegatorGroupId(((KimGroupRecipient) delegator).getGroupId());
		}
		//val.setRouteHeader(routeHeader);
		val.setCurrentIndicator(currentInd);
		KEWServiceLocator.getActionTakenService().saveActionTaken(val);
		return val;
	}

	/**
	 * Returns the highest priority delegator in the list of action requests.
	 */
	protected Recipient findDelegatorForActionRequests(List actionRequests) {
		return getActionRequestService().findDelegator(actionRequests);
	}

	public String getActionTakenCode() {
		return actionTakenCode;
	}

	protected void setActionTakenCode(String string) {
		actionTakenCode = string;
	}

	protected String getDocumentId() {
		return this.routeHeader.getDocumentId();
	}

	/*protected void delete() {
	    KEWServiceLocator.getActionTakenService().delete(actionTaken);
	}*/

	protected boolean isRunPostProcessorLogic() {
        return this.runPostProcessorLogic;
    }
	
	protected List<String> getGroupIdsForPrincipal() {
		if (groupIdsForPrincipal == null) {
			groupIdsForPrincipal = KimApiServiceLocator.getGroupService().getGroupIdsByPrincipalId(
                    getPrincipal().getPrincipalId());
		}
		return groupIdsForPrincipal;
	}


	public void setQueueDocumentAfterAction(boolean queueDocumentAfterAction) {
		this.queueDocumentAfterAction = queueDocumentAfterAction;
	}

	private void processPostProcessorException(Exception e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
        }
        throw new WorkflowRuntimeException(e);
	}

	
}
