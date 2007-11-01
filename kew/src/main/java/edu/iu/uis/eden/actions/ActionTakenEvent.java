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

import java.util.List;

import javax.xml.namespace.QName;

import edu.iu.uis.eden.DocumentRouteStatusChange;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestService;
import edu.iu.uis.eden.actiontaken.ActionTakenService;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.docsearch.SearchableAttributeProcessingService;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.messaging.KEWXMLService;
import edu.iu.uis.eden.messaging.MessageQueueService;
import edu.iu.uis.eden.messaging.MessageServiceNames;
import edu.iu.uis.eden.postprocessor.PostProcessor;
import edu.iu.uis.eden.postprocessor.ProcessDocReport;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.RouteHeaderService;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.Workgroup;
import edu.iu.uis.eden.workgroup.WorkgroupService;

/**
 * Super class containing mostly often used methods by all actions. Holds common
 * state as well, {@link DocumentRouteHeaderValue} document,
 * {@link ActionTakenValue} action taken (once saved), {@link WorkflowUser} user
 * that has taken the action
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class ActionTakenEvent {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionTakenEvent.class);

	private String actionTakenCode;

	protected String annotation;

	protected ActionTakenValue actionTaken;

	protected DocumentRouteHeaderValue routeHeader;

	protected Long routeHeaderId;

	protected Boolean currentInd = new Boolean(true);

	private WorkflowUser user;

	public ActionTakenEvent(DocumentRouteHeaderValue routeHeader, WorkflowUser user) {
		this.routeHeader = routeHeader;
		this.user = user;
		this.routeHeaderId = routeHeader.getRouteHeaderId();
	}

	public ActionTakenEvent(DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation) {
		this.routeHeader = routeHeader;
		this.user = user;
		this.annotation = annotation;
		this.routeHeaderId = routeHeader.getRouteHeaderId();
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

	public WorkflowUser getUser() {
		return user;
	}

	/**
	 * Code of the action performed by the user
	 *
	 * Method may be overriden is action performed will be different than action
	 * taken
	 */
	public String getActionPerformedCode() {
		return getActionTakenCode();
	}

	/**
	 * Validates whether or not this action is valid for the given WorkflowUser
	 * and DocumentRouteHeaderValue.
	 */
	public boolean isActionValid() throws EdenUserNotFoundException {
		return Utilities.isEmpty(validateActionRules());
	}

	/**
	 * Placeholder for validation rules for each action
	 *
	 * @return error message string of specific error message
	 * @throws EdenUserNotFoundException
	 */
	public abstract String validateActionRules() throws EdenUserNotFoundException;

	/**
	 * Method to indicate that this action may require initiator execution only
	 *
	 * @return false if action can be performed by users outside the initiator
	 */
	protected boolean requireInitiatorCheck() {
		LOG.debug("requireInitiatorCheck() Default method = returning true");
		return true;
	}

	protected boolean isActionCompatibleRequest(List requests) throws EdenUserNotFoundException {
		LOG.debug("isActionCompatibleRequest() Default method = returning true");
		return true;
	}

	protected String validateActionTakenRules() {
		if (requireInitiatorCheck() && (!user.getWorkflowUserId().getWorkflowId().equals(routeHeader.getInitiatorWorkflowId()) && (routeHeader.isStateSaved() || routeHeader.isStateInitiated()))) {
			return "Only the initiator can take action on an initiated or saved document of this type";
		}
		return "";
	}

	public abstract void recordAction() throws InvalidActionTakenException, EdenUserNotFoundException;


	public void checkLocking() throws InvalidActionTakenException {
		if (routeHeader.isLocked()) {
			throw new InvalidActionTakenException("The document " + routeHeader.getRouteHeaderId() + " is locked.  Action cannot be taken.");
		}
	}

	public void updateSearchableAttributesIfPossible() {
		// queue the document up so that it can be indexed for searching if it
		// has searchable attributes
		if (routeHeader.getDocumentType().hasSearchableAttributes()) {
			SearchableAttributeProcessingService searchableAttService = (SearchableAttributeProcessingService) MessageServiceNames.getSearchableAttributeService(routeHeader);
			searchableAttService.indexDocument(routeHeaderId);
		}
	}

	protected void notifyActionTaken(ActionTakenValue actionTaken) {
		if (actionTaken == null) {
			return;
		}
		try {
			LOG.debug("Notifying post processor of action taken");
			PostProcessor postProcessor = routeHeader.getDocumentType().getPostProcessor();
			ProcessDocReport report = postProcessor.doActionTaken(new edu.iu.uis.eden.ActionTakenEvent(routeHeader.getRouteHeaderId(), routeHeader.getAppDocId(), actionTaken));
			if (!report.isSuccess()) {
				LOG.warn(report.getMessage(), report.getProcessException());
				throw new InvalidActionTakenException(report.getMessage());
			}
		} catch (Exception ex) {
			LOG.warn(ex, ex);
			throw new WorkflowRuntimeException(ex.getMessage());
		}
	}

	protected void notifyStatusChange(String newStatusCode, String oldStatusCode) throws InvalidActionTakenException {
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

	public void queueDocument() {
		QName documentServiceName = new QName(getRouteHeader().getDocumentType().getMessageEntity(), MessageServiceNames.DOCUMENT_ROUTING_SERVICE);
		KEWXMLService documentRoutingService = (KEWXMLService) MessageServiceNames.getServiceAsynchronously(documentServiceName, getRouteHeader());
		try {
			documentRoutingService.invoke(String.valueOf(getRouteHeaderId()));
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
	}

	protected ActionTakenValue saveActionTaken() {
		return saveActionTaken(null);
	}

	protected ActionTakenValue saveActionTaken(Recipient delegator) {
		ActionTakenValue val = new ActionTakenValue();
		val.setActionTaken(actionTakenCode);
		val.setAnnotation(annotation);
		val.setDocVersion(routeHeader.getDocVersion());
		val.setRouteHeaderId(routeHeaderId);
		val.setWorkflowId(user.getWorkflowUserId().getWorkflowId());
		if (delegator instanceof WorkflowUser) {
			val.setDelegatorWorkflowId(((WorkflowUser) delegator).getWorkflowUserId().getWorkflowId());
		} else if (delegator instanceof Workgroup) {
			val.setDelegatorWorkgroupId(((Workgroup) delegator).getWorkflowGroupId().getGroupId());
		}
		val.setRouteHeader(routeHeader);
		val.setCurrentIndicator(currentInd);
		getActionTakenService().saveActionTaken(val);
		this.actionTaken = val;
		// notifyActionTaken(this.actionTaken);
		return val;
	}

	/**
	 * Returns the highest priority delegator in the list of action requests.
	 */
	protected Recipient findDelegatorForActionRequests(List actionRequests) throws EdenUserNotFoundException {
		return getActionRequestService().findDelegator(actionRequests);
	}

	public void setUser(WorkflowUser user) {
		this.user = user;
	}

	public String getActionTakenCode() {
		return actionTakenCode;
	}

	public void setActionTakenCode(String string) {
		actionTakenCode = string;
	}

	public Long getRouteHeaderId() {
		return this.routeHeader.getRouteHeaderId();
	}

	public Long getActionTakenId() {
		return actionTaken.getActionTakenId();
	}

	public ActionTakenValue getActionTaken() {
		return actionTaken;
	}

	public void delete() {
		getActionTakenService().delete(actionTaken);
	}

	public ActionTakenService getActionTakenService() {
		return (ActionTakenService) KEWServiceLocator.getService(KEWServiceLocator.ACTION_TAKEN_SRV);
	}

	public DocumentTypeService getDocumentTypeService() {
		return (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
	}

	public RouteHeaderService getRouteHeaderService() {
		return (RouteHeaderService) KEWServiceLocator.getService(KEWServiceLocator.DOC_ROUTE_HEADER_SRV);
	}

    public WorkgroupService getWorkgroupService() {
        return (WorkgroupService) KEWServiceLocator.getService(KEWServiceLocator.WORKGROUP_SRV);
    }

	public Boolean getCurrentInd() {
		return currentInd;
	}

	public void setCurrentInd(Boolean currentInd) {
		this.currentInd = currentInd;
	}
}