/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kns.workflow.service.impl;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.ActionTakenDTO;
import org.kuali.rice.kew.dto.ReturnPointDTO;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.exception.InvalidActionTakenException;
import org.kuali.rice.kew.exception.ResourceUnavailableException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.service.WorkflowDocumentActions;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.KualiWorkflowInfo;

public class KualiWorkflowDocumentImpl implements KualiWorkflowDocument, Serializable {

    private WorkflowDocument workflowDocument;
    
    public static KualiWorkflowDocumentImpl createKualiDocumentImpl(String principalId, String documentType) throws WorkflowException {
		return new KualiWorkflowDocumentImpl(principalId, documentType, null);
	}
    
	public static KualiWorkflowDocumentImpl LoadKualiDocumentImpl(String principalId, String documentId) throws WorkflowException {
		return new KualiWorkflowDocumentImpl(principalId, null, documentId);
	}
        
    private KualiWorkflowDocumentImpl(String principalId, String documentType, String documentId) throws WorkflowException {
        if (StringUtils.isNotBlank(documentType)) { 
            workflowDocument = WorkflowDocument.createDocument(principalId, documentType);
        } else if (StringUtils.isNotBlank(documentId)) {
            workflowDocument = WorkflowDocument.loadDocument(principalId, documentId);
    	}
    }
    
    // ########################
    // Document Content methods
    // ########################

    /**
     * Returns the application specific document content.
     * 
     * For documents routed prior to Workflow 2.0: If the application did NOT use attributes for XML generation, this method will
     * return the entire document content XML. Otherwise it will return the empty string.
     */
    public String getApplicationContent() {
        return workflowDocument.getApplicationContent();
    }

    /**
     * Sets the application specific document content.
     */
    public void setApplicationContent(String applicationContent) {
        workflowDocument.setApplicationContent(applicationContent);
    }

    /**
     * Clears all attribute document content from the document. Typically, this will be used if it is necessary to update the
     * attribute doc content on the document. This can be accomplished by clearing the content and then adding the desired attribute
     * definitions.
     * 
     * In order for these changes to take effect, an action must be performed on the document (such as "save").
     */
    public void clearAttributeContent() {
        workflowDocument.clearAttributeContent();
    }

    /**
     * Returns the attribute-generated document content.
     */
    public String getAttributeContent() {
        return workflowDocument.getAttributeContent();
    }

    /**
     * Adds an attribute definition which defines creation parameters for a WorkflowAttribute implementation. The created attribute
     * will be used to generate attribute document content. When the document is sent to the server, this will be appended to the
     * existing attribute doc content. If it is required to replace the attribute document content, then the clearAttributeContent()
     * method should be invoked prior to adding attribute definitions.
     */
    public void addAttributeDefinition(WorkflowAttributeDefinitionDTO attributeDefinition) {
        workflowDocument.addAttributeDefinition(attributeDefinition);
    }

    public void removeAttributeDefinition(WorkflowAttributeDefinitionDTO attributeDefinition) {
        workflowDocument.removeAttributeDefinition(attributeDefinition);
    }

    public void clearAttributeDefinitions() {
        workflowDocument.clearAttributeDefinitions();
    }

    public WorkflowAttributeDefinitionDTO[] getAttributeDefinitions() {
        return workflowDocument.getAttributeDefinitions();
    }

    /**
     * Adds a searchable attribute definition which defines creation parameters for a SearchableAttribute implementation. The
     * created attribute will be used to generate searchable document content. When the document is sent to the server, this will be
     * appended to the existing searchable doc content. If it is required to replace the searchable document content, then the
     * clearSearchableContent() method should be invoked prior to adding definitions.
     */
    public void addSearchableDefinition(WorkflowAttributeDefinitionDTO searchableDefinition) {
        workflowDocument.addSearchableDefinition(searchableDefinition);
    }

    public void removeSearchableDefinition(WorkflowAttributeDefinitionDTO searchableDefinition) {
        workflowDocument.removeSearchableDefinition(searchableDefinition);
    }

    public void clearSearchableDefinitions() {
        workflowDocument.clearSearchableDefinitions();
    }

    public WorkflowAttributeDefinitionDTO[] getSearchableDefinitions() {
        return workflowDocument.getSearchableDefinitions();
    }

    // ########################
    // END Document Content methods
    // ########################

    public RouteHeaderDTO getRouteHeader() {
        return workflowDocument.getRouteHeader();
    }

    public String getDocumentId() throws WorkflowException {
        return workflowDocument.getDocumentId();
    }

    public void setAppDocId(String appDocId) {
        workflowDocument.setAppDocId(appDocId);
    }

    public String getAppDocId() {
        return workflowDocument.getAppDocId();
    }
    
    public String getTitle() {
        return workflowDocument.getTitle();
    }
    
    public String getInitiatorPrincipalId() {
    	return workflowDocument.getRouteHeader().getInitiatorPrincipalId();
    }
    
    public String getRoutedByPrincipalId() {
    	return workflowDocument.getRouteHeader().getRoutedByPrincipalId();
    }

    public void saveDocument(String annotation) throws WorkflowException {
        workflowDocument.saveDocument(annotation);
    }

    public void routeDocument(String annotation) throws WorkflowException {
        workflowDocument.routeDocument(annotation);
    }

    public void disapprove(String annotation) throws WorkflowException {
        workflowDocument.disapprove(annotation);
    }

    public void approve(String annotation) throws WorkflowException {
        workflowDocument.approve(annotation);
    }

    public void superUserApprove(String annotation) throws WorkflowException {
        workflowDocument.superUserApprove(annotation);
    }

    public void superUserActionRequestApprove(Long actionRequestId, String annotation) throws WorkflowException {
	workflowDocument.superUserActionRequestApprove(actionRequestId, annotation);
    }

    public void superUserCancel(String annotation) throws WorkflowException {
        workflowDocument.superUserCancel(annotation);
    }

    public void superUserDisapprove(String annotation) throws WorkflowException {
        workflowDocument.superUserDisapprove(annotation);
    }

    public void cancel(String annotation) throws WorkflowException {
        workflowDocument.cancel(annotation);
    }

    public void blanketApprove(String annotation) throws WorkflowException {
        workflowDocument.blanketApprove(annotation);
    }

    public void saveRoutingData() throws WorkflowException {
        workflowDocument.saveRoutingData();
    }

    public void acknowledge(String annotation) throws WorkflowException {
        workflowDocument.acknowledge(annotation);
    }

    public void fyi() throws WorkflowException {
        workflowDocument.fyi();
    }

    public void delete() throws WorkflowException {
        workflowDocument.delete();
    }

    public void refreshContent() throws WorkflowException {
        workflowDocument.refreshContent();
    }

    public void adHocRouteDocumentToPrincipal(String actionRequested, String routeTypeName, String annotation, String principalId, String responsibilityDesc, boolean forceAction) throws WorkflowException {
    	workflowDocument.adHocRouteDocumentToPrincipal(actionRequested, routeTypeName, annotation, principalId, responsibilityDesc, forceAction);
    }

    public void adHocRouteDocumentToGroup(String actionRequested, String routeTypeName, String annotation, String groupId, String responsibilityDesc, boolean forceAction) throws WorkflowException {
        workflowDocument.adHocRouteDocumentToGroup(actionRequested, routeTypeName, annotation, groupId, responsibilityDesc, forceAction);
    }

    public void adHocRouteDocumentToPrincipal(String actionRequested, String routeTypeName, String annotation, String principalId, String responsibilityDesc, boolean forceAction, String actionRequestLabel) throws WorkflowException {
    	workflowDocument.adHocRouteDocumentToPrincipal(actionRequested, routeTypeName, annotation, principalId, responsibilityDesc, forceAction, actionRequestLabel);
    }

    public void adHocRouteDocumentToGroup(String actionRequested, String routeTypeName, String annotation, String groupId, String responsibilityDesc, boolean forceAction, String actionRequestLabel) throws WorkflowException {
    	workflowDocument.adHocRouteDocumentToGroup(actionRequested, routeTypeName, annotation, groupId, responsibilityDesc, forceAction, actionRequestLabel);
    }
    
    public void setTitle(String title) throws WorkflowException {
        workflowDocument.setTitle(title);
    }

    public String getDocumentType() {
        return workflowDocument.getDocumentType();
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isAdHocRequested()
     */
    public boolean isAdHocRequested() {
        boolean isAdHocRequested = false;
        String documentId = null;
        KualiWorkflowInfo workflowInfo = null;
        try {
            documentId = getDocumentId();
            workflowInfo = KNSServiceLocatorWeb.getWorkflowInfoService();
            String principalId = workflowDocument.getPrincipalId();
            ActionRequestDTO[] actionRequests = workflowInfo.getActionRequests(documentId);
            for (int actionRequestIndex = 0; actionRequestIndex < actionRequests.length; actionRequestIndex++) {
                if (actionRequests[actionRequestIndex].isActivated() && actionRequests[actionRequestIndex].isAdHocRequest()) {
                    if (actionRequests[actionRequestIndex].isUserRequest() && principalId.equals(actionRequests[actionRequestIndex].getPrincipalId())) {
                        isAdHocRequested = true;
                    }
                    else if (actionRequests[actionRequestIndex].isGroupRequest()) {
                    	if (KimApiServiceLocator.getIdentityManagementService().isMemberOfGroup(principalId, actionRequests[actionRequestIndex].getGroupId())) {
                    		isAdHocRequested = true;
                    	}
                    }
                }
            }
        }
        catch (WorkflowException e) {
            throw new RuntimeException(new StringBuffer(getClass().getName()).append(" encountered an exception while attempting to get the actoins requests for documentId: ").append(documentId).toString(), e);
        }
        return isAdHocRequested;
    }

    /**
     * 
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isAcknowledgeRequested()
     */
    public boolean isAcknowledgeRequested() {
        return workflowDocument.isAcknowledgeRequested();
    }

    /**
     * 
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isApprovalRequested()
     */
    public boolean isApprovalRequested() {
        return workflowDocument.isApprovalRequested();
    }

    /**
     * 
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isCompletionRequested()
     */
    public boolean isCompletionRequested() {
        return workflowDocument.isCompletionRequested();
    }

    /**
     * 
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isFYIRequested()
     */
    public boolean isFYIRequested() {
        return workflowDocument.isFYIRequested();
    }

    /**
     * 
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isBlanketApproveCapable()
     */
    public boolean isBlanketApproveCapable() {
        return workflowDocument.isBlanketApproveCapable();
    }

    @SuppressWarnings("deprecation")
    public Integer getDocRouteLevel() {
        return workflowDocument.getDocRouteLevel();
    }

    /**
     * @param annotation
     * @throws InvalidActionTakenException
     * @throws ResourceUnavailableException
     * @throws WorkflowException
     */
    public void complete(String annotation) throws WorkflowException {
        workflowDocument.complete(annotation);
    }

    /**
     * Performs the 'returnToPrevious' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @param nodeName the node to return to
     * @throws WorkflowException in case an error occurs returning to previous node
     * @see WorkflowDocumentActions#returnDocumentToPreviousNode(String, RouteHeaderDTO, ReturnPointDTO, String)
     */
    public void returnToPreviousNode(String annotation, String nodeName) throws WorkflowException {
        workflowDocument.returnToPreviousNode(annotation, nodeName);
    }

    /**
     * Performs the 'returnToPrevious' action on the document this WorkflowDocument represents.  If this is a new document,
     * the document is created first.
     * @param annotation the message to log for the action
     * @param ReturnPointDTO the node to return to
     * @throws WorkflowException in case an error occurs returning to previous node
     * @see WorkflowDocumentActions#returnDocumentToPreviousNode(String, RouteHeaderDTO, ReturnPointDTO, String)
     */
    public void returnToPreviousNode(String annotation, ReturnPointDTO returnPoint) throws WorkflowException {
        workflowDocument.returnToPreviousNode(annotation, returnPoint);
    }

    /**
     * @param annotation
     * @param destRouteLevel
     * @throws WorkflowException
     * @throws InvalidActionTakenException
     * @throws ResourceUnavailableException
     * @deprecated
     */
    @SuppressWarnings("deprecation")
    public void returnToPreviousRouteLevel(String annotation, Integer destRouteLevel) throws WorkflowException {
        workflowDocument.returnToPreviousRouteLevel(annotation, destRouteLevel);
    }

    public void logDocumentAction(String annotation) throws WorkflowException {
        workflowDocument.logDocumentAction(annotation);
    }

    /**
     * Indicates if the document is in the initated state or not.
     * 
     * @return true if in the specified state
     */
    public boolean stateIsInitiated() {
        return workflowDocument.stateIsInitiated();
    }

    /**
     * Indicates if the document is in the saved state or not.
     * 
     * @return true if in the specified state
     */
    public boolean stateIsSaved() {
        return workflowDocument.stateIsSaved();
    }

    /**
     * Indicates if the document is in the enroute state or not.
     * 
     * @return true if in the specified state
     */
    public boolean stateIsEnroute() {
        return workflowDocument.stateIsEnroute();
    }

    /**
     * Indicates if the document is in the final state or not.
     * 
     * @return true if in the specified state
     */
    public boolean stateIsFinal() {
        return workflowDocument.stateIsFinal();
    }

    /**
     * Indicates if the document is in the exception state or not.
     * 
     * @return true if in the specified state
     */
    public boolean stateIsException() {
        return workflowDocument.stateIsException();
    }

    /**
     * Indicates if the document is in the canceled state or not.
     * 
     * @return true if in the specified state
     */
    public boolean stateIsCanceled() {
        return workflowDocument.stateIsCanceled();
    }

    /**
     * Indicates if the document is in the disapproved state or not.
     * 
     * @return true if in the specified state
     */
    public boolean stateIsDisapproved() {
        return workflowDocument.stateIsDisapproved();
    }

    /**
     * Indicates if the document is in the approved state or not. Will answer true is document is in Processed or Finalized state.
     * 
     * @return true if in the specified state
     */
    public boolean stateIsApproved() {
        return workflowDocument.stateIsApproved();
    }

    /**
     * Indicates if the document is in the processed state or not.
     * 
     * @return true if in the specified state
     */
    public boolean stateIsProcessed() {
        return workflowDocument.stateIsProcessed();
    }

    public String getStatusDisplayValue() {
        return workflowDocument.getStatusDisplayValue();
    }

    public Timestamp getCreateDate() {
        return workflowDocument.getDateCreated();
    }


    /**
     * 
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#userIsInitiator(org.kuali.rice.kns.bo.user.KualiUser)
     */
    public boolean userIsInitiator(Person user) {
        if (user == null) {
            throw new IllegalArgumentException("invalid (null) user");
        }

        return StringUtils.equalsIgnoreCase(getInitiatorPrincipalId(), user.getPrincipalId());
    }

    /**
     * 
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#userIsRoutedByUser(org.kuali.rice.kns.bo.user.KualiUser)
     */
    public boolean userIsRoutedByUser(Person user) {
        if (user == null) {
            throw new IllegalArgumentException("invalid (null) user");
        }

        return StringUtils.equalsIgnoreCase(getRoutedByPrincipalId(), user.getPrincipalId());
    }

    public String[] getNodeNames() throws WorkflowException {
        return workflowDocument.getNodeNames();
    }

    public String getCurrentRouteNodeNames() {
	return workflowDocument.getRouteHeader().getCurrentRouteNodeNames();
    }

    public boolean isStandardSaveAllowed() {
        return workflowDocument.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_SAVED_CD);
    }

    public void setReceiveFutureRequests() throws WorkflowException {
        workflowDocument.setReceiveFutureRequests();
    }
    
    public void setDoNotReceiveFutureRequests() throws WorkflowException {
        workflowDocument.setDoNotReceiveFutureRequests();
    }
    
    public void setClearFutureRequests() throws WorkflowException {
        workflowDocument.setClearFutureRequests();  
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        ArrayList trueFlags = new ArrayList();
        if (isAcknowledgeRequested()) {
            trueFlags.add("acknowledgeRequested");
        }
        if (isApprovalRequested()) {
            trueFlags.add("approvalRequested");
        }
        if (isBlanketApproveCapable()) {
            trueFlags.add("blanketApproveCapable");
        }
        if (isCompletionRequested()) {
            trueFlags.add("completionRequested");
        }
        if (isFYIRequested()) {
            trueFlags.add("FYIRequested");
        }

        if (stateIsApproved()) {
            trueFlags.add("stateIsApproved");
        }
        if (stateIsCanceled()) {
            trueFlags.add("stateIsCanceled");
        }
        if (stateIsDisapproved()) {
            trueFlags.add("stateIsDisapproved");
        }
        if (stateIsEnroute()) {
            trueFlags.add("stateIsEnroute");
        }
        if (stateIsException()) {
            trueFlags.add("stateIsException");
        }
        if (stateIsFinal()) {
            trueFlags.add("stateIsFinal");
        }
        if (stateIsInitiated()) {
            trueFlags.add("stateIsInitiated");
        }
        if (stateIsProcessed()) {
            trueFlags.add("stateIsProcessed");
        }
        if (stateIsSaved()) {
            trueFlags.add("stateIsSaved");
        }

        StringBuffer b = new StringBuffer("true flags=(");
        for (Iterator i = trueFlags.iterator(); i.hasNext();) {
            b.append(i.next());
            if (i.hasNext()) {
                b.append(",");
            }
        }
        b.append(")");

        return b.toString();
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getAllPriorApprovers()
     */
    public Set<Person> getAllPriorApprovers() throws WorkflowException {
        org.kuali.rice.kim.service.PersonService personService = KimApiServiceLocator.getPersonService();
        ActionTakenDTO[] actionsTaken = workflowDocument.getActionsTaken();
        Set<String> principalIds = new HashSet<String>();
        Set<Person> persons = new HashSet<Person>();
        
        for (ActionTakenDTO actionTaken : actionsTaken) {
            if (KEWConstants.ACTION_TAKEN_APPROVED_CD.equals(actionTaken.getActionTaken())) {
                String principalId = actionTaken.getPrincipalId();
                if (!principalIds.contains(principalId)) {
                    principalIds.add(principalId);
                    persons.add(personService.getPerson(principalId));
                }
            }
        }
        return persons;
    }
}
