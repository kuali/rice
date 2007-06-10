/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.workflow.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.KualiWorkflowInfo;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.UserVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.ResourceUnavailableException;
import edu.iu.uis.eden.exception.WorkflowException;

public class KualiWorkflowDocumentImpl implements KualiWorkflowDocument {

    private WorkflowDocument workflowDocument;

    public KualiWorkflowDocumentImpl(UserIdVO userId, String documentType) throws WorkflowException {
        workflowDocument = new WorkflowDocument(userId, documentType);
    }

    public KualiWorkflowDocumentImpl(UserIdVO userId, Long routeHeaderId) throws WorkflowException {
        workflowDocument = new WorkflowDocument(userId, routeHeaderId);
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
    public void addAttributeDefinition(WorkflowAttributeDefinitionVO attributeDefinition) {
        workflowDocument.addAttributeDefinition(attributeDefinition);
    }

    public void removeAttributeDefinition(WorkflowAttributeDefinitionVO attributeDefinition) {
        workflowDocument.removeAttributeDefinition(attributeDefinition);
    }

    public void clearAttributeDefinitions() {
        workflowDocument.clearAttributeDefinitions();
    }

    public WorkflowAttributeDefinitionVO[] getAttributeDefinitions() {
        return workflowDocument.getAttributeDefinitions();
    }

    /**
     * Adds a searchable attribute definition which defines creation parameters for a SearchableAttribute implementation. The
     * created attribute will be used to generate searchable document content. When the document is sent to the server, this will be
     * appended to the existing searchable doc content. If it is required to replace the searchable document content, then the
     * clearSearchableContent() method should be invoked prior to adding definitions.
     */
    public void addSearchableDefinition(WorkflowAttributeDefinitionVO searchableDefinition) {
        workflowDocument.addSearchableDefinition(searchableDefinition);
    }

    public void removeSearchableDefinition(WorkflowAttributeDefinitionVO searchableDefinition) {
        workflowDocument.removeSearchableDefinition(searchableDefinition);
    }

    public void clearSearchableDefinitions() {
        workflowDocument.clearSearchableDefinitions();
    }

    public WorkflowAttributeDefinitionVO[] getSearchableDefinitions() {
        return workflowDocument.getSearchableDefinitions();
    }

    // ########################
    // END Document Content methods
    // ########################

    public RouteHeaderVO getRouteHeader() {
        return workflowDocument.getRouteHeader();
    }

    public Long getRouteHeaderId() throws WorkflowException {
        return workflowDocument.getRouteHeaderId();
    }

    public void setAppDocId(String appDocId) {
        workflowDocument.setAppDocId(appDocId);
    }

    public String getAppDocId() {
        return workflowDocument.getAppDocId();
    }

    /**
     * @return
     */
    public String getInitiatorNetworkId() {
        // TODO this was done so conditions in documentControls.tag will work
        return workflowDocument.getRouteHeader().getInitiator().getNetworkId();
    }

    /**
     * @return
     */
    public String getInitiatorUuId() {
        return workflowDocument.getRouteHeader().getInitiator().getUuId();
    }
    
    public String getTitle() {
        return workflowDocument.getTitle();
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

    public void cancel(String annotation) throws WorkflowException {
        workflowDocument.cancel(annotation);
    }

    public void blanketApprove(String annotation) throws WorkflowException {
        workflowDocument.blanketApprove(annotation);
    }

    /**
     * 
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#blanketApprove(java.lang.String, java.lang.Integer)
     * @deprecated
     */
    @SuppressWarnings("deprecation")
    public void blanketApprove(String annotation, Integer routeLevel) throws WorkflowException {
        workflowDocument.blanketApprove(annotation, routeLevel);
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

    public void appSpecificRouteDocumentToUser(String actionRequested, String routeTypeName, int priority, String annotation, UserIdVO recipient, String responsibilityDesc, boolean ignorePreviousActions) throws WorkflowException {
        workflowDocument.appSpecificRouteDocumentToUser(actionRequested, routeTypeName, priority, annotation, recipient, responsibilityDesc, ignorePreviousActions);
    }

    public void appSpecificRouteDocumentToWorkgroup(String actionRequested, String routeTypeName, int priority, String annotation, WorkgroupIdVO workgroupId, String responsibilityDesc, boolean ignorePreviousActions) throws WorkflowException {
        workflowDocument.appSpecificRouteDocumentToWorkgroup(actionRequested, routeTypeName, priority, annotation, workgroupId, responsibilityDesc, ignorePreviousActions);
    }

    public void setTitle(String title) throws WorkflowException {
        workflowDocument.setTitle(title);
    }

    public String getDocumentType() {
        return workflowDocument.getDocumentType();
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isAdHocRequested()
     */
    public boolean isAdHocRequested() {
        boolean isAdHocRequested = false;
        Long routeHeaderId = null;
        KualiWorkflowInfo workflowInfo = null;
        try {
            routeHeaderId = getRouteHeaderId();
            workflowInfo = KNSServiceLocator.getWorkflowInfoService();
            UserVO currentUser = workflowInfo.getWorkflowUser(workflowDocument.getUserId());
            ActionRequestVO[] actionRequests = workflowInfo.getActionRequests(routeHeaderId);
            for (int actionRequestIndex = 0; actionRequestIndex < actionRequests.length; actionRequestIndex++) {
                if (actionRequests[actionRequestIndex].isActivated() && actionRequests[actionRequestIndex].isAdHocRequest()) {
                    if (actionRequests[actionRequestIndex].isUserRequest() && currentUser.getWorkflowId().equals(actionRequests[actionRequestIndex].getUserVO().getWorkflowId())) {
                        isAdHocRequested = true;
                    }
                    else if (actionRequests[actionRequestIndex].isWorkgroupRequest()) {
                        for (int workgroupMemberIndex = 0; workgroupMemberIndex < actionRequests[actionRequestIndex].getWorkgroupVO().getMembers().length; workgroupMemberIndex++) {
                            if (currentUser.getWorkflowId().equals(actionRequests[actionRequestIndex].getWorkgroupVO().getMembers()[workgroupMemberIndex].getWorkflowId())) {
                                isAdHocRequested = true;
                            }
                        }
                    }
                }
            }
        }
        catch (WorkflowException e) {
            throw new RuntimeException(new StringBuffer(getClass().getName()).append(" encountered an exception while attempting to get the actoins requests for routeHeaderId: ").append(routeHeaderId).toString(), e);
        }
        return isAdHocRequested;
    }

    /**
     * 
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isAcknowledgeRequested()
     */
    public boolean isAcknowledgeRequested() {
        return workflowDocument.isAcknowledgeRequested();
    }

    /**
     * 
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isApprovalRequested()
     */
    public boolean isApprovalRequested() {
        return workflowDocument.isApprovalRequested();
    }

    /**
     * 
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isCompletionRequested()
     */
    public boolean isCompletionRequested() {
        return workflowDocument.isCompletionRequested();
    }

    /**
     * 
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isFYIRequested()
     */
    public boolean isFYIRequested() {
        return workflowDocument.isFYIRequested();
    }

    /**
     * 
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#isBlanketApproveCapable()
     */
    public boolean isBlanketApproveCapable() {
        return workflowDocument.isBlanketApproveCapable();
    }

    @SuppressWarnings("deprecation")
    public Integer getDocRouteLevel() {
        return workflowDocument.getDocRouteLevel();
    }

    /**
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getDocRouteLevelName()
     * @deprecated
     */
    // it would probably be easier to just put this info on bean from the server
    @SuppressWarnings("deprecation")
    public String getDocRouteLevelName() throws WorkflowException {
        return workflowDocument.getDocRouteLevelName();
    }

    /**
     * 
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#getRouteTypeName()
     * @deprecated
     */
    // it would probably be easier to just put this info on bean from the server
    @SuppressWarnings("deprecation")
    public String getRouteTypeName() throws WorkflowException {
        return workflowDocument.getRouteMethodName();
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
     * @see org.kuali.core.workflow.service.KualiWorkflowDocument#userIsInitiator(org.kuali.core.bo.user.KualiUser)
     */
    public boolean userIsInitiator(UniversalUser user) {
        if (user == null) {
            throw new IllegalArgumentException("invalid (null) user");
        }

        return StringUtils.equalsIgnoreCase(getInitiatorUuId(), user.getPersonUniversalIdentifier());
    }

    public String[] getNodeNames() throws WorkflowException {
        return workflowDocument.getNodeNames();
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
}