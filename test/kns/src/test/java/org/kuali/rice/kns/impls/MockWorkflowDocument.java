/*
 * Copyright 2006-2007 The Kuali Foundation
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
package org.kuali.rice.kns.impls;

import java.sql.Timestamp;
import java.util.Set;

import org.kuali.rice.kew.dto.ReturnPointDTO;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


/**
 * This class is the base class for a MockWorkflowDocument. It can be extended by any other kind of mock document that needs
 * to override certain methods. This class has absolutely no state or behavior. There is no public constructor, and no member
 * variables. All void methods do nothing. All methods with a return value return null. All state and behavior needs to be
 * added via a subclass.
 */
/**
 * This is a description of what this class does - bh79 don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public abstract class MockWorkflowDocument implements KualiWorkflowDocument {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentService.class);

    /**
     * Constructs a MockWorkflowDocument.java.
     */
    protected MockWorkflowDocument() {}

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isStandardSaveAllowed()
     */
    public boolean isStandardSaveAllowed() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getApplicationContent()
     */
    public String getApplicationContent() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#setApplicationContent(java.lang.String)
     */
    public void setApplicationContent(String applicationContent) {
        LOG.debug("Using MockWorkflowDocument");
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#clearAttributeContent()
     */
    public void clearAttributeContent() {
        LOG.debug("Using MockWorkflowDocument");
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getAttributeContent()
     */
    public String getAttributeContent() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#addAttributeDefinition(org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO)
     */
    public void addAttributeDefinition(WorkflowAttributeDefinitionDTO attributeDefinition) {
        LOG.debug("Using MockWorkflowDocument");
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#removeAttributeDefinition(org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO)
     */
    public void removeAttributeDefinition(WorkflowAttributeDefinitionDTO attributeDefinition) {
        LOG.debug("Using MockWorkflowDocument");
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#clearAttributeDefinitions()
     */
    public void clearAttributeDefinitions() {
        LOG.debug("Using MockWorkflowDocument");
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getAttributeDefinitions()
     */
    public WorkflowAttributeDefinitionDTO[] getAttributeDefinitions() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#addSearchableDefinition(org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO)
     */
    public void addSearchableDefinition(WorkflowAttributeDefinitionDTO searchableDefinition) {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#removeSearchableDefinition(org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO)
     */
    public void removeSearchableDefinition(WorkflowAttributeDefinitionDTO searchableDefinition) {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#clearSearchableDefinitions()
     */
    public void clearSearchableDefinitions() {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getSearchableDefinitions()
     */
    public WorkflowAttributeDefinitionDTO[] getSearchableDefinitions() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getRouteHeader()
     */
    public RouteHeaderDTO getRouteHeader() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getRouteHeaderId()
     */
    public Long getRouteHeaderId() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#setAppDocId(java.lang.String)
     */
    public void setAppDocId(String appDocId) {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getAppDocId()
     */
    public String getAppDocId() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getTitle()
     */
    public String getTitle() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#saveDocument(java.lang.String)
     */
    public void saveDocument(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#routeDocument(java.lang.String)
     */
    public void routeDocument(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#disapprove(java.lang.String)
     */
    public void disapprove(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#approve(java.lang.String)
     */
    public void approve(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#superUserApprove(java.lang.String)
     */
    public void superUserApprove(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#superUserActionRequestApprove(java.lang.Long,
     *      java.lang.String)
     */
    public void superUserActionRequestApprove(Long actionRequestId, String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#superUserCancel(java.lang.String)
     */
    public void superUserCancel(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#superUserDisapprove(java.lang.String)
     */
    public void superUserDisapprove(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#cancel(java.lang.String)
     */
    public void cancel(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#blanketApprove(java.lang.String)
     */
    public void blanketApprove(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#blanketApprove(java.lang.String, java.lang.Integer)
     */
    public void blanketApprove(String annotation, Integer routeLevel) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#saveRoutingData()
     */
    public void saveRoutingData() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#acknowledge(java.lang.String)
     */
    public void acknowledge(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#fyi()
     */
    public void fyi() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#delete()
     */
    public void delete() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#refreshContent()
     */
    public void refreshContent() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#adHocRouteDocumentToPrincipal(java.lang.String,
     *      java.lang.String, int, java.lang.String, org.kuali.rice.kew.dto.UserIdDTO, java.lang.String, boolean)
     */
    public void adHocRouteDocumentToPrincipal(String actionRequested, String routeTypeName, String annotation, String principalId, String responsibilityDesc, boolean forceAction) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    public void adHocRouteDocumentToWorkgroup(String actionRequested, String routeTypeName, int priority, String annotation, String groupId, String responsibilityDesc, boolean forceAction) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#setTitle(java.lang.String)
     */
    public void setTitle(String title) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getDocumentType()
     */
    public String getDocumentType() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isAdHocRequested()
     */
    public boolean isAdHocRequested() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isAcknowledgeRequested()
     */
    public boolean isAcknowledgeRequested() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isApprovalRequested()
     */
    public boolean isApprovalRequested() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isCompletionRequested()
     */
    public boolean isCompletionRequested() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isFYIRequested()
     */
    public boolean isFYIRequested() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isBlanketApproveCapable()
     */
    public boolean isBlanketApproveCapable() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getDocRouteLevel()
     */
    public Integer getDocRouteLevel() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getDocRouteLevelName()
     */
    public String getDocRouteLevelName() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getRouteTypeName()
     */
    public String getRouteTypeName() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#complete(java.lang.String)
     */
    public void complete(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#returnToPreviousNode(java.lang.String, java.lang.String)
     */
    public void returnToPreviousNode(String annotation, String nodeName) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#returnToPreviousNode(java.lang.String, org.kuali.rice.kew.dto.ReturnPointDTO)
     */
    public void returnToPreviousNode(String annotation, ReturnPointDTO returnPoint) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#returnToPreviousRouteLevel(java.lang.String,
     *      java.lang.Integer)
     */
    public void returnToPreviousRouteLevel(String annotation, Integer destRouteLevel) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#logDocumentAction(java.lang.String)
     */
    public void logDocumentAction(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsInitiated()
     */
    public boolean stateIsInitiated() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsSaved()
     */
    public boolean stateIsSaved() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsEnroute()
     */
    public boolean stateIsEnroute() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsFinal()
     */
    public boolean stateIsFinal() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsException()
     */
    public boolean stateIsException() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsCanceled()
     */
    public boolean stateIsCanceled() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsDisapproved()
     */
    public boolean stateIsDisapproved() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsApproved()
     */
    public boolean stateIsApproved() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsProcessed()
     */
    public boolean stateIsProcessed() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getStatusDisplayValue()
     */
    public String getStatusDisplayValue() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getCreateDate()
     */
    public Timestamp getCreateDate() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#userIsInitiator(org.kuali.rice.kim.bo.Person)
     */
    public boolean userIsInitiator(Person user) {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#userIsRoutedByUser(org.kuali.rice.kim.bo.Person)
     */
    public boolean userIsRoutedByUser(Person user) {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getNodeNames()
     */
    public String[] getNodeNames() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getCurrentRouteNodeNames()
     */
    public String getCurrentRouteNodeNames() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
}

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getAllPriorApprovers()
     */
    public Set<Person> getAllPriorApprovers() throws WorkflowException {
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#setClearFutureRequests()
     */
    public void setClearFutureRequests() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#setDoNotReceiveFutureRequests()
     */
    public void setDoNotReceiveFutureRequests() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#setReceiveFutureRequests()
     */
    public void setReceiveFutureRequests() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");
    }
    
}

