/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kew.actionrequest.service;

import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.engine.ActivationContext;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * INTERNAL: Service to handle the building, sorting, saving, activating and deactivating of action request graphs.
 *
 * <p>These lists are what determine role and delegation behaviors in graphs of action requests. Fetching that is being
 * done is also taking into account the 'weight' of action request codes.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ActionRequestService {

	ActionRequestValue initializeActionRequestGraph(ActionRequestValue actionRequest, DocumentRouteHeaderValue document, RouteNodeInstance nodeInstance);

    List<ActionRequestValue> findAllValidRequests(String principalId, String documentId, String requestCode);

    List<ActionRequestValue> findAllValidRequests(String principalId, List<ActionRequestValue> actionRequests, String requestCode);

    List<ActionRequestValue> findPendingByDoc(String documentId);

    ActionRequestValue saveActionRequest(ActionRequestValue actionRequest);

    ActionRequestValue activateRequest(ActionRequestValue actionRequest);

    ActionRequestValue activateRequest(ActionRequestValue actionRequest, boolean simulate);

    ActionRequestValue activateRequest(ActionRequestValue actionRequest, ActivationContext activationContext);

    List<ActionRequestValue> activateRequests(List<ActionRequestValue> actionRequests);

    List<ActionRequestValue> activateRequests(List<ActionRequestValue> actionRequests, boolean simulate);

    List<ActionRequestValue> activateRequests(List<ActionRequestValue> actionRequests, ActivationContext activationContext);

    ActionRequestValue activateRequestNoNotification(ActionRequestValue actionRequest, ActivationContext activationContext);

    ActionRequestValue deactivateRequest(ActionTakenValue actionTaken, ActionRequestValue actionRequest);

    List<ActionRequestValue> deactivateRequests(ActionTakenValue actionTaken, List<ActionRequestValue> actionRequests);

    ActionRequestValue deactivateRequest(ActionTakenValue actionTaken, ActionRequestValue actionRequest, ActivationContext activationContext);

    List<ActionRequestValue> deactivateRequests(ActionTakenValue actionTaken, List<ActionRequestValue> actionRequests, boolean simulate);

    List<ActionRequestValue> deactivateRequests(ActionTakenValue actionTaken, List<ActionRequestValue> actionRequests, ActivationContext activationContext);

    void deleteActionRequestGraph(ActionRequestValue actionRequest);

    void deleteActionRequestGraphNoOutbox(ActionRequestValue actionRequest);

    ActionRequestValue findByActionRequestId(String actionRequestId);

    List<ActionRequestValue> findPendingRootRequestsByDocId(String documentId);

    List<ActionRequestValue> findPendingRootRequestsByDocumentType(String documentTypeId);

    List<ActionRequestValue> findAllActionRequestsByDocumentId(String documentId);
    
    List<ActionRequestValue> findAllRootActionRequestsByDocumentId(String documentId);

    List<ActionRequestValue> findPendingByActionRequestedAndDocId(String actionRequestedCdCd, String documentId);

    /**
     * This method gets a list of ids of all principals who have a pending action request for a document.
     */
    List<String> getPrincipalIdsWithPendingActionRequestByActionRequestedAndDocId(String actionRequestedCd, String documentId);

    List<ActionRequestValue> findByStatusAndDocId(String statusCd, String documentId);

    List<ActionRequestValue> findByDocumentIdIgnoreCurrentInd(String documentId);

    List<ActionRequestValue> findActivatedByGroup(String groupId);

    void updateActionRequestsForResponsibilityChange(Set<String> responsibilityIds);

    ActionRequestValue getRoot(ActionRequestValue actionRequest);

    List<ActionRequestValue> getRootRequests(Collection<ActionRequestValue> actionRequests);

    List<ActionRequestValue> findPendingByDocRequestCdNodeName(String documentId, String requestCode, String nodeName);
    
    /**
     * Returns all pending requests for a given routing entity
     * @param documentId the id of the document header being routed
     * @return a List of all pending ActionRequestValues for the document
     */
    List<ActionRequestValue> findAllPendingRequests(String documentId);
    
	/**
	 * Filters action requests based on if they occur after the given requestCode, and if they relate to 
	 * the given principal
	 * @param actionRequests the List of ActionRequestValues to filter
	 * @param principalId the id of the principal to find active requests for
	 * @param principalGroupIds List of group ids that the principal belongs to
	 * @param requestCode the request code for all ActionRequestValues to be after
	 * @return the filtered List of ActionRequestValues
	 */
	List<ActionRequestValue> filterActionRequestsByCode(List<ActionRequestValue> actionRequests, String principalId, List<String> principalGroupIds, String requestCode);

    /**
     * Returns the highest priority delegator in the list of action requests.
     */
    Recipient findDelegator(List<ActionRequestValue> actionRequests);

    ActionRequestValue findDelegatorRequest(ActionRequestValue actionRequest);

    List<ActionRequestValue> findPendingRootRequestsByDocIdAtRouteNode(String documentId, String nodeInstanceId);

    List<ActionRequestValue> findRootRequestsByDocIdAtRouteNode(String documentId, String nodeInstanceId);

    List<ActionRequestValue> getDelegateRequests(ActionRequestValue actionRequest);

    /**
     * If this is a role request, then this method returns a List of the action request for each recipient within the
     * role.  Otherwise, it will return a List with just the original action request.
     */
    List<ActionRequestValue> getTopLevelRequests(ActionRequestValue actionRequest);

    /**
     * Checks if the given user has any Action Requests on the given document.
     */
    boolean doesPrincipalHaveRequest(String principalId, String documentId);

    Map<String, String> getActionsRequested(DocumentRouteHeaderValue routeHeader, String principalId, boolean completeAndApproveTheSame);

    ActionRequestValue getActionRequestForRole(String actionTakenId);
}
