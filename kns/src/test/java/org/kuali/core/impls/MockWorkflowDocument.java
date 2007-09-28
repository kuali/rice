/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.core.impls;

import java.sql.Timestamp;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.DocumentService;
import org.kuali.core.workflow.service.KualiWorkflowDocument;

import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class is the base class for a MockWorkflowDocument. It can be extended by any other kind of mock document that needs to
 * override certain methods.
 * 
 * This class has absolutely no state or behavior. There is no public constructor, and no member variables. All void methods do
 * nothing. All methods with a return value return null.
 * 
 * All state and behavior needs to be added via a subclass.
 * 
 * 
 */
public abstract class MockWorkflowDocument implements KualiWorkflowDocument {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentService.class);

    /**
     * Constructs a MockWorkflowDocument.java.
     */
    protected MockWorkflowDocument() {
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isStandardSaveAllowed()
     */
    public boolean isStandardSaveAllowed() {
    	LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getApplicationContent()
     */
    public String getApplicationContent() {
	LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#setApplicationContent(java.lang.String)
     */
    public void setApplicationContent(String applicationContent) {
	LOG.debug("Using MockWorkflowDocument");
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#clearAttributeContent()
     */
    public void clearAttributeContent() {
	LOG.debug("Using MockWorkflowDocument");
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getAttributeContent()
     */
    public String getAttributeContent() {
	LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#addAttributeDefinition(edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO)
     */
    public void addAttributeDefinition(WorkflowAttributeDefinitionVO attributeDefinition) {
	LOG.debug("Using MockWorkflowDocument");
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#removeAttributeDefinition(edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO)
     */
    public void removeAttributeDefinition(WorkflowAttributeDefinitionVO attributeDefinition) {
	LOG.debug("Using MockWorkflowDocument");
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#clearAttributeDefinitions()
     */
    public void clearAttributeDefinitions() {
	LOG.debug("Using MockWorkflowDocument");
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getAttributeDefinitions()
     */
    public WorkflowAttributeDefinitionVO[] getAttributeDefinitions() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#addSearchableDefinition(edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO)
     */
    public void addSearchableDefinition(WorkflowAttributeDefinitionVO searchableDefinition) {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#removeSearchableDefinition(edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO)
     */
    public void removeSearchableDefinition(WorkflowAttributeDefinitionVO searchableDefinition) {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#clearSearchableDefinitions()
     */
    public void clearSearchableDefinitions() {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getSearchableDefinitions()
     */
    public WorkflowAttributeDefinitionVO[] getSearchableDefinitions() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getRouteHeader()
     */
    public RouteHeaderVO getRouteHeader() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getRouteHeaderId()
     */
    public Long getRouteHeaderId() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#setAppDocId(java.lang.String)
     */
    public void setAppDocId(String appDocId) {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getAppDocId()
     */
    public String getAppDocId() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getInitiatorNetworkId()
     */
    public String getInitiatorNetworkId() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getRoutedByUserNetworkId()
     */
    public String getRoutedByUserNetworkId() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getTitle()
     */
    public String getTitle() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#saveDocument(java.lang.String)
     */
    public void saveDocument(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#routeDocument(java.lang.String)
     */
    public void routeDocument(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#disapprove(java.lang.String)
     */
    public void disapprove(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#approve(java.lang.String)
     */
    public void approve(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#superUserApprove(java.lang.String)
     */
    public void superUserApprove(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#superUserActionRequestApprove(java.lang.Long, java.lang.String)
     */
    public void superUserActionRequestApprove(Long actionRequestId, String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");
    }
    
    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#superUserCancel(java.lang.String)
     */
    public void superUserCancel(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#superUserDisapprove(java.lang.String)
     */
    public void superUserDisapprove(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#cancel(java.lang.String)
     */
    public void cancel(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#blanketApprove(java.lang.String)
     */
    public void blanketApprove(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#blanketApprove(java.lang.String, java.lang.Integer)
     */
    public void blanketApprove(String annotation, Integer routeLevel) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#saveRoutingData()
     */
    public void saveRoutingData() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#acknowledge(java.lang.String)
     */
    public void acknowledge(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#fyi()
     */
    public void fyi() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#delete()
     */
    public void delete() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#refreshContent()
     */
    public void refreshContent() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#appSpecificRouteDocumentToUser(java.lang.String, java.lang.String,
     *      int, java.lang.String, edu.iu.uis.eden.clientapp.vo.UserIdVO, java.lang.String, boolean)
     */
    public void appSpecificRouteDocumentToUser(String actionRequested, String routeTypeName, int priority, String annotation, UserIdVO recipient, String responsibilityDesc, boolean ignorePreviousActions) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#appSpecificRouteDocumentToWorkgroup(java.lang.String,
     *      java.lang.String, int, java.lang.String, edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO, java.lang.String, boolean)
     */
    public void appSpecificRouteDocumentToWorkgroup(String actionRequested, String routeTypeName, int priority, String annotation, WorkgroupIdVO workgroupId, String responsibilityDesc, boolean ignorePreviousActions) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#setTitle(java.lang.String)
     */
    public void setTitle(String title) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getDocumentType()
     */
    public String getDocumentType() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isAdHocRequested()
     */
    public boolean isAdHocRequested() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isAcknowledgeRequested()
     */
    public boolean isAcknowledgeRequested() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isApprovalRequested()
     */
    public boolean isApprovalRequested() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isCompletionRequested()
     */
    public boolean isCompletionRequested() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isFYIRequested()
     */
    public boolean isFYIRequested() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isBlanketApproveCapable()
     */
    public boolean isBlanketApproveCapable() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getDocRouteLevel()
     */
    public Integer getDocRouteLevel() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getDocRouteLevelName()
     */
    public String getDocRouteLevelName() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getRouteTypeName()
     */
    public String getRouteTypeName() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#complete(java.lang.String)
     */
    public void complete(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#returnToPreviousRouteLevel(java.lang.String, java.lang.Integer)
     */
    public void returnToPreviousRouteLevel(String annotation, Integer destRouteLevel) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#logDocumentAction(java.lang.String)
     */
    public void logDocumentAction(String annotation) throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");

    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsInitiated()
     */
    public boolean stateIsInitiated() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsSaved()
     */
    public boolean stateIsSaved() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsEnroute()
     */
    public boolean stateIsEnroute() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsFinal()
     */
    public boolean stateIsFinal() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsException()
     */
    public boolean stateIsException() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsCanceled()
     */
    public boolean stateIsCanceled() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsDisapproved()
     */
    public boolean stateIsDisapproved() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsApproved()
     */
    public boolean stateIsApproved() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#stateIsProcessed()
     */
    public boolean stateIsProcessed() {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getStatusDisplayValue()
     */
    public String getStatusDisplayValue() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getCreateDate()
     */
    public Timestamp getCreateDate() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#userIsInitiator(org.kuali.core.bo.user.UniversalUser)
     */
    public boolean userIsInitiator(UniversalUser user) {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }
    
    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#userIsRoutedByUser(org.kuali.core.bo.user.UniversalUser)
     */
    public boolean userIsRoutedByUser(UniversalUser user) {
        LOG.debug("Using MockWorkflowDocument");
        return false;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getNodeNames()
     */
    public String[] getNodeNames() throws WorkflowException {
        LOG.debug("Using MockWorkflowDocument");
        return null;
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getCurrentRouteNodeNames()
     */
    public String getCurrentRouteNodeNames() {
        LOG.debug("Using MockWorkflowDocument");
        return null;
}
}
