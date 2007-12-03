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
package edu.iu.uis.eden.actionrequests;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.engine.ActivationContext;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Service to handle the building, sorting, saving, activating and deactivating of action request graphs.  These lists are 
 * what determine role and delegation behaviors in graphs of action requests.  
 * 
 * Fetching that is being done is also taking into account the 'weight' of action request codes.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface ActionRequestService {

	public ActionRequestValue initializeActionRequestGraph(ActionRequestValue actionRequest, DocumentRouteHeaderValue document, RouteNodeInstance nodeInstance);
	
    public void deactivateRequest(ActionTakenValue actionTaken, ActionRequestValue actionRequest);
    
    public void deactivateRequests(ActionTakenValue actionTaken, List actionRequests);
    
    public void deactivateRequest(ActionTakenValue actionTaken, ActionRequestValue actionRequest, boolean simulate);

    public void deactivateRequest(ActionTakenValue actionTaken, ActionRequestValue actionRequest, ActivationContext activationContext);

    public void deactivateRequests(ActionTakenValue actionTaken, List actionRequests, boolean simulate);

    public void deactivateRequests(ActionTakenValue actionTaken, List actionRequests, ActivationContext activationContext);

    public void deleteActionRequestGraph(ActionRequestValue actionRequest);
    
    public List findAllValidRequests(WorkflowUser user, Long routeHeaderId, String requestCode) throws EdenUserNotFoundException;
    
    public List findAllValidRequests(WorkflowUser user, Collection actionRequests, String requestCode) throws EdenUserNotFoundException;

    public List findPendingByDoc(Long routeHeaderId);

    public void saveActionRequest(ActionRequestValue actionRequest) throws EdenUserNotFoundException;

    public void activateRequest(ActionRequestValue actionRequest) throws EdenUserNotFoundException;
    
    public void activateRequest(ActionRequestValue actionRequest, boolean simulate) throws EdenUserNotFoundException;

    public void activateRequest(ActionRequestValue actionRequest, ActivationContext activationContext) throws EdenUserNotFoundException;

    public void activateRequests(Collection actionRequests) throws EdenUserNotFoundException;
    
    public void activateRequests(Collection actionRequests, boolean simulate) throws EdenUserNotFoundException;

	public void activateRequests(Collection actionRequests, ActivationContext activationContext) throws EdenUserNotFoundException;

    public List activateRequestNoNotification(ActionRequestValue actionRequest, boolean simulate) throws EdenUserNotFoundException;

    public List activateRequestNoNotification(ActionRequestValue actionRequest, ActivationContext activationContext) throws EdenUserNotFoundException;

    public ActionRequestValue findByActionRequestId(Long actionRequestId);

    public List findPendingRootRequestsByDocId(Long routeHeaderId);
    
    public List findPendingRootRequestsByDocIdAtRouteLevel(Long routeHeaderId, Integer routeLevel);
    
    public List findPendingByDocIdAtOrBelowRouteLevel(Long routeHeaderId, Integer routeLevel);
    
    public List findPendingRootRequestsByDocIdAtOrBelowRouteLevel(Long routeHeaderId, Integer routeLevel);
    
    public List findPendingRootRequestsByDocumentType(Long documentTypeId);

    public List findAllActionRequestsByRouteHeaderId(Long routeHeaderId);

    public List findPendingByActionRequestedAndDocId(String actionRequestedCdCd, Long routeHeaderId);
    
    public List findByStatusAndDocId(String statusCd, Long routeHeaderId);

    public void alterActionRequested(List actionRequests, String actionRequestCd)throws EdenUserNotFoundException;

    public List findByRouteHeaderIdIgnoreCurrentInd(Long routeHeaderId);
    
    public List findActivatedByWorkgroup(Workgroup workgroup);
    
    public void updateActionRequestsForResponsibilityChange(Set responsibilityIds);
    
    public ActionRequestValue getRoot(ActionRequestValue actionRequest);
    
    public List getRootRequests(Collection actionRequests);
    
    public boolean isDuplicateRequest(ActionRequestValue actionRequest);
 
    public List findPendingByDocRequestCdRouteLevel(Long routeHeaderId, String requestCode, Integer routeLevel);
    
    public List findPendingByDocRequestCdNodeName(Long routeHeaderId, String requestCode, String nodeName);
        
    /**
     * Returns the highest priority delegator in the list of action requests.
     */
    public Recipient findDelegator(List actionRequests)  throws EdenUserNotFoundException;
    
    /**
     * Returns the closest delegator for the given ActionRequest
     */
    public Recipient findDelegator(ActionRequestValue actionRequest) throws EdenUserNotFoundException;
    
    public ActionRequestValue findDelegatorRequest(ActionRequestValue actionRequest);
    
    public void deleteByRouteHeaderId(Long routeHeaderId);
    
    public void deleteByActionRequestId(Long actionRequestId);
    
    public void validateActionRequest(ActionRequestValue actionRequest);
    
    public List<ActionRequestValue> findPendingRootRequestsByDocIdAtRouteNode(Long routeHeaderId, Long nodeInstanceId);
    
    public List findRootRequestsByDocIdAtRouteNode(Long documentId, Long nodeInstanceId);
    
    public List getDelegateRequests(ActionRequestValue actionRequest);
    
    /**
     * If this is a role request, then this method returns a List of the action request for each recipient within the
     * role.  Otherwise, it will return a List with just the original action request.
     */
    public List getTopLevelRequests(ActionRequestValue actionRequest);
    
    public boolean isValidActionRequestCode(String actionRequestCode);
    
    /**
     * Checks if the given user has any Action Requests on the given document.
     */
    public boolean doesUserHaveRequest(WorkflowUser user, Long documentId);

}
