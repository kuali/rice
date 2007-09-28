/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.core.workflow.service;

import java.sql.Timestamp;

import org.kuali.core.bo.user.UniversalUser;

import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.ResourceUnavailableException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;

public interface KualiWorkflowDocument {

    public abstract String getApplicationContent();

    /**
     * Sets the application specific document content.
     */
    public abstract void setApplicationContent(String applicationContent);

    /**
     * Clears all attribute document content from the document. Typically, this will be used if it is necessary to update the
     * attribute doc content on the document. This can be accomplished by clearing the content and then adding the desired attribute
     * definitions.
     * 
     * In order for these changes to take effect, an action must be performed on the document (such as "save").
     */
    public abstract void clearAttributeContent();

    /**
     * Returns the attribute-generated document content.
     */
    public abstract String getAttributeContent();

    /**
     * Adds an attribute definition which defines creation parameters for a WorkflowAttribute implementation. The created attribute
     * will be used to generate attribute document content. When the document is sent to the server, this will be appended to the
     * existing attribute doc content. If it is required to replace the attribute document content, then the clearAttributeContent()
     * method should be invoked prior to adding attribute definitions.
     */
    public abstract void addAttributeDefinition(WorkflowAttributeDefinitionVO attributeDefinition);

    public abstract void removeAttributeDefinition(WorkflowAttributeDefinitionVO attributeDefinition);

    public abstract void clearAttributeDefinitions();

    public abstract WorkflowAttributeDefinitionVO[] getAttributeDefinitions();

    /**
     * Adds a searchable attribute definition which defines creation parameters for a SearchableAttribute implementation. The
     * created attribute will be used to generate searchable document content. When the document is sent to the server, this will be
     * appended to the existing searchable doc content. If it is required to replace the searchable document content, then the
     * clearSearchableContent() method should be invoked prior to adding definitions.
     */
    public abstract void addSearchableDefinition(WorkflowAttributeDefinitionVO searchableDefinition);

    public abstract void removeSearchableDefinition(WorkflowAttributeDefinitionVO searchableDefinition);

    public abstract void clearSearchableDefinitions();

    public abstract WorkflowAttributeDefinitionVO[] getSearchableDefinitions();

    // ########################
    public abstract RouteHeaderVO getRouteHeader();

    public abstract Long getRouteHeaderId() throws WorkflowException;

    public abstract void setAppDocId(String appDocId);

    public abstract String getAppDocId();

    public abstract String getInitiatorNetworkId();

    public abstract String getRoutedByUserNetworkId();

    public abstract String getTitle();

    public abstract void saveDocument(String annotation) throws WorkflowException;

    public abstract void routeDocument(String annotation) throws WorkflowException;

    public abstract void disapprove(String annotation) throws WorkflowException;

    public abstract void approve(String annotation) throws WorkflowException;

    public abstract void superUserApprove(String annotation) throws WorkflowException;

    public void superUserActionRequestApprove(Long actionRequestId, String annotation) throws WorkflowException;

    public void superUserCancel(String annotation) throws WorkflowException;

    public void superUserDisapprove(String annotation) throws WorkflowException;

    public abstract void cancel(String annotation) throws WorkflowException;

    public abstract void blanketApprove(String annotation) throws WorkflowException;

    public abstract void blanketApprove(String annotation, Integer routeLevel) throws WorkflowException;

    public abstract void saveRoutingData() throws WorkflowException;

    public abstract void acknowledge(String annotation) throws WorkflowException;

    public abstract void fyi() throws WorkflowException;

    public abstract void delete() throws WorkflowException;

    public abstract void refreshContent() throws WorkflowException;

    public abstract void appSpecificRouteDocumentToUser(String actionRequested, String routeTypeName, int priority, String annotation, UserIdVO recipient, String responsibilityDesc, boolean ignorePreviousActions) throws WorkflowException;

    public abstract void appSpecificRouteDocumentToWorkgroup(String actionRequested, String routeTypeName, int priority, String annotation, WorkgroupIdVO workgroupId, String responsibilityDesc, boolean ignorePreviousActions) throws WorkflowException;

    public abstract void setTitle(String title) throws WorkflowException;

    public abstract String getDocumentType();

    /**
     * This method determines whether workflowDocument.getUserId() currently has an ad hoc request is his/her action list
     * 
     * @return boolean indicating whether there is an active ad hoc request for this user
     */
    public boolean isAdHocRequested();

    /**
     * 
     * Returns true if the user currently has this document in their Action List with an Acknowledge Request.
     * 
     * The user in this case is whatever user was passed in when the document was loaded. This is typically the current webapp user.
     * 
     * @return true if the user has the document in their Action List waiting for an Acknowledgement, false otherwise.
     * 
     */
    public abstract boolean isAcknowledgeRequested();

    /**
     * 
     * Returns true if the user currently has this document in their Action List with an Approval Request.
     * 
     * The user in this case is whatever user was passed in when the document was loaded. This is typically the current webapp user.
     * 
     * @return true if the user has the document in their Action List waiting for an Approval, false otherwise.
     * 
     */
    public abstract boolean isApprovalRequested();

    /**
     * 
     * Returns true if the user currently has this document in their Action List with an Completion Request.
     * 
     * The user in this case is whatever user was passed in when the document was loaded. This is typically the current webapp user.
     * 
     * @return true if the user has the document in their Action List waiting for a Completion, false otherwise.
     * 
     */
    public abstract boolean isCompletionRequested();

    /**
     * 
     * Returns true if the user currently has this document in their Action List with an FYI.
     * 
     * The user in this case is whatever user was passed in when the document was loaded. This is typically the current webapp user.
     * 
     * @return true if the user has the document in their Action List waiting for an FYI, false otherwise.
     * 
     */
    public abstract boolean isFYIRequested();

    /**
     * 
     * Returns true if the user is authorized to Blanket Approve this document.
     * 
     * The user in this case is whatever user was passed in when the document was loaded. This is typically the current webapp user.
     * 
     * @return true if the user is authorized to Blanket Approve this document, false otherwise.
     * 
     */
    public abstract boolean isBlanketApproveCapable();

    /**
     * Checks to see if this document is allowed to have a standard 'save' performed
     * 
     * @return true if the saveDocument() method is valid to be called.... false otherwise
     */
    public boolean isStandardSaveAllowed();

    public abstract Integer getDocRouteLevel();

    // it would probably be easier to just put this info on bean from the server
    public abstract String getDocRouteLevelName() throws WorkflowException;

    // it would probably be easier to just put this info on bean from the server
    public abstract String getRouteTypeName() throws WorkflowException;

    /**
     * @param annotation
     * @throws InvalidActionTakenException
     * @throws ResourceUnavailableException
     * @throws WorkflowException
     */
    public abstract void complete(String annotation) throws WorkflowException;

    /**
     * @param annotation
     * @param destRouteLevel
     * @throws WorkflowException
     * @throws InvalidActionTakenException
     * @throws ResourceUnavailableException
     */
    public abstract void returnToPreviousRouteLevel(String annotation, Integer destRouteLevel) throws WorkflowException;

    public abstract void logDocumentAction(String annotation) throws WorkflowException;

    /**
     * Indicates if the document is in the initated state or not.
     * 
     * @return true if in the specified state
     */
    public abstract boolean stateIsInitiated();

    /**
     * Indicates if the document is in the saved state or not.
     * 
     * @return true if in the specified state
     */
    public abstract boolean stateIsSaved();

    /**
     * Indicates if the document is in the enroute state or not.
     * 
     * @return true if in the specified state
     */
    public abstract boolean stateIsEnroute();


    /**
     * Indicates if the document is in the final state or not.
     * 
     * @return true if in the specified state
     */
    public abstract boolean stateIsFinal();

    /**
     * Indicates if the document is in the exception state or not.
     * 
     * @return true if in the specified state
     */
    public abstract boolean stateIsException();

    /**
     * Indicates if the document is in the canceled state or not.
     * 
     * @return true if in the specified state
     */
    public abstract boolean stateIsCanceled();

    /**
     * Indicates if the document is in the disapproved state or not.
     * 
     * @return true if in the specified state
     */
    public abstract boolean stateIsDisapproved();

    /**
     * Indicates if the document is in the approved state or not. Will answer true is document is in Processed or Finalized state.
     * 
     * @return true if in the specified state
     */
    public abstract boolean stateIsApproved();

    /**
     * Indicates if the document is in the processed state or not.
     * 
     * @return true if in the specified state
     */
    public abstract boolean stateIsProcessed();

    public abstract String getStatusDisplayValue();

    public abstract Timestamp getCreateDate();


    /**
     * Returns true if the personUserIdentifier of the given KualiUser matches the initiatorNetworkId of this document
     * 
     * @param user
     * @return true if the given user is the initiator of this document
     */
    public boolean userIsInitiator(UniversalUser user);

    /**
     * Returns true if the personUserIdentifier of the given KualiUser matches the routedByUserNetworkId of this document
     * 
     * @param user
     * @return true if the given user is the user who routed this document
     */
    public boolean userIsRoutedByUser(UniversalUser user);
    
    /**
     * Returns the names of the nodes that the document is currently at.
     */
    public String[] getNodeNames() throws WorkflowException;

    /**
     * Returns the current node names of the document delimited by the constant: 
     * {@link DocumentRouteHeaderValue#CURRENT_ROUTE_NODE_NAME_DELIMITER}
     */
    public String getCurrentRouteNodeNames();
}