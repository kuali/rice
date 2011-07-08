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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kew.dto.ActionItemDTO;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.ActionTakenDTO;
import org.kuali.rice.kew.dto.DocumentContentDTO;
import org.kuali.rice.kew.dto.DocumentDetailDTO;
import org.kuali.rice.kew.dto.DocumentSearchCriteriaDTO;
import org.kuali.rice.kew.dto.DocumentSearchResultDTO;
import org.kuali.rice.kew.dto.DocumentStatusTransitionDTO;
import org.kuali.rice.kew.dto.ReportCriteriaDTO;
import org.kuali.rice.kew.dto.RouteNodeInstanceDTO;
import org.kuali.rice.kew.dto.RuleDTO;
import org.kuali.rice.kew.dto.RuleReportCriteriaDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeValidationErrorDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;

/**
 * Convenience class for client applications to query workflow.  This class is one of two
 * (Java) client interfaces to the KEW system.
 *
 * <p>The first time an instance of this class is created, it will read the client configuration to
 * determine how to connect to KEW.  To use this API, simply create a new instance using the
 * empty constructor.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkflowInfo implements java.io.Serializable {

	private static final long serialVersionUID = 3231835171780770399L;

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
    
    public AttributeSet getActionsRequested(String principalId, String documentId) throws WorkflowException {
    	return getWorkflowUtility().getActionsRequested(principalId, documentId);
    }

    /**
     * Returns the status of the document with the given ID.
     *
     * @since 0.9.1
     * @throws WorkflowException if document cannot be found for the given ID or
     * if the given document ID is null.
     */
    public String getDocumentStatus(String documentId) throws WorkflowException {
   		return getWorkflowUtility().getDocumentStatus(documentId);
    }

    /**
     * Returns the status of the document with the given ID.
     *
     * @since 0.9.1
     * @throws WorkflowException if document cannot be found for the given ID or
     * if the given document ID is null.
     */
    public DocumentStatusTransitionDTO[] getDocumentStatusTransitionHistory(String documentId) throws WorkflowException {
   		return getWorkflowUtility().getDocumentStatusTransitionHistory(documentId);
    }

    /**
     * Returns a new unique id to be used as a responsibility id
     * @return a new unique id to be used as a responsibility id
     * @throws WorkflowException if an error occurs obtaining a new responsibility id
     * @see WorkflowUtility#getNewResponsibilityId()
     */
    public String getNewResponsibilityId() throws WorkflowException {
    	return getWorkflowUtility().getNewResponsibilityId();
    }

    public Integer getUserActionItemCount(String principalId) throws WorkflowException {
    	return getWorkflowUtility().getUserActionItemCount(principalId);
    }
    
    public ActionItemDTO[] getActionItems(String documentId) throws WorkflowException {
    	return getWorkflowUtility().getAllActionItems(documentId);
    }

    public ActionItemDTO[] getActionItems(String documentId, String[] actionRequestedCodes) throws WorkflowException {
    	return getWorkflowUtility().getActionItems(documentId, actionRequestedCodes);
    }

    /**
     * Returns the pending action requests of the document of the specified id
     * @param documentId the id of the document whose action requests will be retrieved
     * @return the pending action requests of the document of the specified id
     * @throws WorkflowException if an error occurs obtaining the documents action requests
     * @see WorkflowUtility#getActionRequests(Long)
     */
    public ActionRequestDTO[] getActionRequests(String documentId) throws WorkflowException {
    	return getWorkflowUtility().getAllActionRequests(documentId);
    }

   /**
    * Returns the pending action requests of the document of the specified id for the specified
    * user and/or the specified node name.  If both user and node name are empty then will
    * return all pending action requests.
    * @param documentId the id of the document whose action requests will be retrieved
    * @param nodeName the node name of the requests to be retrieved
    * @param userId the user that the requests would be satisfied by
    * @return the pending action requests of the document of the specified id
    * @throws WorkflowException if an error occurs obtaining the documents action requests
    * @see WorkflowUtility#getActionRequests(Long)
    */
   public ActionRequestDTO[] getActionRequests(String documentId, String nodeName, String principalId) throws WorkflowException {
	   return getWorkflowUtility().getActionRequests(documentId, nodeName, principalId);
   }

   /**


    /**
     * Returns the actions taken on the document of the specified id
     * @param documentId the id of the document whose actions taken will be retrieved
     * @return the actions taken on the document of the specified id
     * @throws WorkflowException if an error occurs obtaining the actions taken
     * @see WorkflowUtility#getActionsTaken(Long)
     */
    public ActionTakenDTO[] getActionsTaken(String documentId) throws WorkflowException {
    	return getWorkflowUtility().getActionsTaken(documentId);
    }

    /**
     * Returns whether the user is in the document's route log (whether an action request has been, or may be,
     * generated for the user)
     * @param documentId the id of the document to analyze
     * @param userId the id of the user to check
     * @param lookFuture whether to evaluate potential future requests
     * @return whether the user is in the document's route log
     * @throws WorkflowException if an error occurs determining whether the user is in the document's route log
     * @see WorkflowUtility#isUserInRouteLog(Long, String, boolean)
     */
    public boolean isUserAuthenticatedByRouteLog(String documentId, String principalId, boolean lookFuture) throws WorkflowException {
    	return getWorkflowUtility().isUserInRouteLog(documentId, principalId, lookFuture);
    }
    
    /**
     * Returns whether the user is in the document's route log (whether an action request has been, or may be,
     * generated for the user).  The flattenNodes boolean instructs the underlying Simulation Engine to perform
     * a "flattened" evaluation or not and only takes effect if lookFuture is true.  When doing a "flattened"
     * evaluation, all branches will be followed regardless of split and join logic built into the document type.
     * 
     * @param documentId the id of the document to analyze
     * @param userId the id of the user to check
     * @param lookFuture whether to evaluate potential future requests
     * @return whether the user is in the document's route log
     * @throws WorkflowException if an error occurs determining whether the user is in the document's route log
     * @see WorkflowUtility#isUserInRouteLog(Long, String, boolean)
     */
    public boolean isUserAuthenticatedByRouteLog(String documentId, String principalId, boolean lookFuture, boolean flattenNodes) throws WorkflowException {
    	return getWorkflowUtility().isUserInRouteLogWithOptionalFlattening(documentId, principalId, lookFuture, flattenNodes);
    }

    /**
     * Returns whether the specified user is the final approver for the document
     * @param documentId the id of document to check
     * @param userId the id of the user to check
     * @return whether the specified user is the final approver for the document
     * @throws WorkflowException if an error occurs determining whether the user is the final approver on the document
     * @see WorkflowUtility#isFinalApprover(Long, String)
     */
    public boolean isFinalApprover(String documentId, String principalId) throws WorkflowException {
    	return getWorkflowUtility().isFinalApprover(documentId, principalId);
    }

    /**
     * Validate the WorkflowAttributeDefinition against it's attribute on the server.  This will validate
     * the inputs that will eventually become xml.
     *
     * Only applies to attributes implementing WorkflowAttributeXmlValidator.
     *
     * @param attributeDefinition the attribute definition to validate
     * @return WorkflowAttributeValidationErrorVO[] of error from the attribute
     * @throws WorkflowException when attribute doesn't implement WorkflowAttributeXmlValidator
     * @see WorkflowUtility#validateWorkflowAttributeDefinitionVO(WorkflowAttributeDefinitionDTO)
     */
    public WorkflowAttributeValidationErrorDTO[] validAttributeDefinition(WorkflowAttributeDefinitionDTO attributeDefinition) throws WorkflowException {
    	return getWorkflowUtility().validateWorkflowAttributeDefinitionVO(attributeDefinition);
    }

    // WORKFLOW 2.3: new methods

    /**
     * Runs a "rule report" give a rule report criteria.
     * @param ruleReportCriteria the criteria for the rule report
     * @return an array of RuleVO representing rules that will fire under the specified criteria
     * @see WorkflowUtility#ruleReport(RuleReportCriteriaDTO)
     */
    public RuleDTO[] ruleReport(RuleReportCriteriaDTO ruleReportCriteria) throws WorkflowException {
    	return getWorkflowUtility().ruleReport(ruleReportCriteria);
    }

    // WORKFLOW 2.1: new methods

    /**
     * Returns a document detail VO representing the route header along with action requests, actions taken,
     * and route node instances.
     * @param documentId id of the document whose details should be returned
     * @return Returns a document detail VO representing the route header along with action requests, actions taken, and route node instances.
     * @throws WorkflowException
     * @see WorkflowUtility#getDocumentDetail(Long)
     */
    public DocumentDetailDTO getDocumentDetail(String documentId) throws WorkflowException {
    	return getWorkflowUtility().getDocumentDetail(documentId);
    }

    /**
     * Returns a node instance of the specified note instance id
     * @param nodeInstanceId the id of the node instance to return
     * @return a node instance of the specified note instance id
     * @throws WorkflowException if an error occurs obtaining the node instance
     * @see WorkflowUtility#getNodeInstance(String)
     */
    public RouteNodeInstanceDTO getNodeInstance(String nodeInstanceId) throws WorkflowException {
    	return getWorkflowUtility().getNodeInstance(nodeInstanceId);
    }

    /**
     * Returns the route node instances that have been created so far during the life of the specified document.  This includes
     * all previous instances which have already been processed and are no longer active.
     * @param documentId the id of the document whose route node instances should be returned
     * @return the route node instances that have been created so far during the life of this document
     * @throws WorkflowException if there is an error getting the route node instances for the document
     * @see WorkflowUtility#getDocumentRouteNodeInstances(Long)
     */
    public RouteNodeInstanceDTO[] getDocumentRouteNodeInstances(String documentId) throws WorkflowException {
    	return getWorkflowUtility().getDocumentRouteNodeInstances(documentId);
    }

    /**
     * Returns all active node instances on the document.
     * @param documentId id of the document whose active node instances should be returned
     * @return all active node instances on the document
     * @throws WorkflowException if there is an error obtaining the currently active nodes on the document
     * @see WorkflowUtility#getActiveNodeInstances(Long)
     */
    public RouteNodeInstanceDTO[] getActiveNodeInstances(String documentId) throws WorkflowException {
    	return getWorkflowUtility().getActiveNodeInstances(documentId);
    }

    /**
     * Returns all node instances on the document which have no successor.
     * @param documentId id of the document whose terminal node instances should be returned
     * @return all terminal node instances of the document
     * @throws WorkflowException if there is an error obtaining the terminal node instances on the document
     * @see WorkflowUtility#getTerminalNodeInstances(Long)
     */
    public RouteNodeInstanceDTO[] getTerminalNodeInstances(String documentId) throws WorkflowException {
    	return getWorkflowUtility().getTerminalNodeInstances(documentId);
    }
    
    /**
     * Returns the current node instances on the document.  If the document has active nodes, those will
     * be returned.  Otherwise, all terminal nodes will be returned.
     * @param documentId id of the document whose current node instances should be returned
     * @return all current node instances of the document
     * @throws WorkflowException if there is an error obtaining the current node instances on the document
     * @see WorkflowUtility#getCurrentNodeInstances(Long)
     */
    public RouteNodeInstanceDTO[] getCurrentNodeInstances(String documentId) throws WorkflowException {
    	return getWorkflowUtility().getCurrentNodeInstances(documentId);
    }
    
    /**
     * Returns names of all current nodes the document is currently at.  If the document has active nodes, those
     * will be returned.  Otherwise, the document's terminal nodes will be returned.
     *
     * @return names of all current nodes the document is currently at.
     * @throws WorkflowException if there is an error obtaining the current nodes on the document
     * @see WorkflowUtility#getCurrentNodeInstances(Long)
     */
    public String[] getCurrentNodeNames(String documentId) throws WorkflowException {
    	RouteNodeInstanceDTO[] currentNodeInstances = getWorkflowUtility().getCurrentNodeInstances(documentId);
    	String[] nodeNames = new String[(currentNodeInstances == null ? 0 : currentNodeInstances.length)];
    	for (int index = 0; index < currentNodeInstances.length; index++) {
    		nodeNames[index] = currentNodeInstances[index].getName();
    	}
    	return nodeNames;
    }

    /**
     * Re-resolves the specified role on the document, and refreshes any pending action requests.
     * @param documentTypeName the type of the document for which to re-resolve roles
     * @param roleName the role name to re-resolve
     * @param qualifiedRoleNameLabel the qualified role name label
     * @throws WorkflowException if an error occurs re-resolving the role
     * @see WorkflowUtility#reResolveRole(String, String, String)
     */
    public void reResolveRoleByDocTypeName(String documentTypeName, String roleName, String qualifiedRoleNameLabel) throws WorkflowException {
    	getWorkflowUtility().reResolveRoleByDocTypeName(documentTypeName, roleName, qualifiedRoleNameLabel);
    }

    /**
     * Re-resolves the specified role on the document, and refreshes any pending action requests.
     * @param documentId the id of the document for which to re-resolve roles
     * @param roleName the role name to re-resolve
     * @param qualifiedRoleNameLabel the qualified role name label
     * @throws WorkflowException if an error occurs re-resolving the role
     * @see WorkflowUtility#reResolveRoleByDocumentId(Long, String, String)
     */
    public void reResolveRoleByDocumentId(String documentId, String roleName, String qualifiedRoleNameLabel) throws WorkflowException {
    	getWorkflowUtility().reResolveRoleByDocumentId(documentId, roleName, qualifiedRoleNameLabel);
    }

    /**
     * Runs a "routing report"
     * @param reportCriteria routing report criteria
     * @return DocumentDetailVO detailing the actionrequests that would be generated under the specified criteria
     * @see WorkflowUtility#routingReport(ReportCriteriaDTO)
     */
    public DocumentDetailDTO routingReport(ReportCriteriaDTO reportCriteria) throws WorkflowException {
    	return getWorkflowUtility().routingReport(reportCriteria);
    }
    
    /**
     * Returns the application document id for the document with the given id.
     *
     * @param documentId the document id of the document for which to fetch the application document id
     * @return the application document of the document with the given id or null if the document does not have an application document id
     */
    public String getAppDocId(String documentId)  throws WorkflowException {
    	return getWorkflowUtility().getAppDocId(documentId);
    }

    /**
     * Returns whether the specified user is the last approver at the specified node name
     * @param documentId the id of document to check
     * @param userId the id of the user to check
     * @param nodeName name of node to check
     * @return whether the specified user is the last approver at the specified node name
     * @throws WorkflowException if an error occurs determining whether the user is the last approver at the specified node
     * @see WorkflowUtility#isLastApproverAtNode(Long, String, String)
     */
    public boolean isLastApproverAtNode(String documentId, String principalId, String nodeName) throws WorkflowException {
    	return getWorkflowUtility().isLastApproverAtNode(documentId, principalId, nodeName);
    }

    /**
     * Returns whether the specified node on the specified document type would produce approve or complete requests
     * @param docType the type of the document to check
     * @param docContent the content to use
     * @param nodeName the node to check
     * @return whether the specified node on the specified document type would produce approve or complete requests
     * @throws WorkflowException if an error occurs
     * @see WorkflowUtility#routeNodeHasApproverActionRequest(String, String, String)
     */
    public boolean routeNodeHasApproverActionRequest(String docType, String docContent, String nodeName) throws WorkflowException {
    	return getWorkflowUtility().routeNodeHasApproverActionRequest(docType, docContent, nodeName);
    }

    /**
     * This method allows a document search to be executed just as would occur from the User Interface using the given user as
     * the searching user
     * 
     * @param userId - user to use when executing the search (for security filtering purposes)
     * @param criteriaVO - criteria to use for the search
     * @return a {@link DocumentSearchResultDTO} object containing a list of search result columns and data rows
     * @throws WorkflowException
     */
    public DocumentSearchResultDTO performDocumentSearch(String principalId, DocumentSearchCriteriaDTO criteriaVO) throws WorkflowException {
    	return getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteriaVO);
    }

    /**
     * This method allows a document search to be executed just as would occur from the User Interface
     * 
     * @param criteriaVO - criteria to use for the search
     * @return a {@link DocumentSearchResultDTO} object containing a list of search result columns and data rows
     * @throws WorkflowException
     */
    public DocumentSearchResultDTO performDocumentSearch(DocumentSearchCriteriaDTO criteriaVO) throws WorkflowException {
    	return getWorkflowUtility().performDocumentSearch(criteriaVO);
    }

    /**
     * Returns the document content VO of the specified document
     * @param documentId the id of the document whose content should be returned
     * @return the document content VO of the specified document
     * @throws WorkflowException if an error occurs obtaining the document content
     * @see WorkflowUtility#getDocumentContent(Long)
     */
    public DocumentContentDTO getDocumentContent(String documentId) throws WorkflowException {
    	return getWorkflowUtility().getDocumentContent(documentId);
    }

    /**
     * Returns names of nodes already traversed
     * @param documentId id of the document to check
     * @return names of nodes already traversed
     * @throws WorkflowException if an error occurs
     * @see WorkflowUtility#getPreviousRouteNodeNames(Long)
     */
    public String[] getPreviousRouteNodeNames(String documentId) throws WorkflowException {
    	return getWorkflowUtility().getPreviousRouteNodeNames(documentId);
    }

    /**
     * Checks whether a document would product at least one action request under the specified criteria
     * @param reportCriteriaDTO criteria under which to perform the check
     * @param actionRequestedCodes the types of action requests to check for
     * @param ignoreCurrentActionRequests determines if method should look only at simulation generated requests 
     *        or both simulation generated requests and requests that are currently active on the document
     * @return whether a document would product at least one action request under the specified criteria
     * @throws WorkflowException if an error occurs
     * @see WorkflowUtility#documentWillHaveAtLeastOneActionRequest(ReportCriteriaDTO, String[], boolean)
     */
    public boolean documentWillHaveAtLeastOneActionRequest(ReportCriteriaDTO reportCriteriaDTO, String[] actionRequestedCodes, boolean ignoreCurrentActionRequests) throws WorkflowException {
    	return getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, actionRequestedCodes, ignoreCurrentActionRequests);
    }

    /**
     * @deprecated use {@link #documentWillHaveAtLeastOneActionRequest(ReportCriteriaDTO, String[], boolean)} instead
     * 
     * This method assumes both existing and generated requests should be taken into account
     */
    public boolean documentWillHaveAtLeastOneActionRequest(ReportCriteriaDTO reportCriteriaDTO, String[] actionRequestedCodes) throws WorkflowException {
    	return getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, actionRequestedCodes, false);
    }

    // DEPRECATED: as of Workflow 2.1

    /**
     * @deprecated use {@link #isLastApproverAtNode(Long, String, String) instead
     */
    protected boolean isLastApproverInRouteLevel(String documentId, String principalId, Integer routeLevel) throws WorkflowException {
    	return getWorkflowUtility().isLastApproverInRouteLevel(documentId, principalId, routeLevel);
    }

    /**
     * @deprecated use {@link #routeNodeHasApproverActionRequest(String, String, String)}
     */
    protected boolean routeLevelHasApproverActionRequest(String docType, String docContent, Integer routeLevel) throws WorkflowException {
    	return getWorkflowUtility().routeLevelHasApproverActionRequest(docType, docContent, routeLevel);
    }
    
    /**
     * 
     * This method gets a list of ids of all principals who have a pending action request for a document.
     * 
     * @param actionRequestedCd
     * @param documentId
     * @return
     * @throws WorkflowException
     */
	public List<String> getPrincipalIdsWithPendingActionRequestByActionRequestedAndDocId(String actionRequestedCd, String documentId) throws WorkflowException {
		String[] results = getWorkflowUtility().getPrincipalIdsWithPendingActionRequestByActionRequestedAndDocId(actionRequestedCd, documentId);
		if (ObjectUtils.equals(null, results)) {
			return null;
		}
		return (List<String>) Arrays.asList(results);
    }

    /**
     * This method gets a list of ids of all principals in the route log - 
     * - initiators, 
     * - people who have taken action, 
     * - people with a pending action request, 
	 * - people who will receive an action request for the document in question
	 * 
     * @param documentId
     * @param lookFuture
     * @return
     * @throws WorkflowException
     */
	public List<String> getPrincipalIdsInRouteLog(String documentId, boolean lookFuture) throws WorkflowException {
		String[] results = getWorkflowUtility().getPrincipalIdsInRouteLog(documentId, lookFuture);
		if (ObjectUtils.equals(null, results)) {
			return null;
		}
		return (List<String>) Arrays.asList(results);
    }
    
    public String getDocumentInitiatorPrincipalId( String documentId ) throws WorkflowException {
    	return getWorkflowUtility().getDocumentInitiatorPrincipalId(documentId);
    }
    public String getDocumentRoutedByPrincipalId( String documentId ) throws WorkflowException {
    	return getWorkflowUtility().getDocumentRoutedByPrincipalId(documentId);
    }
        
}
