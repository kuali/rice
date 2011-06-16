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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kew.api.action.WorkflowDocumentActionsService;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContent;
import org.kuali.rice.kew.api.document.DocumentContentUpdate;
import org.kuali.rice.kew.api.document.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.document.WorkflowAttributeValidationError;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;

public class WorkflowDocument implements java.io.Serializable {

	private static final long serialVersionUID = -3672966990721719088L;

    private String principalId;
    
    private Document document;
    
    private ModifiableDocumentContent documentContent;

    public static WorkflowDocument createDocument(String principalId, String documentTypeName) {
    	return createDocument(principalId, documentTypeName, null);
    }
    
    public static WorkflowDocument createDocument(String principalId, String documentTypeName, String title) {
    	return createDocument(principalId, documentTypeName, title, null);
    }
    
	public static WorkflowDocument createDocument(String principalId, String documentTypeName, String title, DocumentContentUpdate documentContentUpdate) {
		if (StringUtils.isBlank(principalId)) {
			throw new IllegalArgumentException("principalId was null or blank");
		}
		if (StringUtils.isBlank(documentTypeName)) {
			throw new IllegalArgumentException("documentTypeName was null or blank");
		}
		Document document = getWorkflowDocumentActionsService().create(documentTypeName, principalId, title, documentContentUpdate);
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
		this.document = document;
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
    
    public String getDocumentId() {
    	return getDocument().getDocumentId();
    }
    
    public Document getDocument() {
        return document;
    }

    protected ModifiableDocumentContent getModifiableDocumentContent() {
    	if (this.documentContent == null) {
    		DocumentContent documentContent = getWorkflowDocumentService().getDocumentContent(getDocumentId());
    		if (documentContent == null) {
    			throw new IllegalStateException("Failed to load document content for documentId: " + getDocumentId());
    		}
    		this.documentContent = new ModifiableDocumentContent(documentContent);
    	}
    	return this.documentContent;
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

    public List<ActionRequest> getActionRequests() {
    	return getWorkflowDocumentService().getActionRequests(getDocumentId());
    }

    public List<ActionTaken> getActionsTaken() {
    	return getWorkflowDocumentService().getActionsTaken(getDocumentId());
    }
    
//
//    /**
//     * Sets the "application doc id" on the document
//     * @param appDocId the "application doc id" to set on the workflow document
//     */
//    public void setAppDocId(String appDocId) {
//        routeHeader.setAppDocId(appDocId);
//    }
//
//    /**
//     * Returns the "application doc id" set on this workflow document (if any)
//     * @return the "application doc id" set on this workflow document (if any)
//     */
//    public String getAppDocId() {
//        return routeHeader.getAppDocId();
//    }
//
//    /**
//     * Returns the date/time the document was created, or null if the document has not yet been created
//     * @return the date/time the document was created, or null if the document has not yet been created
//     */
//    public Timestamp getDateCreated() {
//    	if (routeHeader.getDateCreated() == null) {
//    		return null;
//    	}
//    	return new Timestamp(routeHeader.getDateCreated().getTime().getTime());
//    }
//
//    /**
//     * Returns the title of the document
//     * @return the title of the document
//     */
//    public String getTitle() {
//        return getRouteHeader().getDocTitle();
//    }
//
//    /**
//     * Performs the 'save' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @throws WorkflowException in case an error occurs saving the document
//     * @see WorkflowDocumentActions#saveDocument(UserIdDTO, RouteHeaderDTO, String)
//     */
//    public void saveDocument(String annotation) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().saveDocument(principalId, getRouteHeader(), annotation);
//    	documentContentDirty = true;
//    }
//
//    /**
//     * Performs the 'route' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @throws WorkflowException in case an error occurs routing the document
//     * @see WorkflowDocumentActions#routeDocument(UserIdDTO, RouteHeaderDTO, String)
//     */
//    public void routeDocument(String annotation) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().routeDocument(principalId, routeHeader, annotation);
//    	documentContentDirty = true;
//    }
//
//    /**
//     * Performs the 'disapprove' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @throws WorkflowException in case an error occurs disapproving the document
//     * @see WorkflowDocumentActions#disapproveDocument(UserIdDTO, RouteHeaderDTO, String)
//     */
//    public void disapprove(String annotation) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().disapproveDocument(principalId, getRouteHeader(), annotation);
//    	documentContentDirty = true;
//    }
//
//    /**
//     * Performs the 'approve' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @throws WorkflowException in case an error occurs approving the document
//     * @see WorkflowDocumentActions#approveDocument(UserIdDTO, RouteHeaderDTO, String)
//     */
//    public void approve(String annotation) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().approveDocument(principalId, getRouteHeader(), annotation);
//    	documentContentDirty = true;
//    }
//
//    /**
//     * Performs the 'cancel' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @throws WorkflowException in case an error occurs canceling the document
//     * @see WorkflowDocumentActions#cancelDocument(UserIdDTO, RouteHeaderDTO, String)
//     */
//    public void cancel(String annotation) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().cancelDocument(principalId, getRouteHeader(), annotation);
//    	documentContentDirty = true;
//    }
//
//    /**
//     * Performs the 'blanket-approve' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @throws WorkflowException in case an error occurs blanket-approving the document
//     * @see WorkflowDocumentActions#blanketApprovalToNodes(UserIdDTO, RouteHeaderDTO, String, String[])
//     */
//    public void blanketApprove(String annotation) throws WorkflowException {
//        blanketApprove(annotation, (String)null);
//    }
//
//    /**
//     * Commits any changes made to the local copy of this document to the workflow system.  If this is a new document,
//     * the document is created first.
//     * @throws WorkflowException in case an error occurs saving the document
//     * @see WorkflowDocumentActions#saveRoutingData(UserIdDTO, RouteHeaderDTO)
//     */
//    public void saveRoutingData() throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().saveRoutingData(principalId, getRouteHeader());
//    	documentContentDirty = true;
//    }
//
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
//    
//    /**
//     * Performs the 'acknowledge' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @throws WorkflowException in case an error occurs acknowledging the document
//     * @see WorkflowDocumentActions#acknowledgeDocument(UserIdDTO, RouteHeaderDTO, String)
//     */
//    public void acknowledge(String annotation) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().acknowledgeDocument(principalId, getRouteHeader(), annotation);
//    	documentContentDirty = true;
//    }
//
//    /**
//     * Performs the 'fyi' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @throws WorkflowException in case an error occurs fyi-ing the document
//     */
//    public void fyi() throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().clearFYIDocument(principalId, getRouteHeader());
//    	documentContentDirty = true;
//    }
//
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
//
//    /**
//     * Sends an ad hoc request to the specified user at the current active node on the document.  If the document is
//     * in a terminal state, the request will be attached to the terminal node.
//     */
//    public void adHocRouteDocumentToPrincipal(String actionRequested, String annotation, String principalId, String responsibilityDesc, boolean forceAction) throws WorkflowException {
//    	adHocRouteDocumentToPrincipal(actionRequested, null, annotation, principalId, responsibilityDesc, forceAction);
//    }
//
//    /**
//     * Sends an ad hoc request to the specified user at the specified node on the document.  If the document is
//     * in a terminal state, the request will be attached to the terminal node.
//     */
//    public void adHocRouteDocumentToPrincipal(String actionRequested, String nodeName, String annotation, String principalId, String responsibilityDesc, boolean forceAction) throws WorkflowException {
//    	adHocRouteDocumentToPrincipal(actionRequested, nodeName, annotation, principalId, responsibilityDesc, forceAction, null);
//    }
//
//    /**
//     * Sends an ad hoc request to the specified user at the specified node on the document.  If the document is
//     * in a terminal state, the request will be attached to the terminal node.
//     */
//    public void adHocRouteDocumentToPrincipal(String actionRequested, String nodeName, String annotation, String principalId, String responsibilityDesc, boolean forceAction, String requestLabel) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().adHocRouteDocumentToPrincipal(principalId, getRouteHeader(), actionRequested, nodeName, annotation, principalId, responsibilityDesc, forceAction, requestLabel);
//    	documentContentDirty = true;
//    }
//
//    /**
//     * Sends an ad hoc request to the specified workgroup at the current active node on the document.  If the document is
//     * in a terminal state, the request will be attached to the terminal node.
//     */
//    public void adHocRouteDocumentToGroup(String actionRequested, String annotation, String groupId, String responsibilityDesc, boolean forceAction) throws WorkflowException {
//    	adHocRouteDocumentToGroup(actionRequested, null, annotation, groupId, responsibilityDesc, forceAction);
//    }
//
//    /**
//     * Sends an ad hoc request to the specified workgroup at the specified node on the document.  If the document is
//     * in a terminal state, the request will be attached to the terminal node.
//     */
//    public void adHocRouteDocumentToGroup(String actionRequested, String nodeName, String annotation, String groupId, String responsibilityDesc, boolean forceAction) throws WorkflowException {
//    	adHocRouteDocumentToGroup(actionRequested, nodeName, annotation, groupId, responsibilityDesc, forceAction, null);
//    }
//
//    /**
//     * Sends an ad hoc request to the specified workgroup at the specified node on the document.  If the document is
//     * in a terminal state, the request will be attached to the terminal node.
//     */
//    public void adHocRouteDocumentToGroup(String actionRequested, String nodeName, String annotation, String groupId, String responsibilityDesc, boolean forceAction, String requestLabel) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().adHocRouteDocumentToGroup(principalId, getRouteHeader(), actionRequested, nodeName, annotation, groupId, responsibilityDesc, forceAction, requestLabel);
//    	documentContentDirty = true;
//    }
//
//    /**
//     * Revokes AdHoc request(s) according to the given AdHocRevokeVO which is passed in.
//     *
//     * If a specific action request ID is specified on the revoke bean, and that ID is not a valid ID, this method should throw a
//     * WorkflowException.
//     * @param revoke AdHocRevokeVO
//     * @param annotation message to note for this action
//     * @throws WorkflowException if an error occurs revoking adhoc requests
//     * @see WorkflowDocumentActions#revokeAdHocRequests(UserIdDTO, RouteHeaderDTO, AdHocRevokeDTO, String)
//     */
//    public void revokeAdHocRequests(AdHocRevokeDTO revoke, String annotation) throws WorkflowException {
//    	if (getRouteHeader().getDocumentId() == null) {
//    		throw new WorkflowException("Can't revoke request, the workflow document has not yet been created!");
//    	}
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().revokeAdHocRequests(principalId, getRouteHeader(), revoke, annotation);
//    	documentContentDirty = true;
//    }
//
//    /**
//     * Sets the title of the document, empty string if null is specified.
//     * @param title title of the document to set, or null
//     */
//    // WorkflowException is declared but not thrown...
//    public void setTitle(String title) throws WorkflowException {
//        if (title == null) {
//            title = "";
//        }
//        if (title.length() > KEWConstants.TITLE_MAX_LENGTH) {
//            title = title.substring(0, KEWConstants.TITLE_MAX_LENGTH);
//        }
//        getRouteHeader().setDocTitle(title);
//    }
//
//    /**
//     * Returns the document type of the workflow document
//     * @return the document type of the workflow document
//     * @throws RuntimeException if document does not exist (is not yet created)
//     * @see RouteHeaderDTO#getDocTypeName()
//     */
//    public String getDocumentType() {
//        if (getRouteHeader() == null) {
//            // HACK: FIXME: we should probably proscribe, or at least handle consistently, these corner cases
//            // NPEs are not nice
//            throw new RuntimeException("No such document!");
//        }
//        return getRouteHeader().getDocTypeName();
//    }
//
//    /**
//     * Returns whether an acknowledge is requested of the user for this document.  This is
//     * a convenience method that delegates to {@link RouteHeaderDTO#isAckRequested()}.
//     * @return whether an acknowledge is requested of the user for this document
//     * @see RouteHeaderDTO#isAckRequested()
//     */
//    public boolean isAcknowledgeRequested() {
//        return getRouteHeader().isAckRequested();
//    }
//
//    /**
//     * Returns whether an approval is requested of the user for this document.  This is
//     * a convenience method that delegates to {@link RouteHeaderDTO#isApproveRequested()}.
//     * @return whether an approval is requested of the user for this document
//     * @see RouteHeaderDTO#isApproveRequested()
//     */
//    public boolean isApprovalRequested() {
//        return getRouteHeader().isApproveRequested();
//    }
//
//    /**
//     * Returns whether a completion is requested of the user for this document.  This is
//     * a convenience method that delegates to {@link RouteHeaderDTO#isCompleteRequested()}.
//     * @return whether an approval is requested of the user for this document
//     * @see RouteHeaderDTO#isCompleteRequested()
//     */
//    public boolean isCompletionRequested() {
//        return getRouteHeader().isCompleteRequested();
//    }
//
//    /**
//     * Returns whether an FYI is requested of the user for this document.  This is
//     * a convenience method that delegates to {@link RouteHeaderDTO#isFyiRequested()}.
//     * @return whether an FYI is requested of the user for this document
//     * @see RouteHeaderDTO#isFyiRequested()
//     */
//    public boolean isFYIRequested() {
//        return getRouteHeader().isFyiRequested();
//    }
//
//    /**
//     * Returns whether the user can blanket approve the document
//     * @return whether the user can blanket approve the document
//     * @see RouteHeaderDTO#getValidActions()
//     */
//    public boolean isBlanketApproveCapable() {
//        // TODO delyea - refactor this to take into account non-initiator owned documents
//    	return getRouteHeader().getValidActions().contains(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD) && (isCompletionRequested() || isApprovalRequested() || stateIsInitiated());
//    }
//
//    /**
//     * Returns whether the specified action code is valid for the current user and document
//     * @return whether the user can blanket approve the document
//     * @see RouteHeaderDTO#getValidActions()
//     */
//    public boolean isActionCodeValidForDocument(String actionTakenCode) {
//    	return getRouteHeader().getValidActions().contains(actionTakenCode);
//    }
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
//
//    /**
//     * Performs the 'complete' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @throws WorkflowException in case an error occurs clearing completing the document
//     * @see WorkflowDocumentActions#completeDocument(UserIdDTO, RouteHeaderDTO, String)
//     */
//    public void complete(String annotation) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().completeDocument(principalId, getRouteHeader(), annotation);
//    	documentContentDirty = true;
//    }
//
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
//    /**
//     * Indicates if the document is in the initiated state or not.
//     *
//     * @return true if in the specified state
//     */
//    public boolean stateIsInitiated() {
//        return KEWConstants.ROUTE_HEADER_INITIATED_CD.equals(getRouteHeader().getDocRouteStatus());
//    }
//
//    /**
//     * Indicates if the document is in the saved state or not.
//     *
//     * @return true if in the specified state
//     */
//    public boolean stateIsSaved() {
//        return KEWConstants.ROUTE_HEADER_SAVED_CD.equals(getRouteHeader().getDocRouteStatus());
//    }
//
//    /**
//     * Indicates if the document is in the enroute state or not.
//     *
//     * @return true if in the specified state
//     */
//    public boolean stateIsEnroute() {
//        return KEWConstants.ROUTE_HEADER_ENROUTE_CD.equals(getRouteHeader().getDocRouteStatus());
//    }
//
//    /**
//     * Indicates if the document is in the exception state or not.
//     *
//     * @return true if in the specified state
//     */
//    public boolean stateIsException() {
//        return KEWConstants.ROUTE_HEADER_EXCEPTION_CD.equals(getRouteHeader().getDocRouteStatus());
//    }
//
//    /**
//     * Indicates if the document is in the canceled state or not.
//     *
//     * @return true if in the specified state
//     */
//    public boolean stateIsCanceled() {
//        return KEWConstants.ROUTE_HEADER_CANCEL_CD.equals(getRouteHeader().getDocRouteStatus());
//    }
//
//    /**
//     * Indicates if the document is in the disapproved state or not.
//     *
//     * @return true if in the specified state
//     */
//    public boolean stateIsDisapproved() {
//        return KEWConstants.ROUTE_HEADER_DISAPPROVED_CD.equals(getRouteHeader().getDocRouteStatus());
//    }
//
//    /**
//     * Indicates if the document is in the approved state or not. Will answer true is document is in Processed or Finalized state.
//     *
//     * @return true if in the specified state
//     */
//    public boolean stateIsApproved() {
//        return KEWConstants.ROUTE_HEADER_APPROVED_CD.equals(getRouteHeader().getDocRouteStatus()) || stateIsProcessed() || stateIsFinal();
//    }
//
//    /**
//     * Indicates if the document is in the processed state or not.
//     *
//     * @return true if in the specified state
//     */
//    public boolean stateIsProcessed() {
//        return KEWConstants.ROUTE_HEADER_PROCESSED_CD.equals(getRouteHeader().getDocRouteStatus());
//    }
//
//    /**
//     * Indicates if the document is in the final state or not.
//     *
//     * @return true if in the specified state
//     */
//    public boolean stateIsFinal() {
//        return KEWConstants.ROUTE_HEADER_FINAL_CD.equals(getRouteHeader().getDocRouteStatus());
//    }
//
//    /**
//     * Returns the display value of the current document status
//     * @return the display value of the current document status
//     */
//    public String getStatusDisplayValue() {
//        return (String) KEWConstants.DOCUMENT_STATUSES.get(getRouteHeader().getDocRouteStatus());
//    }
//
//    /**
//     * Returns the principalId with which this WorkflowDocument was constructed
//     * @return the principalId with which this WorkflowDocument was constructed
//     */
//    public String getPrincipalId() {
//        return principalId;
//    }
//
//    /**
//     * Sets the principalId under which actions against this document should be taken
//     * @param principalId principalId under which actions against this document should be taken
//     */
//    public void setPrincipalId(String principalId) {
//        this.principalId = principalId;
//    }
//
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
//
//    /**
//     * Returns names of all active nodes the document is currently at.
//     *
//     * @return names of all active nodes the document is currently at.
//     * @throws WorkflowException if there is an error obtaining the currently active nodes on the document
//     * @see WorkflowUtility#getActiveNodeInstances(Long)
//     */
//    public String[] getNodeNames() throws WorkflowException {
//    	RouteNodeInstanceDTO[] activeNodeInstances = getWorkflowUtility().getActiveNodeInstances(getDocumentId());
//    	String[] nodeNames = new String[(activeNodeInstances == null ? 0 : activeNodeInstances.length)];
//    	for (int index = 0; index < activeNodeInstances.length; index++) {
//    		nodeNames[index] = activeNodeInstances[index].getName();
//    	}
//    	return nodeNames;
//    }
//
//    /**
//     * Performs the 'returnToPrevious' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @param nodeName the node to return to
//     * @throws WorkflowException in case an error occurs returning to previous node
//     * @see WorkflowDocumentActions#returnDocumentToPreviousNode(UserIdDTO, RouteHeaderDTO, ReturnPointDTO, String)
//     */
//    public void returnToPreviousNode(String annotation, String nodeName) throws WorkflowException {
//        ReturnPointDTO returnPoint = new ReturnPointDTO(nodeName);
//        returnToPreviousNode(annotation, returnPoint);
//    }
//
//    /**
//     * Performs the 'returnToPrevious' action on the document this WorkflowDocument represents.  If this is a new document,
//     * the document is created first.
//     * @param annotation the message to log for the action
//     * @param ReturnPointDTO the node to return to
//     * @throws WorkflowException in case an error occurs returning to previous node
//     * @see WorkflowDocumentActions#returnDocumentToPreviousNode(UserIdDTO, RouteHeaderDTO, ReturnPointDTO, String)
//     */
//    public void returnToPreviousNode(String annotation, ReturnPointDTO returnPoint) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader = getWorkflowDocumentActions().returnDocumentToPreviousNode(principalId, getRouteHeader(), returnPoint, annotation);
//    	documentContentDirty = true;
//    }
//
//    /**
//     * Moves the document from a current node in it's route to another node.  If this is a new document,
//     * the document is created first.
//     * @param MovePointDTO VO representing the node at which to start, and the number of steps to move (negative steps is reverse)
//     * @param annotation the message to log for the action
//     * @throws WorkflowException in case an error occurs moving the document
//     * @see WorkflowDocumentActions#moveDocument(UserIdDTO, RouteHeaderDTO, MovePointDTO, String)
//     */
//    public void moveDocument(MovePointDTO movePoint, String annotation) throws WorkflowException {
//    	createDocumentIfNeccessary();
//    	routeHeader =  getWorkflowDocumentActions().moveDocument(principalId, getRouteHeader(), movePoint, annotation);
//    	documentContentDirty = true;
//    }
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

}
