package org.kuali.rice.kew.api;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.action.AdHocRevoke;
import org.kuali.rice.kew.api.action.MovePoint;
import org.kuali.rice.kew.api.action.RequestedActions;
import org.kuali.rice.kew.api.action.ReturnPoint;
import org.kuali.rice.kew.api.action.ValidActions;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContent;
import org.kuali.rice.kew.api.document.DocumentContentUpdate;
import org.kuali.rice.kew.api.document.DocumentDetail;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.RouteNodeInstance;
import org.kuali.rice.kew.api.document.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.document.WorkflowAttributeValidationError;

/**
 * TODO ..
 * 
 * TODO - it is intended that operations against document data on this are only "flushed" when an action is performed...
 * 
 * <p>This class is *not* thread safe.
 *
 */
public interface WorkflowDocument {

	String getDocumentId();

	Document getDocument();

	DocumentContent getDocumentContent();

	String getApplicationContent();

	void setApplicationContent(String applicationContent);

	void clearAttributeContent();

	String getAttributeContent();

	void addAttributeDefinition(WorkflowAttributeDefinition attributeDefinition);

	void removeAttributeDefinition(
			WorkflowAttributeDefinition attributeDefinition);

	void clearAttributeDefinitions();

	List<WorkflowAttributeDefinition> getAttributeDefinitions();

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

	Set<String> getNodeNames();

	void returnToPreviousNode(String nodeName, String annotation);

	void returnToPreviousNode(ReturnPoint returnPoint, String annotation);

	void move(MovePoint movePoint, String annotation);

	List<RouteNodeInstance> getActiveRouteNodeInstances();

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