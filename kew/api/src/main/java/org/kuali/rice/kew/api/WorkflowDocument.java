/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.kew.api;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.action.AdHocRevoke;
import org.kuali.rice.kew.api.action.AdHocToGroup;
import org.kuali.rice.kew.api.action.AdHocToPrincipal;
import org.kuali.rice.kew.api.action.MovePoint;
import org.kuali.rice.kew.api.action.RequestedActions;
import org.kuali.rice.kew.api.action.ReturnPoint;
import org.kuali.rice.kew.api.action.ValidActions;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContent;
import org.kuali.rice.kew.api.document.DocumentContentUpdate;
import org.kuali.rice.kew.api.document.DocumentContract;
import org.kuali.rice.kew.api.document.DocumentDetail;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeValidationError;
import org.kuali.rice.kew.api.document.node.RouteNodeInstance;

/**
 * TODO ..
 * 
 * TODO - it is intended that operations against document data on this are only "flushed" when an action is performed...
 * 
 * <p>This class is *not* thread safe.
 *
 */
public interface WorkflowDocument extends DocumentContract {

	String getDocumentId();

	Document getDocument();

	DocumentContent getDocumentContent();

	String getApplicationContent();

	void setApplicationContent(String applicationContent);

    public void setAttributeContent(String attributeContent);
	
	void clearAttributeContent();

	String getAttributeContent();

	void addAttributeDefinition(WorkflowAttributeDefinition attributeDefinition);

	void removeAttributeDefinition(
			WorkflowAttributeDefinition attributeDefinition);

	void clearAttributeDefinitions();

	List<WorkflowAttributeDefinition> getAttributeDefinitions();

	public void setSearchableContent(String searchableContent);
	
	void addSearchableDefinition(
			WorkflowAttributeDefinition searchableDefinition);

	void removeSearchableDefinition(
			WorkflowAttributeDefinition searchableDefinition);

	void clearSearchableDefinitions();

	void clearSearchableContent();

	List<WorkflowAttributeDefinition> getSearchableDefinitions();

	List<WorkflowAttributeValidationError> validateAttributeDefinition(
			WorkflowAttributeDefinition attributeDefinition);

	List<ActionRequest> getRootActionRequests();

	List<ActionTaken> getActionsTaken();

	void setApplicationDocumentId(String applicationDocumentId);

	String getApplicationDocumentId();

	DateTime getDateCreated();

	String getTitle();

	ValidActions getValidActions();

	RequestedActions getRequestedActions();

	void saveDocument(String annotation);

	void route(String annotation);

	void disapprove(String annotation);

	void approve(String annotation);

	void cancel(String annotation);

	void blanketApprove(String annotation);

	void blanketApprove(String annotation, String... nodeNames);

	void saveDocumentData();

	void setApplicationDocumentStatus(String applicationDocumentStatus);

	void acknowledge(String annotation);

	void fyi(String annotation);

	void fyi();

	/**
	 * TODO - be sure to mention that once this document is deleted, this api effectively becomes "dead" when you try to
	 * execute any document operation
	 */
	void delete();

	void refresh();

	void adHocToPrincipal(ActionRequestType actionRequested, String annotation,
			String targetPrincipalId, String responsibilityDescription,
			boolean forceAction);

	void adHocToPrincipal(ActionRequestType actionRequested, String nodeName,
			String annotation, String targetPrincipalId,
			String responsibilityDescription, boolean forceAction);

	void adHocToPrincipal(ActionRequestType actionRequested, String nodeName,
			String annotation, String targetPrincipalId,
			String responsibilityDescription, boolean forceAction,
			String requestLabel);

    void adHocToPrincipal(AdHocToPrincipal adHocToPrincipal, String annotation);

	void adHocToGroup(ActionRequestType actionRequested, String annotation,
			String targetGroupId, String responsibilityDescription,
			boolean forceAction);

	void adHocToGroup(ActionRequestType actionRequested, String nodeName,
			String annotation, String targetGroupId,
			String responsibilityDescription, boolean forceAction);

	void adHocToGroup(ActionRequestType actionRequested, String nodeName,
			String annotation, String targetGroupId,
			String responsibilityDescription, boolean forceAction,
			String requestLabel);

    void adHocToGroup(AdHocToGroup adHocToGroup, String annotation);

	void revokeAdHocRequestById(String actionRequestId, String annotation);

	void revokeAdHocRequests(AdHocRevoke revoke, String annotation);

	void revokeAllAdHocRequests(String annotation);

	void setTitle(String title);

	String getDocumentTypeName();

	boolean isCompletionRequested();

	boolean isApprovalRequested();

	boolean isAcknowledgeRequested();

	boolean isFYIRequested();

	boolean isBlanketApproveCapable();

	boolean isRouteCapable();

	boolean isValidAction(ActionType actionType);

	void superUserBlanketApprove(String annotation);

	void superUserNodeApprove(String nodeName, String annotation);

	void superUserTakeRequestedAction(String actionRequestId, String annotation);

	void superUserDisapprove(String annotation);

	void superUserCancel(String annotation);

	void superUserReturnToPreviousNode(ReturnPoint returnPoint,
			String annotation);

	void complete(String annotation);

	void logAnnotation(String annotation);

	DocumentStatus getStatus();

	boolean checkStatus(DocumentStatus status);

	/**
	 * Indicates if the document is in the initiated state or not.
	 *
	 * @return true if in the specified state
	 */
	boolean isInitiated();

	/**
	 * Indicates if the document is in the saved state or not.
	 *
	 * @return true if in the specified state
	 */
	boolean isSaved();

	/**
	 * Indicates if the document is in the enroute state or not.
	 *
	 * @return true if in the specified state
	 */
	boolean isEnroute();

	/**
	 * Indicates if the document is in the exception state or not.
	 *
	 * @return true if in the specified state
	 */
	boolean isException();

	/**
	 * Indicates if the document is in the canceled state or not.
	 *
	 * @return true if in the specified state
	 */
	boolean isCanceled();

	/**
	 * Indicates if the document is in the disapproved state or not.
	 *
	 * @return true if in the specified state
	 */
	boolean isDisapproved();

	/**
	 * Indicates if the document is in the Processed or Finalized state.
	 *
	 * @return true if in the specified state
	 */
	boolean isApproved();

	/**
	 * Indicates if the document is in the processed state or not.
	 *
	 * @return true if in the specified state
	 */
	boolean isProcessed();

	/**
	 * Indicates if the document is in the final state or not.
	 *
	 * @return true if in the specified state
	 */
	boolean isFinal();

	/**
	 * Returns the principalId with which this WorkflowDocument was constructed
	 * 
	 * @return the principalId with which this WorkflowDocument was constructed
	 */
	String getPrincipalId();

	void switchPrincipal(String principalId);

	void takeGroupAuthority(String annotation, String groupId);

	void releaseGroupAuthority(String annotation, String groupId);

    /**
     * Returns the names of the route nodes on the document which are currently active.
     *
     * <p>If the document has completed it's routing (i.e. it is in processed or final status) then this method may
     * return an empty set since no nodes are active at that time.  In order to get either the active *or* terminal
     * nodes, use the {@link #getCurrentNodeNames()} method.</p>
     *
     * @return an unmodifiable set containing the names of the active nodes for this document
     */
	Set<String> getNodeNames();

    /**
     * Returns the names of the nodes at which the document is currently at in it's route path.
     *
     * <p>This method differs from {@link #getNodeNames()} in the fact that if there are no active nodes, it will
     * return the last nodes on the document instead (a.k.a. the document's terminal nodes).</p>
     *
     * @return an unmodifiable set containing the names of the nodes at which this document is currently located within it's route path
     */
    Set<String> getCurrentNodeNames();

	void returnToPreviousNode(String nodeName, String annotation);

	void returnToPreviousNode(ReturnPoint returnPoint, String annotation);

	void move(MovePoint movePoint, String annotation);

	List<RouteNodeInstance> getActiveRouteNodeInstances();

    List<RouteNodeInstance> getCurrentRouteNodeInstances();

	List<RouteNodeInstance> getRouteNodeInstances();

	List<String> getPreviousNodeNames();

	DocumentDetail getDocumentDetail();

	void updateDocumentContent(DocumentContentUpdate documentContentUpdate);

	void placeInExceptionRouting(String annotation);

	void setVariable(String name, String value);

	String getVariableValue(String name);

	void setReceiveFutureRequests();

	void setDoNotReceiveFutureRequests();

	void setClearFutureRequests();

	String getReceiveFutureRequestsValue();

	String getDoNotReceiveFutureRequestsValue();

	String getClearFutureRequestsValue();

}