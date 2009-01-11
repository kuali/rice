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

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.KimGroupRecipient;
import org.kuali.rice.kew.actionrequest.KimPrincipalRecipient;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.docsearch.service.SearchableAttributeProcessingService;
import org.kuali.rice.kew.exception.InvalidActionTakenException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.messaging.MessageServiceNames;
import org.kuali.rice.kew.messaging.RouteDocumentMessageService;
import org.kuali.rice.kew.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kew.postprocessor.PostProcessor;
import org.kuali.rice.kew.postprocessor.ProcessDocReport;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.ksb.messaging.service.KSBXMLService;


/**
 * Super class containing mostly often used methods by all actions. Holds common
 * state as well, {@link DocumentRouteHeaderValue} document,
 * {@link ActionTakenValue} action taken (once saved), {@link KimPrincipal} principal
 * that has taken the action
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class ActionTakenEvent {

	private static final Logger LOG = Logger.getLogger(ActionTakenEvent.class);

	/**
	 * Used when saving an ActionTakenValue, and for validation in validateActionRules
	 */
	private String actionTakenCode;

	protected final String annotation;

	protected DocumentRouteHeaderValue routeHeader;

	private final KimPrincipal principal;

    private final boolean runPostProcessorLogic;

	public ActionTakenEvent(String actionTakenCode, DocumentRouteHeaderValue routeHeader, KimPrincipal principal) {
		this(actionTakenCode, routeHeader, principal, null, true);
	}

    public ActionTakenEvent(String actionTakenCode, DocumentRouteHeaderValue routeHeader, KimPrincipal principal, String annotation) {
        this(actionTakenCode, routeHeader, principal, annotation, true);
    }

	public ActionTakenEvent(String actionTakenCode, DocumentRouteHeaderValue routeHeader, KimPrincipal principal, String annotation, boolean runPostProcessorLogic) {
	    this.actionTakenCode = actionTakenCode;
	    this.routeHeader = routeHeader;
        this.principal = principal;
        this.annotation = annotation == null ? "" : annotation;
		this.runPostProcessorLogic = runPostProcessorLogic;
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

	public KimPrincipal getPrincipal() {
		return principal;
	}

	/**
	 * Code of the action performed by the user
	 *
	 * Method may be overriden is action performed will be different than action
	 * taken
	 */
	protected String getActionPerformedCode() {
		return getActionTakenCode();
	}

	/**
	 * Validates whether or not this action is valid for the given principal
	 * and DocumentRouteHeaderValue.
	 */
	protected boolean isActionValid() {
		return Utilities.isEmpty(validateActionRules());
	}

	/**
	 * Placeholder for validation rules for each action
	 *
	 * @return error message string of specific error message
	 */
	protected abstract String validateActionRules();

	protected boolean isActionCompatibleRequest(List<ActionRequestValue> requests) {
		LOG.debug("isActionCompatibleRequest() Default method = returning true");
		return true;
	}

	public void performAction() throws InvalidActionTakenException {
	    recordAction();
	    queueDocumentProcessing();
	}

	protected abstract void recordAction() throws InvalidActionTakenException;

	public void performDeferredAction() {

	}

	protected void updateSearchableAttributesIfPossible() {
		// queue the document up so that it can be indexed for searching if it
		// has searchable attributes
		if (routeHeader.getDocumentType().hasSearchableAttributes()) {
			SearchableAttributeProcessingService searchableAttService = (SearchableAttributeProcessingService) MessageServiceNames.getSearchableAttributeService(routeHeader);
			searchableAttService.indexDocument(getRouteHeaderId());
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
			ProcessDocReport report = postProcessor.doActionTaken(new org.kuali.rice.kew.postprocessor.ActionTakenEvent(routeHeader.getRouteHeaderId(), routeHeader.getAppDocId(), actionTaken));
			if (!report.isSuccess()) {
				LOG.warn(report.getMessage(), report.getProcessException());
				throw new InvalidActionTakenException(report.getMessage());
			}
		} catch (Exception ex) {
			LOG.warn(ex, ex);
			throw new WorkflowRuntimeException(ex.getMessage(), ex);
		}
	}

	protected void notifyStatusChange(String newStatusCode, String oldStatusCode) throws InvalidActionTakenException {
        if (!isRunPostProcessorLogic()) {
            return;
        }
		DocumentRouteStatusChange statusChangeEvent = new DocumentRouteStatusChange(routeHeader.getRouteHeaderId(), routeHeader.getAppDocId(), oldStatusCode, newStatusCode);
		try {
			LOG.debug("Notifying post processor of status change " + oldStatusCode + "->" + newStatusCode);
			PostProcessor postProcessor = routeHeader.getDocumentType().getPostProcessor();
			ProcessDocReport report = postProcessor.doRouteStatusChange(statusChangeEvent);
			if (!report.isSuccess()) {
				LOG.warn(report.getMessage(), report.getProcessException());
				throw new InvalidActionTakenException(report.getMessage());
			}
		} catch (Exception ex) {
			throw new WorkflowRuntimeException(ex);
		}
	}

	/**
	 * Asynchronously queues the documented to be processed by the workflow engine.
	 */
	protected void queueDocumentProcessing() {
		QName documentServiceName = new QName(getRouteHeader().getDocumentType().getServiceNamespace(), MessageServiceNames.DOCUMENT_ROUTING_SERVICE);
		KSBXMLService documentRoutingService = (KSBXMLService) MessageServiceNames.getServiceAsynchronously(documentServiceName, getRouteHeader());
		try {
//			String content = String.valueOf(getRouteHeaderId());
			RouteDocumentMessageService.RouteMessageXmlElement element = new RouteDocumentMessageService.RouteMessageXmlElement(getRouteHeaderId(),isRunPostProcessorLogic());
			String content = element.translate();
			documentRoutingService.invoke(content);
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
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
		val.setRouteHeaderId(routeHeader.getRouteHeaderId());
		val.setPrincipalId(principal.getPrincipalId());
		if (delegator instanceof KimPrincipalRecipient) {
			val.setDelegatorPrincipalId(((KimPrincipalRecipient)delegator).getPrincipalId());
		} else if (delegator instanceof KimGroupRecipient) {
			val.setDelegatorGroupId(((KimGroupRecipient) delegator).getGroupId());
		}
		val.setRouteHeader(routeHeader);
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

	protected Long getRouteHeaderId() {
		return this.routeHeader.getRouteHeaderId();
	}

	/*protected void delete() {
	    KEWServiceLocator.getActionTakenService().delete(actionTaken);
	}*/

	protected boolean isRunPostProcessorLogic() {
        return this.runPostProcessorLogic;
    }
}