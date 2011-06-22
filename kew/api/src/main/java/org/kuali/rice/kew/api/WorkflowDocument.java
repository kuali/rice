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
package org.kuali.rice.kew.api;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.action.AdHocRevoke;
import org.kuali.rice.kew.api.action.AdHocToGroup;
import org.kuali.rice.kew.api.action.AdHocToPrincipal;
import org.kuali.rice.kew.api.action.DocumentActionResult;
import org.kuali.rice.kew.api.action.InvalidActionTakenException;
import org.kuali.rice.kew.api.action.MovePoint;
import org.kuali.rice.kew.api.action.RequestedActions;
import org.kuali.rice.kew.api.action.ReturnPoint;
import org.kuali.rice.kew.api.action.ValidActions;
import org.kuali.rice.kew.api.action.WorkflowDocumentActionsService;
import org.kuali.rice.kew.api.doctype.DocumentTypeNotFoundException;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContent;
import org.kuali.rice.kew.api.document.DocumentContentUpdate;
import org.kuali.rice.kew.api.document.DocumentCreationException;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.DocumentUpdate;
import org.kuali.rice.kew.api.document.RouteNodeInstance;
import org.kuali.rice.kew.api.document.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.document.WorkflowAttributeValidationError;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;

/**
 * TODO ..
 * 
 * <p>This class is *not* thread safe.
 *
 */
public class WorkflowDocument implements java.io.Serializable {

	private static final long serialVersionUID = -3672966990721719088L;

    private String principalId;
    
    private ModifiableDocument modifiableDocument;    
    private ModifiableDocumentContent modifiableDocumentContent;
    private ValidActions validActions;
    private RequestedActions requestedActions;

    /**
     * TODO 
     * 
     * @param principalId TODO
     * @param documentTypeName TODO
     * 
     * @return TODO
     * 
     * @throws IllegalArgumentException if principalId is null or blank
     * @throws IllegalArgumentException if documentTypeName is null or blank
     * @throws DocumentTypeNotFoundException if documentTypeName does not represent a valid document type
     * @throws DocumentCreationException if the document type does not allow for creation of a document,
     * this can occur when the given document type is used only as a parent and has no route path configured
     * @throws InvalidActionTakenException if the caller is not allowed to execute this action
     */
    public static WorkflowDocument createDocument(String principalId, String documentTypeName) {
    	return createDocument(principalId, documentTypeName, null, null);
    }
    
    /**
     * TODO
     * 
     * @param principalId TODO
     * @param documentTypeName TODO
     * @param title TODO
     * 
     * @return TODO
     * 
     * @throws IllegalArgumentException if principalId is null or blank
     * @throws IllegalArgumentException if documentTypeName is null or blank
     * @throws DocumentTypeNotFoundException if documentTypeName does not represent a valid document type
     */
    public static WorkflowDocument createDocument(String principalId, String documentTypeName, String title) {
    	DocumentUpdate.Builder builder = DocumentUpdate.Builder.create();
    	builder.setTitle(title);
    	return createDocument(principalId, documentTypeName, builder.build(), null);
    }
    
    /**
     * TODO
     * 
     * @param principalId TODO
     * @param documentTypeName TODO
     * @param documentUpdate TODO
     * @param documentContentUpdate TODO
     * 
     * @return TODO
     * 
     * @throws IllegalArgumentException if principalId is null or blank
     * @throws IllegalArgumentException if documentTypeName is null or blank
     * @throws DocumentTypeNotFoundException if documentTypeName does not represent a valid document type
     */
	public static WorkflowDocument createDocument(String principalId, String documentTypeName, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate) {
		if (StringUtils.isBlank(principalId)) {
			throw new IllegalArgumentException("principalId was null or blank");
		}
		if (StringUtils.isBlank(documentTypeName)) {
			throw new IllegalArgumentException("documentTypeName was null or blank");
		}
		Document document = getWorkflowDocumentActionsService().create(documentTypeName, principalId, documentUpdate, documentContentUpdate);
		return new WorkflowDocument(principalId, document);
	}
	
	public static WorkflowDocument loadDocument(String principalId, String documentId) {
		if (StringUtils.isBlank(principalId)) {
			throw new IllegalArgumentException("principalId was null or blank");
		}
		if (StringUtils.isBlank(documentId)) {
			throw new IllegalArgumentException("documentId was null or blank");
		}
		Document document = getWorkflowDocumentService().getDocument(documentId);
		if (document == null) {
			throw new IllegalArgumentException("Failed to locate workflow document for given documentId: " + documentId);
		}
		return new WorkflowDocument(principalId, document);
	}
	
	protected WorkflowDocument(String principalId, Document document) {
		if (StringUtils.isBlank("principalId")) {
			throw new IllegalArgumentException("principalId was null or blank");
		}
		if (document == null) {
			throw new IllegalArgumentException("document was null");
		}
		this.principalId = principalId;
		this.modifiableDocument = new ModifiableDocument(document);
    }

    private static WorkflowDocumentActionsService getWorkflowDocumentActionsService() {
    	WorkflowDocumentActionsService workflowDocumentActionsService =  KewApiServiceLocator.getWorkflowDocumentActionsService();
    	if (workflowDocumentActionsService == null) {
    		throw new ConfigurationException("Could not locate the WorkflowDocumentActionsService.  Please ensure that KEW client is configured properly!");
    	}
    	return workflowDocumentActionsService;
    }
    
    private static WorkflowDocumentService getWorkflowDocumentService() {
    	WorkflowDocumentService workflowDocumentService =  KewApiServiceLocator.getWorkflowDocumentService();
    	if (workflowDocumentService == null) {
    		throw new ConfigurationException("Could not locate the WorkflowDocumentService.  Please ensure that KEW client is configured properly!");
    	}
    	return workflowDocumentService;
    }
    
    protected ModifiableDocument getModifiableDocument() {
    	return modifiableDocument;
    }
    
    protected ModifiableDocumentContent getModifiableDocumentContent() {
    	if (this.modifiableDocumentContent == null) {
    		DocumentContent documentContent = getWorkflowDocumentService().getDocumentContent(getDocumentId());
    		if (documentContent == null) {
    			throw new IllegalStateException("Failed to load document content for documentId: " + getDocumentId());
    		}
    		this.modifiableDocumentContent = new ModifiableDocumentContent(documentContent);
    	}
    	return this.modifiableDocumentContent;
    }
    
    public String getDocumentId() {
    	return getModifiableDocument().getDocumentId();
    }
    
    public Document getDocument() {
        return getModifiableDocument().getDocument();
    }
    
    public DocumentContent getDocumentContent() {
    	return getModifiableDocumentContent().getDocumentContent();
    }

    public String getApplicationContent() {
        return getDocumentContent().getApplicationContent();
    }

    public void setApplicationContent(String applicationContent) {
    	getModifiableDocumentContent().setApplicationContent(applicationContent);
    }

    public void clearAttributeContent() {
    	getModifiableDocumentContent().setAttributeContent("");
    }

    public String getAttributeContent() {
        return getDocumentContent().getAttributeContent();
    }

    public void addAttributeDefinition(WorkflowAttributeDefinition attributeDefinition) {
    	getModifiableDocumentContent().addAttributeDefinition(attributeDefinition);
    }

    public void removeAttributeDefinition(WorkflowAttributeDefinition attributeDefinition) {
    	getModifiableDocumentContent().removeAttributeDefinition(attributeDefinition);
    }

    public void clearAttributeDefinitions() {
    	getAttributeDefinitions().clear();
    }

    public List<WorkflowAttributeDefinition> getAttributeDefinitions() {
        return getModifiableDocumentContent().getAttributeDefinitions();
    }

    public void addSearchableDefinition(WorkflowAttributeDefinition searchableDefinition) {
    	getModifiableDocumentContent().addSearchableDefinition(searchableDefinition);
    }

    public void removeSearchableDefinition(WorkflowAttributeDefinition searchableDefinition) {
    	getModifiableDocumentContent().removeSearchableDefinition(searchableDefinition);
    }

    public void clearSearchableDefinitions() {
    	getSearchableDefinitions().clear();
    }

    public void clearSearchableContent() {
    	getModifiableDocumentContent().setSearchableContent("");
    }

    public List<WorkflowAttributeDefinition> getSearchableDefinitions() {
        return getModifiableDocumentContent().getSearchableDefinitions();
    }
    
    public List<WorkflowAttributeValidationError> validateAttributeDefinition(WorkflowAttributeDefinition attributeDefinition) {
    	return getWorkflowDocumentActionsService().validateWorkflowAttributeDefinition(attributeDefinition);
    }

    public List<ActionRequest> getRootActionRequests() {
    	return getWorkflowDocumentService().getRootActionRequests(getDocumentId());
    }

    public List<ActionTaken> getActionsTaken() {
    	return getWorkflowDocumentService().getActionsTaken(getDocumentId());
    }

    public void setApplicationDocumentId(String applicationDocumentId) {
    	getModifiableDocument().setApplicationDocumentId(applicationDocumentId);
    }

    public String getApplicationDocumentId() {
    	return getModifiableDocument().getApplicationDocumentId();
    }

    public DateTime getDateCreated() {
    	return getModifiableDocument().getDateCreated();
    }

    public String getTitle() {
        return getModifiableDocument().getTitle();
    }
    
    public ValidActions getValidActions() {
    	if (validActions == null) {
    		validActions = getWorkflowDocumentActionsService().determineValidActions(getDocumentId(), getPrincipalId());
    	}
    	return validActions;
    }
    
    public RequestedActions getRequestedActions() {
    	if (requestedActions == null) {
    		requestedActions = getWorkflowDocumentActionsService().determineRequestedActions(getDocumentId(), getPrincipalId());
    	}
    	return requestedActions;
    }
    
    protected DocumentUpdate getDocumentUpdateIfDirty() {
    	if (getModifiableDocument().isDirty()) {
    		return getModifiableDocument().getBuilder().build();
    	}
    	return null;
    }
    
    protected DocumentContentUpdate getDocumentContentUpdateIfDirty() {
    	if (getModifiableDocumentContent().isDirty()) {
    		return getModifiableDocumentContent().getBuilder().build();
    	}
    	return null;
    }
    
    protected void resetStateAfterAction(DocumentActionResult response) {
    	this.modifiableDocument = new ModifiableDocument(response.getDocument());
    	this.validActions = null;
    	if (response.getValidActions() != null) {
    		this.validActions = response.getValidActions();
    	}
    	this.requestedActions = null;
    	if (response.getRequestedActions() != null) {
    		this.requestedActions = response.getRequestedActions();
    	}
    	// regardless of whether modifiable document content is dirty, we null it out so it will be re-fetched next time it's needed
    	this.modifiableDocumentContent = null;
    }
    
    public void saveDocument(String annotation) {
    	DocumentActionResult result = getWorkflowDocumentActionsService().save(getDocumentId(), principalId, annotation, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty());
    	resetStateAfterAction(result);
    }

    public void route(String annotation) {
    	DocumentActionResult result = getWorkflowDocumentActionsService().route(getDocumentId(), principalId, annotation, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty());
    	resetStateAfterAction(result);
    }
    
    public void disapprove(String annotation) {
    	DocumentActionResult result = getWorkflowDocumentActionsService().disapprove(getDocumentId(), principalId, annotation, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty());
    	resetStateAfterAction(result);
    }

    public void approve(String annotation) {
    	DocumentActionResult result = getWorkflowDocumentActionsService().approve(getDocumentId(), principalId, annotation, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty());
    	resetStateAfterAction(result);
    }

    public void cancel(String annotation) {
    	DocumentActionResult result = getWorkflowDocumentActionsService().cancel(getDocumentId(), principalId, annotation, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty());
    	resetStateAfterAction(result);
    }

    public void blanketApprove(String annotation) {
    	DocumentActionResult result = getWorkflowDocumentActionsService().blanketApprove(getDocumentId(), principalId, annotation, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty());
 		resetStateAfterAction(result);
    }
    
    public void blanketApprove(String annotation, String nodeName) {
    	if (StringUtils.isEmpty(nodeName)) {
    		throw new IllegalArgumentException("nodeName was null or blank");
    	}
    	blanketApprove(annotation, (nodeName == null ? new String[] {} : new String[] { nodeName }));
    }

    public void blanketApprove(String annotation, String[] nodeNames) {
    	Set<String> nodeNamesSet = new HashSet<String>(Arrays.asList(nodeNames));
    	DocumentActionResult result = getWorkflowDocumentActionsService().blanketApproveToNodes(getDocumentId(), principalId, annotation, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty(), nodeNamesSet);
 		resetStateAfterAction(result);
    }

    public void saveDocumentData() {
    	DocumentActionResult result = getWorkflowDocumentActionsService().saveDocumentData(getDocumentId(), principalId, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty());
    	resetStateAfterAction(result);
    }

//    /**
//     * 
//     * This method sets the Application Document Status and then calls saveRoutingData() to commit 
//     * the changes to the workflow system.
//     * 
//     * @param appDocStatus
//     * @throws WorkflowException
//     */
//    public void updateAppDocStatus(String appDocStatus) throws WorkflowException {
//       	getRouteHeader().setAppDocStatus(appDocStatus);
//       	saveRoutingData();
//    }
    
    public void acknowledge(String annotation) {
    	DocumentActionResult result = getWorkflowDocumentActionsService().acknowledge(getDocumentId(), principalId, annotation, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty());
    	resetStateAfterAction(result);
    }

    public void fyi(String annotation) {
    	DocumentActionResult result = getWorkflowDocumentActionsService().clearFyi(getDocumentId(), principalId, annotation, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty());
    	resetStateAfterAction(result);
    }
    
    public void fyi() {
    	fyi("");
    }

//    /**
//     * Performs the 'delete' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @throws WorkflowException in case an error occurs deleting the document
//     * @see WorkflowDocumentActions#deleteDocument(UserIdDTO, RouteHeaderDTO)
//     */
//    public void delete() throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	getWorkflowDocumentActions().deleteDocument(principalId, getRouteHeader());
//    	documentContentDirty = true;
//    }
//
//    /**
//     * Reloads the document route header.  If this is a new document, the document is created first.
//     * Next time document content is accessed, an up-to-date copy will be retrieved from workflow.
//     * @throws WorkflowException in case an error occurs retrieving the route header
//     */
//    public void refreshContent() throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowUtility().getRouteHeader(getDocumentId());
//    	documentContentDirty = true;
//    }

    public void adHocToPrincipal(ActionRequestType actionRequested, String annotation, String targetPrincipalId, String responsibilityDescription, boolean forceAction) {
    	adHocToPrincipal(actionRequested, null, annotation, targetPrincipalId, responsibilityDescription, forceAction);
    }

    public void adHocToPrincipal(ActionRequestType actionRequested, String nodeName, String annotation, String targetPrincipalId, String responsibilityDescription, boolean forceAction) {
    	adHocToPrincipal(actionRequested, nodeName, annotation, targetPrincipalId, responsibilityDescription, forceAction, null);
    }

    public void adHocToPrincipal(ActionRequestType actionRequested, String nodeName, String annotation, String targetPrincipalId, String responsibilityDescription, boolean forceAction, String requestLabel) {
    	AdHocToPrincipal.Builder builder = AdHocToPrincipal.Builder.create(actionRequested, nodeName, targetPrincipalId);
    	builder.setResponsibilityDescription(responsibilityDescription);
    	builder.setForceAction(forceAction);
    	builder.setRequestLabel(requestLabel);
    	DocumentActionResult result = getWorkflowDocumentActionsService().adHocToPrincipal(getDocumentId(), getPrincipalId(), annotation, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty(), builder.build());
    	resetStateAfterAction(result);
    }

    public void adHocToGroup(ActionRequestType actionRequested, String annotation, String targetGroupId, String responsibilityDescription, boolean forceAction) {
    	adHocToGroup(actionRequested, null, annotation, targetGroupId, responsibilityDescription, forceAction);
    }

    public void adHocToGroup(ActionRequestType actionRequested, String nodeName, String annotation, String targetGroupId, String responsibilityDescription, boolean forceAction) {
    	adHocToGroup(actionRequested, nodeName, annotation, targetGroupId, responsibilityDescription, forceAction, null);
    }

    public void adHocToGroup(ActionRequestType actionRequested, String nodeName, String annotation, String targetGroupId, String responsibilityDescription, boolean forceAction, String requestLabel) {
    	AdHocToGroup.Builder builder = AdHocToGroup.Builder.create(actionRequested, nodeName, targetGroupId);
    	builder.setResponsibilityDescription(responsibilityDescription);
    	builder.setForceAction(forceAction);
    	builder.setRequestLabel(requestLabel);
    	DocumentActionResult result = getWorkflowDocumentActionsService().adHocToGroup(getDocumentId(), getPrincipalId(), annotation, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty(), builder.build());
    	resetStateAfterAction(result);
    }

    public void revokeAdHocRequestById(String actionRequestId, String annotation) {
    	if (StringUtils.isBlank(actionRequestId)) {
    		throw new IllegalArgumentException("actionRequestId was null or blank");
    	}
    	DocumentActionResult result = getWorkflowDocumentActionsService().revokeAdHocRequestById(getDocumentId(), principalId, annotation, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty(), actionRequestId);
    	resetStateAfterAction(result);
    }
    
    public void revokeAdHocRequests(AdHocRevoke revoke, String annotation) {
    	if (revoke == null) {
    		throw new IllegalArgumentException("revokeFromPrincipal was null");
    	}
    	DocumentActionResult result = getWorkflowDocumentActionsService().revokeAdHocRequests(getDocumentId(), principalId, annotation, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty(), revoke);
    	resetStateAfterAction(result);
    }
    
    public void revokeAllAdHocRequests(String annotation) {
    	DocumentActionResult result = getWorkflowDocumentActionsService().revokeAllAdHocRequests(getDocumentId(), principalId, annotation, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty());
    	resetStateAfterAction(result);
    }

    public void setTitle(String title) {
        getModifiableDocument().setTitle(title);
    }

    public String getDocumentTypeName() {
    	return getDocument().getDocumentTypeName();
    }

    /**
     * Returns whether a completion is requested of the user for this document.  This is
     * a convenience method that delegates to {@link RouteHeaderDTO#isCompleteRequested()}.
     * @return whether an approval is requested of the user for this document
     * @see RouteHeaderDTO#isCompleteRequested()
     */
    public boolean isCompletionRequested() {
        return getRequestedActions().isCompleteRequested();
    }
    
    /**
     * Returns whether an approval is requested of the user for this document.  This is
     * a convenience method that delegates to {@link RouteHeaderDTO#isApproveRequested()}.
     * @return whether an approval is requested of the user for this document
     * @see RouteHeaderDTO#isApproveRequested()
     */
    public boolean isApprovalRequested() {
        return getRequestedActions().isApproveRequested();
    }
    
    /**
     * Returns whether an acknowledge is requested of the user for this document.  This is
     * a convenience method that delegates to {@link RouteHeaderDTO#isAckRequested()}.
     * @return whether an acknowledge is requested of the user for this document
     * @see RouteHeaderDTO#isAckRequested()
     */
    public boolean isAcknowledgeRequested() {
    	return getRequestedActions().isAcknowledgeRequested();
    }
    
    /**
     * Returns whether an FYI is requested of the user for this document.  This is
     * a convenience method that delegates to {@link RouteHeaderDTO#isFyiRequested()}.
     * @return whether an FYI is requested of the user for this document
     * @see RouteHeaderDTO#isFyiRequested()
     */
    public boolean isFYIRequested() {
        return getRequestedActions().isFyiRequested();
    }

    /**
     * Returns whether the user can blanket approve the document
     * @return whether the user can blanket approve the document
     * @see RouteHeaderDTO#getValidActions()
     */
    public boolean isBlanketApproveCapable() {
    	return isActionValid(ActionType.BLANKET_APPROVE) && (isCompletionRequested() || isApprovalRequested() || isInitiated());
    }

    /**
     * Returns whether the specified action code is valid for the current user and document
     * @return whether the user can blanket approve the document
     * @see RouteHeaderDTO#getValidActions()
     */
    public boolean isActionCodeValid(String actionCode) {
    	if (StringUtils.isBlank(actionCode)) {
    		throw new IllegalArgumentException("actionTakenCode was null or blank");
    	}
    	return getValidActions().getValidActions().contains(ActionType.fromCode(actionCode));
    }
    
    public boolean isActionValid(ActionType actionType) {
    	if (actionType == null) {
    		throw new IllegalArgumentException("actionType was null");
    	}
    	return getValidActions().getValidActions().contains(actionType);
    }

    //
//    /**
//     * Performs the 'super-user-approve' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @throws WorkflowException in case an error occurs super-user-approve-ing the document
//     * @see WorkflowDocumentActions#superUserApprove(UserIdDTO, RouteHeaderDTO, String)
//     */
//    public void superUserApprove(String annotation) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().superUserApprove(principalId, getRouteHeader(), annotation);
//    	documentContentDirty = true;
//    }
//
//    /**
//     * Performs the 'super-user-action-request-approve' action on the document this WorkflowDocument represents and the action
//     * request the id represents.
//     * @param actionRequestId the action request id for the action request the super user is approved
//     * @param annotation the message to log for the action
//     * @throws WorkflowException in case an error occurs super-user-action-request-approve-ing the document
//     * @see WorkflowDocumentActions#superUserApprove(UserIdVO, RouteHeaderVO, String)(UserIdVO, RouteHeaderVO, String)
//     */
//    public void superUserActionRequestApprove(Long actionRequestId, String annotation) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().superUserActionRequestApprove(principalId, getRouteHeader(), actionRequestId, annotation);
//    	documentContentDirty = true;
//    }
//
//    /**
//     * Performs the 'super-user-disapprove' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @throws WorkflowException in case an error occurs super-user-disapprove-ing the document
//     * @see WorkflowDocumentActions#superUserDisapprove(UserIdDTO, RouteHeaderDTO, String)
//     */
//    public void superUserDisapprove(String annotation) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().superUserDisapprove(principalId, getRouteHeader(), annotation);
//    	documentContentDirty = true;
//    }
//
//    /**
//     * Performs the 'super-user-cancel' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @throws WorkflowException in case an error occurs super-user-cancel-ing the document
//     * @see WorkflowDocumentActions#superUserCancel(UserIdDTO, RouteHeaderDTO, String)
//     */
//    public void superUserCancel(String annotation) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().superUserCancel(principalId, getRouteHeader(), annotation);
//    	documentContentDirty = true;
//    }
//
//    /**
//     * Returns whether the user is a super user on this document
//     * @return whether the user is a super user on this document
//     * @throws WorkflowException if an error occurs determining whether the user is a super user on this document
//     * @see WorkflowUtility#isSuperUserForDocumentType(UserIdDTO, Long)
//     */
//    public boolean isSuperUser() throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	return getWorkflowUtility().isSuperUserForDocumentType(principalId, getRouteHeader().getDocTypeId());
//	}
//
//    /**
//     * Returns whether the user passed into WorkflowDocument at instantiation can route
//     * the document.
//	 * @return if user passed into WorkflowDocument at instantiation can route
//	 *         the document.
//	 */
//    public boolean isRouteCapable() {
//        return isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_ROUTED_CD);
//    }
//
//    /**
//     * Performs the 'clearFYI' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @throws WorkflowException in case an error occurs clearing FYI on the document
//     * @see WorkflowDocumentActions#clearFYIDocument(UserIdDTO, RouteHeaderDTO)
//     */
//    public void clearFYI() throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	getWorkflowDocumentActions().clearFYIDocument(principalId, getRouteHeader());
//    	documentContentDirty = true;
//    }

    public void complete(String annotation) {
    	DocumentActionResult result = getWorkflowDocumentActionsService().complete(getDocumentId(), principalId, annotation, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty());
    	resetStateAfterAction(result);
    }

//    /**
//     * Performs the 'logDocumentAction' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.  The 'logDocumentAction' simply logs a message on the document.
//     * @param annotation the message to log for the action
//     * @throws WorkflowException in case an error occurs logging a document action on the document
//     * @see WorkflowDocumentActions#logDocumentAction(UserIdDTO, RouteHeaderDTO, String)
//     */
//    public void logDocumentAction(String annotation) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	getWorkflowDocumentActions().logDocumentAction(principalId, getRouteHeader(), annotation);
//    	documentContentDirty = true;
//    }
//
  
    public DocumentStatus getStatus() {
    	return getDocument().getStatus();
    }
    
    public boolean checkStatus(DocumentStatus status) {
    	if (status == null) {
    		throw new IllegalArgumentException("status was null");
    	}
    	return status == getStatus();
    }
    
    /**
     * Indicates if the document is in the initiated state or not.
     *
     * @return true if in the specified state
     */
    public boolean isInitiated() {
        return checkStatus(DocumentStatus.INITIATED);
    }

    /**
     * Indicates if the document is in the saved state or not.
     *
     * @return true if in the specified state
     */
    public boolean isSaved() {
        return checkStatus(DocumentStatus.SAVED);
    }

    /**
     * Indicates if the document is in the enroute state or not.
     *
     * @return true if in the specified state
     */
    public boolean isEnroute() {
        return checkStatus(DocumentStatus.ENROUTE);
    }

    /**
     * Indicates if the document is in the exception state or not.
     *
     * @return true if in the specified state
     */
    public boolean isException() {
        return checkStatus(DocumentStatus.EXCEPTION);
    }

    /**
     * Indicates if the document is in the canceled state or not.
     *
     * @return true if in the specified state
     */
    public boolean isCanceled() {
        return checkStatus(DocumentStatus.CANCELED);
    }

    /**
     * Indicates if the document is in the disapproved state or not.
     *
     * @return true if in the specified state
     */
    public boolean isDisapproved() {
        return checkStatus(DocumentStatus.DISAPPROVED);
    }

    /**
     * Indicates if the document is in the Processed or Finalized state.
     *
     * @return true if in the specified state
     */
    public boolean isApproved() {
    	return isProcessed() || isFinal();
    }

    /**
     * Indicates if the document is in the processed state or not.
     *
     * @return true if in the specified state
     */
    public boolean isProcessed() {
        return checkStatus(DocumentStatus.PROCESSED);
    }

    /**
     * Indicates if the document is in the final state or not.
     *
     * @return true if in the specified state
     */
    public boolean isFinal() {
        return checkStatus(DocumentStatus.FINAL);
    }

    /**
     * Returns the principalId with which this WorkflowDocument was constructed
     * 
     * @return the principalId with which this WorkflowDocument was constructed
     */
    public String getPrincipalId() {
        return principalId;
    }
    
    public void switchPrincipal(String principalId) {
    	if (StringUtils.isBlank(this.principalId)) {
    		throw new IllegalArgumentException("principalId was null or blank");
    	}
    	this.principalId = principalId;
    	// TODO reload valid actions
    }

//    /**
//     * Checks if the document has been created or not (i.e. has a document id or not) and issues
//     * a call to the server to create the document if it has not yet been created.
//     *
//     * Also checks if the document content has been updated and saves it if it has.
//     */
//    private void createDocumentIfNeccessary() throws WorkflowException {
//    	if (getRouteHeader().getDocumentId() == null) {
//    		routeHeader = getWorkflowDocumentActions().createDocument(principalId, getRouteHeader());
//    	}
//    	if (documentContent != null && documentContent.isModified()) {
//    		saveDocumentContent(documentContent);
//    	}
//    }
//
//    /**
//     * Like handleException except it returns a RuntimeException.
//     */
//    private RuntimeException handleExceptionAsRuntime(Exception e) {
//    	if (e instanceof RuntimeException) {
//    		return (RuntimeException)e;
//    	}
//    	return new WorkflowRuntimeException(e);
//    }
//
//    // WORKFLOW 2.1: new methods
//
//    /**
//     * Performs the 'blanketApprove' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @param nodeName the extent to which to blanket approve; blanket approval will stop at this node
//     * @throws WorkflowException in case an error occurs blanket-approving the document
//     * @see WorkflowDocumentActions#blanketApprovalToNodes(UserIdDTO, RouteHeaderDTO, String, String[])
//     */
//    public void blanketApprove(String annotation, String nodeName) throws WorkflowException {
//        blanketApprove(annotation, (nodeName == null ? new String[] {} : new String[] { nodeName }));
//    }
//
//    /**
//     * Performs the 'blanketApprove' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @param nodeNames the nodes at which blanket approval will stop (in case the blanket approval traverses a split, in which case there may be multiple "active" nodes)
//     * @throws WorkflowException in case an error occurs blanket-approving the document
//     * @see WorkflowDocumentActions#blanketApprovalToNodes(UserIdDTO, RouteHeaderDTO, String, String[])
//     */
//    public void blanketApprove(String annotation, String[] nodeNames) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().blanketApprovalToNodes(principalId, getRouteHeader(), annotation, nodeNames);
//    	documentContentDirty = true;
//    }
//
//    /**
//     * The user taking action removes the action items for this workgroup and document from all other
//     * group members' action lists.   If this is a new document, the document is created first.
//     *
//     * @param annotation the message to log for the action
//     * @param workgroupId the workgroup on which to take authority
//     * @throws WorkflowException user taking action is not in workgroup
//     */
//    public void takeGroupAuthority(String annotation, String groupId) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().takeGroupAuthority(principalId, getRouteHeader(), groupId, annotation);
//    	documentContentDirty = true;
//    }
//
//    /**
//     * The user that took the group authority is putting the action items back in the other users action lists.
//     * If this is a new document, the document is created first.
//     *
//     * @param annotation the message to log for the action
//     * @param workgroupId the workgroup on which to take authority
//     * @throws WorkflowException user taking action is not in workgroup or did not take workgroup authority
//     */
//    public void releaseGroupAuthority(String annotation, String groupId) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().releaseGroupAuthority(principalId, getRouteHeader(), groupId, annotation);
//    	documentContentDirty = true;
//    }

    public Set<String> getNodeNames() {
    	List<RouteNodeInstance> activeNodeInstances = getWorkflowDocumentService().getActiveNodeInstances(getDocumentId());
    	Set<String> nodeNames = new HashSet<String>(activeNodeInstances.size());
    	for (RouteNodeInstance routeNodeInstance : activeNodeInstances) {
    		nodeNames.add(routeNodeInstance.getName());
    	}
    	return Collections.unmodifiableSet(nodeNames);
    }

    public void returnToPreviousNode(String nodeName, String annotation) {
    	if (StringUtils.isBlank(nodeName)) {
    		throw new IllegalArgumentException("nodeName was null or blank");
    	}
        returnToPreviousNode(ReturnPoint.create(nodeName), annotation);
    }

    public void returnToPreviousNode(ReturnPoint returnPoint, String annotation) {
    	if (returnPoint == null) {
    		throw new IllegalArgumentException("returnPoint was null");
    	}
    	DocumentActionResult result = getWorkflowDocumentActionsService().returnToPreviousNode(getDocumentId(), principalId, annotation, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty(), returnPoint);
    	resetStateAfterAction(result);
    }

    public void moveDocument(MovePoint movePoint, String annotation) {
    	if (movePoint == null) {
    		throw new IllegalArgumentException("movePoint was null");
    	}
    	DocumentActionResult result = getWorkflowDocumentActionsService().move(getDocumentId(), principalId, annotation, getDocumentUpdateIfDirty(), getDocumentContentUpdateIfDirty(), movePoint);
    	resetStateAfterAction(result);
    }
    
//
//    /**
//     * Returns the route node instances that have been created so far during the life of this document.  This includes
//     * all previous instances which have already been processed and are no longer active.
//     * @return the route node instances that have been created so far during the life of this document
//     * @throws WorkflowException if there is an error getting the route node instances for the document
//     * @see WorkflowUtility#getDocumentRouteNodeInstances(Long)
//     */
//    public RouteNodeInstanceDTO[] getRouteNodeInstances() throws WorkflowException {
//    	return getWorkflowUtility().getDocumentRouteNodeInstances(getDocumentId());
//    }
//
//    /**
//     * Returns Array of Route Nodes Names that can be safely returned to using the 'returnToPreviousXXX' methods.
//     * Names are sorted in reverse chronological order.
//     *
//     * @return array of Route Nodes Names that can be safely returned to using the 'returnToPreviousXXX' methods
//     * @throws WorkflowException if an error occurs obtaining the names of the previous route nodes for this document
//     * @see WorkflowUtility#getPreviousRouteNodeNames(Long)
//     */
//    public String[] getPreviousNodeNames() throws WorkflowException {
//    	return getWorkflowUtility().getPreviousRouteNodeNames(getDocumentId());
//	}
//
//    /**
//     * Returns a document detail VO representing the route header along with action requests, actions taken,
//     * and route node instances.
//     * @return Returns a document detail VO representing the route header along with action requests, actions taken, and route node instances.
//     * @throws WorkflowException
//     */
//    public DocumentDetailDTO getDetail() throws WorkflowException {
//    	return getWorkflowUtility().getDocumentDetail(getDocumentId());
//    }
//
//    /**
//     * Saves the given DocumentContentVO for this document.
//     * @param documentContent document content VO to store for this document
//     * @since 2.3
//     * @see WorkflowDocumentActions#saveDocumentContent(DocumentContentDTO)
//     */
//    public DocumentContentDTO saveDocumentContent(DocumentContentDTO documentContent) throws WorkflowException {
//    	if (documentContent.getDocumentId() == null) {
//    		throw new WorkflowException("Document Content does not have a valid document ID.");
//    	}
//    	// important to check directly against getRouteHeader().getDocumentId() instead of just getDocumentId() because saveDocumentContent
//    	// is called from createDocumentIfNeccessary which is called from getDocumentId().  If that method was used, we would have an infinite loop.
//    	if (!documentContent.getDocumentId().equals(getRouteHeader().getDocumentId())) {
//    		throw new WorkflowException("Attempted to save content on this document with an invalid document id of " + documentContent.getDocumentId());
//    	}
//    	DocumentContentDTO newDocumentContent = getWorkflowDocumentActions().saveDocumentContent(documentContent);
//    	this.documentContent = new ModifiableDocumentContentDTO(newDocumentContent);
//    	documentContentDirty = false;
//    	return this.documentContent;
//    }
//    
//    public void placeInExceptionRouting(String annotation) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().placeInExceptionRouting(principalId, getRouteHeader(), annotation);
//    	documentContentDirty = true;
//    }
//
//    /**
//     * Returns a list of NoteVO representing the notes on the document
//     * @return a list of NoteVO representing the notes on the document
//     * @see RouteHeaderDTO#getNotes()
//     */
//    public List<NoteDTO> getNoteList(){
//    	List<NoteDTO> notesList = new ArrayList<NoteDTO>();
//    	NoteDTO[] notes = routeHeader.getNotes();
//    	if (notes != null){
//	    	for (int i=0; i<notes.length; i++){
//	    		if (! isDeletedNote(notes[i])){
//	    			notesList.add(notes[i]);
//	    		}
//	    	}
//    	}
//    	return notesList;
//    }
//
//    /**
//     * Deletes a note from the document.  The deletion is deferred until the next time the document is committed (via an action).
//     * @param noteVO the note to remove from the document
//     */
//    public void deleteNote(NoteDTO noteVO){
//    	if (noteVO != null && noteVO.getNoteId()!=null){
//    		NoteDTO noteToDelete = new NoteDTO();
//    		noteToDelete.setNoteId(new Long(noteVO.getNoteId().longValue()));
//    		/*noteToDelete.setDocumentId(noteVO.getDocumentId());
//    		noteToDelete.setNoteAuthorWorkflowId(noteVO.getNoteAuthorWorkflowId());
//    		noteToDelete.setNoteCreateDate(noteVO.getNoteCreateDate());
//    		noteToDelete.setNoteText(noteVO.getNoteText());
//    		noteToDelete.setLockVerNbr(noteVO.getLockVerNbr());*/
//    		increaseNotesToDeleteArraySizeByOne();
//    		routeHeader.getNotesToDelete()[routeHeader.getNotesToDelete().length - 1]=noteToDelete;
//    	}
//    }
//
//    /**
//     * Updates the note of the same note id, on the document. The update is deferred until the next time the document is committed (via an action).
//     * @param noteVO the note to update
//     */
//    public void updateNote (NoteDTO noteVO){
//    	boolean isUpdateNote = false;
//    	if (noteVO != null){
//    		NoteDTO[] notes = routeHeader.getNotes();
//    		NoteDTO  copyNote = new NoteDTO();
//			if (noteVO.getNoteId() != null){
//				copyNote.setNoteId(new Long(noteVO.getNoteId().longValue()));
//			}
//
//			if (noteVO.getDocumentId() != null){
//				copyNote.setDocumentId(noteVO.getDocumentId());
//			} else {
//				copyNote.setDocumentId(routeHeader.getDocumentId());
//			}
//			
//			if (noteVO.getNoteAuthorWorkflowId() != null){
//				copyNote.setNoteAuthorWorkflowId(new String(noteVO.getNoteAuthorWorkflowId()));
//			} else {
//			    copyNote.setNoteAuthorWorkflowId(principalId.toString())	;
//			}
//
//			if (noteVO.getNoteCreateDate() != null){
//				Calendar cal = Calendar.getInstance();
//				cal.setTimeInMillis(noteVO.getNoteCreateDate().getTimeInMillis());
//				copyNote.setNoteCreateDate(cal);
//			} else {
//				copyNote.setNoteCreateDate(Calendar.getInstance());
//			}
//
//			if (noteVO.getNoteText() != null){
//				copyNote.setNoteText(new String(noteVO.getNoteText()));
//			}
//			if (noteVO.getLockVerNbr() != null){
//				copyNote.setLockVerNbr(new Integer(noteVO.getLockVerNbr().intValue()));
//			}
//    		if (notes != null){
//	    		for (int i=0; i<notes.length; i++){
//	    			if (notes[i].getNoteId()!= null && notes[i].getNoteId().equals(copyNote.getNoteId())){
//	    				notes[i] = copyNote;
//	    				isUpdateNote = true;
//	    				break;
//	    			}
//	    		}
//    		}
//    		// add new note to the notes array
//    		if (! isUpdateNote){
//	    		copyNote.setNoteId(null);
//	    		increaseNotesArraySizeByOne();
//	    		routeHeader.getNotes()[routeHeader.getNotes().length-1]= copyNote;
//    		}
//    	}
//    }
//
//    /**
//     * Sets a variable on the document.  The assignment is deferred until the next time the document is committed (via an action).
//     * @param name name of the variable
//     * @param value value of the variable
//     */
//    public void setVariable(String name, String value) throws WorkflowException {
//    	createDocumentIfNeccessary();
//        getRouteHeader().setVariable(name, value);
//    }
//
//    /**
//     * Gets the value of a variable on the document, creating the document first if it does not exist.
//     * @param name variable name
//     * @return variable value
//     */
//    public String getVariable(String name) throws WorkflowException {
//    	createDocumentIfNeccessary();
//        return getRouteHeader().getVariable(name);
//    }
//
//    /**
//     *
//     * Tells workflow that the current the document is constructed as will receive all future requests routed to them
//     * disregarding any force action flags set on the action request.  Uses the setVariable method behind the seens so
//     * an action needs taken on the document to set this state on the document.
//     *
//     * @throws WorkflowException
//     */
//    public void setReceiveFutureRequests() throws WorkflowException {
//        WorkflowUtility workflowUtility = getWorkflowUtility();
//        this.setVariable(workflowUtility.getFutureRequestsKey(principalId), workflowUtility.getReceiveFutureRequestsValue());
//    }
//
//    /**
//     * Tell workflow that the current document is constructed as will not receive any future requests routed to them
//     * disregarding any force action flags set on action requests.  Uses the setVariable method behind the scenes so
//     * an action needs taken on the document to set this state on the document.
//     *
//     * @throws WorkflowException
//     */
//    public void setDoNotReceiveFutureRequests() throws WorkflowException {
//        WorkflowUtility workflowUtility = getWorkflowUtility();
//        this.setVariable(workflowUtility.getFutureRequestsKey(principalId), workflowUtility.getDoNotReceiveFutureRequestsValue());
//    }
//
//    /**
//     * Clears any state set on the document regarding a user receiving or not receiving action requests.  Uses the setVariable method
//     * behind the seens so an action needs taken on the document to set this state on the document.
//     *
//     * @throws WorkflowException
//     */
//    public void setClearFutureRequests() throws WorkflowException {
//        WorkflowUtility workflowUtility = getWorkflowUtility();
//        this.setVariable(workflowUtility.getFutureRequestsKey(principalId), workflowUtility.getClearFutureRequestsValue());
//    }
//
//    /**
//     * Deletes the note of with the same id as that of the argument on the document.
//     * @param noteVO the note to test for deletion
//     * @return whether the note is already marked for deletion.
//     */
//    private boolean isDeletedNote(NoteDTO noteVO) {
//    	NoteDTO[] notesToDelete = routeHeader.getNotesToDelete();
//    	if (notesToDelete != null){
//    		for (int i=0; i<notesToDelete.length; i++){
//    			if (notesToDelete[i].getNoteId().equals(noteVO.getNoteId())){
//    				return true;
//    			}
//    		}
//    	}
//    	return false;
//    }
//
//    /**
//     * Increases the size of the routeHeader notes VO array
//     */
//   private void increaseNotesArraySizeByOne() {
//	   NoteDTO[] tempArray;
//	   NoteDTO[] notes = routeHeader.getNotes();
//	   if (notes == null){
//		   tempArray = new NoteDTO[1];
//	   } else {
//		   tempArray = new NoteDTO[notes.length + 1];
//		   for (int i=0; i<notes.length; i++){
//			   tempArray[i] = notes[i];
//		   }
//	   }
//	   routeHeader.setNotes(tempArray);
//   }
//
//   /**
//    * Increases the size of the routeHeader notesToDelete VO array
//    */
//   private void increaseNotesToDeleteArraySizeByOne() {
//	   NoteDTO[] tempArray;
//	   NoteDTO[] notesToDelete = routeHeader.getNotesToDelete();
//	   if (notesToDelete == null){
//		   tempArray = new NoteDTO[1];
//	   } else {
//		   tempArray = new NoteDTO[notesToDelete.length + 1];
//		   for (int i=0; i<notesToDelete.length; i++){
//			   tempArray[i] = notesToDelete[i];
//		   }
//	   }
//	   routeHeader.setNotesToDelete(tempArray);
//   }
//   
//   //add 1 link between 2 docs by DTO, double link added
//   public void addLinkedDocument(DocumentLinkDTO docLinkVO) throws WorkflowException{
//	   try{
//		   if(DocumentLinkDTO.checkDocLink(docLinkVO))
//			   getWorkflowUtility().addDocumentLink(docLinkVO);
//	   }
//	   catch(Exception e){
//		   throw handleExceptionAsRuntime(e); 
//	   } 
//   }
//   
//   //get link from orgn doc to a specifc doc
//   public DocumentLinkDTO getLinkedDocument(DocumentLinkDTO docLinkVO) throws WorkflowException{
//	   try{
//		   if(DocumentLinkDTO.checkDocLink(docLinkVO))
//			   return getWorkflowUtility().getLinkedDocument(docLinkVO);
//		   else
//			   return null;
//	   }
//	   catch(Exception e){
//		   throw handleExceptionAsRuntime(e); 
//	   }
//   }
//   
//   //get all links to orgn doc
//   public List<DocumentLinkDTO> getLinkedDocumentsByDocId(String documentId) throws WorkflowException{
//	   if(documentId == null)
//		   throw new WorkflowException("document Id is null");
//	   try{   
//		   return getWorkflowUtility().getLinkedDocumentsByDocId(documentId);
//	   } 
//	   catch (Exception e) {
//		   throw handleExceptionAsRuntime(e);
//	   }
//   }
//   
//   //remove all links from orgn: double links removed
//   public void removeLinkedDocuments(String docId) throws WorkflowException{
//	   
//	   if(docId == null)
//		   throw new WorkflowException("doc id is null");
//	   
//	   try{   
//		   getWorkflowUtility().deleteDocumentLinksByDocId(docId);
//	   } 
//	   catch (Exception e) {
//		   throw handleExceptionAsRuntime(e);
//	   }
//   }
//   
//   //remove link between 2 docs, double link removed
//   public void removeLinkedDocument(DocumentLinkDTO docLinkVO) throws WorkflowException{
//	   
//	   try{
//		   if(DocumentLinkDTO.checkDocLink(docLinkVO))
//			   getWorkflowUtility().deleteDocumentLink(docLinkVO);
//	   }
//	   catch(Exception e){
//		   throw handleExceptionAsRuntime(e); 
//	   } 
//   }
   
   protected static class ModifiableDocumentContent implements Serializable {
	   
	   private static final long serialVersionUID = -4458431160327214042L;

	   private boolean dirty;
	   private DocumentContent originalDocumentContent;
	   private DocumentContentUpdate.Builder builder;
	   
	   protected ModifiableDocumentContent(DocumentContent documentContent) {
		   this.dirty = false;
		   this.originalDocumentContent = documentContent;
		   this.builder = DocumentContentUpdate.Builder.create(documentContent);
	   }
	   
	   protected DocumentContent getDocumentContent() {
		   if (!dirty) {
			   return originalDocumentContent;
		   }
		   DocumentContent.Builder documentContentBuilder = DocumentContent.Builder.create(originalDocumentContent);
		   documentContentBuilder.setApplicationContent(builder.getApplicationContent());
		   documentContentBuilder.setAttributeContent(builder.getApplicationContent());
		   documentContentBuilder.setSearchableContent(builder.getSearchableContent());
		   return documentContentBuilder.build();
	   }
	   
	   protected DocumentContentUpdate.Builder getBuilder() {
		   return builder;
	   }
	   
	   protected void addAttributeDefinition(WorkflowAttributeDefinition definition) {
		   builder.getAttributeDefinitions().add(definition);
		   dirty = true;
	   }
	   
	   protected void removeAttributeDefinition(WorkflowAttributeDefinition definition) {
		   builder.getAttributeDefinitions().remove(definition);
		   dirty = true;
	   }
	   
	   protected List<WorkflowAttributeDefinition> getAttributeDefinitions() {
		   return builder.getAttributeDefinitions();
	   }

	   protected void addSearchableDefinition(WorkflowAttributeDefinition definition) {
		   builder.getSearchableDefinitions().add(definition);
		   dirty = true;
	   }

	   protected void removeSearchableDefinition(WorkflowAttributeDefinition definition) {
		   builder.getSearchableDefinitions().remove(definition);
		   dirty = true;
	   }
	   
	   protected List<WorkflowAttributeDefinition> getSearchableDefinitions() {
		   return builder.getAttributeDefinitions();
	   }

	   protected void setApplicationContent(String applicationContent) {
		   builder.setApplicationContent(applicationContent);
		   dirty = true;
	   }

	   protected void setAttributeContent(String attributeContent) {
		   builder.setAttributeContent(attributeContent);
		   dirty = true;
	   }

	   public void setAttributeDefinitions(List<WorkflowAttributeDefinition> attributeDefinitions) {
		   builder.setAttributeDefinitions(attributeDefinitions);
		   dirty = true;
	   }

	   public void setSearchableContent(String searchableContent) {
		   builder.setSearchableContent(searchableContent);
		   dirty = true;
	   }

	   public void setSearchableDefinitions(List<WorkflowAttributeDefinition> searchableDefinitions) {
		   builder.setSearchableDefinitions(searchableDefinitions);
		   dirty = true;
	   }
	   
	   boolean isDirty() {
		   return dirty;
	   }
	   	   
   }

	protected static class ModifiableDocument implements Serializable {

		private static final long serialVersionUID = -3234793238863410378L;

		private boolean dirty;
		private Document originalDocument;
		private DocumentUpdate.Builder builder;

		protected ModifiableDocument(Document document) {
			this.dirty = false;
			this.originalDocument = document;
			this.builder = DocumentUpdate.Builder.create(document);
		}

		protected Document getDocument() {
			if (!dirty) {
				return originalDocument;
			}
			Document.Builder documentBuilder = Document.Builder.create(originalDocument);
			documentBuilder.setApplicationDocumentId(builder.getApplicationDocumentId());
			documentBuilder.setTitle(builder.getTitle());
			return documentBuilder.build();
		}

		protected DocumentUpdate.Builder getBuilder() {
			return builder;
		}
		
		/**
		 * Immutable value which is accessed frequently, provide direct access to it.
		 */
		protected String getDocumentId() {
			return originalDocument.getDocumentId();
		}
		
		/**
		 * Immutable value which is accessed frequently, provide direct access to it.
		 */
		protected DateTime getDateCreated() {
			return originalDocument.getDateCreated();
		}

		protected String getApplicationDocumentId() {
			return builder.getApplicationDocumentId();
		}
		
		protected void setApplicationDocumentId(String applicationDocumentId) {
			builder.setApplicationDocumentId(applicationDocumentId);
			dirty = true;
		}

		protected String getTitle() {
			return builder.getTitle();
		}
		
		protected void setTitle(String title) {
			builder.setTitle(title);
			dirty = true;
		}

		boolean isDirty() {
			return dirty;
		}

	}
   
}
