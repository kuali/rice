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
package edu.iu.uis.eden.clientapp;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.kuali.rice.config.Config;
import org.kuali.rice.config.RiceConfigurer;
import org.kuali.rice.core.Core;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.workflow.config.KEWConfigurer;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.ActionTakenVO;
import edu.iu.uis.eden.clientapp.vo.AdHocRevokeVO;
import edu.iu.uis.eden.clientapp.vo.DocumentContentVO;
import edu.iu.uis.eden.clientapp.vo.DocumentDetailVO;
import edu.iu.uis.eden.clientapp.vo.EmplIdVO;
import edu.iu.uis.eden.clientapp.vo.ModifiableDocumentContentVO;
import edu.iu.uis.eden.clientapp.vo.MovePointVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.NoteVO;
import edu.iu.uis.eden.clientapp.vo.ResponsiblePartyVO;
import edu.iu.uis.eden.clientapp.vo.ReturnPointVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.clientapp.vo.RouteNodeInstanceVO;
import edu.iu.uis.eden.clientapp.vo.RouteTemplateEntryVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.UuIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeValidationErrorVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO;
import edu.iu.uis.eden.exception.DocumentTypeNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.messaging.config.KSBThinClientConfigurer;
import edu.iu.uis.eden.server.WorkflowDocumentActions;
import edu.iu.uis.eden.server.WorkflowUtility;
import edu.iu.uis.eden.util.Utilities;

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
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkflowDocument implements java.io.Serializable {

	private static final long serialVersionUID = -3672966990721719088L;

	/**
	 * UserId VO of the user as whom actions will be taken on the KEW document
	 */
    private UserIdVO userId;
    /**
     * RouteHeader VO of the KEW document this WorkflowDocument represents
     */
    private RouteHeaderVO routeHeader;
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
    private ModifiableDocumentContentVO documentContent;

    /**
     * Constructs a WorkflowDocument representing a new document in the workflow system.
     * Creation/committing of the new document is deferred until the first action is
     * taken on the document.
     * @param userId the user as which to take actions on the document
     * @param documentType the type of the document to create
     * @throws WorkflowException if anything goes awry
     */
    public WorkflowDocument(UserIdVO userId, String documentType) throws WorkflowException {
        init(userId, documentType, null);
    }

    /**
     * Loads a workflow document with the given route header ID for the given User.  If no document
     * can be found with the given ID, then the {@link getRouteHeader()} method of the WorkflowDocument
     * which is created will return null.
     *
     * @throws WorkflowException if there is a problem loading the WorkflowDocument
     */
    public WorkflowDocument(UserIdVO userId, Long routeHeaderId) throws WorkflowException {
        init(userId, null, routeHeaderId);
    }

    /**
     * Initializes this WorkflowDocument object, by either attempting to load an existing document by routeHeaderid
     * if one is supplied (non-null), or by constructing an empty document of the specified type.
     * @param userId the user under which actions via this API on the specified document will be taken
     * @param documentType the type of document this WorkflowDocument should represent (either this parameter or routeHeaderId must be specified, non-null)
     * @param routeHeaderId the id of an existing document to load (either this parameter or documentType must be specified, non-null)
     * @throws WorkflowException if a routeHeaderId is specified but an exception occurs trying to load the document route header
     */
    private void init(UserIdVO userId, String documentType, Long routeHeaderId) throws WorkflowException {
        try {
            this.userId = userId;
            routeHeader = new RouteHeaderVO();
            routeHeader.setDocTypeName(documentType);
            if (routeHeaderId != null) {
                routeHeader = getWorkflowUtility().getRouteHeaderWithUser(userId, routeHeaderId);
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Retrieves the WorkflowUtility proxy from the locator.  The locator will cache this for us.
     */
    private WorkflowUtility getWorkflowUtility() throws WorkflowException {
    	initializeBus();
        return (WorkflowUtility)GlobalResourceLoader.getService(KEWServiceLocator.WORKFLOW_UTILITY_SERVICE);
    }

    /**
     * Retrieves the WorkflowDocumentActions proxy from the locator.  The locator will cache this for us.
     */
    private WorkflowDocumentActions getWorkflowDocumentActions() throws WorkflowException {
    	initializeBus();
    	return (WorkflowDocumentActions)GlobalResourceLoader.getService(KEWServiceLocator.WORKFLOW_DOCUMENT_ACTIONS_SERVICE);
    }

    /**
     * Initializes the KSB configuration if it has not already been initialized by the application;
     * in that case only the KEW configurer is added.
     * @throws WorkflowException if there is an error starting the RiceConfigurer
     */
    private synchronized void initializeBus() throws WorkflowException {
    	if (!isLocal() && !GlobalResourceLoader.isInitialized()) {
    		RiceConfigurer configurer = new RiceConfigurer();
    		configurer.setMessageEntity(EdenConstants.KEW_MESSAGING_ENTITY);
    		// thin client allows us to still have access to the DigitalSignatureService but not use the full capabilities of the bus
    		configurer.getModules().add(new KSBThinClientConfigurer());
    		configurer.getModules().add(new KEWConfigurer());
    		try {
    			configurer.start();
    		} catch (Exception e) {
    			if (e instanceof WorkflowException) {
    				throw (WorkflowException)e;
    			} else if (e instanceof RuntimeException) {
    				throw (RuntimeException)e;
    			}
    			throw new WorkflowException(e);
    		}
    	}
    }

    private boolean isLocal() {
	Config config = Core.getCurrentContextConfig();
	if (config != null) {
	    return config.getProperty(Config.CLIENT_PROTOCOL).equals(EdenConstants.LOCAL_CLIENT_PROTOCOL);
	}
	return false;
    }

    // ########################
    // Document Content methods
    // ########################

    /**
     * Returns an up-to-date DocumentContent of this document.
     * @see WorkflowUtility#getDocumentContent(Long)
     */
    public DocumentContentVO getDocumentContent() {
    	try {
    		// create the document if it hasn't already been created
    		if (getRouteHeader().getRouteHeaderId() == null) {
        		routeHeader = getWorkflowDocumentActions().createDocument(userId, getRouteHeader());
        	}
    		if (documentContent == null || documentContentDirty) {
    			documentContent = new ModifiableDocumentContentVO(getWorkflowUtility().getDocumentContent(routeHeader.getRouteHeaderId()));
    			documentContentDirty = false;
    		}
    	} catch (Exception e) {
    		throw handleExceptionAsRuntime(e);
    	}
    	return documentContent;
    }

    /**
     * Returns the application specific section of the document content. This is
     * a convenience method that delegates to the {@link DocumentContentVO}.
     *
     * For documents routed prior to Workflow 2.0:
     * If the application did NOT use attributes for XML generation, this method will
     * return the entire document content XML.  Otherwise it will return the empty string.
     * @see DocumentContentVO#getApplicationContent()
     */
    public String getApplicationContent() {
        return getDocumentContent().getApplicationContent();
    }

    /**
     * Sets the application specific section of the document content. This is
     * a convenience method that delegates to the {@link DocumentContentVO}.
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
     * This is a convenience method that delegates to the {@link DocumentContentVO}.
     *
     * In order for these changes to take effect, an action must be performed on the document (such as "save").
     */
    public void clearAttributeContent() {
        getDocumentContent().setAttributeContent("");
    }

    /**
     * Returns the attribute-generated section of the document content. This is
     * a convenience method that delegates to the {@link DocumentContentVO}.
     * @see DocumentContentVO#getAttributeContent()
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
     * This is a convenience method that delegates to the {@link DocumentContentVO}.
     * @see DocumentContentVO#addAttributeDefinition(WorkflowAttributeDefinitionVO)
     */
    public void addAttributeDefinition(WorkflowAttributeDefinitionVO attributeDefinition) {
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
     * @see WorkflowUtility#validateWorkflowAttributeDefinitionVO(WorkflowAttributeDefinitionVO)
     */
    public WorkflowAttributeValidationErrorVO[] validateAttributeDefinition(WorkflowAttributeDefinitionVO attributeDefinition) throws WorkflowException {
        try {
            return getWorkflowUtility().validateWorkflowAttributeDefinitionVO(attributeDefinition);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Removes an attribute definition from the document content.  This is
     * a convenience method that delegates to the {@link DocumentContentVO}.
     * @param attributeDefinition the attribute definition VO to remove
     */
    public void removeAttributeDefinition(WorkflowAttributeDefinitionVO attributeDefinition) {
        getDocumentContent().removeAttributeDefinition(attributeDefinition);
    }

    /**
     * Removes all attribute definitions from the document content. This is
     * a convenience method that delegates to the {@link DocumentContentVO}.
     */
    public void clearAttributeDefinitions() {
    	getDocumentContent().setAttributeDefinitions(new WorkflowAttributeDefinitionVO[0]);
    }

    /**
     * Returns the attribute definition VOs currently defined on the document content. This is
     * a convenience method that delegates to the {@link DocumentContentVO}.
     * @return the attribute definition VOs currently defined on the document content.
     * @see DocumentContentVO#getAttributeDefinitions()
     */
    public WorkflowAttributeDefinitionVO[] getAttributeDefinitions() {
        return getDocumentContent().getAttributeDefinitions();
    }

    /**
     * Adds a searchable attribute definition which defines creation parameters for a SearchableAttribute
     * implementation.  The created attribute will be used to generate searchable document content.
     * When the document is sent to the server, this will be appended to the existing searchable
     * doc content.  If it is required to replace the searchable document content, then the
     * clearSearchableContent() method should be invoked prior to adding definitions. This is
     * a convenience method that delegates to the {@link DocumentContentVO}.
     */
    public void addSearchableDefinition(WorkflowAttributeDefinitionVO searchableDefinition) {
        getDocumentContent().addSearchableDefinition(searchableDefinition);
    }

    /**
     * Removes a searchable attribute definition from the document content. This is
     * a convenience method that delegates to the {@link DocumentContentVO}.
     * @param searchableDefinition the searchable attribute definition to remove
     */
    public void removeSearchableDefinition(WorkflowAttributeDefinitionVO searchableDefinition) {
        getDocumentContent().removeSearchableDefinition(searchableDefinition);
    }

    /**
     * Removes all searchable attribute definitions from the document content. This is
     * a convenience method that delegates to the {@link DocumentContentVO}.
     */
    public void clearSearchableDefinitions() {
        getDocumentContent().setSearchableDefinitions(new WorkflowAttributeDefinitionVO[0]);
    }

    /**
     * Clears the searchable content from the document content. This is
     * a convenience method that delegates to the {@link DocumentContentVO}.
     */
    public void clearSearchableContent() {
    	getDocumentContent().setSearchableContent("");
    }

    /**
     * Returns the searchable attribute definitions on the document content. This is
     * a convenience method that delegates to the {@link DocumentContentVO}.
     * @return the searchable attribute definitions on the document content.
     * @see DocumentContentVO#getSearchableDefinitions()
     */
    public WorkflowAttributeDefinitionVO[] getSearchableDefinitions() {
        return getDocumentContent().getSearchableDefinitions();
    }

    // ########################
    // END Document Content methods
    // ########################

    /**
     * Returns the RouteHeaderVO for the workflow document this WorkflowDocument represents
     */
    public RouteHeaderVO getRouteHeader() {
        return routeHeader;
    }

    /**
     * Returns the id of the workflow document this WorkflowDocument represents.  If this is a new document
     * that has not yet been created, the document is first created (and therefore this will return a new id)
     * @return the id of the workflow document this WorkflowDocument represents
     * @throws WorkflowException if an error occurs during document creation
     */
    public Long getRouteHeaderId() throws WorkflowException {
        try {
            createDocumentIfNeccessary();
            return getRouteHeader().getRouteHeaderId();
        } catch (Exception e) {
            throw handleException(e);
        }
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
    public ActionRequestVO[] getActionRequests() throws WorkflowException {
        if (getRouteHeaderId() == null) {
            return new ActionRequestVO[0];
        }
        try {
            return getWorkflowUtility().getActionRequests(getRouteHeaderId());
        } catch (Exception e) {
            throw handleException(e);
        }
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
    public ActionTakenVO[] getActionsTaken() throws WorkflowException {
        if (getRouteHeaderId() == null) {
            return new ActionTakenVO[0];
        }
        try {
            return getWorkflowUtility().getActionsTaken(getRouteHeaderId());
        } catch (RemoteException e) {
            throw handleException(e);
        }
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
        return Utilities.convertCalendar(routeHeader.getDateCreated());
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
     * @see WorkflowDocumentActions#saveDocument(UserIdVO, RouteHeaderVO, String)
     */
    public void saveDocument(String annotation) throws WorkflowException {
        try {
        	createDocumentIfNeccessary();
            routeHeader = getWorkflowDocumentActions().saveDocument(userId, getRouteHeader(), annotation);
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Performs the 'route' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs routing the document
     * @see WorkflowDocumentActions#routeDocument(UserIdVO, RouteHeaderVO, String)
     */
    public void routeDocument(String annotation) throws WorkflowException {
        try {
        	createDocumentIfNeccessary();
            routeHeader = getWorkflowDocumentActions().routeDocument(userId, routeHeader, annotation);
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Performs the 'disapprove' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs disapproving the document
     * @see WorkflowDocumentActions#disapproveDocument(UserIdVO, RouteHeaderVO, String)
     */
    public void disapprove(String annotation) throws WorkflowException {
        try {
        	createDocumentIfNeccessary();
            routeHeader = getWorkflowDocumentActions().disapproveDocument(userId, getRouteHeader(), annotation);
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }

    }

    /**
     * Performs the 'approve' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs approving the document
     * @see WorkflowDocumentActions#approveDocument(UserIdVO, RouteHeaderVO, String)
     */
    public void approve(String annotation) throws WorkflowException {
        try {
        	createDocumentIfNeccessary();
            routeHeader = getWorkflowDocumentActions().approveDocument(userId, getRouteHeader(), annotation);
            documentContentDirty = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Performs the 'cancel' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs canceling the document
     * @see WorkflowDocumentActions#cancelDocument(UserIdVO, RouteHeaderVO, String)
     */
    public void cancel(String annotation) throws WorkflowException {
        try {
        	createDocumentIfNeccessary();
            routeHeader = getWorkflowDocumentActions().cancelDocument(userId, getRouteHeader(), annotation);
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Performs the 'blanket-approve' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs blanket-approving the document
     * @see WorkflowDocumentActions#blanketApprovalToNodes(UserIdVO, RouteHeaderVO, String, String[])
     */
    public void blanketApprove(String annotation) throws WorkflowException {
        blanketApprove(annotation, (String)null);
    }

    /**
     * Commits any changes made to the local copy of this document to the workflow system.  If this is a new document,
     * the document is created first.
     * @throws WorkflowException in case an error occurs saving the document
     * @see WorkflowDocumentActions#saveRoutingData(UserIdVO, RouteHeaderVO)
     */
    public void saveRoutingData() throws WorkflowException {
        try {
        	createDocumentIfNeccessary();
            routeHeader = getWorkflowDocumentActions().saveRoutingData(userId, getRouteHeader());
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Performs the 'acknowledge' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs acknowledging the document
     * @see WorkflowDocumentActions#acknowledgeDocument(UserIdVO, RouteHeaderVO, String)
     */
    public void acknowledge(String annotation) throws WorkflowException {
        try {
        	createDocumentIfNeccessary();
            routeHeader = getWorkflowDocumentActions().acknowledgeDocument(userId, getRouteHeader(), annotation);
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Performs the 'fyi' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs fyi-ing the document
     */
    public void fyi() throws WorkflowException {
        try {
        	createDocumentIfNeccessary();
            routeHeader = getWorkflowDocumentActions().clearFYIDocument(userId, getRouteHeader());
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Performs the 'delete' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs deleting the document
     * @see WorkflowDocumentActions#deleteDocument(UserIdVO, RouteHeaderVO)
     */
    public void delete() throws WorkflowException {
        try {
        	createDocumentIfNeccessary();
            getWorkflowDocumentActions().deleteDocument(userId, getRouteHeader());
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Reloads the document route header.  If this is a new document, the document is created first.
     * Next time document content is accessed, an up-to-date copy will be retrieved from workflow.
     * @throws WorkflowException in case an error occurs retrieving the route header
     */
    public void refreshContent() throws WorkflowException {
        try {
        	createDocumentIfNeccessary();
            routeHeader = getWorkflowUtility().getRouteHeader(getRouteHeaderId());
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * @deprecated use {@link #appSpecificRouteDocumentToUser(String, String, String, UserIdVO, String, boolean)}
     */
    public void appSpecificRouteDocumentToUser(String actionRequested, String nodeName, int priority, String annotation, UserIdVO recipient, String responsibilityDesc, boolean ignorePreviousActions) throws WorkflowException {
        appSpecificRouteDocumentToUser(actionRequested, nodeName, annotation, recipient, responsibilityDesc, ignorePreviousActions);
    }

    /**
     * @deprecated use {@link #appSpecificRouteDocumentToWorkgroup(String, String, String, WorkgroupIdVO, String, boolean)}
     */
    public void appSpecificRouteDocumentToWorkgroup(String actionRequested, String nodeName, int priority, String annotation, WorkgroupIdVO workgroupId, String responsibilityDesc, boolean ignorePreviousActions) throws WorkflowException {
    	appSpecificRouteDocumentToWorkgroup(actionRequested, nodeName, annotation, workgroupId, responsibilityDesc, ignorePreviousActions);
    }

    /**
     * Sends an ad hoc request to the specified user at the current active node on the document.  If the document is
     * in a terminal state, the request will be attached to the terminal node.
     * @see #appSpecificRouteDocumentToUser(String, String, String, UserIdVO, String, boolean)
     * @see WorkflowDocumentActions#appSpecificRouteDocument(UserIdVO, RouteHeaderVO, String, String, String, ResponsiblePartyVO, String, boolean)
     */
    public void appSpecificRouteDocumentToUser(String actionRequested, String annotation, UserIdVO recipient, String responsibilityDesc, boolean ignorePreviousActions) throws WorkflowException {
    	appSpecificRouteDocumentToUser(actionRequested, null, annotation, recipient, responsibilityDesc, ignorePreviousActions);
    }

    /**
     * Sends an ad hoc request to the specified user at the specified node on the document.  If the document is
     * in a terminal state, the request will be attached to the terminal node.
     * @see #appSpecificRouteDocumentToUser(String, String, UserIdVO, String, boolean)
     * @see WorkflowDocumentActions#appSpecificRouteDocument(UserIdVO, RouteHeaderVO, String, String, String, ResponsiblePartyVO, String, boolean)
     */
    public void appSpecificRouteDocumentToUser(String actionRequested, String nodeName, String annotation, UserIdVO recipient, String responsibilityDesc, boolean ignorePreviousActions) throws WorkflowException {
        try {
        	createDocumentIfNeccessary();
            routeHeader = getWorkflowDocumentActions().appSpecificRouteDocument(userId, getRouteHeader(), actionRequested, nodeName, annotation, new ResponsiblePartyVO(recipient), responsibilityDesc, ignorePreviousActions);
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Sends an ad hoc request to the specified workgroup at the current active node on the document.  If the document is
     * in a terminal state, the request will be attached to the terminal node.
     * @see #appSpecificRouteDocumentToWorkgroup(String, String, String, WorkgroupIdVO, String, boolean)
     * @see WorkflowDocumentActions#appSpecificRouteDocument(UserIdVO, RouteHeaderVO, String, String, String, ResponsiblePartyVO, String, boolean)
     */
    public void appSpecificRouteDocumentToWorkgroup(String actionRequested, String annotation, WorkgroupIdVO workgroupId, String responsibilityDesc, boolean ignorePreviousActions) throws WorkflowException {
    	appSpecificRouteDocumentToWorkgroup(actionRequested, null, annotation, workgroupId, responsibilityDesc, ignorePreviousActions);
    }

    /**
     * Sends an ad hoc request to the specified workgroup at the specified node on the document.  If the document is
     * in a terminal state, the request will be attached to the terminal node.
     * @see #appSpecificRouteDocumentToWorkgroup(String, String, String, WorkgroupIdVO, String, boolean)
     * @see WorkflowDocumentActions#appSpecificRouteDocument(UserIdVO, RouteHeaderVO, String, String, String, ResponsiblePartyVO, String, boolean)
     */
    public void appSpecificRouteDocumentToWorkgroup(String actionRequested, String nodeName, String annotation, WorkgroupIdVO workgroupId, String responsibilityDesc, boolean ignorePreviousActions) throws WorkflowException {
        try {
        	createDocumentIfNeccessary();
            routeHeader = getWorkflowDocumentActions().appSpecificRouteDocument(userId, getRouteHeader(), actionRequested, nodeName, annotation, new ResponsiblePartyVO(workgroupId), responsibilityDesc, ignorePreviousActions);
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Revokes AdHoc request(s) according to the given AdHocRevokeVO which is passed in.
     *
     * If a specific action request ID is specified on the revoke bean, and that ID is not a valid ID, this method should throw a
     * WorkflowException.
     * @param revoke AdHocRevokeVO
     * @param annotation message to note for this action
     * @throws WorkflowException if an error occurs revoking adhoc requests
     * @see WorkflowDocumentActions#revokeAdHocRequests(UserIdVO, RouteHeaderVO, AdHocRevokeVO, String)
     */
    public void revokeAdHocRequests(AdHocRevokeVO revoke, String annotation) throws WorkflowException {
    	if (getRouteHeader().getRouteHeaderId() == null) {
    		throw new WorkflowException("Can't revoke request, the workflow document has not yet been created!");
    	}
    	try {
    		createDocumentIfNeccessary();
    		routeHeader = getWorkflowDocumentActions().revokeAdHocRequests(userId, getRouteHeader(), revoke, annotation);
    		documentContentDirty = true;
    	} catch (Exception e) {
    		throw handleException(e);
    	}
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
        if (title.length() > EdenConstants.TITLE_MAX_LENGTH) {
            title = title.substring(0, EdenConstants.TITLE_MAX_LENGTH);
        }
        getRouteHeader().setDocTitle(title);
    }

    /**
     * Returns the document type of the workflow document
     * @return the document type of the workflow document
     * @throws RuntimeException if document does not exist (is not yet created)
     * @see RouteHeaderVO#getDocTypeName()
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
     * a convenience method that delegates to {@link RouteHeaderVO#isAckRequested()}.
     * @return whether an acknowledge is requested of the user for this document
     * @see RouteHeaderVO#isAckRequested()
     */
    public boolean isAcknowledgeRequested() {
        return getRouteHeader().isAckRequested();
    }

    /**
     * Returns whether an approval is requested of the user for this document.  This is
     * a convenience method that delegates to {@link RouteHeaderVO#isApproveRequested()}.
     * @return whether an approval is requested of the user for this document
     * @see RouteHeaderVO#isApproveRequested()
     */
    public boolean isApprovalRequested() {
        return getRouteHeader().isApproveRequested();
    }

    /**
     * Returns whether a completion is requested of the user for this document.  This is
     * a convenience method that delegates to {@link RouteHeaderVO#isCompleteRequested()}.
     * @return whether an approval is requested of the user for this document
     * @see RouteHeaderVO#isCompleteRequested()
     */
    public boolean isCompletionRequested() {
        return getRouteHeader().isCompleteRequested();
    }

    /**
     * Returns whether an FYI is requested of the user for this document.  This is
     * a convenience method that delegates to {@link RouteHeaderVO#isFyiRequested()}.
     * @return whether an FYI is requested of the user for this document
     * @see RouteHeaderVO#isFyiRequested()
     */
    public boolean isFYIRequested() {
        return getRouteHeader().isFyiRequested();
    }

    /**
     * Returns whether the user can blanket approve the document
     * @return whether the user can blanket approve the document
     * @see RouteHeaderVO#getValidActions()
     */
    public boolean isBlanketApproveCapable() {
        // TODO delyea - refactor this to take into account non-initiator owned documents
    	return getRouteHeader().getValidActions().contains(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD) && (isCompletionRequested() || isApprovalRequested() || stateIsInitiated());
    }

    /**
     * Returns whether the specified action code is valid for the current user and document
     * @return whether the user can blanket approve the document
     * @see RouteHeaderVO#getValidActions()
     */
    public boolean isActionCodeValidForDocument(String actionTakenCode) {
    	return getRouteHeader().getValidActions().contains(actionTakenCode);
    }

    /**
     * Performs the 'super-user-approve' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs super-user-approve-ing the document
     * @see WorkflowDocumentActions#superUserApprove(UserIdVO, RouteHeaderVO, String)
     */
    public void superUserApprove(String annotation) throws WorkflowException {
    	try {
    		createDocumentIfNeccessary();
    		routeHeader = getWorkflowDocumentActions().superUserApprove(getUserId(), getRouteHeader(), annotation);
    		documentContentDirty = true;
    	} catch (Exception e) {
			throw handleException(e);
		}
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
        try {
            createDocumentIfNeccessary();
            routeHeader = getWorkflowDocumentActions().superUserActionRequestApprove(getUserId(), getRouteHeader(), actionRequestId, annotation);
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Performs the 'super-user-disapprove' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs super-user-disapprove-ing the document
     * @see WorkflowDocumentActions#superUserDisapprove(UserIdVO, RouteHeaderVO, String)
     */
    public void superUserDisapprove(String annotation) throws WorkflowException {
    	try {
    		createDocumentIfNeccessary();
    		routeHeader = getWorkflowDocumentActions().superUserDisapprove(getUserId(), getRouteHeader(), annotation);
    		documentContentDirty = true;
    	} catch (Exception e) {
			throw handleException(e);
		}
    }

    /**
     * Performs the 'super-user-cancel' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs super-user-cancel-ing the document
     * @see WorkflowDocumentActions#superUserCancel(UserIdVO, RouteHeaderVO, String)
     */
    public void superUserCancel(String annotation) throws WorkflowException {
    	try {
    		createDocumentIfNeccessary();
    		routeHeader = getWorkflowDocumentActions().superUserCancel(getUserId(), getRouteHeader(), annotation);
    		documentContentDirty = true;
    	} catch (Exception e) {
			throw handleException(e);
		}
    }

    /**
     * Returns whether the user is a super user on this document
     * @return whether the user is a super user on this document
     * @throws WorkflowException if an error occurs determining whether the user is a super user on this document
     * @see WorkflowUtility#isSuperUserForDocumentType(UserIdVO, Long)
     */
    public boolean isSuperUser() throws WorkflowException {
		try {
			createDocumentIfNeccessary();
			return getWorkflowUtility().isSuperUserForDocumentType(getUserId(), getRouteHeader().getDocTypeId());
		} catch (Exception e) {
			throw handleException(e);
		}
	}

    /**
     * Returns whether the user passed into WorkflowDocument at instantiation can route
     * the document.
	 * @return if user passed into WorkflowDocument at instantiation can route
	 *         the document.
	 */
    // TODO delyea - Should this be removed due to policies and valid actions adjustments?
    public boolean isRouteCapable() {
        UserIdVO userId = getUserId();
        if (userId instanceof NetworkIdVO) {
            return ((NetworkIdVO) userId).getNetworkId().equals(getRouteHeader().getInitiator().getNetworkId()) && stateIsInitiated();
        } else if (userId instanceof UuIdVO) {
            return ((UuIdVO) userId).getUuId().equals(getRouteHeader().getInitiator().getUuId()) && stateIsInitiated();
        } else if (userId instanceof EmplIdVO) {
            return ((EmplIdVO) userId).getEmplId().equals(getRouteHeader().getInitiator().getEmplId()) && stateIsInitiated();
        } else if (userId instanceof WorkflowIdVO) {
            return ((WorkflowIdVO) userId).getWorkflowId().equals(getRouteHeader().getInitiator().getWorkflowId()) && stateIsInitiated();
        }
        throw new UnsupportedOperationException("UserId type not yet supported on this method.");
    }

    /**
     * Performs the 'clearFYI' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs clearing FYI on the document
     * @see WorkflowDocumentActions#clearFYIDocument(UserIdVO, RouteHeaderVO)
     */
    public void clearFYI() throws WorkflowException {
        try {
        	createDocumentIfNeccessary();
            getWorkflowDocumentActions().clearFYIDocument(userId, getRouteHeader());
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Performs the 'complete' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs clearing completing the document
     * @see WorkflowDocumentActions#completeDocument(UserIdVO, RouteHeaderVO, String)
     */
    public void complete(String annotation) throws WorkflowException {
        try {
        	createDocumentIfNeccessary();
            routeHeader = getWorkflowDocumentActions().completeDocument(userId, getRouteHeader(), annotation);
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Performs the 'logDocumentAction' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.  The 'logDocumentAction' simply logs a message on the document.
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs logging a document action on the document
     * @see WorkflowDocumentActions#logDocumentAction(UserIdVO, RouteHeaderVO, String)
     */
    public void logDocumentAction(String annotation) throws WorkflowException {
        try {
        	createDocumentIfNeccessary();
            getWorkflowDocumentActions().logDocumentAction(userId, getRouteHeader(), annotation);
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Indicates if the document is in the initiated state or not.
     *
     * @return true if in the specified state
     */
    public boolean stateIsInitiated() {
        return EdenConstants.ROUTE_HEADER_INITIATED_CD.equals(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Indicates if the document is in the saved state or not.
     *
     * @return true if in the specified state
     */
    public boolean stateIsSaved() {
        return EdenConstants.ROUTE_HEADER_SAVED_CD.equals(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Indicates if the document is in the enroute state or not.
     *
     * @return true if in the specified state
     */
    public boolean stateIsEnroute() {
        return EdenConstants.ROUTE_HEADER_ENROUTE_CD.equals(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Indicates if the document is in the exception state or not.
     *
     * @return true if in the specified state
     */
    public boolean stateIsException() {
        return EdenConstants.ROUTE_HEADER_EXCEPTION_CD.equals(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Indicates if the document is in the canceled state or not.
     *
     * @return true if in the specified state
     */
    public boolean stateIsCanceled() {
        return EdenConstants.ROUTE_HEADER_CANCEL_CD.equals(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Indicates if the document is in the disapproved state or not.
     *
     * @return true if in the specified state
     */
    public boolean stateIsDisapproved() {
        return EdenConstants.ROUTE_HEADER_DISAPPROVED_CD.equals(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Indicates if the document is in the approved state or not. Will answer true is document is in Processed or Finalized state.
     *
     * @return true if in the specified state
     */
    public boolean stateIsApproved() {
        return EdenConstants.ROUTE_HEADER_APPROVED_CD.equals(getRouteHeader().getDocRouteStatus()) || stateIsProcessed() || stateIsFinal();
    }

    /**
     * Indicates if the document is in the processed state or not.
     *
     * @return true if in the specified state
     */
    public boolean stateIsProcessed() {
        return EdenConstants.ROUTE_HEADER_PROCESSED_CD.equals(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Indicates if the document is in the final state or not.
     *
     * @return true if in the specified state
     */
    public boolean stateIsFinal() {
        return EdenConstants.ROUTE_HEADER_FINAL_CD.equals(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Returns the display value of the current document status
     * @return the display value of the current document status
     */
    public String getStatusDisplayValue() {
        return (String) EdenConstants.DOCUMENT_STATUSES.get(getRouteHeader().getDocRouteStatus());
    }

    /**
     * Returns the userId with which this WorkflowDocument was constructed
     * @return the userId with which this WorkflowDocument was constructed
     */
    public UserIdVO getUserId() {
        return userId;
    }

    /**
     * Sets the userId under which actions against this document should be taken
     * @param userId userId under which actions against this document should be taken
     */
    public void setUserId(UserIdVO userId) {
        this.userId = userId;
    }

    /**
     * Checks if the document has been created or not (i.e. has a route header id or not) and issues
     * a call to the server to create the document if it has not yet been created.
     *
     * Also checks if the document content has been updated and saves it if it has.
     */
    private void createDocumentIfNeccessary() throws RemoteException, WorkflowException {
    	if (getRouteHeader().getRouteHeaderId() == null) {
    		routeHeader = getWorkflowDocumentActions().createDocument(userId, getRouteHeader());
    	}
    	if (documentContent != null && documentContent.isModified()) {
    		saveDocumentContent(documentContent);
    	}
    }

    /**
     * Helper to prevent us from needlessly wrapping a WorkflowException in another WorkflowException.
     */
    private WorkflowException handleException(Exception e) {
    	if (e instanceof WorkflowException) {
    		return (WorkflowException)e;
    	}
    	return new WorkflowException(e);
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
     * @see WorkflowDocumentActions#blanketApprovalToNodes(UserIdVO, RouteHeaderVO, String, String[])
     */
    public void blanketApprove(String annotation, String nodeName) throws WorkflowException {
        blanketApprove(annotation, (nodeName == null ? null : new String[] { nodeName }));
    }

    /**
     * Performs the 'blanketApprove' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @param nodeNames the nodes at which blanket approval will stop (in case the blanket approval traverses a split, in which case there may be multiple "active" nodes)
     * @throws WorkflowException in case an error occurs blanket-approving the document
     * @see WorkflowDocumentActions#blanketApprovalToNodes(UserIdVO, RouteHeaderVO, String, String[])
     */
    public void blanketApprove(String annotation, String[] nodeNames) throws WorkflowException {
        try {
            createDocumentIfNeccessary();
            routeHeader = getWorkflowDocumentActions().blanketApprovalToNodes(userId, getRouteHeader(), annotation, nodeNames);
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * The user taking action removes the action items for this workgroup and document from all other
     * group members' action lists.   If this is a new document, the document is created first.
     *
     * @param annotation the message to log for the action
     * @param workgroupId the workgroup on which to take authority
     * @throws WorkflowException user taking action is not in workgroup
     * @see WorkflowDocumentActions#takeWorkgroupAuthority(UserIdVO, RouteHeaderVO, WorkgroupIdVO, String)
     */
    public void takeWorkgroupAuthority(String annotation, WorkgroupIdVO workgroupId) throws WorkflowException {
        try {
            createDocumentIfNeccessary();
            routeHeader = getWorkflowDocumentActions().takeWorkgroupAuthority(userId, getRouteHeader(), workgroupId, annotation);
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * The user that took the group authority is putting the action items back in the other users action lists.
     * If this is a new document, the document is created first.
     *
     * @param annotation the message to log for the action
     * @param workgroupId the workgroup on which to take authority
     * @throws WorkflowException user taking action is not in workgroup or did not take workgroup authority
     */
    public void releaseWorkgroupAuthority(String annotation, WorkgroupIdVO workgroupId) throws WorkflowException {
        try {
            createDocumentIfNeccessary();
            routeHeader = getWorkflowDocumentActions().releaseWorkgroupAuthority(userId, getRouteHeader(), workgroupId, annotation);
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns names of all active nodes the document is currently at.
     *
     * @return names of all active nodes the document is currently at.
     * @throws WorkflowException if there is an error obtaining the currently active nodes on the document
     * @see WorkflowUtility#getActiveNodeInstances(Long)
     */
    public String[] getNodeNames() throws WorkflowException {
        try {
            RouteNodeInstanceVO[] activeNodeInstances = getWorkflowUtility().getActiveNodeInstances(getRouteHeaderId());
            String[] nodeNames = new String[(activeNodeInstances == null ? 0 : activeNodeInstances.length)];
            for (int index = 0; index < activeNodeInstances.length; index++) {
                nodeNames[index] = activeNodeInstances[index].getName();
            }
            return nodeNames;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Performs the 'returnToPrevious' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @param nodeName the node to return to
     * @throws WorkflowException in case an error occurs returning to previous node
     * @see WorkflowDocumentActions#returnDocumentToPreviousNode(UserIdVO, RouteHeaderVO, ReturnPointVO, String)
     */
    public void returnToPreviousNode(String annotation, String nodeName) throws WorkflowException {
        ReturnPointVO returnPoint = new ReturnPointVO(nodeName);
        returnToPreviousNode(annotation, returnPoint);
    }

    /**
     * Performs the 'returnToPrevious' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @param ReturnPointVO the node to return to
     * @throws WorkflowException in case an error occurs returning to previous node
     * @see WorkflowDocumentActions#returnDocumentToPreviousNode(UserIdVO, RouteHeaderVO, ReturnPointVO, String)
     */
    public void returnToPreviousNode(String annotation, ReturnPointVO returnPoint) throws WorkflowException {
        try {
            createDocumentIfNeccessary();
            routeHeader = getWorkflowDocumentActions().returnDocumentToPreviousNode(userId, getRouteHeader(), returnPoint, annotation);
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Moves the document from a current node in it's route to another node.  If this is a new document,
     * the document is created first.
     * @param MovePointVO VO representing the node at which to start, and the number of steps to move (negative steps is reverse)
     * @param annotation the message to log for the action
     * @throws WorkflowException in case an error occurs moving the document
     * @see WorkflowDocumentActions#moveDocument(UserIdVO, RouteHeaderVO, MovePointVO, String)
     */
    public void moveDocument(MovePointVO movePoint, String annotation) throws WorkflowException {
        try {
            createDocumentIfNeccessary();
            routeHeader =  getWorkflowDocumentActions().moveDocument(userId, getRouteHeader(), movePoint, annotation);
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns the route node instances that have been created so far during the life of this document.  This includes
     * all previous instances which have already been processed and are no longer active.
     * @return the route node instances that have been created so far during the life of this document
     * @throws WorkflowException if there is an error getting the route node instances for the document
     * @see WorkflowUtility#getDocumentRouteNodeInstances(Long)
     */
    public RouteNodeInstanceVO[] getRouteNodeInstances() throws WorkflowException {
        try {
            return getWorkflowUtility().getDocumentRouteNodeInstances(getRouteHeaderId());
        } catch (Exception e) {
            throw handleException(e);
        }
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
		try {
			return getWorkflowUtility().getPreviousRouteNodeNames(getRouteHeaderId());
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

    /**
     * Returns a document detail VO representing the route header along with action requests, actions taken,
     * and route node instances.
     * @return Returns a document detail VO representing the route header along with action requests, actions taken, and route node instances.
     * @throws WorkflowException
     */
    public DocumentDetailVO getDetail() throws WorkflowException {
        try {
            return getWorkflowUtility().getDocumentDetail(getRouteHeaderId());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Saves the given DocumentContentVO for this document.
     * @param documentContent document content VO to store for this document
     * @since 2.3
     * @see WorkflowDocumentActions#saveDocumentContent(DocumentContentVO)
     */
    public DocumentContentVO saveDocumentContent(DocumentContentVO documentContent) throws WorkflowException {
    	try {
    		if (documentContent.getRouteHeaderId() == null) {
    			throw new WorkflowException("Document Content does not have a valid document ID.");
    		}
    		// important to check directly against getRouteHeader().getRouteHeaderId() instead of just getRouteHeaderId() because saveDocumentContent
    		// is called from createDocumentIfNeccessary which is called from getRouteHeaderId().  If that method was used, we would have an infinite loop.
    		if (!documentContent.getRouteHeaderId().equals(getRouteHeader().getRouteHeaderId())) {
    			throw new WorkflowException("Attempted to save content on this document with an invalid document id of " + documentContent.getRouteHeaderId());
    		}
    		DocumentContentVO newDocumentContent = getWorkflowDocumentActions().saveDocumentContent(documentContent);
    		this.documentContent = new ModifiableDocumentContentVO(newDocumentContent);
    		documentContentDirty = false;
    		return this.documentContent;
    	} catch (Exception e) {
    		throw handleException(e);
    	}
    }


    // DEPRECATED: as of Workflow 2.0

    /**
     * @deprecated use getRouteHeader.getInitiator
     */
    public String getInitiatorNetworkId() {
        if (routeHeader.getInitiator() != null) {
            return routeHeader.getInitiator().getNetworkId();
        } else {
            return "";
        }
    }

    // DEPRECATED: as of Workflow 2.1

    /**
     * @deprecated use blanketApprove(String annotation, String nodeName) instead
     */
    public void blanketApprove(String annotation, Integer routeLevel) throws WorkflowException {
        try {
            createDocumentIfNeccessary();
            routeHeader = getWorkflowDocumentActions().blanketApproval(userId, getRouteHeader(), annotation, routeLevel);
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * @deprecated use getNodeNames() instead
     */
    public Integer getDocRouteLevel() {
        return routeHeader.getDocRouteLevel();
    }

    /**
     * TODO this method still needs to be updated to work properly for Workflow 2.1
     * it would probably be easier to just put this info on bean from the server
     *
     * @deprecated use getNodeNames() instead
     */
    public String getDocRouteLevelName() throws WorkflowException {
        try {
            if (getDocumentType() == null) {
                throw new DocumentTypeNotFoundException("Document Type Name is null");
            }
            RouteTemplateEntryVO[] routeLevels = getWorkflowUtility().getDocRoute(getDocumentType());
            for (int i = 0; i < routeLevels.length; i++) {
                if (routeLevels[i].getRouteLevel().equals(getDocRouteLevel())) {
                    return routeLevels[i].getRouteLevelName();
                }
            }
        } catch (Exception e) {
            throw handleException(e);
        }
        throw new WorkflowException("Did not find a route level");
    }

    /**
     * TODO this method still needs to be updated to work properly for Workflow 2.1
     * it would probably be easier to just put this info on bean from the server
     *
     * @deprecated use getRouteMethodNames instead
     */
    public String getRouteMethodName() throws WorkflowException {
        if (getDocumentType() == null) {
            throw new WorkflowException("Document Type Name is null");
        }

        try {
            RouteTemplateEntryVO[] routeLevels = getWorkflowUtility().getDocRoute(getDocumentType());
            for (int i = 0; i < routeLevels.length; i++) {
                if (routeLevels[i].getRouteLevel().equals(getDocRouteLevel())) {
                    return routeLevels[i].getRouteMethodName();
                }
            }
        } catch (Exception e) {
            throw handleException(e);
        }

        throw new WorkflowException("Did not find a route level");
    }

    /**
     * @deprecated use returnToPreviousNode(String annotation, String nodeName) instead
     */
    public void returnToPreviousRouteLevel(String annotation, Integer destRouteLevel) throws WorkflowException {
        try {
            createDocumentIfNeccessary();
            getWorkflowDocumentActions().returnDocumentToPreviousRouteLevel(userId, getRouteHeader(), destRouteLevel, annotation);
            documentContentDirty = true;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * Returns a list of NoteVO representing the notes on the document
     * @return a list of NoteVO representing the notes on the document
     * @see RouteHeaderVO#getNotes()
     */
    public List<NoteVO> getNoteList(){
    	List<NoteVO> notesList = new ArrayList<NoteVO>();
    	NoteVO[] notes = routeHeader.getNotes();
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
    public void deleteNote(NoteVO noteVO){
    	if (noteVO != null && noteVO.getNoteId()!=null){
    		NoteVO noteToDelete = new NoteVO();
    		noteToDelete.setNoteId(new Long(noteVO.getNoteId().longValue()));
    		/*noteToDelete.setRouteHeaderId(noteVO.getRouteHeaderId());
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
    public void updateNote (NoteVO noteVO){
    	boolean isUpdateNote = false;
    	if (noteVO != null){
    		NoteVO[] notes = routeHeader.getNotes();
    		NoteVO  copyNote = new NoteVO();
			if (noteVO.getNoteId() != null){
				copyNote.setNoteId(new Long(noteVO.getNoteId().longValue()));
			}

			if (noteVO.getRouteHeaderId() != null){
				copyNote.setRouteHeaderId(new Long(noteVO.getRouteHeaderId().longValue()));
			} else {
				copyNote.setRouteHeaderId(routeHeader.getRouteHeaderId());
			}

			if (noteVO.getNoteAuthorWorkflowId() != null){
				copyNote.setNoteAuthorWorkflowId(new String(noteVO.getNoteAuthorWorkflowId()));
			} else {
			    copyNote.setNoteAuthorWorkflowId(userId.toString())	;
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
        try {
            createDocumentIfNeccessary();
        } catch (Exception e) {
            throw handleException(e);
        }
        getRouteHeader().setVariable(name, value);
    }

    /**
     * Gets the value of a variable on the document, creating the document first if it does not exist.
     * @param name variable name
     * @return variable value
     */
    public String getVariable(String name) throws WorkflowException {
        try {
            createDocumentIfNeccessary();
        } catch (Exception e) {
            throw handleException(e);
        }
        return getRouteHeader().getVariable(name);
    }

    /**
     * Deletes the note of with the same id as that of the argument on the document.
     * @param noteVO the note to test for deletion
     * @return whether the note is already marked for deletion.
     */
    private boolean isDeletedNote(NoteVO noteVO) {
    	NoteVO[] notesToDelete = routeHeader.getNotesToDelete();
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
	   NoteVO[] tempArray;
	   NoteVO[] notes = routeHeader.getNotes();
	   if (notes == null){
		   tempArray = new NoteVO[1];
	   } else {
		   tempArray = new NoteVO[notes.length + 1];
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
	   NoteVO[] tempArray;
	   NoteVO[] notesToDelete = routeHeader.getNotesToDelete();
	   if (notesToDelete == null){
		   tempArray = new NoteVO[1];
	   } else {
		   tempArray = new NoteVO[notesToDelete.length + 1];
		   for (int i=0; i<notesToDelete.length; i++){
			   tempArray[i] = notesToDelete[i];
		   }
	   }
	   routeHeader.setNotesToDelete(tempArray);
   }
}