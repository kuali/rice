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
package org.kuali.rice.kew.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.ActionTakenDTO;
import org.kuali.rice.kew.dto.AdHocRevokeDTO;
import org.kuali.rice.kew.dto.DocumentContentDTO;
import org.kuali.rice.kew.dto.DocumentDetailDTO;
import org.kuali.rice.kew.dto.DocumentLinkDTO;
import org.kuali.rice.kew.dto.ModifiableDocumentContentDTO;
import org.kuali.rice.kew.dto.MovePointDTO;
import org.kuali.rice.kew.dto.NoteDTO;
import org.kuali.rice.kew.dto.ReturnPointDTO;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.dto.RouteNodeInstanceDTO;
import org.kuali.rice.kew.dto.UserIdDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeValidationErrorDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;

/**
 * Represents a document in Workflow from the perspective of the client.  This class is one of two
 * (Java) client interfaces to the KEW system (the other being {@link WorkflowInfo} class).  The
 * first time an instance of this class is created, it will read the client configuration to determine
 * how to connect to KEW.
 *
 * <p>This class is used by creating new instances using the appropriate constructor.  To create a new
 * document in KEW, create an instance of this class passing a UserIdVO and a
 * document type name.  To load an existing document, create an instance of this class passing a
 * UserIdVO and a document ID number.
 *
 * <p>Internally, this wrapper interacts with the {@link WorkflowDocumentActions} service exported
 * over the KSB, maintaining state.
 *
 * <p>This class is not thread safe and must by synchronized externally for concurrent access.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkflowDocument implements java.io.Serializable {

	private static final long serialVersionUID = -3672966990721719088L;

	/**
	 * The principal ID of the user as whom actions will be taken on the KEW document
	 */
    private String principalId;
    /**
     * RouteHeader VO of the KEW document this WorkflowDocument represents
     */
    private RouteHeaderDTO routeHeader;
    /**
     * Flag that indicates whether the document content currently loaded needs to be refreshed.
     * This is the case either if the document content has not yet been loaded, or an action
     * that might possibly affect the document content (which is potentially any action) has
     * subsequently been taken on the document through this API.
     */
    private boolean documentContentDirty = false;
    /**
     * Value Object encapsulating the document content
     */
    private ModifiableDocumentContentDTO documentContent;

    /**
     * This method constructs a WorkflowDocument representing a new document in the 
     * workflow system.  Creation/committing of the new document is deferred until
     *  the first action is taken on the document.
     * 
     * @param principalId the user as which to take actions on the document
     * @param documentType the type of the document to create
     * @throws WorkflowException if anything goes awry
     */
	public static WorkflowDocument createDocument(String principalId, String documentType) throws WorkflowException {
		return new WorkflowDocument(principalId, documentType, null);
	}
	
    /**
     * This method loads a workflow document with the given document ID for the 
     * given principalId.  If no document can be found with the 
     * given ID, then the {@link getRouteHeader()} method of the WorkflowDocument
     * which is created will return null.
     * 
     * @param principalId the user as which to take actions on the document
     * @param documentId the document id of the document to load
     * @throws WorkflowException if anything goes awry
     */
	public static WorkflowDocument loadDocument(String principalId, String documentId) throws WorkflowException {
		return new WorkflowDocument(principalId, null, documentId);
	}
	
	protected WorkflowDocument(String principalId, String documentType, String documentId) throws WorkflowException {
        init(principalId, documentType, documentId);
    }
    
    /**
     * Initializes this WorkflowDocument object, by either attempting to load an existing document by documentId
     * if one is supplied (non-null), or by constructing an empty document of the specified type.
     * @param principalId the user under which actions via this API on the specified document will be taken
     * @param documentType the type of document this WorkflowDocument should represent (either this parameter or documentId must be specified, non-null)
     * @param documentId the id of an existing document to load (either this parameter or documentType must be specified, non-null)
     * @throws WorkflowException if a documentId is specified but an exception occurs trying to load the document route header
     */
    private void init(String principalId, String documentType, String documentId) throws WorkflowException {
    	this.principalId = principalId;
    	routeHeader = new RouteHeaderDTO();
    	routeHeader.setDocTypeName(documentType);
    	if (documentId != null) {
    		routeHeader = getWorkflowUtility().getRouteHeaderWithPrincipal(principalId, documentId);
    	}
    }

    /**
     * Retrieves the WorkflowUtility proxy from the locator.  The locator will cache this for us.
     */
    private WorkflowUtility getWorkflowUtility() throws WorkflowException {
        WorkflowUtility workflowUtility = 
        	(WorkflowUtility)GlobalResourceLoader.getService(KEWConstants.WORKFLOW_UTILITY_SERVICE);
    	if (workflowUtility == null) {
    		throw new WorkflowException("Could not locate the WorkflowUtility service.  Please ensure that KEW client is configured properly!");
    	}
    	return workflowUtility;

    }

    /**
     * Retrieves the WorkflowDocumentActions proxy from the locator.  The locator will cache this for us.
     */
    private WorkflowDocumentActions getWorkflowDocumentActions() throws WorkflowException {
    	WorkflowDocumentActions workflowDocumentActions = 
    		(WorkflowDocumentActions) GlobalResourceLoader.getService(KEWConstants.WORKFLOW_DOCUMENT_ACTIONS_SERVICE);
    	if (workflowDocumentActions == null) {
    		throw new WorkflowException("Could not locate the WorkflowDocumentActions service.  Please ensure that KEW client is configured properly!");
    	}
    	return workflowDocumentActions;
    }

    // ########################
    // Document Content methods
    // ########################

    /**
     * Returns an up-to-date DocumentContent of this document.
     * @see WorkflowUtility#getDocumentContent(Long)
     */
    public DocumentContentDTO getDocumentContent() {
    	try {
    		// create the document if it hasn't already been created
    		if (getRouteHeader().getDocumentId() == null) {
        		routeHeader = getWorkflowDocumentActions().createDocument(principalId, getRouteHeader());
        	}
    		if (documentContent == null || documentContentDirty) {
    			documentContent = new ModifiableDocumentContentDTO(getWorkflowUtility().getDocumentContent(routeHeader.getDocumentId()));
    			documentContentDirty = false;
    		}
    	} catch (Exception e) {
    		throw handleExceptionAsRuntime(e);
    	}
    	return documentContent;
    }

    /**
     * Returns the application specific section of the document content. This is
     * a convenience method that delegates to the {@link DocumentContentDTO}.
     *
     * For documents routed prior to Workflow 2.0:
     * If the application did NOT use attributes for XML generation, this method will
     * return the entire document content XML.  Otherwise it will return the empty string.
     * @see DocumentContentDTO#getApplicationContent()
     */
    public String getApplicationContent() {
        return getDocumentContent().getApplicationContent();
    }

    /**
     * Sets the application specific section of the document content. This is
     * a convenience method that delegates to the {@link DocumentContentDTO}.
     */
    public void setApplicationContent(String applicationContent) {
        getDocumentContent().setApplicationContent(applicationContent);
    }

    /**
     * Clears all attribute document content from the document.
     * Typically, this will be used if it is necessary to update the attribute doc content on
     * the document.  This can be accomplished by clearing the content and then adding the
     * desired attribute definitions.
     *
     * This is a convenience method that delegates to the {@link DocumentContentDTO}.
     *
     * In order for these changes to take effect, an action must be performed on the document (such as "save").
     */
    public void clearAttributeContent() {
        getDocumentContent().setAttributeContent("");
    }

    /**
     * Returns the attribute-generated section of the document content. This is
     * a convenience method that delegates to the {@link DocumentContentDTO}.
     * @see DocumentContentDTO#getAttributeContent()
     */
    public String getAttributeContent() {
        return getDocumentContent().getAttributeContent();
    }

    /**
     * Adds an attribute definition which defines creation parameters for a WorkflowAttribute
     * implementation.  The created attribute will be used to generate attribute document content.
     * When the document is sent to the server, this will be appended to the existing attribute
     * doc content.  If it is required to replace the attribute document content, then the
     * clearAttributeContent() method should be invoked prior to adding attribute definitions.
     *
     * This is a convenience method that delegates to the {@link DocumentContentDTO}.
     * @see DocumentContentDTO#addAttributeDefinition(WorkflowAttributeDefinitionDTO)
     */
    public void addAttributeDefinition(WorkflowAttributeDefinitionDTO attributeDefinition) {
        getDocumentContent().addAttributeDefinition(attributeDefinition);
    }

    /**
     * Validate the WorkflowAttributeDefinition against it's attribute on the server.  This will validate
     * the inputs that will eventually become xml.
     *
     * Only applies to attributes implementing WorkflowAttributeXmlValidator.
     *
     * This is a call through to the WorkflowInfo object and is here for convenience.
     *
     * @param attributeDefinition the workflow attribute definition VO to validate
     * @return WorkflowAttributeValidationErrorVO[] of error from the attribute
     * @throws WorkflowException when attribute doesn't implement WorkflowAttributeXmlValidator
     * @see WorkflowUtility#validateWorkflowAttributeDefinitionVO(WorkflowAttributeDefinitionDTO)
     */
    public WorkflowAttributeValidationErrorDTO[] validateAttributeDefinition(WorkflowAttributeDefinitionDTO attributeDefinition) throws WorkflowException {
    	return getWorkflowUtility().validateWorkflowAttributeDefinitionVO(attributeDefinition);
    }

    /**
     * Removes an attribute definition from the document content.  This is
     * a convenience method that delegates to the {@link DocumentContentDTO}.
     * @param attributeDefinition the attribute definition VO to remove
     */
    public void removeAttributeDefinition(WorkflowAttributeDefinitionDTO attributeDefinition) {
        getDocumentContent().removeAttributeDefinition(attributeDefinition);
    }

    /**
     * Removes all attribute definitions from the document content. This is
     * a convenience method that delegates to the {@link DocumentContentDTO}.
     */
    public void clearAttributeDefinitions() {
    	getDocumentContent().setAttributeDefinitions(new WorkflowAttributeDefinitionDTO[0]);
    }

    /**
     * Returns the attribute definition VOs currently defined on the document content. This is
     * a convenience method that delegates to the {@link DocumentContentDTO}.
     * @return the attribute definition VOs currently defined on the document content.
     * @see DocumentContentDTO#getAttributeDefinitions()
     */
    public WorkflowAttributeDefinitionDTO[] getAttributeDefinitions() {
        return getDocumentContent().getAttributeDefinitions();
    }

    /**
     * Adds a searchable attribute definition which defines creation parameters for a SearchableAttribute
     * implementation.  The created attribute will be used to generate searchable document content.
     * When the document is sent to the server, this will be appended to the existing searchable
     * doc content.  If it is required to replace the searchable document content, then the
     * clearSearchableContent() method should be invoked prior to adding definitions. This is
     * a convenience method that delegates to the {@link DocumentContentDTO}.
     */
    public void addSearchableDefinition(WorkflowAttributeDefinitionDTO searchableDefinition) {
        getDocumentContent().addSearchableDefinition(searchableDefinition);
    }

    /**
     * Removes a searchable attribute definition from the document content. This is
     * a convenience method that delegates to the {@link DocumentContentDTO}.
     * @param searchableDefinition the searchable attribute definition to remove
     */
    public void removeSearchableDefinition(WorkflowAttributeDefinitionDTO searchableDefinition) {
        getDocumentContent().removeSearchableDefinition(searchableDefinition);
    }

    /**
     * Removes all searchable attribute definitions from the document content. This is
     * a convenience method that delegates to the {@link DocumentContentDTO}.
     */
    public void clearSearchableDefinitions() {
        getDocumentContent().setSearchableDefinitions(new WorkflowAttributeDefinitionDTO[0]);
    }

    /**
     * Clears the searchable content from the document content. This is
     * a convenience method that delegates to the {@link DocumentContentDTO}.
     */
    public void clearSearchableContent() {
    	getDocumentContent().setSearchableContent("");
    }

    /**
     * Returns the searchable attribute definitions on the document content. This is
     * a convenience method that delegates to the {@link DocumentContentDTO}.
     * @return the searchable attribute definitions on the document content.
     * @see DocumentContentDTO#getSearchableDefinitions()
     */
    public WorkflowAttributeDefinitionDTO[] getSearchableDefinitions() {
        return getDocumentContent().getSearchableDefinitions();
    }

    // ########################
    // END Document Content methods
    // ########################

    /**
     * Returns the RouteHeaderVO for the workflow document this WorkflowDocument represents
     */
    public RouteHeaderDTO getRouteHeader() {
        return routeHeader;
    }

    /**
     * Returns the id of the workflow document this WorkflowDocument represents.  If this is a new document
     * that has not yet been created, the document is first created (and therefore this will return a new id)
     * @return the id of the workflow document this WorkflowDocument represents
     * @throws WorkflowException if an error occurs during document creation
     */
    public String getDocumentId() throws WorkflowException {
    	createDocumentIfNeccessary();
    	return getRouteHeader().getDocumentId();
    }

    /**
     * Returns VOs of the pending ActionRequests on this document.  If this object represents a new document
     * that has not yet been created, then an empty array will be returned.  The ordering of ActionRequests
     * returned by this method is not guaranteed.
     *
     * This method relies on the WorkflowUtility service
     *
     * @return VOs of the pending ActionRequests on this document
     * @throws WorkflowException if an error occurs obtaining the pending action requests for this document
     * @see WorkflowUtility#getActionRequests(Long)
     */
    public ActionRequestDTO[] getActionRequests() throws WorkflowException {
        if (getDocumentId() == null) {
            return new ActionRequestDTO[0];
        }
        return getWorkflowUtility().getAllActionRequests(getDocumentId());
    }

    /**
     * Returns VOs of the actions taken on this document.  If this object represents a new document
     * that has not yet been created, then an empty array will be returned.  The ordering of actions taken
     * returned by this method is not guaranteed.
     *
     * This method relies on the WorkflowUtility service
     *
     * @return VOs of the actions that have been taken on this document
     * @throws WorkflowException if an error occurs obtaining the actions taken on this document
     * @see WorkflowUtility#getActionsTaken(Long)
     */
    public ActionTakenDTO[] getActionsTaken() throws WorkflowException {
        if (getDocumentId() == null) {
            return new ActionTakenDTO[0];
        }
        return getWorkflowUtility().getActionsTaken(getDocumentId());
    }

    /**
     * Sets the "application doc id" on the document
     * @param appDocId the "application doc id" to set on the workflow document
     */
    public void setAppDocId(String appDocId) {
        routeHeader.setAppDocId(appDocId);
    }

    /**
     * Returns the "application doc id" set on this workflow document (if any)
     * @return the "application doc id" set on this workflow document (if any)
     */
    public String getAppDocId() {
        return routeHeader.getAppDocId();
    }

    /**
     * Returns the date/time the document was created, or null if the document has not yet been created
     * @return the date/time the document was created, or null if the document has not yet been created
     */
    public Timestamp getDateCreated() {
    	if (routeHeader.getDateCreated() == null) {
    		return null;
    	}
    	return new Timestamp(routeHeader.getDateCreated().getTime().getTime());
    }

    /**
     * Returns the title of the document
     * @return the title of the document
     */
    public String getTitle() {
        return getRouteHeader().getDocTitle();
    }

    /**
     * Performs the 'save' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs saving the document
     * @see WorkflowDocumentActions#saveDocument(UserIdDTO, RouteHeaderDTO, String)
     */
    public void saveDocument(String annotation) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().saveDocument(principalId, getRouteHeader(), annotation);
    	documentContentDirty = true;
    }

    /**
     * Performs the 'route' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs routing the document
     * @see WorkflowDocumentActions#routeDocument(UserIdDTO, RouteHeaderDTO, String)
     */
    public void routeDocument(String annotation) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().routeDocument(principalId, routeHeader, annotation);
    	documentContentDirty = true;
    }

    /**
     * Performs the 'disapprove' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs disapproving the document
     * @see WorkflowDocumentActions#disapproveDocument(UserIdDTO, RouteHeaderDTO, String)
     */
    public void disapprove(String annotation) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().disapproveDocument(principalId, getRouteHeader(), annotation);
    	documentContentDirty = true;
    }

    /**
     * Performs the 'approve' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs approving the document
     * @see WorkflowDocumentActions#approveDocument(UserIdDTO, RouteHeaderDTO, String)
     */
    public void approve(String annotation) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().approveDocument(principalId, getRouteHeader(), annotation);
    	documentContentDirty = true;
    }

    /**
     * Performs the 'cancel' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs canceling the document
     * @see WorkflowDocumentActions#cancelDocument(UserIdDTO, RouteHeaderDTO, String)
     */
    public void cancel(String annotation) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().cancelDocument(principalId, getRouteHeader(), annotation);
    	documentContentDirty = true;
    }

    /**
     * Performs the 'blanket-approve' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs blanket-approving the document
     * @see WorkflowDocumentActions#blanketApprovalToNodes(UserIdDTO, RouteHeaderDTO, String, String[])
     */
    public void blanketApprove(String annotation) throws WorkflowException {
        blanketApprove(annotation, (String)null);
    }

    /**
     * Commits any changes made to the local copy of this document to the workflow system.  If this is a new document,
     * the document is created first.
     * @throws WorkflowException in case an error occurs saving the document
     * @see WorkflowDocumentActions#saveRoutingData(UserIdDTO, RouteHeaderDTO)
     */
    public void saveRoutingData() throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().saveRoutingData(principalId, getRouteHeader());
    	documentContentDirty = true;
    }

    /**
     * 
     * This method sets the Application Document Status and then calls saveRoutingData() to commit 
     * the changes to the workflow system.
     * 
     * @param appDocStatus
     * @throws WorkflowException
     */
    public void updateAppDocStatus(String appDocStatus) throws WorkflowException {
       	getRouteHeader().setAppDocStatus(appDocStatus);
       	saveRoutingData();
    }
    
    /**
     * Performs the 'acknowledge' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs acknowledging the document
     * @see WorkflowDocumentActions#acknowledgeDocument(UserIdDTO, RouteHeaderDTO, String)
     */
    public void acknowledge(String annotation) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().acknowledgeDocument(principalId, getRouteHeader(), annotation);
    	documentContentDirty = true;
    }

    /**
     * Performs the 'fyi' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs fyi-ing the document
     */
    public void fyi() throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().clearFYIDocument(principalId, getRouteHeader());
    	documentContentDirty = true;
    }

    /**
     * Performs the 'delete' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs deleting the document
     * @see WorkflowDocumentActions#deleteDocument(UserIdDTO, RouteHeaderDTO)
     */
    public void delete() throws WorkflowException {
    	createDocumentIfNeccessary();
    	getWorkflowDocumentActions().deleteDocument(principalId, getRouteHeader());
    	documentContentDirty = true;
    }

    /**
     * Reloads the document route header.  If this is a new document, the document is created first.
     * Next time document content is accessed, an up-to-date copy will be retrieved from workflow.
     * @throws WorkflowException in case an error occurs retrieving the route header
     */
    public void refreshContent() throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowUtility().getRouteHeader(getDocumentId());
    	documentContentDirty = true;
    }

    /**
     * Sends an ad hoc request to the specified user at the current active node on the document.  If the document is
     * in a terminal state, the request will be attached to the terminal node.
     */
    public void adHocRouteDocumentToPrincipal(String actionRequested, String annotation, String principalId, String responsibilityDesc, boolean forceAction) throws WorkflowException {
    	adHocRouteDocumentToPrincipal(actionRequested, null, annotation, principalId, responsibilityDesc, forceAction);
    }

    /**
     * Sends an ad hoc request to the specified user at the specified node on the document.  If the document is
     * in a terminal state, the request will be attached to the terminal node.
     */
    public void adHocRouteDocumentToPrincipal(String actionRequested, String nodeName, String annotation, String principalId, String responsibilityDesc, boolean forceAction) throws WorkflowException {
    	adHocRouteDocumentToPrincipal(actionRequested, nodeName, annotation, principalId, responsibilityDesc, forceAction, null);
    }

    /**
     * Sends an ad hoc request to the specified user at the specified node on the document.  If the document is
     * in a terminal state, the request will be attached to the terminal node.
     */
    public void adHocRouteDocumentToPrincipal(String actionRequested, String nodeName, String annotation, String principalId, String responsibilityDesc, boolean forceAction, String requestLabel) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().adHocRouteDocumentToPrincipal(principalId, getRouteHeader(), actionRequested, nodeName, annotation, principalId, responsibilityDesc, forceAction, requestLabel);
    	documentContentDirty = true;
    }

    /**
     * Sends an ad hoc request to the specified workgroup at the current active node on the document.  If the document is
     * in a terminal state, the request will be attached to the terminal node.
     */
    public void adHocRouteDocumentToGroup(String actionRequested, String annotation, String groupId, String responsibilityDesc, boolean forceAction) throws WorkflowException {
    	adHocRouteDocumentToGroup(actionRequested, null, annotation, groupId, responsibilityDesc, forceAction);
    }

    /**
     * Sends an ad hoc request to the specified workgroup at the specified node on the document.  If the document is
     * in a terminal state, the request will be attached to the terminal node.
     */
    public void adHocRouteDocumentToGroup(String actionRequested, String nodeName, String annotation, String groupId, String responsibilityDesc, boolean forceAction) throws WorkflowException {
    	adHocRouteDocumentToGroup(actionRequested, nodeName, annotation, groupId, responsibilityDesc, forceAction, null);
    }

    /**
     * Sends an ad hoc request to the specified workgroup at the specified node on the document.  If the document is
     * in a terminal state, the request will be attached to the terminal node.
     */
    public void adHocRouteDocumentToGroup(String actionRequested, String nodeName, String annotation, String groupId, String responsibilityDesc, boolean forceAction, String requestLabel) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().adHocRouteDocumentToGroup(principalId, getRouteHeader(), actionRequested, nodeName, annotation, groupId, responsibilityDesc, forceAction, requestLabel);
    	documentContentDirty = true;
    }

    /**
     * Revokes AdHoc request(s) according to the given AdHocRevokeVO which is passed in.
     *
     * If a specific action request ID is specified on the revoke bean, and that ID is not a valid ID, this method should throw a
     * WorkflowException.
     * @param revoke AdHocRevokeVO
     * @param annotation message to note for this action
     * @throws WorkflowException if an error occurs revoking adhoc requests
     * @see WorkflowDocumentActions#revokeAdHocRequests(UserIdDTO, RouteHeaderDTO, AdHocRevokeDTO, String)
     */
    public void revokeAdHocRequests(AdHocRevokeDTO revoke, String annotation) throws WorkflowException {
    	if (getRouteHeader().getDocumentId() == null) {
    		throw new WorkflowException("Can't revoke request, the workflow document has not yet been created!");
    	}
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().revokeAdHocRequests(principalId, getRouteHeader(), revoke, annotation);
    	documentContentDirty = true;
    }

    /**
     * Sets the title of the document, empty string if null is specified.
     * @param title title of the document to set, or null
     */
    // WorkflowException is declared but not thrown...
    public void setTitle(String title) throws WorkflowException {
        if (title == null) {
            title = "";
        }
        if (title.length() > KEWConstants.TITLE_MAX_LENGTH) {
            title = title.substring(0, KEWConstants.TITLE_MAX_LENGTH);
        }
        getRouteHeader().setDocTitle(title);
    }

    /**
     * Returns the document type of the workflow document
     * @return the document type of the workflow document
     * @throws RuntimeException if document does not exist (is not yet created)
     * @see RouteHeaderDTO#getDocTypeName()
     */
    public String getDocumentType() {
        if (getRouteHeader() == null) {
            // HACK: FIXME: we should probably proscribe, or at least handle consistently, these corner cases
            // NPEs are not nice
            throw new RuntimeException("No such document!");
        }
        return getRouteHeader().getDocTypeName();
    }

    /**
     * Returns whether an acknowledge is requested of the user for this document.  This is
     * a convenience method that delegates to {@link RouteHeaderDTO#isAckRequested()}.
     * @return whether an acknowledge is requested of the user for this document
     * @see RouteHeaderDTO#isAckRequested()
     */
    public boolean isAcknowledgeRequested() {
        return getRouteHeader().isAckRequested();
    }

    /**
     * Returns whether an approval is requested of the user for this document.  This is
     * a convenience method that delegates to {@link RouteHeaderDTO#isApproveRequested()}.
     * @return whether an approval is requested of the user for this document
     * @see RouteHeaderDTO#isApproveRequested()
     */
    public boolean isApprovalRequested() {
        return getRouteHeader().isApproveRequested();
    }

    /**
     * Returns whether a completion is requested of the user for this document.  This is
     * a convenience method that delegates to {@link RouteHeaderDTO#isCompleteRequested()}.
     * @return whether an approval is requested of the user for this document
     * @see RouteHeaderDTO#isCompleteRequested()
     */
    public boolean isCompletionRequested() {
        return getRouteHeader().isCompleteRequested();
    }

    /**
     * Returns whether an FYI is requested of the user for this document.  This is
     * a convenience method that delegates to {@link RouteHeaderDTO#isFyiRequested()}.
     * @return whether an FYI is requested of the user for this document
     * @see RouteHeaderDTO#isFyiRequested()
     */
    public boolean isFYIRequested() {
        return getRouteHeader().isFyiRequested();
    }

    /**
     * Returns whether the user can blanket approve the document
     * @return whether the user can blanket approve the document
     * @see RouteHeaderDTO#getValidActions()
     */
    public boolean isBlanketApproveCapable() {
        // TODO delyea - refactor this to take into account non-initiator owned documents
    	return getRouteHeader().getValidActions().contains(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD) && (isCompletionRequested() || isApprovalRequested() || stateIsInitiated());
    }

    /**
     * Returns whether the specified action code is valid for the current user and document
     * @return whether the user can blanket approve the document
     * @see RouteHeaderDTO#getValidActions()
     */
    public boolean isActionCodeValidForDocument(String actionTakenCode) {
    	return getRouteHeader().getValidActions().contains(actionTakenCode);
    }

    /**
     * Performs the 'super-user-approve' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs super-user-approve-ing the document
     * @see WorkflowDocumentActions#superUserApprove(UserIdDTO, RouteHeaderDTO, String)
     */
    public void superUserApprove(String annotation) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().superUserApprove(principalId, getRouteHeader(), annotation);
    	documentContentDirty = true;
    }

    /**
     * Performs the 'super-user-action-request-approve' action on the document this WorkflowDocument represents and the action
     * request the id represents.
     * @param actionRequestId the action request id for the action request the super user is approved
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs super-user-action-request-approve-ing the document
     * @see WorkflowDocumentActions#superUserApprove(UserIdVO, RouteHeaderVO, String)(UserIdVO, RouteHeaderVO, String)
     */
    public void superUserActionRequestApprove(Long actionRequestId, String annotation) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().superUserActionRequestApprove(principalId, getRouteHeader(), actionRequestId, annotation);
    	documentContentDirty = true;
    }

    /**
     * Performs the 'super-user-disapprove' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs super-user-disapprove-ing the document
     * @see WorkflowDocumentActions#superUserDisapprove(UserIdDTO, RouteHeaderDTO, String)
     */
    public void superUserDisapprove(String annotation) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().superUserDisapprove(principalId, getRouteHeader(), annotation);
    	documentContentDirty = true;
    }

    /**
     * Performs the 'super-user-cancel' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs super-user-cancel-ing the document
     * @see WorkflowDocumentActions#superUserCancel(UserIdDTO, RouteHeaderDTO, String)
     */
    public void superUserCancel(String annotation) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().superUserCancel(principalId, getRouteHeader(), annotation);
    	documentContentDirty = true;
    }

    /**
     * Returns whether the user is a super user on this document
     * @return whether the user is a super user on this document
     * @throws WorkflowException if an error occurs determining whether the user is a super user on this document
     * @see WorkflowUtility#isSuperUserForDocumentType(UserIdDTO, Long)
     */
    public boolean isSuperUser() throws WorkflowException {
    	createDocumentIfNeccessary();
    	return getWorkflowUtility().isSuperUserForDocumentType(principalId, getRouteHeader().getDocTypeId());
	}

    /**
     * Returns whether the user passed into WorkflowDocument at instantiation can route
     * the document.
	 * @return if user passed into WorkflowDocument at instantiation can route
	 *         the document.
	 */
    public boolean isRouteCapable() {
        return isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_ROUTED_CD);
    }

    /**
     * Performs the 'clearFYI' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs clearing FYI on the document
     * @see WorkflowDocumentActions#clearFYIDocument(UserIdDTO, RouteHeaderDTO)
     */
    public void clearFYI() throws WorkflowException {
    	createDocumentIfNeccessary();
    	getWorkflowDocumentActions().clearFYIDocument(principalId, getRouteHeader());
    	documentContentDirty = true;
    }

    /**
     * Performs the 'complete' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs clearing completing the document
     * @see WorkflowDocumentActions#completeDocument(UserIdDTO, RouteHeaderDTO, String)
     */
    public void complete(String annotation) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().completeDocument(principalId, getRouteHeader(), annotation);
    	documentContentDirty = true;
    }

    /**
     * Performs the 'logDocumentAction' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.  The 'logDocumentAction' simply logs a message on the document.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs logging a document action on the document
     * @see WorkflowDocumentActions#logDocumentAction(UserIdDTO, RouteHeaderDTO, String)
     */
    public void logDocumentAction(String annotation) throws WorkflowException {
    	createDocumentIfNeccessary();
    	getWorkflowDocumentActions().logDocumentAction(principalId, getRouteHeader(), annotation);
    	documentContentDirty = true;
    }

    /**
     * Indicates if the document is in the initiated state or not.
     *
     * @return true if in the specified state
     */
    public boolean stateIsInitiated() {
        return KEWConstants.ROUTE_HEADER_INITIATED_CD.equals(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Indicates if the document is in the saved state or not.
     *
     * @return true if in the specified state
     */
    public boolean stateIsSaved() {
        return KEWConstants.ROUTE_HEADER_SAVED_CD.equals(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Indicates if the document is in the enroute state or not.
     *
     * @return true if in the specified state
     */
    public boolean stateIsEnroute() {
        return KEWConstants.ROUTE_HEADER_ENROUTE_CD.equals(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Indicates if the document is in the exception state or not.
     *
     * @return true if in the specified state
     */
    public boolean stateIsException() {
        return KEWConstants.ROUTE_HEADER_EXCEPTION_CD.equals(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Indicates if the document is in the canceled state or not.
     *
     * @return true if in the specified state
     */
    public boolean stateIsCanceled() {
        return KEWConstants.ROUTE_HEADER_CANCEL_CD.equals(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Indicates if the document is in the disapproved state or not.
     *
     * @return true if in the specified state
     */
    public boolean stateIsDisapproved() {
        return KEWConstants.ROUTE_HEADER_DISAPPROVED_CD.equals(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Indicates if the document is in the approved state or not. Will answer true is document is in Processed or Finalized state.
     *
     * @return true if in the specified state
     */
    public boolean stateIsApproved() {
        return KEWConstants.ROUTE_HEADER_APPROVED_CD.equals(getRouteHeader().getDocRouteStatus()) || stateIsProcessed() || stateIsFinal();
    }

    /**
     * Indicates if the document is in the processed state or not.
     *
     * @return true if in the specified state
     */
    public boolean stateIsProcessed() {
        return KEWConstants.ROUTE_HEADER_PROCESSED_CD.equals(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Indicates if the document is in the final state or not.
     *
     * @return true if in the specified state
     */
    public boolean stateIsFinal() {
        return KEWConstants.ROUTE_HEADER_FINAL_CD.equals(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Returns the display value of the current document status
     * @return the display value of the current document status
     */
    public String getStatusDisplayValue() {
        return (String) KEWConstants.DOCUMENT_STATUSES.get(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Returns the principalId with which this WorkflowDocument was constructed
     * @return the principalId with which this WorkflowDocument was constructed
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId under which actions against this document should be taken
     * @param principalId principalId under which actions against this document should be taken
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * Checks if the document has been created or not (i.e. has a document id or not) and issues
     * a call to the server to create the document if it has not yet been created.
     *
     * Also checks if the document content has been updated and saves it if it has.
     */
    private void createDocumentIfNeccessary() throws WorkflowException {
    	if (getRouteHeader().getDocumentId() == null) {
    		routeHeader = getWorkflowDocumentActions().createDocument(principalId, getRouteHeader());
    	}
    	if (documentContent != null && documentContent.isModified()) {
    		saveDocumentContent(documentContent);
    	}
    }

    /**
     * Like handleException except it returns a RuntimeException.
     */
    private RuntimeException handleExceptionAsRuntime(Exception e) {
    	if (e instanceof RuntimeException) {
    		return (RuntimeException)e;
    	}
    	return new WorkflowRuntimeException(e);
    }

    // WORKFLOW 2.1: new methods

    /**
     * Performs the 'blanketApprove' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @param nodeName the extent to which to blanket approve; blanket approval will stop at this node
     * @throws WorkflowException in case an error occurs blanket-approving the document
     * @see WorkflowDocumentActions#blanketApprovalToNodes(UserIdDTO, RouteHeaderDTO, String, String[])
     */
    public void blanketApprove(String annotation, String nodeName) throws WorkflowException {
        blanketApprove(annotation, (nodeName == null ? new String[] {} : new String[] { nodeName }));
    }

    /**
     * Performs the 'blanketApprove' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @param nodeNames the nodes at which blanket approval will stop (in case the blanket approval traverses a split, in which case there may be multiple "active" nodes)
     * @throws WorkflowException in case an error occurs blanket-approving the document
     * @see WorkflowDocumentActions#blanketApprovalToNodes(UserIdDTO, RouteHeaderDTO, String, String[])
     */
    public void blanketApprove(String annotation, String[] nodeNames) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().blanketApprovalToNodes(principalId, getRouteHeader(), annotation, nodeNames);
    	documentContentDirty = true;
    }

    /**
     * The user taking action removes the action items for this workgroup and document from all other
     * group members' action lists.   If this is a new document, the document is created first.
     *
     * @param annotation the message to log for the action
     * @param workgroupId the workgroup on which to take authority
     * @throws WorkflowException user taking action is not in workgroup
     */
    public void takeGroupAuthority(String annotation, String groupId) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().takeGroupAuthority(principalId, getRouteHeader(), groupId, annotation);
    	documentContentDirty = true;
    }

    /**
     * The user that took the group authority is putting the action items back in the other users action lists.
     * If this is a new document, the document is created first.
     *
     * @param annotation the message to log for the action
     * @param workgroupId the workgroup on which to take authority
     * @throws WorkflowException user taking action is not in workgroup or did not take workgroup authority
     */
    public void releaseGroupAuthority(String annotation, String groupId) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().releaseGroupAuthority(principalId, getRouteHeader(), groupId, annotation);
    	documentContentDirty = true;
    }

    /**
     * Returns names of all active nodes the document is currently at.
     *
     * @return names of all active nodes the document is currently at.
     * @throws WorkflowException if there is an error obtaining the currently active nodes on the document
     * @see WorkflowUtility#getActiveNodeInstances(Long)
     */
    public String[] getNodeNames() throws WorkflowException {
    	RouteNodeInstanceDTO[] activeNodeInstances = getWorkflowUtility().getActiveNodeInstances(getDocumentId());
    	String[] nodeNames = new String[(activeNodeInstances == null ? 0 : activeNodeInstances.length)];
    	for (int index = 0; index < activeNodeInstances.length; index++) {
    		nodeNames[index] = activeNodeInstances[index].getName();
    	}
    	return nodeNames;
    }

    /**
     * Performs the 'returnToPrevious' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @param nodeName the node to return to
     * @throws WorkflowException in case an error occurs returning to previous node
     * @see WorkflowDocumentActions#returnDocumentToPreviousNode(UserIdDTO, RouteHeaderDTO, ReturnPointDTO, String)
     */
    public void returnToPreviousNode(String annotation, String nodeName) throws WorkflowException {
        ReturnPointDTO returnPoint = new ReturnPointDTO(nodeName);
        returnToPreviousNode(annotation, returnPoint);
    }

    /**
     * Performs the 'returnToPrevious' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @param ReturnPointDTO the node to return to
     * @throws WorkflowException in case an error occurs returning to previous node
     * @see WorkflowDocumentActions#returnDocumentToPreviousNode(UserIdDTO, RouteHeaderDTO, ReturnPointDTO, String)
     */
    public void returnToPreviousNode(String annotation, ReturnPointDTO returnPoint) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().returnDocumentToPreviousNode(principalId, getRouteHeader(), returnPoint, annotation);
    	documentContentDirty = true;
    }

    /**
     * Moves the document from a current node in it's route to another node.  If this is a new document,
     * the document is created first.
     * @param MovePointDTO VO representing the node at which to start, and the number of steps to move (negative steps is reverse)
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs moving the document
     * @see WorkflowDocumentActions#moveDocument(UserIdDTO, RouteHeaderDTO, MovePointDTO, String)
     */
    public void moveDocument(MovePointDTO movePoint, String annotation) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader =  getWorkflowDocumentActions().moveDocument(principalId, getRouteHeader(), movePoint, annotation);
    	documentContentDirty = true;
    }

    /**
     * Returns the route node instances that have been created so far during the life of this document.  This includes
     * all previous instances which have already been processed and are no longer active.
     * @return the route node instances that have been created so far during the life of this document
     * @throws WorkflowException if there is an error getting the route node instances for the document
     * @see WorkflowUtility#getDocumentRouteNodeInstances(Long)
     */
    public RouteNodeInstanceDTO[] getRouteNodeInstances() throws WorkflowException {
    	return getWorkflowUtility().getDocumentRouteNodeInstances(getDocumentId());
    }

    /**
     * Returns Array of Route Nodes Names that can be safely returned to using the 'returnToPreviousXXX' methods.
     * Names are sorted in reverse chronological order.
     *
     * @return array of Route Nodes Names that can be safely returned to using the 'returnToPreviousXXX' methods
     * @throws WorkflowException if an error occurs obtaining the names of the previous route nodes for this document
     * @see WorkflowUtility#getPreviousRouteNodeNames(Long)
     */
    public String[] getPreviousNodeNames() throws WorkflowException {
    	return getWorkflowUtility().getPreviousRouteNodeNames(getDocumentId());
	}

    /**
     * Returns a document detail VO representing the route header along with action requests, actions taken,
     * and route node instances.
     * @return Returns a document detail VO representing the route header along with action requests, actions taken, and route node instances.
     * @throws WorkflowException
     */
    public DocumentDetailDTO getDetail() throws WorkflowException {
    	return getWorkflowUtility().getDocumentDetail(getDocumentId());
    }

    /**
     * Saves the given DocumentContentVO for this document.
     * @param documentContent document content VO to store for this document
     * @since 2.3
     * @see WorkflowDocumentActions#saveDocumentContent(DocumentContentDTO)
     */
    public DocumentContentDTO saveDocumentContent(DocumentContentDTO documentContent) throws WorkflowException {
    	if (documentContent.getDocumentId() == null) {
    		throw new WorkflowException("Document Content does not have a valid document ID.");
    	}
    	// important to check directly against getRouteHeader().getDocumentId() instead of just getDocumentId() because saveDocumentContent
    	// is called from createDocumentIfNeccessary which is called from getDocumentId().  If that method was used, we would have an infinite loop.
    	if (!documentContent.getDocumentId().equals(getRouteHeader().getDocumentId())) {
    		throw new WorkflowException("Attempted to save content on this document with an invalid document id of " + documentContent.getDocumentId());
    	}
    	DocumentContentDTO newDocumentContent = getWorkflowDocumentActions().saveDocumentContent(documentContent);
    	this.documentContent = new ModifiableDocumentContentDTO(newDocumentContent);
    	documentContentDirty = false;
    	return this.documentContent;
    }
    
    public void placeInExceptionRouting(String annotation) throws WorkflowException {
    	createDocumentIfNeccessary();
    	routeHeader = getWorkflowDocumentActions().placeInExceptionRouting(principalId, getRouteHeader(), annotation);
    	documentContentDirty = true;
    }

    /**
     * Returns a list of NoteVO representing the notes on the document
     * @return a list of NoteVO representing the notes on the document
     * @see RouteHeaderDTO#getNotes()
     */
    public List<NoteDTO> getNoteList(){
    	List<NoteDTO> notesList = new ArrayList<NoteDTO>();
    	NoteDTO[] notes = routeHeader.getNotes();
    	if (notes != null){
	    	for (int i=0; i<notes.length; i++){
	    		if (! isDeletedNote(notes[i])){
	    			notesList.add(notes[i]);
	    		}
	    	}
    	}
    	return notesList;
    }

    /**
     * Deletes a note from the document.  The deletion is deferred until the next time the document is committed (via an action).
     * @param noteVO the note to remove from the document
     */
    public void deleteNote(NoteDTO noteVO){
    	if (noteVO != null && noteVO.getNoteId()!=null){
    		NoteDTO noteToDelete = new NoteDTO();
    		noteToDelete.setNoteId(new Long(noteVO.getNoteId().longValue()));
    		/*noteToDelete.setDocumentId(noteVO.getDocumentId());
    		noteToDelete.setNoteAuthorWorkflowId(noteVO.getNoteAuthorWorkflowId());
    		noteToDelete.setNoteCreateDate(noteVO.getNoteCreateDate());
    		noteToDelete.setNoteText(noteVO.getNoteText());
    		noteToDelete.setLockVerNbr(noteVO.getLockVerNbr());*/
    		increaseNotesToDeleteArraySizeByOne();
    		routeHeader.getNotesToDelete()[routeHeader.getNotesToDelete().length - 1]=noteToDelete;
    	}
    }

    /**
     * Updates the note of the same note id, on the document. The update is deferred until the next time the document is committed (via an action).
     * @param noteVO the note to update
     */
    public void updateNote (NoteDTO noteVO){
    	boolean isUpdateNote = false;
    	if (noteVO != null){
    		NoteDTO[] notes = routeHeader.getNotes();
    		NoteDTO  copyNote = new NoteDTO();
			if (noteVO.getNoteId() != null){
				copyNote.setNoteId(new Long(noteVO.getNoteId().longValue()));
			}

			if (noteVO.getDocumentId() != null){
				copyNote.setDocumentId(noteVO.getDocumentId());
			} else {
				copyNote.setDocumentId(routeHeader.getDocumentId());
			}
			
			if (noteVO.getNoteAuthorWorkflowId() != null){
				copyNote.setNoteAuthorWorkflowId(new String(noteVO.getNoteAuthorWorkflowId()));
			} else {
			    copyNote.setNoteAuthorWorkflowId(principalId.toString())	;
			}

			if (noteVO.getNoteCreateDate() != null){
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(noteVO.getNoteCreateDate().getTimeInMillis());
				copyNote.setNoteCreateDate(cal);
			} else {
				copyNote.setNoteCreateDate(Calendar.getInstance());
			}

			if (noteVO.getNoteText() != null){
				copyNote.setNoteText(new String(noteVO.getNoteText()));
			}
			if (noteVO.getLockVerNbr() != null){
				copyNote.setLockVerNbr(new Integer(noteVO.getLockVerNbr().intValue()));
			}
    		if (notes != null){
	    		for (int i=0; i<notes.length; i++){
	    			if (notes[i].getNoteId()!= null && notes[i].getNoteId().equals(copyNote.getNoteId())){
	    				notes[i] = copyNote;
	    				isUpdateNote = true;
	    				break;
	    			}
	    		}
    		}
    		// add new note to the notes array
    		if (! isUpdateNote){
	    		copyNote.setNoteId(null);
	    		increaseNotesArraySizeByOne();
	    		routeHeader.getNotes()[routeHeader.getNotes().length-1]= copyNote;
    		}
    	}
    }

    /**
     * Sets a variable on the document.  The assignment is deferred until the next time the document is committed (via an action).
     * @param name name of the variable
     * @param value value of the variable
     */
    public void setVariable(String name, String value) throws WorkflowException {
    	createDocumentIfNeccessary();
        getRouteHeader().setVariable(name, value);
    }

    /**
     * Gets the value of a variable on the document, creating the document first if it does not exist.
     * @param name variable name
     * @return variable value
     */
    public String getVariable(String name) throws WorkflowException {
    	createDocumentIfNeccessary();
        return getRouteHeader().getVariable(name);
    }

    /**
     *
     * Tells workflow that the current the document is constructed as will receive all future requests routed to them
     * disregarding any force action flags set on the action request.  Uses the setVariable method behind the seens so
     * an action needs taken on the document to set this state on the document.
     *
     * @throws WorkflowException
     */
    public void setReceiveFutureRequests() throws WorkflowException {
        WorkflowUtility workflowUtility = getWorkflowUtility();
        this.setVariable(workflowUtility.getFutureRequestsKey(principalId), workflowUtility.getReceiveFutureRequestsValue());
    }

    /**
     * Tell workflow that the current document is constructed as will not receive any future requests routed to them
     * disregarding any force action flags set on action requests.  Uses the setVariable method behind the scenes so
     * an action needs taken on the document to set this state on the document.
     *
     * @throws WorkflowException
     */
    public void setDoNotReceiveFutureRequests() throws WorkflowException {
        WorkflowUtility workflowUtility = getWorkflowUtility();
        this.setVariable(workflowUtility.getFutureRequestsKey(principalId), workflowUtility.getDoNotReceiveFutureRequestsValue());
    }

    /**
     * Clears any state set on the document regarding a user receiving or not receiving action requests.  Uses the setVariable method
     * behind the seens so an action needs taken on the document to set this state on the document.
     *
     * @throws WorkflowException
     */
    public void setClearFutureRequests() throws WorkflowException {
        WorkflowUtility workflowUtility = getWorkflowUtility();
        this.setVariable(workflowUtility.getFutureRequestsKey(principalId), workflowUtility.getClearFutureRequestsValue());
    }

    /**
     * Deletes the note of with the same id as that of the argument on the document.
     * @param noteVO the note to test for deletion
     * @return whether the note is already marked for deletion.
     */
    private boolean isDeletedNote(NoteDTO noteVO) {
    	NoteDTO[] notesToDelete = routeHeader.getNotesToDelete();
    	if (notesToDelete != null){
    		for (int i=0; i<notesToDelete.length; i++){
    			if (notesToDelete[i].getNoteId().equals(noteVO.getNoteId())){
    				return true;
    			}
    		}
    	}
    	return false;
    }

    /**
     * Increases the size of the routeHeader notes VO array
     */
   private void increaseNotesArraySizeByOne() {
	   NoteDTO[] tempArray;
	   NoteDTO[] notes = routeHeader.getNotes();
	   if (notes == null){
		   tempArray = new NoteDTO[1];
	   } else {
		   tempArray = new NoteDTO[notes.length + 1];
		   for (int i=0; i<notes.length; i++){
			   tempArray[i] = notes[i];
		   }
	   }
	   routeHeader.setNotes(tempArray);
   }

   /**
    * Increases the size of the routeHeader notesToDelete VO array
    */
   private void increaseNotesToDeleteArraySizeByOne() {
	   NoteDTO[] tempArray;
	   NoteDTO[] notesToDelete = routeHeader.getNotesToDelete();
	   if (notesToDelete == null){
		   tempArray = new NoteDTO[1];
	   } else {
		   tempArray = new NoteDTO[notesToDelete.length + 1];
		   for (int i=0; i<notesToDelete.length; i++){
			   tempArray[i] = notesToDelete[i];
		   }
	   }
	   routeHeader.setNotesToDelete(tempArray);
   }
   
   //add 1 link between 2 docs by DTO, double link added
   public void addLinkedDocument(DocumentLinkDTO docLinkVO) throws WorkflowException{
	   try{
		   if(DocumentLinkDTO.checkDocLink(docLinkVO))
			   getWorkflowUtility().addDocumentLink(docLinkVO);
	   }
	   catch(Exception e){
		   throw handleExceptionAsRuntime(e); 
	   } 
   }
   
   //get link from orgn doc to a specifc doc
   public DocumentLinkDTO getLinkedDocument(DocumentLinkDTO docLinkVO) throws WorkflowException{
	   try{
		   if(DocumentLinkDTO.checkDocLink(docLinkVO))
			   return getWorkflowUtility().getLinkedDocument(docLinkVO);
		   else
			   return null;
	   }
	   catch(Exception e){
		   throw handleExceptionAsRuntime(e); 
	   }
   }
   
   //get all links to orgn doc
   public List<DocumentLinkDTO> getLinkedDocumentsByDocId(String documentId) throws WorkflowException{
	   if(documentId == null)
		   throw new WorkflowException("document Id is null");
	   try{   
		   return getWorkflowUtility().getLinkedDocumentsByDocId(documentId);
	   } 
	   catch (Exception e) {
		   throw handleExceptionAsRuntime(e);
	   }
   }
   
   //remove all links from orgn: double links removed
   public void removeLinkedDocuments(String docId) throws WorkflowException{
	   
	   if(docId == null)
		   throw new WorkflowException("doc id is null");
	   
	   try{   
		   getWorkflowUtility().deleteDocumentLinksByDocId(docId);
	   } 
	   catch (Exception e) {
		   throw handleExceptionAsRuntime(e);
	   }
   }
   
   //remove link between 2 docs, double link removed
   public void removeLinkedDocument(DocumentLinkDTO docLinkVO) throws WorkflowException{
	   
	   try{
		   if(DocumentLinkDTO.checkDocLink(docLinkVO))
			   getWorkflowUtility().deleteDocumentLink(docLinkVO);
	   }
	   catch(Exception e){
		   throw handleExceptionAsRuntime(e); 
	   } 
   }

}
