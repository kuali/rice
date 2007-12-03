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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.rice.core.Core;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionlist.ActionListService;
import edu.iu.uis.eden.actionrequests.dao.ActionRequestDAO;
import edu.iu.uis.eden.actiontaken.ActionTakenService;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.engine.ActivationContext;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.messaging.MessageQueueService;
import edu.iu.uis.eden.messaging.MessageServiceNames;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.RouteHeaderService;
import edu.iu.uis.eden.routemodule.RouteModule;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.util.PerformanceLogger;
import edu.iu.uis.eden.util.ResponsibleParty;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.Workgroup;
import edu.iu.uis.eden.workgroup.WorkgroupService;

/**
 * Default implementation of the {@link ActionRequestService}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionRequestServiceImpl implements ActionRequestService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionRequestServiceImpl.class);

    private ActionRequestDAO actionRequestDAO;

    public ActionRequestValue findByActionRequestId(Long actionRequestId) {
        return getActionRequestDAO().getActionRequestByActionRequestId(actionRequestId);
    }

    public ActionRequestValue initializeActionRequestGraph(ActionRequestValue actionRequest, DocumentRouteHeaderValue document, RouteNodeInstance nodeInstance) {
    	if (actionRequest.getParentActionRequest() != null) {
    		LOG.warn("-->A non parent action request from doc " + document.getRouteHeaderId());
    		actionRequest = KEWServiceLocator.getActionRequestService().getRoot(actionRequest);
    	}
    	propagatePropertiesToRequestGraph(actionRequest, document, nodeInstance);
    	return actionRequest;
    }

	private void propagatePropertiesToRequestGraph(ActionRequestValue actionRequest, DocumentRouteHeaderValue document, RouteNodeInstance nodeInstance) {
		setPropertiesToRequest(actionRequest, document, nodeInstance);
		for (Iterator iterator = actionRequest.getChildrenRequests().iterator(); iterator.hasNext();) {
			propagatePropertiesToRequestGraph((ActionRequestValue) iterator.next(), document, nodeInstance);
		}
	}

	private void setPropertiesToRequest(ActionRequestValue actionRequest, DocumentRouteHeaderValue document, RouteNodeInstance nodeInstance) {
		actionRequest.setRouteHeaderId(document.getRouteHeaderId());
		actionRequest.setDocVersion(document.getDocVersion());
		actionRequest.setRouteLevel(document.getDocRouteLevel());
		actionRequest.setNodeInstance(nodeInstance);
		actionRequest.setStatus(EdenConstants.ACTION_REQUEST_INITIALIZED);
	}

	public void activateRequests(Collection actionRequests) throws EdenUserNotFoundException {
		activateRequests(actionRequests, new ActivationContext(!ActivationContext.CONTEXT_IS_SIMULATION));
	}

	public void activateRequests(Collection actionRequests, boolean simulate) throws EdenUserNotFoundException {
		activateRequests(actionRequests, new ActivationContext(simulate));
	}

	public void activateRequests(Collection actionRequests, ActivationContext activationContext) throws EdenUserNotFoundException {
		if (actionRequests == null) {
			return;
		}
		PerformanceLogger performanceLogger = new PerformanceLogger();
        activationContext.setGeneratedActionItems(new ArrayList());
		activateRequestsInternal(actionRequests, activationContext);
        if (!activationContext.isSimulation()) {
            KEWServiceLocator.getNotificationService().notify(activationContext.getGeneratedActionItems());
        }
		performanceLogger.log("Time to " + (activationContext.isSimulation() ? "simulate activation of " : "activate ") + actionRequests.size() + " action requests.");
		LOG.debug("Generated " + activationContext.getGeneratedActionItems().size() + " action items.");
	}

	public void activateRequest(ActionRequestValue actionRequest) throws EdenUserNotFoundException {
		activateRequests(Utilities.asList(actionRequest), new ActivationContext(!ActivationContext.CONTEXT_IS_SIMULATION));
	}

	public void activateRequest(ActionRequestValue actionRequest, boolean simulate) throws EdenUserNotFoundException {
		activateRequests(Utilities.asList(actionRequest), new ActivationContext(simulate));
	}

	public void activateRequest(ActionRequestValue actionRequest, ActivationContext activationContext) throws EdenUserNotFoundException {
		activateRequests(Utilities.asList(actionRequest), activationContext);
	}

    public List activateRequestNoNotification(ActionRequestValue actionRequest, boolean simulate) throws EdenUserNotFoundException {
        return activateRequestNoNotification(actionRequest, new ActivationContext(simulate));
    }

    public List activateRequestNoNotification(ActionRequestValue actionRequest, ActivationContext activationContext) throws EdenUserNotFoundException {
        activationContext.setGeneratedActionItems(new ArrayList());
        activateRequestInternal(actionRequest, activationContext);
        return activationContext.getGeneratedActionItems();
    }

	/**
	 * Internal helper method for activating a Collection of action requests and their children.
	 * Maintains an accumulator for generated action items.
	 */
    private void activateRequestsInternal(Collection actionRequests, ActivationContext activationContext) throws EdenUserNotFoundException {
        if (actionRequests == null) {
            return;
        }
        for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
            activateRequestInternal(actionRequest, activationContext);
        }
    }

	/**
	 * Internal helper method for activating a single action requests and it's children.
	 * Maintains an accumulator for generated action items.
	 */
    private void activateRequestInternal(ActionRequestValue actionRequest, ActivationContext activationContext) throws EdenUserNotFoundException {
        PerformanceLogger performanceLogger = new PerformanceLogger();
        if (actionRequest == null || actionRequest.isActive() || actionRequest.isDeactivated()) {
            return;
        }
        processResponsibilityId(actionRequest);
        if (deactivateOnActionAlreadyTaken(actionRequest, activationContext)) {
            return;
        }
        actionRequest.setStatus(EdenConstants.ACTION_REQUEST_ACTIVATED);
        if (!activationContext.isSimulation()) {
            saveActionRequest(actionRequest);
            activationContext.getGeneratedActionItems().addAll(getActionListService().generateActionItems(actionRequest, activationContext.isSimulation()));
        }
        activateRequestsInternal(actionRequest.getChildrenRequests(), activationContext);
        activateRequestInternal(actionRequest.getParentActionRequest(), activationContext);
        if (activationContext.isSimulation()) {
        	performanceLogger.log("Time to simulate activation of request.");
        } else {
        	performanceLogger.log("Time to activate action request with id " + actionRequest.getActionRequestId());
        }
    }

    private void processResponsibilityId(ActionRequestValue actionRequest) {
        Long responsibilityId = actionRequest.getResponsibilityId();
        try {
            RouteModule routeModule = KEWServiceLocator.getRouteModuleService().findRouteModule(actionRequest);
            if (responsibilityId != null && actionRequest.isRouteModuleRequest()) {
                LOG.debug("Resolving responsibility id for action request id="+actionRequest.getActionRequestId()+" and responsibility id="+actionRequest.getResponsibilityId());
                ResponsibleParty responsibleParty = routeModule.resolveResponsibilityId(actionRequest.getResponsibilityId());
                if (responsibleParty == null) {
                    return;
                }
                if (responsibleParty.getUserId() != null) {
                    WorkflowUser user = getUserService().getWorkflowUser(responsibleParty.getUserId());
                    actionRequest.setWorkflowId(user.getWorkflowUserId().getWorkflowId());
                } else if (responsibleParty.getGroupId() != null) {
                    Workgroup workgroup = getWorkgroupService().getWorkgroup(responsibleParty.getGroupId());
                    actionRequest.setWorkgroupId(workgroup.getWorkflowGroupId().getGroupId());
                } else if (responsibleParty.getRoleName() != null) {
                    actionRequest.setRoleName(responsibleParty.getRoleName());
                }
            }
        } catch (Exception e) {
            LOG.error("Exception thrown when trying to resolve responsibility id " + responsibilityId, e);
            throw new RuntimeException(e);
        }
    }

    private boolean deactivateOnActionAlreadyTaken(ActionRequestValue actionRequestToActivate, ActivationContext activationContext) throws EdenUserNotFoundException {
        if (!actionRequestToActivate.getIgnorePrevAction().booleanValue()) {
        	ActionTakenValue previousActionTaken = null;
        	if (!activationContext.isSimulation()) {
                previousActionTaken = getActionTakenService().getPreviousAction(actionRequestToActivate);
        	} else {
                previousActionTaken = getActionTakenService().getPreviousAction(actionRequestToActivate,activationContext.getSimulatedActionsTaken());
        	}
            if (previousActionTaken != null) {
                LOG.debug("found a satisfying action taken so setting this request done.  Action Request Id " + actionRequestToActivate.getActionRequestId());
                //set up the delegation for an action taken if this is a delegate request and the delegate has
                //already taken action.
                if (!previousActionTaken.isForDelegator() && actionRequestToActivate.getParentActionRequest() != null) {
                    previousActionTaken.setDelegator(actionRequestToActivate.getParentActionRequest().getRecipient());
                    if (!activationContext.isSimulation()) {
                        getActionTakenService().saveActionTaken(previousActionTaken);
                    }
                }
                deactivateRequest(previousActionTaken, actionRequestToActivate, null, activationContext);
                return true;
            }
        }
        LOG.debug("Ignoring previous for action request " + actionRequestToActivate.getActionRequestId());
        return false;
    }

    public void deactivateRequest(ActionTakenValue actionTaken, ActionRequestValue actionRequest) {
        deactivateRequest(actionTaken, actionRequest, null, new ActivationContext(!ActivationContext.CONTEXT_IS_SIMULATION));
    }

    public void deactivateRequest(ActionTakenValue actionTaken, ActionRequestValue actionRequest, boolean simulate) {
        deactivateRequest(actionTaken, actionRequest, null, new ActivationContext(simulate));
    }

    public void deactivateRequest(ActionTakenValue actionTaken, ActionRequestValue actionRequest, ActivationContext activationContext) {
        deactivateRequest(actionTaken, actionRequest, null, activationContext);
    }

    public void deactivateRequests(ActionTakenValue actionTaken, List actionRequests) {
        deactivateRequests(actionTaken, actionRequests, null, new ActivationContext(!ActivationContext.CONTEXT_IS_SIMULATION));
    }

    public void deactivateRequests(ActionTakenValue actionTaken, List actionRequests, boolean simulate) {
        deactivateRequests(actionTaken, actionRequests, null, new ActivationContext(simulate));
    }

    public void deactivateRequests(ActionTakenValue actionTaken, List actionRequests, ActivationContext activationContext) {
        deactivateRequests(actionTaken, actionRequests, null, activationContext);
    }

    private void deactivateRequests(ActionTakenValue actionTaken, Collection actionRequests, ActionRequestValue deactivationRequester, ActivationContext activationContext) {
        if (actionRequests == null) {
            return;
        }
        for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
            deactivateRequest(actionTaken, actionRequest, deactivationRequester, activationContext);
        }
    }

    private void deactivateRequest(ActionTakenValue actionTaken, ActionRequestValue actionRequest, ActionRequestValue deactivationRequester, ActivationContext activationContext) {
        if (actionRequest == null || actionRequest.isDeactivated() || haltForAllApprove(actionRequest, deactivationRequester)) {
            return;
        }
        actionRequest.setStatus(EdenConstants.ACTION_REQUEST_DONE_STATE);
        actionRequest.setActionTaken(actionTaken);
        if (actionTaken != null) {
            actionTaken.getActionRequests().add(actionRequest);
        }
        if (!activationContext.isSimulation()) {
            getActionRequestDAO().saveActionRequest(actionRequest);
            getActionListService().deleteActionItems(actionRequest.getActionRequestId());
        }
        deactivateRequests(actionTaken, actionRequest.getChildrenRequests(), actionRequest, activationContext);
        deactivateRequest(actionTaken, actionRequest.getParentActionRequest(), actionRequest, activationContext);
    }

    /**
     * this method is a hack that is in response to bug http://fms.dfa.cornell.edu:8080/browse/KULWF-428
     * the solution is to have multiple action items although the solution seemed best suited here...
     *
     * basically in certain scenarios we can be left we active workgroup action requests
     * that have no action items and this method seeks to repair that.  It is evil
     */
    /*private void activateAnyAbandonedWorkgroupActionRequests(Long documentId) throws EdenUserNotFoundException {
        List activeRequests = findByStatusAndDocId(EdenConstants.ACTION_REQUEST_ACTIVATED, documentId);
        List requestsGoneBad = new ArrayList();
        for (Iterator iter = activeRequests.iterator(); iter.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
            if (actionRequest.isWorkgroupRequest() && actionRequest.getActionItems().size() != actionRequest.getWorkgroup().getMembers().size() &&
                    actionRequest.getParentActionRequest() == null) {
                //set to pending to evade activation logic
                actionRequest.setStatus(EdenConstants.ACTION_REQUEST_INITIALIZED);
                requestsGoneBad.add(actionRequest);
            }
        }
        activateRequests(requestsGoneBad);
    }*/

    /**
     * Returns true if we are dealing with an 'All Approve' request, the requester of the deactivation is a child of the 'All Approve' request, and all of the children have not been deactivated. If all of the children are already deactivated or a non-child request initiated deactivation, then this method returns false. false otherwise.
     */
    private boolean haltForAllApprove(ActionRequestValue actionRequest, ActionRequestValue deactivationRequester) {
        if (EdenConstants.APPROVE_POLICY_ALL_APPROVE.equals(actionRequest.getApprovePolicy()) && actionRequest.hasChild(deactivationRequester)) {
            boolean allDeactivated = true;
            for (Iterator iterator = actionRequest.getChildrenRequests().iterator(); iterator.hasNext();) {
                ActionRequestValue childRequest = (ActionRequestValue) iterator.next();
                if (!(allDeactivated = allDeactivated && childRequest.isDeactivated())) {
                    return true;
                }
            }
        }
        return false;
    }

    public List getRootRequests(Collection actionRequests) {
        Set unsavedRequests = new HashSet();
        Map requestMap = new HashMap();
        for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
            ActionRequestValue rootRequest = getRoot(actionRequest);
            if (rootRequest.getActionRequestId() != null) {
                requestMap.put(rootRequest.getActionRequestId(), rootRequest);
            } else {
                unsavedRequests.add(rootRequest);
            }
        }
        List requests = new ArrayList();
        requests.addAll(requestMap.values());
        requests.addAll(unsavedRequests);
        return requests;
    }

    public ActionRequestValue getRoot(ActionRequestValue actionRequest) {
        if (actionRequest == null) {
            return null;
        }
        if (actionRequest.getParentActionRequest() != null) {
            return getRoot(actionRequest.getParentActionRequest());
        }
        return actionRequest;
    }

    public List findAllValidRequests(WorkflowUser user, Long routeHeaderId, String requestCode) throws EdenUserNotFoundException {
    	ActionRequestDAO arDAO = getActionRequestDAO();
        Collection pendingArs = arDAO.findAllPendingByDocId(routeHeaderId);
        return findAllValidRequests(user, pendingArs, requestCode);
    }

    public List findAllValidRequests(WorkflowUser user, Collection actionRequests, String requestCode) throws EdenUserNotFoundException {
        List matchedArs = new ArrayList();
        for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
            ActionRequestValue ar = (ActionRequestValue) iter.next();
            if (ActionRequestValue.compareActionCode(ar.getActionRequested(), requestCode) > 0) {
                continue;
            }
            if (ar.isUserRequest() && user.getWorkflowUserId().getWorkflowId().equals(ar.getWorkflowId())) {
                matchedArs.add(ar);
            } else if (ar.isWorkgroupRequest()) {
                Workgroup workgroup = getWorkgroupService().getWorkgroup(new WorkflowGroupId(ar.getWorkgroupId()));
                if (workgroup.hasMember(user)) {
                    matchedArs.add(ar);
                }
            }
        }
        return matchedArs;
    }

    public void updateActionRequestsForResponsibilityChange(Set responsibilityIds) {
        PerformanceLogger performanceLogger = new PerformanceLogger();
        Collection documentsAffected = getRouteHeaderService().findPendingByResponsibilityIds(responsibilityIds);
        String cacheWaitValue = Utilities.getApplicationConstant(EdenConstants.RULE_CACHE_REQUEUE_WAIT_TIME_KEY);
        Long cacheWait = new Long(EdenConstants.DEFAULT_CACHE_REQUEUE_WAIT_TIME);
        if (!Utilities.isEmpty(cacheWaitValue)) {
            try {
                cacheWait = Long.valueOf(cacheWaitValue);
            } catch (NumberFormatException e) {
                LOG.warn("Cache wait time is not a valid number: " + cacheWaitValue);
            }
        }
        LOG.info("Scheduling requeue of "+documentsAffected.size()+" documents, affected by "+responsibilityIds.size()+" responsibility changes.  Installing a processing wait time of "+cacheWait.longValue()+" milliseconds to avoid stale rule cache.");
        for (Iterator iterator = documentsAffected.iterator(); iterator.hasNext();) {
        	Long routeHeaderId = (Long) iterator.next();
            String messageEntity = KEWServiceLocator.getRouteHeaderService().getMessageEntityByDocumentId(routeHeaderId);
            if (messageEntity == null) {
                messageEntity = Core.getCurrentContextConfig().getMessageEntity();
            }
            DocumentRequeuerService documentRequeuer = MessageServiceNames.getDocumentRequeuerService(messageEntity, routeHeaderId, cacheWait);
            documentRequeuer.requeueDocument(routeHeaderId);
        }
        performanceLogger.log("Time to updateActionRequestsForResponsibilityChange");
    }

    /**
     * Deletes an action request and all of its action items following the graph down through the action request's children. This method should be invoked on a top-level action request.
     */
    public void deleteActionRequestGraph(ActionRequestValue actionRequest) {
        for (Iterator iterator = actionRequest.getActionItems().iterator(); iterator.hasNext();) {
            getActionListService().deleteActionItem((ActionItem) iterator.next());
        }
        getActionRequestDAO().delete(actionRequest.getActionRequestId());
        for (Iterator iterator = actionRequest.getChildrenRequests().iterator(); iterator.hasNext();) {
            deleteActionRequestGraph((ActionRequestValue) iterator.next());
        }
    }

    public List findByRouteHeaderIdIgnoreCurrentInd(Long routeHeaderId) {
        return getActionRequestDAO().findByRouteHeaderIdIgnoreCurrentInd(routeHeaderId);
    }

    public List findAllActionRequestsByRouteHeaderId(Long routeHeaderId) {
        return getActionRequestDAO().findAllByDocId(routeHeaderId);
    }

    public List findAllRootActionRequestsByRouteHeaderId(Long routeHeaderId) {
        return getActionRequestDAO().findAllRootByDocId(routeHeaderId);
    }

    public List findPendingByActionRequestedAndDocId(String actionRequestedCd, Long routeHeaderId) {
        return getActionRequestDAO().findPendingByActionRequestedAndDocId(actionRequestedCd, routeHeaderId);
    }

    public List findPendingByDocIdAtOrBelowRouteLevel(Long routeHeaderId, Integer routeLevel) {
        return getActionRequestDAO().findPendingByDocIdAtOrBelowRouteLevel(routeHeaderId, routeLevel);
    }

    public List findPendingRootRequestsByDocId(Long routeHeaderId) {
    	return getRootRequests(findPendingByDoc(routeHeaderId));
    }

    public List findPendingRootRequestsByDocIdAtRouteNode(Long routeHeaderId, Long nodeInstanceId) {
        return getActionRequestDAO().findPendingRootRequestsByDocIdAtRouteNode(routeHeaderId, nodeInstanceId);
    }

    public List findRootRequestsByDocIdAtRouteNode(Long documentId, Long nodeInstanceId) {
        return getActionRequestDAO().findRootRequestsByDocIdAtRouteNode(documentId, nodeInstanceId);
    }

    public List findPendingRootRequestsByDocIdAtOrBelowRouteLevel(Long routeHeaderId, Integer routeLevel) {
        return getActionRequestDAO().findPendingRootRequestsByDocIdAtOrBelowRouteLevel(routeHeaderId, routeLevel);
    }

    public List findPendingRootRequestsByDocIdAtRouteLevel(Long routeHeaderId, Integer routeLevel) {
        return getActionRequestDAO().findPendingRootRequestsByDocIdAtRouteLevel(routeHeaderId, routeLevel);
    }

    public List findPendingRootRequestsByDocumentType(Long documentTypeId) {
    	return getActionRequestDAO().findPendingRootRequestsByDocumentType(documentTypeId);
    }

    public void saveActionRequest(ActionRequestValue actionRequest) throws EdenUserNotFoundException {

        if (actionRequest.isWorkgroupRequest() && ! actionRequest.getWorkgroup().getActiveInd().booleanValue()) {
            throw new RuntimeException("Routing to inactive workgroup.  Putting document in exception routing.");
        }
        getActionRequestDAO().saveActionRequest(actionRequest);
    }

    public List findPendingByDoc(Long routeHeaderId) {
        return getActionRequestDAO().findAllPendingByDocId(routeHeaderId);
    }

    public List findPendingByDocRequestCdRouteLevel(Long routeHeaderId, String requestCode, Integer routeLevel) {
        List requests = new ArrayList();
        for (Iterator iter = getActionRequestDAO().findAllPendingByDocId(routeHeaderId).iterator(); iter.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
            if (ActionRequestValue.compareActionCode(actionRequest.getActionRequested(), requestCode) > 0) {
                continue;
            }
            if(actionRequest.getRouteLevel().intValue() == routeLevel.intValue()){
                requests.add(actionRequest);
            }
        }
        return requests;
    }

    public List findPendingByDocRequestCdNodeName(Long routeHeaderId, String requestCode, String nodeName) {
        List requests = new ArrayList();
        for (Iterator iter = getActionRequestDAO().findAllPendingByDocId(routeHeaderId).iterator(); iter.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
            if (ActionRequestValue.compareActionCode(actionRequest.getActionRequested(), requestCode) > 0) {
                continue;
            }
            if(actionRequest.getNodeInstance() != null && actionRequest.getNodeInstance().getName().equals(nodeName)) {
                requests.add(actionRequest);
            }
        }
        return requests;
    }

    public List findActivatedByWorkgroup(Workgroup workgroup) {
        return getActionRequestDAO().findActivatedByWorkgroup(workgroup);
    }

    private WorkgroupService getWorkgroupService() {
        return (WorkgroupService) KEWServiceLocator.getWorkgroupService();
    }

    private ActionListService getActionListService() {
        return (ActionListService) KEWServiceLocator.getActionListService();
    }

    private ActionTakenService getActionTakenService() {
        return (ActionTakenService) KEWServiceLocator.getActionTakenService();
    }

    private UserService getUserService() {
        return (UserService) KEWServiceLocator.getUserService();
    }

    public ActionRequestDAO getActionRequestDAO() {
        return actionRequestDAO;
    }

    public void setActionRequestDAO(ActionRequestDAO actionRequestDAO) {
        this.actionRequestDAO = actionRequestDAO;
    }

    private RouteHeaderService getRouteHeaderService() {
        return (RouteHeaderService) KEWServiceLocator.getService(KEWServiceLocator.DOC_ROUTE_HEADER_SRV);
    }

    public List findByStatusAndDocId(String statusCd, Long routeHeaderId) {
        return getActionRequestDAO().findByStatusAndDocId(statusCd, routeHeaderId);
    }

    public void alterActionRequested(List actionRequests, String actionRequestCd) throws EdenUserNotFoundException {
        for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();

            actionRequest.setActionRequested(actionRequestCd);
            for (Iterator iterator = actionRequest.getActionItems().iterator(); iterator.hasNext();) {
                ActionItem item = (ActionItem) iterator.next();
                item.setActionRequestCd(actionRequestCd);
            }

            saveActionRequest(actionRequest);
        }
    }

    // TODO this still won't work in certain cases when checking from the root
    public boolean isDuplicateRequest(ActionRequestValue actionRequest) {
        List requests = findAllRootActionRequestsByRouteHeaderId(actionRequest.getRouteHeader().getRouteHeaderId());
        for (Iterator iterator = requests.iterator(); iterator.hasNext();) {
            ActionRequestValue existingRequest = (ActionRequestValue) iterator.next();
            if (existingRequest.getStatus().equals(EdenConstants.ACTION_REQUEST_DONE_STATE) && existingRequest.getRouteLevel().equals(actionRequest.getRouteHeader().getDocRouteLevel()) && ObjectUtils.equals(existingRequest.getWorkflowId(), actionRequest.getWorkflowId()) && ObjectUtils.equals(existingRequest.getWorkgroupId(), actionRequest.getWorkgroupId()) && ObjectUtils.equals(existingRequest.getRoleName(), actionRequest.getRoleName()) && ObjectUtils.equals(existingRequest.getQualifiedRoleName(), actionRequest.getQualifiedRoleName()) && existingRequest.getActionRequested().equals(actionRequest.getActionRequested())) {
                return true;
            }
        }
        return false;
    }

    public Recipient findDelegator(List actionRequests) throws EdenUserNotFoundException {
        Recipient delegator = null;
        String requestCode = EdenConstants.ACTION_REQUEST_FYI_REQ;
        for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
            ActionRequestValue delegatorRequest = findDelegatorRequest(actionRequest);
            if (delegatorRequest != null) {
                if (ActionRequestValue.compareActionCode(delegatorRequest.getActionRequested(), requestCode) >= 0) {
                    delegator = delegatorRequest.getRecipient();
                    requestCode = delegatorRequest.getActionRequested();
                }
            }
        }
        return delegator;
    }

    public Recipient findDelegator(ActionRequestValue actionRequest) throws EdenUserNotFoundException {
        ActionRequestValue delegatorRequest = findDelegatorRequest(actionRequest);
        Recipient delegator = null;
        if (delegatorRequest != null) {
            delegator = delegatorRequest.getRecipient();
        }
        return delegator;
    }

    public ActionRequestValue findDelegatorRequest(ActionRequestValue actionRequest) {
        ActionRequestValue parentRequest = actionRequest.getParentActionRequest();
        if (parentRequest != null && !(parentRequest.isUserRequest() || parentRequest.isWorkgroupRequest())) {
            parentRequest = findDelegatorRequest(parentRequest);
        }
        return parentRequest;
    }

    public void deleteByRouteHeaderId(Long routeHeaderId){
        actionRequestDAO.deleteByRouteHeaderId(routeHeaderId);
    }

    public void deleteByActionRequestId(Long actionRequestId){
        actionRequestDAO.delete(actionRequestId);
    }

    public void validateActionRequest(ActionRequestValue actionRequest){
        LOG.debug("Enter validateActionRequest(..)");
        List errors = new ArrayList();

        String actionRequestCd = actionRequest.getActionRequested();
        if(actionRequestCd == null || actionRequestCd.trim().equals("")){
            errors.add(new WorkflowServiceErrorImpl("ActionRequest cd null.", "actionrequest.actionrequestcd.empty", actionRequest.getActionRequestId().toString()));
        } else if(!EdenConstants.ACTION_REQUEST_CD.containsKey(actionRequestCd)){
            errors.add(new WorkflowServiceErrorImpl("ActionRequest cd invalid.", "actionrequest.actionrequestcd.invalid", actionRequest.getActionRequestId().toString()));
        }

        Long routeHeaderId = actionRequest.getRouteHeaderId();
        if(routeHeaderId == null || routeHeaderId.longValue() == 0){
            errors.add(new WorkflowServiceErrorImpl("ActionRequest Document id empty.", "actionrequest.routeheaderid.empty", actionRequest.getActionRequestId().toString()));
        } else if(getRouteHeaderService().getRouteHeader(routeHeaderId) == null){
            errors.add(new WorkflowServiceErrorImpl("ActionRequest Document id invalid.", "actionrequest.routeheaderid.invalid", actionRequest.getActionRequestId().toString()));
        }

        String actionRequestStatus = actionRequest.getStatus();
        if(actionRequestStatus == null || actionRequestStatus.trim().equals("")){
            errors.add(new WorkflowServiceErrorImpl("ActionRequest status null.", "actionrequest.actionrequeststatus.empty", actionRequest.getActionRequestId().toString()));
        } else if(!EdenConstants.ACTION_REQUEST_STATUS.containsKey(actionRequestStatus)){
            errors.add(new WorkflowServiceErrorImpl("ActionRequest status invalid.", "actionrequest.actionrequeststatus.invalid", actionRequest.getActionRequestId().toString()));
        }

        if(actionRequest.getResponsibilityId() == null){
            errors.add(new WorkflowServiceErrorImpl("ActionRequest responsibility id null.", "actionrequest.responsibilityid.empty", actionRequest.getActionRequestId().toString()));
        }

        Integer priority = actionRequest.getPriority();
        if(priority == null){
            errors.add(new WorkflowServiceErrorImpl("ActionRequest priority null.", "actionrequest.priority.empty", actionRequest.getActionRequestId().toString()));
        }

//        if(actionRequest.getRouteMethodName() == null || actionRequest.getRouteMethodName().trim().equals("")){
//            errors.add(new WorkflowServiceErrorImpl("ActionRequest route method name null.", "actionrequest.routemethodname.empty", actionRequest.getActionRequestId().toString()));
//        }

        Integer routeLevel = actionRequest.getRouteLevel();
        if(routeLevel == null){
            errors.add(new WorkflowServiceErrorImpl("ActionRequest route level null.", "actionrequest.routelevel.empty", actionRequest.getActionRequestId().toString()));
        } else if(routeLevel.intValue() < -1){
            errors.add(new WorkflowServiceErrorImpl("ActionRequest route level invalid.", "actionrequest.routelevel.invalid", actionRequest.getActionRequestId().toString()));
        }

        Integer version = actionRequest.getDocVersion();
        if(version == null){
            errors.add(new WorkflowServiceErrorImpl("ActionRequest doc version null.", "actionrequest.docversion.empty", actionRequest.getActionRequestId().toString()));
        }

        if(actionRequest.getCreateDate() == null){
            errors.add(new WorkflowServiceErrorImpl("ActionRequest create date null.", "actionrequest.createdate.empty", actionRequest.getActionRequestId().toString()));
        }

        String recipientType = actionRequest.getRecipientTypeCd();
        if(recipientType != null && !recipientType.trim().equals("")){
            if(recipientType.equals(EdenConstants.WORKGROUP)){
                Long workgroupId = actionRequest.getWorkgroupId();
                if(workgroupId == null){
                    errors.add(new WorkflowServiceErrorImpl("ActionRequest workgroup null.", "actionrequest.workgroup.empty", actionRequest.getActionRequestId().toString()));
                } else if(getWorkgroupService().getWorkgroup(new WorkflowGroupId(workgroupId)) == null){
                    errors.add(new WorkflowServiceErrorImpl("ActionRequest workgroup invalid.", "actionrequest.workgroup.invalid", actionRequest.getActionRequestId().toString()));
                }

            }
            if(recipientType.equals(EdenConstants.PERSON)){
                String workflowId = actionRequest.getWorkflowId();
                if(workflowId == null || workflowId.trim().equals("")){
                    errors.add(new WorkflowServiceErrorImpl("ActionRequest person id null.", "actionrequest.persosn.empty", actionRequest.getActionRequestId().toString()));
                } else {
                  try{
                     getUserService().getWorkflowUser(new WorkflowUserId(workflowId));
                  } catch(EdenUserNotFoundException e){
                    errors.add(new WorkflowServiceErrorImpl("ActionRequest person id invalid.", "actionrequest.personid.invalid", actionRequest.getActionRequestId().toString()));
                }
            }

            if(recipientType.equals(EdenConstants.ROLE) && (actionRequest.getRoleName() == null || actionRequest.getRoleName().trim().equals(""))){
                errors.add(new WorkflowServiceErrorImpl("ActionRequest role name null.", "actionrequest.rolename.null", actionRequest.getActionRequestId().toString()));
            }
        }
        LOG.debug("Exit validateActionRequest(..) ");
        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("ActionRequest Validation Error", errors);
        }
    }
  }

    public List getDelegateRequests(ActionRequestValue actionRequest) {
        List delegateRequests = new ArrayList();
        List requests = getTopLevelRequests(actionRequest);
        for (Iterator iterator = requests.iterator(); iterator.hasNext();) {
            ActionRequestValue parentActionRequest = (ActionRequestValue) iterator.next();
            delegateRequests.addAll(parentActionRequest.getChildrenRequests());
        }
        return delegateRequests;
    }

    public List getTopLevelRequests(ActionRequestValue actionRequest) {
        List topLevelRequests = new ArrayList();
        if (actionRequest.isRoleRequest()) {
            topLevelRequests.addAll(actionRequest.getChildrenRequests());
        } else {
            topLevelRequests.add(actionRequest);
        }
        return topLevelRequests;
    }

    public boolean isValidActionRequestCode(String actionRequestCode) {
    	return actionRequestCode != null && EdenConstants.ACTION_REQUEST_CODES.containsKey(actionRequestCode);
    }

    public boolean doesUserHaveRequest(WorkflowUser user, Long documentId) {
    	if (getActionRequestDAO().doesDocumentHaveUserRequest(user.getWorkflowId(), documentId)) {
    		return true;
    	}
    	// TODO since we only store the workgroup id for workgroup requests, if the user is in a workgroup that has a request
    	// than we need get all the requests with workgroup ids and see if our user is in that group
    	//
    	// During the 2.4 dev cycle we should be able to remove this because we are planning on creating individual
    	// child action requests of a workgroup request for each user, which will just require the doesDocumentHaveUserRequest
    	// to work
    	List<Long> workgroupNames = getActionRequestDAO().getRequestWorkgroupIds(documentId);
    	for (Long workgroupId : workgroupNames) {
    		Workgroup workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(workgroupId));
    		if (workgroup.hasMember(user)) {
    			return true;
    		}
    	}
		return false;
    }

}