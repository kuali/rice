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
package org.kuali.rice.kew.actionrequest.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.CoreConfigHelper;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionlist.service.ActionListService;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.actionrequest.dao.ActionRequestDAO;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.actiontaken.service.ActionTakenService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.action.ActionRequestPolicy;
import org.kuali.rice.kew.api.action.ActionRequestStatus;
import org.kuali.rice.kew.api.action.RecipientType;
import org.kuali.rice.kew.api.document.DocumentRefreshQueue;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.ActivationContext;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.service.RouteHeaderService;
import org.kuali.rice.kew.routemodule.RouteModule;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.FutureRequestDocumentStateManager;
import org.kuali.rice.kew.util.PerformanceLogger;
import org.kuali.rice.kew.util.ResponsibleParty;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.util.KRADConstants;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

/**
 * Default implementation of the {@link ActionRequestService}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionRequestServiceImpl implements ActionRequestService {

    private static final Logger LOG = Logger.getLogger(ActionRequestServiceImpl.class);

    private static final String STATUS = "status";
    private static final String DOCUMENT_ID = "documentId";
    private static final String CURRENT_INDICATOR = "currentIndicator";
    private static final String PARENT_ACTION_REQUEST = "parentActionRequest";
    private static final String ACTION_REQUESTED = "actionRequested";
    private static final String ROUTE_NODE_INSTANCE_ID = "nodeInstance.routeNodeInstanceId";
    private static final String GROUP_ID = "groupId";
    private static final String ACTION_TAKEN_ID = "actionTaken.actionTakenId";
    private static final String RECIPIENT_TYPE_CD = "recipientTypeCd";
    private static final String PRINCIPAL_ID = "principalId";

    private DataObjectService dataObjectService;
    private ActionRequestDAO actionRequestDAO;

    @Override
    public ActionRequestValue findByActionRequestId(String actionRequestId) {
        return getDataObjectService().find(ActionRequestValue.class, actionRequestId);
    }

    @Override
    public Map<String, String> getActionsRequested(DocumentRouteHeaderValue routeHeader, String principalId, boolean completeAndApproveTheSame) {
    	return getActionsRequested(principalId, routeHeader.getActionRequests(), completeAndApproveTheSame);
    }

    /**
     * Returns a Map of actions that are requested for the given principalId in the given list of action requests.
     */
    protected Map<String, String> getActionsRequested(String principalId, List<ActionRequestValue> actionRequests, boolean completeAndApproveTheSame) {
    	Map<String, String> actionsRequested = new HashMap<String, String>();
        actionsRequested.put(KewApiConstants.ACTION_REQUEST_FYI_REQ, "false");
        actionsRequested.put(KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, "false");
        actionsRequested.put(KewApiConstants.ACTION_REQUEST_APPROVE_REQ, "false");
        actionsRequested.put(KewApiConstants.ACTION_REQUEST_COMPLETE_REQ, "false");
    	String topActionRequested = KewApiConstants.ACTION_REQUEST_FYI_REQ;
        for (ActionRequestValue actionRequest : actionRequests) {
            // we are getting the full list of requests here, so no need to look at role requests, if we did this then
            // we could get a "false positive" for "all approve" roles where only part of the request graph is marked
            // as "done"
            if (!RecipientType.ROLE.getCode().equals(actionRequest.getRecipientTypeCd()) &&
                    actionRequest.isRecipientRoutedRequest(principalId) && actionRequest.isActive()) {
                int actionRequestComparison = ActionRequestValue.compareActionCode(actionRequest.getActionRequested(), topActionRequested, completeAndApproveTheSame);
                if (actionRequest.isFYIRequest() && actionRequestComparison >= 0) {
                    actionsRequested.put(KewApiConstants.ACTION_REQUEST_FYI_REQ, "true");
                } else if (actionRequest.isAcknowledgeRequest() && actionRequestComparison >= 0) {
                    actionsRequested.put(KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, "true");
                    actionsRequested.put(KewApiConstants.ACTION_REQUEST_FYI_REQ, "false");
                    topActionRequested = actionRequest.getActionRequested();
                } else if (actionRequest.isApproveRequest() && actionRequestComparison >= 0) {
                    actionsRequested.put(KewApiConstants.ACTION_REQUEST_APPROVE_REQ, "true");
                    actionsRequested.put(KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, "false");
                    actionsRequested.put(KewApiConstants.ACTION_REQUEST_FYI_REQ, "false");
                    topActionRequested = actionRequest.getActionRequested();
                } else if (actionRequest.isCompleteRequst() && actionRequestComparison >= 0) {
                	actionsRequested.put(KewApiConstants.ACTION_REQUEST_COMPLETE_REQ, "true");
                	actionsRequested.put(KewApiConstants.ACTION_REQUEST_APPROVE_REQ, "false");
                    actionsRequested.put(KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, "false");
                    actionsRequested.put(KewApiConstants.ACTION_REQUEST_FYI_REQ, "false");
                	if (completeAndApproveTheSame) {
                		actionsRequested.put(KewApiConstants.ACTION_REQUEST_APPROVE_REQ, "true");
                	}
                    topActionRequested = actionRequest.getActionRequested();
                }
            }
        }
        return actionsRequested;
    }

    @Override
    public ActionRequestValue initializeActionRequestGraph(ActionRequestValue actionRequest,
            DocumentRouteHeaderValue document, RouteNodeInstance nodeInstance) {
        if (actionRequest.getParentActionRequest() != null) {
            LOG.warn("-->A non parent action request from doc " + document.getDocumentId());
            actionRequest = KEWServiceLocator.getActionRequestService().getRoot(actionRequest);
        }
        propagatePropertiesToRequestGraph(actionRequest, document, nodeInstance);
        return actionRequest;
    }

    private void propagatePropertiesToRequestGraph(ActionRequestValue actionRequest, DocumentRouteHeaderValue document,
            RouteNodeInstance nodeInstance) {
        setPropertiesToRequest(actionRequest, document, nodeInstance);
        for (ActionRequestValue actionRequestValue : actionRequest.getChildrenRequests())
        {
            propagatePropertiesToRequestGraph(actionRequestValue, document, nodeInstance);
        }
    }

    private void setPropertiesToRequest(ActionRequestValue actionRequest, DocumentRouteHeaderValue document,
            RouteNodeInstance nodeInstance) {
        actionRequest.setDocumentId(document.getDocumentId());
        actionRequest.setDocVersion(document.getDocVersion());
        actionRequest.setRouteLevel(document.getDocRouteLevel());
        actionRequest.setNodeInstance(nodeInstance);
        actionRequest.setStatus(ActionRequestStatus.INITIALIZED.getCode());
    }



    @Override
    public List<ActionRequestValue> activateRequests(List<ActionRequestValue> actionRequests) {
        return activateRequests(actionRequests, new ActivationContext(!ActivationContext.CONTEXT_IS_SIMULATION));
    }

    @Override
    public List<ActionRequestValue> activateRequests(List<ActionRequestValue> actionRequests, boolean simulate) {
        return activateRequests(actionRequests, new ActivationContext(simulate));
    }

    @Override
    public List<ActionRequestValue> activateRequests(List<ActionRequestValue> actionRequests, ActivationContext activationContext) {
        if (actionRequests == null) {
            return new ArrayList<ActionRequestValue>();
        }
        PerformanceLogger performanceLogger = null;
        if ( LOG.isInfoEnabled() ) {
        	performanceLogger = new PerformanceLogger();
        }
        activationContext.setGeneratedActionItems(new ArrayList<ActionItem>());
        // first step, we are going to save the action requests, since we are using JPA on the backend doing a save will
        // either persist or merge this action request and all of it's children into the current persistence context so
        // that subsequent saves won't be required and we won't have to return and reset action requests from every
        // internal method because we will *know* it has already been merged into the persistence contest
        if (!activationContext.isSimulation()) {
            actionRequests = saveActionRequests(actionRequests);
        }

        activateRequestsInternal(actionRequests, activationContext);
        if (!activationContext.isSimulation()) {
            KEWServiceLocator.getNotificationService().notify(ActionItem.to(activationContext.getGeneratedActionItems()));
        }
        if ( LOG.isInfoEnabled() ) {
        	performanceLogger.log("Time to " + (activationContext.isSimulation() ? "simulate activation of " : "activate ")
        			+ actionRequests.size() + " action requests.");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Generated " + activationContext.getGeneratedActionItems().size() + " action items.");
        }
        return actionRequests;
    }

    @Override
    public ActionRequestValue activateRequest(ActionRequestValue actionRequest) {
        return activateRequests(Collections.singletonList(actionRequest), new ActivationContext(!ActivationContext.CONTEXT_IS_SIMULATION)).get(0);
    }

    @Override
    public ActionRequestValue activateRequest(ActionRequestValue actionRequest, boolean simulate) {
        return activateRequests(Collections.singletonList(actionRequest), new ActivationContext(simulate)).get(0);
    }

    @Override
    public ActionRequestValue activateRequest(ActionRequestValue actionRequest, ActivationContext activationContext) {
        return activateRequests(Collections.singletonList(actionRequest), activationContext).get(0);
    }

    @Override
    public ActionRequestValue activateRequestNoNotification(ActionRequestValue actionRequest, ActivationContext activationContext) {
        activationContext.setGeneratedActionItems(new ArrayList<ActionItem>());
        actionRequest = saveActionRequest(actionRequest, activationContext.isSimulation());
        activateRequestInternal(actionRequest, activationContext);
        return actionRequest;
    }

    /**
     * Internal helper method for activating a Collection of action requests and their children. Maintains an accumulator
     * for generated action items.
     *
     * <p>IMPORTANT! This method assumes that the action requests given have already been "merged" into the
     * JPA persistence context.</p>
     */
    private void activateRequestsInternal(List<ActionRequestValue> actionRequests, ActivationContext activationContext) {
        if (actionRequests != null) {
            for (ActionRequestValue actionRequest : actionRequests) {
                activateRequestInternal(actionRequest, activationContext);
            }
        }
    }

    /**
     * Internal helper method for activating a single action requests and it's children. Maintains an accumulator for
     * generated action items.
     *
     * <p>IMPORTANT! This method assumes that the action request given has already been "merged" into the
     * JPA persistence context.</p>
     */
    private void activateRequestInternal(ActionRequestValue actionRequest, ActivationContext activationContext) {
        PerformanceLogger performanceLogger = null;
        if ( LOG.isInfoEnabled() ) {
        	performanceLogger = new PerformanceLogger();
        }
        if (actionRequest == null || actionRequest.isActive() || actionRequest.isDeactivated()) {
            return;
        }
        processResponsibilityId(actionRequest);
        if (deactivateOnActionAlreadyTaken(actionRequest, activationContext)) {
            return;
        }
        if (deactivateOnInactiveGroup(actionRequest, activationContext)) {
            return;
        }
        if (deactivateOnEmptyGroup(actionRequest, activationContext)) {
        	return;
        }
        actionRequest.setStatus(ActionRequestStatus.ACTIVATED.getCode());
        if (!activationContext.isSimulation()) {
            activationContext.getGeneratedActionItems().addAll(generateActionItems(actionRequest, activationContext));
        }
        activateRequestsInternal(actionRequest.getChildrenRequests(), activationContext);
        activateRequestInternal(actionRequest.getParentActionRequest(), activationContext);
        if ( LOG.isInfoEnabled() ) {
        	if (activationContext.isSimulation()) {
                performanceLogger.log("Time to simulate activation of request.");
	        } else {
	            performanceLogger.log("Time to activate action request with id " + actionRequest.getActionRequestId());
	        }
        }
    }

    /**
     * Generates ActionItems for the given ActionRequest and returns the List of generated Action Items.
     *
     * @return the List of generated ActionItems
     */
    private List<ActionItem> generateActionItems(ActionRequestValue actionRequest, ActivationContext activationContext) {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug("generating the action items for request " + actionRequest.getActionRequestId());
    	}
        List<ActionItem> actionItems = new ArrayList<ActionItem>();
        if (!actionRequest.isPrimaryDelegator()) {
            if (actionRequest.isGroupRequest()) {
                List<String> principalIds =  KimApiServiceLocator.getGroupService().getMemberPrincipalIds(actionRequest.getGroupId());
                actionItems.addAll(createActionItemsForPrincipals(actionRequest, principalIds));
            } else if (actionRequest.isUserRequest()) {
                ActionItem actionItem = getActionListService().createActionItemForActionRequest(actionRequest);
                actionItems.add(actionItem);
            }
        }
        List<ActionItem> actionItemsToReturn = new ArrayList<ActionItem>(actionItems.size());
        if (!activationContext.isSimulation()) {
            for (ActionItem actionItem: actionItems) {
            	if ( LOG.isDebugEnabled() ) {
            		LOG.debug("Saving action item: " + actionItems);
            	}
                actionItem = getActionListService().saveActionItem(actionItem);
                actionItemsToReturn.add(actionItem);
            }
        } else {
        	actionRequest.getSimulatedActionItems().addAll(actionItems);
        	actionItemsToReturn.addAll(actionItems);
        }
        return actionItemsToReturn;
    }

    private List<ActionItem> createActionItemsForPrincipals(ActionRequestValue actionRequest, List<String> principalIds) {
        List<ActionItem> actionItems = new ArrayList<ActionItem>();
        for (String principalId: principalIds) {

            ActionItem actionItem = getActionListService().createActionItemForActionRequest(actionRequest);
            actionItem.setPrincipalId(principalId);
            actionItem.setRoleName(actionRequest.getQualifiedRoleName());

            //KULRICE-3307 Prevent workflow from attempting to activate requests for null principals
            String ignoreUnknownPrincipalIdsValue = ConfigContext.getCurrentContextConfig().getProperty(KewApiConstants.WORKFLOW_ACTION_IGNORE_UNKOWN_PRINCIPAL_IDS);
            boolean ignoreUnknownPrincipalIds = Boolean.parseBoolean(ignoreUnknownPrincipalIdsValue);

            if(principalId==null && ignoreUnknownPrincipalIds)
            {
                LOG.warn("Ignoring action item with actionRequestID of " + actionRequest.getActionRequestId()  + " due to null principalId.");
            }
            else
            {
                if(principalId==null)
                {
                    IllegalArgumentException e = new IllegalArgumentException("Exception thrown when trying to add action item with null principalId");
                    LOG.error(e);
                    throw e;
                }
                else
                {
                    actionItems.add(actionItem);
                }
            }
        }
        return actionItems;
    }

    private void processResponsibilityId(ActionRequestValue actionRequest) {
    	if (actionRequest.getResolveResponsibility()) {
	        String responsibilityId = actionRequest.getResponsibilityId();
	        try {
	            RouteModule routeModule = KEWServiceLocator.getRouteModuleService().findRouteModule(actionRequest);
	            if (responsibilityId != null && actionRequest.isRouteModuleRequest()) {
	            	if ( LOG.isDebugEnabled() ) {
	            		LOG.debug("Resolving responsibility id for action request id=" + actionRequest.getActionRequestId()
	                        + " and responsibility id=" + actionRequest.getResponsibilityId());
	            	}
	                ResponsibleParty responsibleParty = routeModule.resolveResponsibilityId(actionRequest.getResponsibilityId());
	                if (responsibleParty == null) {
	                    return;
	                }
	                if (responsibleParty.getPrincipalId() != null) {
	                    Principal user = KimApiServiceLocator.getIdentityService()
	                            .getPrincipal(responsibleParty.getPrincipalId());
	                    actionRequest.setPrincipalId(user.getPrincipalId());
	                } else if (responsibleParty.getGroupId() != null) {
	                	actionRequest.setGroupId(responsibleParty.getGroupId());
	                } else if (responsibleParty.getRoleName() != null) {
	                    actionRequest.setRoleName(responsibleParty.getRoleName());
	                }
	            }
	        } catch (Exception e) {
	            LOG.error("Exception thrown when trying to resolve responsibility id " + responsibilityId, e);
	            throw new RuntimeException(e);
	        }
    	}
    }

    protected boolean deactivateOnActionAlreadyTaken(ActionRequestValue actionRequestToActivate,
            ActivationContext activationContext) {

        FutureRequestDocumentStateManager futureRequestStateMngr = null;

        if (actionRequestToActivate.isGroupRequest()) {
            futureRequestStateMngr = new FutureRequestDocumentStateManager(actionRequestToActivate.getRouteHeader(), actionRequestToActivate.getGroup());
        } else if (actionRequestToActivate.isUserRequest()) {
            futureRequestStateMngr = new FutureRequestDocumentStateManager(actionRequestToActivate.getRouteHeader(), actionRequestToActivate.getPrincipalId());
        } else {
            return false;
        }

        if (futureRequestStateMngr.isReceiveFutureRequests()) {
            return false;
        }
        if (!actionRequestToActivate.getForceAction() || futureRequestStateMngr.isDoNotReceiveFutureRequests()) {
            ActionTakenValue previousActionTaken = null;
            if (!activationContext.isSimulation()) {
                previousActionTaken = getActionTakenService().getPreviousAction(actionRequestToActivate);
            } else {
                previousActionTaken = getActionTakenService().getPreviousAction(actionRequestToActivate,
                        activationContext.getSimulatedActionsTaken());
            }
            if (previousActionTaken != null) {
                if ( LOG.isDebugEnabled() ) {
                	LOG.debug("found a satisfying action taken so setting this request done.  Action Request Id "
                            + actionRequestToActivate.getActionRequestId());
                }
                // set up the delegation for an action taken if this is a delegate request and the delegate has
                // already taken action.
                if (!previousActionTaken.isForDelegator() && actionRequestToActivate.getParentActionRequest() != null) {
                    previousActionTaken.setDelegator(actionRequestToActivate.getParentActionRequest().getRecipient());
                    if (!activationContext.isSimulation()) {
                        previousActionTaken = getActionTakenService().saveActionTaken(previousActionTaken);
                    }
                }
                deactivateRequest(previousActionTaken, actionRequestToActivate, null, activationContext);
                return true;
            }
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Forcing action for action request " + actionRequestToActivate.getActionRequestId());
        }
        return false;
    }

    /**
     * Checks if the action request which is being activated has a group with no members.  If this is the case then it will immediately
     * initiate de-activation on the request since a group with no members will result in no action items being generated so should be
     * effectively skipped.
     */
    protected boolean deactivateOnEmptyGroup(ActionRequestValue actionRequestToActivate, ActivationContext activationContext) {
    	if (actionRequestToActivate.isGroupRequest()) {
    		 if (KimApiServiceLocator.getGroupService().getMemberPrincipalIds(actionRequestToActivate.getGroup().getId()).isEmpty()) {
    			 deactivateRequest(null, actionRequestToActivate, null, activationContext);
    			 return true;
         	}
    	}
    	return false;
    }

    /**
     * Checks if the action request which is being activated is being assigned to an inactive group.  If this is the case and if the FailOnInactiveGroup
     * policy is set to false then it will immediately initiate de-activation on the request
     */
    protected boolean deactivateOnInactiveGroup(ActionRequestValue actionRequestToActivate, ActivationContext activationContext) {
        if (actionRequestToActivate.isGroupRequest()) {
            if (!actionRequestToActivate.getGroup().isActive() && !actionRequestToActivate.getRouteHeader().getDocumentType().getFailOnInactiveGroup().getPolicyValue()) {
                deactivateRequest(null, actionRequestToActivate, null, activationContext);
                return true;
            }
        }
        return false;
    }

    @Override
    public ActionRequestValue deactivateRequest(ActionTakenValue actionTaken, ActionRequestValue actionRequest) {
        return deactivateRequest(actionTaken, actionRequest, null, new ActivationContext(!ActivationContext.CONTEXT_IS_SIMULATION));
    }

    @Override
    public ActionRequestValue deactivateRequest(ActionTakenValue actionTaken, ActionRequestValue actionRequest,
            ActivationContext activationContext) {
        return deactivateRequest(actionTaken, actionRequest, null, activationContext);
    }

    @Override
    public List<ActionRequestValue> deactivateRequests(ActionTakenValue actionTaken, List<ActionRequestValue> actionRequests) {
        return deactivateRequests(actionTaken, actionRequests, null,
                new ActivationContext(!ActivationContext.CONTEXT_IS_SIMULATION));
    }

    @Override
    public List<ActionRequestValue> deactivateRequests(ActionTakenValue actionTaken, List<ActionRequestValue> actionRequests, boolean simulate) {
        return deactivateRequests(actionTaken, actionRequests, null, new ActivationContext(simulate));
    }

    @Override
    public List<ActionRequestValue> deactivateRequests(ActionTakenValue actionTaken, List<ActionRequestValue> actionRequests, ActivationContext activationContext) {
        return deactivateRequests(actionTaken, actionRequests, null, activationContext);
    }

    private List<ActionRequestValue> deactivateRequests(ActionTakenValue actionTaken, List<ActionRequestValue> actionRequests,
            ActionRequestValue deactivationRequester, ActivationContext activationContext) {
        List<ActionRequestValue> deactivatedRequests = new ArrayList<ActionRequestValue>();
        if (actionRequests != null) {
            for (ActionRequestValue actionRequest : actionRequests) {
                deactivatedRequests.add(deactivateRequest(actionTaken, actionRequest, deactivationRequester, activationContext));
            }
        }
        return deactivatedRequests;
    }

    private ActionRequestValue deactivateRequest(ActionTakenValue actionTaken, ActionRequestValue actionRequest,
            ActionRequestValue deactivationRequester, ActivationContext activationContext) {
        if (actionRequest == null || actionRequest.isDeactivated()
                || haltForAllApprove(actionRequest, deactivationRequester)) {
            return actionRequest;
        }
        actionRequest.setStatus(ActionRequestStatus.DONE.getCode());
        actionRequest.setActionTaken(actionTaken);

        if (!activationContext.isSimulation()) {
            if (actionTaken != null) {
                // only add it if it's not null and we aren't in a simulation context, if we are in simulation mode, we
                // don't want to modify any action requests, lest they get saved by a JPA flush later!
                actionTaken.getActionRequests().add(actionRequest);
            }
            actionRequest = getDataObjectService().save(actionRequest);
            deleteActionItems(actionRequest, true);
        }
        actionRequest.setChildrenRequests(deactivateRequests(actionTaken, actionRequest.getChildrenRequests(), actionRequest, activationContext));
        actionRequest.setParentActionRequest(deactivateRequest(actionTaken, actionRequest.getParentActionRequest(), actionRequest, activationContext));
        return actionRequest;
    }

    /**
     * Returns true if we are dealing with an 'All Approve' request, the requester of the deactivation is a child of the
     * 'All Approve' request, and all of the children have not been deactivated. If all of the children are already
     * deactivated or a non-child request initiated deactivation, then this method returns false. false otherwise.
     */
    private boolean haltForAllApprove(ActionRequestValue actionRequest, ActionRequestValue deactivationRequester) {
        if (ActionRequestPolicy.ALL.getCode().equals(actionRequest.getApprovePolicy())
                && actionRequest.hasChild(deactivationRequester)) {
            for (ActionRequestValue childRequest : actionRequest.getChildrenRequests()) {
                if (!childRequest.isDeactivated()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<ActionRequestValue> getRootRequests(Collection<ActionRequestValue> actionRequests) {
    	Set<ActionRequestValue> unsavedRequests = new HashSet<ActionRequestValue>();
    	Map<String, ActionRequestValue> requestMap = new HashMap<String, ActionRequestValue>();
    	for (ActionRequestValue actionRequest1 : actionRequests) {
    		ActionRequestValue actionRequest = actionRequest1;
    		ActionRequestValue rootRequest = getRoot(actionRequest);
    		if (rootRequest.getActionRequestId() != null) {
    			requestMap.put(rootRequest.getActionRequestId(), rootRequest);
    		} else {
    			unsavedRequests.add(rootRequest);
    		}
    	}
    	List<ActionRequestValue> requests = new ArrayList<ActionRequestValue>();
    	requests.addAll(requestMap.values());
    	requests.addAll(unsavedRequests);
    	return requests;
    }

    @Override
    public ActionRequestValue getRoot(ActionRequestValue actionRequest) {
        if (actionRequest == null) {
            return null;
        }
        if (actionRequest.getParentActionRequest() != null) {
            return getRoot(actionRequest.getParentActionRequest());
        }
        return actionRequest;
    }

    /**
     * Returns all pending requests for a given routing identity
     * @param documentId the id of the document header being routed
     * @return a List of all pending ActionRequestValues for the document
     */
    @Override
    public List<ActionRequestValue> findAllPendingRequests(String documentId) {
        return findByStatusAndDocId(ActionRequestStatus.ACTIVATED.getCode(), documentId);
    }

    @Override
    public List<ActionRequestValue> findAllValidRequests(String principalId, String documentId, String requestCode) {
        List<ActionRequestValue> pendingArs =
                findByStatusAndDocumentId(ActionRequestStatus.ACTIVATED.getCode(), documentId);
        return findAllValidRequests(principalId, pendingArs, requestCode);
    }

    protected List<ActionRequestValue> findByStatusAndDocumentId(String statusCode, String documentId) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create().setPredicates(
                equal(STATUS, statusCode),
                equal(DOCUMENT_ID, documentId),
                equal(CURRENT_INDICATOR, Boolean.TRUE)
        );
        return getDataObjectService().findMatching(ActionRequestValue.class, criteria.build()).getResults();
    }

    @Override
    public List<ActionRequestValue> findAllValidRequests(String principalId, List<ActionRequestValue> actionRequests, String requestCode) {
        List<String> arGroups = KimApiServiceLocator.getGroupService().getGroupIdsByPrincipalId(principalId);
        return filterActionRequestsByCode(actionRequests, principalId, arGroups, requestCode);
    }

    /**
	 * Filters action requests based on if they occur after the given requestCode, and if they relate to
	 * the given principal
	 * @param actionRequests the List of ActionRequestValues to filter
	 * @param principalId the id of the principal to find active requests for
	 * @param principalGroupIds List of group ids that the principal belongs to
	 * @param requestCode the request code for all ActionRequestValues to be after
	 * @return the filtered List of ActionRequestValues
	 */
	@Override
    public List<ActionRequestValue> filterActionRequestsByCode(List<ActionRequestValue> actionRequests, String principalId, List<String> principalGroupIds, String requestCode) {
		List<ActionRequestValue> filteredActionRequests = new ArrayList<ActionRequestValue>();

        for (ActionRequestValue ar : actionRequests) {
            if (ActionRequestValue.compareActionCode(ar.getActionRequested(), requestCode, true) > 0) {
                continue;
            }
            if (ar.isUserRequest() && principalId.equals(ar.getPrincipalId())) {
            	filteredActionRequests.add(ar);
            } else if (ar.isGroupRequest() && principalGroupIds != null && !principalGroupIds.isEmpty()) {
            	for (String groupId : principalGroupIds) {
            		if (groupId.equals(ar.getGroupId())) {
            			filteredActionRequests.add(ar);
            		}
            	}
            }
        }

		return filteredActionRequests;
	}

    @Override
    public void updateActionRequestsForResponsibilityChange(Set<String> responsibilityIds) {
    	PerformanceLogger performanceLogger = null;
    	if ( LOG.isInfoEnabled() ) {
    		performanceLogger = new PerformanceLogger();
    	}
        Collection<String> documentsAffected = getRouteHeaderService().findPendingByResponsibilityIds(responsibilityIds);
        String cacheWaitValue = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KewApiConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.RULE_DETAIL_TYPE, KewApiConstants.RULE_CACHE_REQUEUE_DELAY);
        Long cacheWait = KewApiConstants.DEFAULT_CACHE_REQUEUE_WAIT_TIME;
        if (!org.apache.commons.lang.StringUtils.isEmpty(cacheWaitValue)) {
            try {
                cacheWait = Long.valueOf(cacheWaitValue);
            } catch (NumberFormatException e) {
                LOG.warn("Cache wait time is not a valid number: " + cacheWaitValue);
            }
        }
        if ( LOG.isInfoEnabled() ) {
        	LOG.info("Scheduling requeue of " + documentsAffected.size() + " documents, affected by " + responsibilityIds.size()
                    + " responsibility changes.  Installing a processing wait time of " + cacheWait
                    + " milliseconds to avoid stale rule cache.");
        }
        for (String documentId : documentsAffected) {

             String applicationId = null;
             DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByDocumentId(documentId);

             if (documentType != null) {
                applicationId = documentType.getApplicationId();
             }

            if (applicationId == null)
            {
                applicationId = CoreConfigHelper.getApplicationId();
            }
            if(documentType.getRegenerateActionRequestsOnChange().getPolicyValue()) {
                DocumentRefreshQueue documentRequeuer = KewApiServiceLocator.getDocumentRequeuerService(applicationId,
                        documentId, cacheWait);
                documentRequeuer.refreshDocument(documentId);
            }
        }
        if ( LOG.isInfoEnabled() ) {
        	performanceLogger.log("Time to updateActionRequestsForResponsibilityChange");
        }
    }

    @Override
    public void deleteActionRequestGraphNoOutbox(ActionRequestValue actionRequest) {
        deleteActionRequestGraph(actionRequest, false);
    }

    /**
     * Deletes an action request and all of its action items following the graph down through the action request's
     * children. This method should be invoked on a top-level action request.
     */
    @Override
    public void deleteActionRequestGraph(ActionRequestValue actionRequest) {
        deleteActionRequestGraph(actionRequest, true);
    }

    protected void deleteActionRequestGraph(ActionRequestValue actionRequest, boolean populateOutbox) {
        if (actionRequest.getParentActionRequest() != null) {
            throw new IllegalArgumentException("Must delete action request graph from the root, encountered a request with a parent: " + actionRequest);
        }
        deleteActionItemsFromGraph(actionRequest, populateOutbox);
        if (actionRequest.getActionTakenId() != null) {
            ActionTakenValue actionTaken = getActionTakenService().findByActionTakenId(actionRequest.getActionTakenId());
            if (actionTaken != null) {
                getActionTakenService().delete(actionTaken);
            }
        }
        // delete from the root, it should cascade down to the children
        getDataObjectService().delete(actionRequest);
        // go ahead and flush to ensure that the deletion happens before we return control to the calling code
        getDataObjectService().flush(ActionRequestValue.class);
    }

    /**
     * Deletes the action items for the action request
     * @param actionRequest the action request whose action items to delete
     */
    private void deleteActionItems(ActionRequestValue actionRequest, boolean populateOutbox) {
    	List<ActionItem> actionItems = actionRequest.getActionItems();
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug("deleting " + actionItems.size() + " action items for action request: " + actionRequest);
    	}
        for (ActionItem actionItem: actionItems) {
        	if ( LOG.isDebugEnabled() ) {
        		LOG.debug("deleting action item: " + actionItem);
        	}
            if (populateOutbox) {
                getActionListService().deleteActionItem(actionItem);
            } else {
                getActionListService().deleteActionItemNoOutbox(actionItem);
            }
        }
    }

    /**
     * Deletes the action items for the *root* action request.
     *
     * @param actionRequest the action request whose action items to delete
     */
    private void deleteActionItemsFromGraph(ActionRequestValue actionRequest, boolean populateOutbox) {
        if (actionRequest.getParentActionRequest() != null) {
            throw new IllegalArgumentException("Must delete action item from root of action request graph!");
        }
        List<ActionItem> actionItemsToDelete = new ArrayList<ActionItem>();
        accumulateActionItemsFromGraph(actionRequest, actionItemsToDelete);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("deleting " + actionItemsToDelete.size() + " action items for action request graph: " + actionRequest);
        }
        for (ActionItem actionItem : actionItemsToDelete) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("deleting action item: " + actionItem);
            }
            if (populateOutbox) {
                getActionListService().deleteActionItem(actionItem);
            } else {
                getActionListService().deleteActionItemNoOutbox(actionItem);
            }
        }
    }

    private void accumulateActionItemsFromGraph(ActionRequestValue actionRequest, List<ActionItem> actionItems) {
        actionItems.addAll(actionRequest.getActionItems());
        for (ActionRequestValue childRequest : actionRequest.getChildrenRequests()) {
            accumulateActionItemsFromGraph(childRequest, actionItems);
        }
    }


    @Override
    public List<ActionRequestValue> findByDocumentIdIgnoreCurrentInd(String documentId) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create().setPredicates(equal(DOCUMENT_ID, documentId));
        return getDataObjectService().findMatching(ActionRequestValue.class, criteria.build()).getResults();
    }

    @Override
    public List<ActionRequestValue> findAllActionRequestsByDocumentId(String documentId) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create().setPredicates(
                equal(DOCUMENT_ID, documentId),
                equal(CURRENT_INDICATOR, Boolean.TRUE)
        );
        return getDataObjectService().findMatching(ActionRequestValue.class, criteria.build()).getResults();
    }

    @Override
    public List<ActionRequestValue> findAllRootActionRequestsByDocumentId(String documentId) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create().setPredicates(
                equal(DOCUMENT_ID, documentId),
                equal(CURRENT_INDICATOR, Boolean.TRUE),
                isNull(PARENT_ACTION_REQUEST)
        );
        return getDataObjectService().findMatching(ActionRequestValue.class, criteria.build()).getResults();
    }

    @Override
    public List<ActionRequestValue> findPendingByActionRequestedAndDocId(String actionRequestedCd, String documentId) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create().setPredicates(
                equal(DOCUMENT_ID, documentId),
                equal(CURRENT_INDICATOR, Boolean.TRUE),
                equal(ACTION_REQUESTED, actionRequestedCd),
                getPendingCriteria()
        );
        return getDataObjectService().findMatching(ActionRequestValue.class, criteria.build()).getResults();
    }



    @Override
    public List<String> getPrincipalIdsWithPendingActionRequestByActionRequestedAndDocId(String actionRequestedCd, String documentId) {
    	List<String> principalIds = new ArrayList<String>();
    	List<ActionRequestValue> actionRequests = findPendingByActionRequestedAndDocId(actionRequestedCd, documentId);
		for(ActionRequestValue actionRequest: actionRequests){
			if(actionRequest.isUserRequest()){
				principalIds.add(actionRequest.getPrincipalId());
			} else if(actionRequest.isGroupRequest()){
				principalIds.addAll(
						KimApiServiceLocator.getGroupService().getMemberPrincipalIds(actionRequest.getGroupId()));
			}
		}
    	return principalIds;
    }

    @Override
    public List<ActionRequestValue> findPendingRootRequestsByDocId(String documentId) {
        return getRootRequests(findPendingByDoc(documentId));
    }

    @Override
    public List<ActionRequestValue> findPendingRootRequestsByDocIdAtRouteNode(String documentId, String nodeInstanceId) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create().setPredicates(
                equal(DOCUMENT_ID, documentId),
                equal(CURRENT_INDICATOR, Boolean.TRUE),
                isNull(PARENT_ACTION_REQUEST),
                getPendingCriteria(),
                equal(ROUTE_NODE_INSTANCE_ID, nodeInstanceId)
        );
        return getDataObjectService().findMatching(ActionRequestValue.class, criteria.build()).getResults();
    }

    @Override
    public List<ActionRequestValue> findRootRequestsByDocIdAtRouteNode(String documentId, String nodeInstanceId) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create().setPredicates(
                equal(DOCUMENT_ID, documentId),
                equal(CURRENT_INDICATOR, Boolean.TRUE),
                isNull(PARENT_ACTION_REQUEST),
                equal(ROUTE_NODE_INSTANCE_ID, nodeInstanceId)
        );
        return getDataObjectService().findMatching(ActionRequestValue.class, criteria.build()).getResults();
    }

    @Override
    public List<ActionRequestValue> findPendingRootRequestsByDocumentType(String documentTypeId) {
        return getActionRequestDAO().findPendingRootRequestsByDocumentType(documentTypeId);
    }

    @Override
    public ActionRequestValue saveActionRequest(ActionRequestValue actionRequest) {
        return saveActionRequest(actionRequest, false);
    }

    protected ActionRequestValue saveActionRequest(ActionRequestValue actionRequest, boolean simulation) {
        if (actionRequest.isGroupRequest()) {
            Group group = actionRequest.getGroup();
            if (group == null)  {
                throw new RiceRuntimeException("Attempted to save an action request with a non-existent group.");
            }
            if (!group.isActive() && actionRequest.getRouteHeader().getDocumentType().getFailOnInactiveGroup().getPolicyValue()) {
                throw new RiceRuntimeException("Attempted to save an action request with an inactive group.");
            }
        }
        if (actionRequest.getActionRequestId() == null) {
            loadDefaultValues(actionRequest);
        }
        if ( actionRequest.getAnnotation() != null && actionRequest.getAnnotation().length() > 2000 ) {
            actionRequest.setAnnotation( StringUtils.abbreviate(actionRequest.getAnnotation(), 2000) );
        }
        if (simulation) {
            return actionRequest;
        } else {
            return getDataObjectService().save(actionRequest);
        }
    }

    private void loadDefaultValues(ActionRequestValue actionRequest) {
        checkNull(actionRequest.getActionRequested(), "action requested");
        checkNull(actionRequest.getResponsibilityId(), "responsibility ID");
        checkNull(actionRequest.getRouteLevel(), "route level");
        checkNull(actionRequest.getDocVersion(), "doc version");
        if (actionRequest.getForceAction() == null) {
            actionRequest.setForceAction(Boolean.FALSE);
        }
        if (actionRequest.getStatus() == null) {
            actionRequest.setStatus(ActionRequestStatus.INITIALIZED.getCode());
        }
        if (actionRequest.getPriority() == null) {
            actionRequest.setPriority(KewApiConstants.ACTION_REQUEST_DEFAULT_PRIORITY);
        }
        if (actionRequest.getCurrentIndicator() == null) {
            actionRequest.setCurrentIndicator(true);
        }
        actionRequest.setCreateDate(new Timestamp(System.currentTimeMillis()));
    }

    private void checkNull(Object value, String valueName) throws RuntimeException {
        if (value == null) {
            throw new IllegalArgumentException("Null value for " + valueName);
        }
    }

    private List<ActionRequestValue> saveActionRequests(Collection<ActionRequestValue> actionRequests) {
        // TODO validate only root requests are being saved here?
        List<ActionRequestValue> savedRequests = new ArrayList<ActionRequestValue>(actionRequests.size());
        for (ActionRequestValue actionRequest : actionRequests) {
            savedRequests.add(saveActionRequest(actionRequest));
        }
        return savedRequests;
    }

    @Override
    public List<ActionRequestValue> findPendingByDoc(String documentId) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create().setPredicates(
                equal(DOCUMENT_ID, documentId),
                equal(CURRENT_INDICATOR, Boolean.TRUE),
                getPendingCriteria()
        );
        return getDataObjectService().findMatching(ActionRequestValue.class, criteria.build()).getResults();
    }

    @Override
    public List<ActionRequestValue> findPendingByDocRequestCdNodeName(String documentId, String requestCode, String nodeName) {
        List<ActionRequestValue> requests = new ArrayList<ActionRequestValue>();
        for (ActionRequestValue actionRequest : findPendingByDoc(documentId)) {
            if (ActionRequestValue.compareActionCode(actionRequest.getActionRequested(), requestCode, true) > 0) {
                continue;
            }
            if (actionRequest.getNodeInstance() != null && actionRequest.getNodeInstance().getName().equals(nodeName)) {
                requests.add(actionRequest);
            }
        }
        return requests;
    }

    @Override
    public List<ActionRequestValue> findActivatedByGroup(String groupId) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create().setPredicates(
                equal(STATUS, ActionRequestStatus.ACTIVATED.getCode()),
                equal(GROUP_ID, groupId),
                equal(CURRENT_INDICATOR, Boolean.TRUE),
                getPendingCriteria()
        );
        return getDataObjectService().findMatching(ActionRequestValue.class, criteria.build()).getResults();
    }

    @Override
    public List<ActionRequestValue> findByStatusAndDocId(String statusCode, String documentId) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create().setPredicates(
                equal(STATUS, statusCode),
                equal(DOCUMENT_ID, documentId),
                equal(CURRENT_INDICATOR, Boolean.TRUE)
        );
        return getDataObjectService().findMatching(ActionRequestValue.class, criteria.build()).getResults();
    }

    @Override
    public Recipient findDelegator(List<ActionRequestValue> actionRequests) {
        Recipient delegator = null;
        String requestCode = KewApiConstants.ACTION_REQUEST_FYI_REQ;
        for (Object actionRequest1 : actionRequests)
        {
            ActionRequestValue actionRequest = (ActionRequestValue) actionRequest1;
            ActionRequestValue delegatorRequest = findDelegatorRequest(actionRequest);
            if (delegatorRequest != null)
            {
                if (ActionRequestValue.compareActionCode(delegatorRequest.getActionRequested(), requestCode, true) >= 0)
                {
                    delegator = delegatorRequest.getRecipient();
                    requestCode = delegatorRequest.getActionRequested();
                }
            }
        }
        return delegator;
    }

    @Override
    public ActionRequestValue findDelegatorRequest(ActionRequestValue actionRequest) {
        ActionRequestValue parentRequest = actionRequest.getParentActionRequest();
        if (parentRequest != null && !(parentRequest.isUserRequest() || parentRequest.isGroupRequest())) {
            parentRequest = findDelegatorRequest(parentRequest);
        }
        return parentRequest;
    }

    @Override
    public List<ActionRequestValue> getDelegateRequests(ActionRequestValue actionRequest) {
        List<ActionRequestValue> delegateRequests = new ArrayList<ActionRequestValue>();
        List<ActionRequestValue> requests = getTopLevelRequests(actionRequest);
        for (Object request : requests)
        {
            ActionRequestValue parentActionRequest = (ActionRequestValue) request;
            delegateRequests.addAll(parentActionRequest.getChildrenRequests());
        }
        return delegateRequests;
    }

    @Override
    public List<ActionRequestValue> getTopLevelRequests(ActionRequestValue actionRequest) {
        List<ActionRequestValue> topLevelRequests = new ArrayList<ActionRequestValue>();
        if (actionRequest.isRoleRequest()) {
            topLevelRequests.addAll(actionRequest.getChildrenRequests());
        } else {
            topLevelRequests.add(actionRequest);
        }
        return topLevelRequests;
    }

    @Override
    public boolean doesPrincipalHaveRequest(String principalId, String documentId) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create().setPredicates(
                equal(PRINCIPAL_ID, principalId),
                equal(DOCUMENT_ID, documentId),
                equal(RECIPIENT_TYPE_CD, RecipientType.PRINCIPAL.getCode()),
                equal(CURRENT_INDICATOR, Boolean.TRUE)
        );
        int count = getDataObjectService().findMatching(ActionRequestValue.class, criteria.build()).getTotalRowCount();
        if (count > 0) {
            return true;
        }
        // TODO since we only store the workgroup id for workgroup requests, if the user is in a workgroup that has a request
        // than we need get all the requests with workgroup ids and see if our user is in that group
        List<String> groupIds = getActionRequestDAO().getRequestGroupIds(documentId);
        for (String groupId : groupIds) {
            if (KimApiServiceLocator.getGroupService().isMemberOfGroup(principalId, groupId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ActionRequestValue getActionRequestForRole(String actionTakenId) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create().setPredicates(
                equal(ACTION_TAKEN_ID, actionTakenId),
                equal(CURRENT_INDICATOR, Boolean.TRUE),
                equal(RECIPIENT_TYPE_CD, RecipientType.ROLE.getCode()),
                isNull(PARENT_ACTION_REQUEST)
        );
        List<ActionRequestValue> actionTakenRoleRequests =
                getDataObjectService().findMatching(ActionRequestValue.class, criteria.build()).getResults();
        if (actionTakenRoleRequests.isEmpty()) {
            return null;
        }
        return actionTakenRoleRequests.get(0);
    }

    /**
     * Returns criteria for selecting "pending" action requests. A request is pending if it's status is activated
     * or initialized.
     *
     * @return criteria for selecting pending action requests
     */
    protected Predicate getPendingCriteria() {
        return or(
                equal(STATUS, ActionRequestStatus.ACTIVATED.getCode()),
                equal(STATUS, ActionRequestStatus.INITIALIZED.getCode())
        );
    }

    private ActionListService getActionListService() {
        return KEWServiceLocator.getActionListService();
    }

    private ActionTakenService getActionTakenService() {
        return KEWServiceLocator.getActionTakenService();
    }

    private RouteHeaderService getRouteHeaderService() {
        return KEWServiceLocator.getService(KEWServiceLocator.DOC_ROUTE_HEADER_SRV);
    }

    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    public ActionRequestDAO getActionRequestDAO() {
        return actionRequestDAO;
    }

    public void setActionRequestDAO(ActionRequestDAO actionRequestDAO) {
        this.actionRequestDAO = actionRequestDAO;
    }

}
