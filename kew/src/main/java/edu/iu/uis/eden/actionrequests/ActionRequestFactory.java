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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routetemplate.ResolvedQualifiedRole;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.RoleRecipient;
import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.CodeTranslator;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.GroupId;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.Workgroup;
import edu.iu.uis.eden.workgroup.WorkgroupService;

/**
 * A factory to aid in creating the ever-so-gnarly ActionRequestValue object.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionRequestFactory {

	private static final Logger LOG = Logger.getLogger(ActionRequestFactory.class);

	private DocumentRouteHeaderValue document;
	private RouteNodeInstance routeNode;
	private List<ActionRequestValue> requestGraphs = new ArrayList<ActionRequestValue>();

	public ActionRequestFactory() {
	}

	public ActionRequestFactory(DocumentRouteHeaderValue document) {
		this.document = document;
	}

	public ActionRequestFactory(DocumentRouteHeaderValue document, RouteNodeInstance routeNode) {
		this.document = document;
		this.routeNode = routeNode;
	}

	/**
	 * Constructs ActionRequestValue using default priority and 0 as responsibility
	 *
	 * @param actionRequested
	 * @param recipient
	 * @param description
	 * @param ignorePrevious
	 *
	 * @return ActionRequestValue
	 */
	public ActionRequestValue createActionRequest(String actionRequested, Recipient recipient, String description, Boolean ignorePrevious, String annotation) {
		return createActionRequest(actionRequested, new Integer(0), recipient, description, EdenConstants.MACHINE_GENERATED_RESPONSIBILITY_ID, ignorePrevious, annotation);
	}

	public ActionRequestValue createActionRequest(String actionRequested, Integer priority, Recipient recipient, String description, Long responsibilityId, Boolean ignorePrevious, String annotation) {
    	return createActionRequest(actionRequested, priority, recipient, description, responsibilityId, ignorePrevious, null, null, annotation);
    }

	public ActionRequestValue createActionRequest(String actionRequested, Integer priority, Recipient recipient, String description, Long responsibilityId, Boolean ignorePrevious, String approvePolicy, Long ruleId, String annotation) {
		ActionRequestValue actionRequest = new ActionRequestValue();
        actionRequest.setActionRequested(actionRequested);
        actionRequest.setDocVersion(document.getDocVersion());
        actionRequest.setPriority(priority);
        actionRequest.setRouteHeader(document);
        actionRequest.setRouteHeaderId(document.getRouteHeaderId());
        actionRequest.setRouteLevel(document.getDocRouteLevel());
    	actionRequest.setNodeInstance(routeNode);
    	actionRequest.setResponsibilityId(responsibilityId);
    	actionRequest.setResponsibilityDesc(description);
    	actionRequest.setApprovePolicy(approvePolicy);
    	actionRequest.setIgnorePrevAction(ignorePrevious);
    	actionRequest.setRuleBaseValuesId(ruleId);
    	actionRequest.setAnnotation(annotation);
    	setDefaultProperties(actionRequest);
    	resolveRecipient(actionRequest, recipient);
        return actionRequest;
	}

	public ActionRequestValue createBlankActionRequest() {
		ActionRequestValue request = new ActionRequestValue();
		request.setRouteHeader(document);
		request.setNodeInstance(routeNode);
		return request;
	}


    public ActionRequestValue createNotificationRequest(String actionRequestCode, WorkflowUser recipient, String reasonActionCode, WorkflowUser reasonActionUser, String responsibilityDesc) {
    	ActionRequestValue request = createActionRequest(actionRequestCode, recipient, responsibilityDesc, Boolean.TRUE, null);
    	String annotation = generateNotificationAnnotation(reasonActionUser, actionRequestCode, reasonActionCode, request);
    	request.setAnnotation(annotation);
    	return request;
    }

    //unify these 2 methods if possible
    public List generateNotifications(List requests, WorkflowUser user, Recipient delegator, String notificationRequestCode, String actionTakenCode) throws EdenUserNotFoundException {
        Workgroup notifyExclusionWorkgroup = getWorkgroupService().getWorkgroup(new GroupNameId(Utilities.getApplicationConstant(EdenConstants.NOTIFICATION_EXCLUDED_USERS_WORKGROUP_NAME)));
        return generateNotifications(null, getActionRequestService().getRootRequests(requests), user, delegator, notificationRequestCode, actionTakenCode, notifyExclusionWorkgroup);
    }
    private List<ActionRequestValue> generateNotifications(ActionRequestValue parentRequest, List requests, WorkflowUser user, Recipient delegator, String notificationRequestCode, String actionTakenCode, Workgroup notifyExclusionWorkgroup) throws EdenUserNotFoundException {
        List<ActionRequestValue> notificationRequests = new ArrayList<ActionRequestValue>();
        for (Iterator iter = requests.iterator(); iter.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
            if (!(actionRequest.isRecipientRoutedRequest(user) || actionRequest.isRecipientRoutedRequest(delegator))) {
                // skip user requests to system users
                if ( (notifyExclusionWorkgroup != null) && (notifyExclusionWorkgroup.hasMember(actionRequest.getRecipient())) ) {
                    continue;
                }
                ActionRequestValue notificationRequest = createNotificationRequest(actionRequest, user, notificationRequestCode, actionTakenCode);
                notificationRequests.add(notificationRequest);
                if (parentRequest != null) {
                    notificationRequest.setParentActionRequest(parentRequest);
                    parentRequest.getChildrenRequests().add(notificationRequest);
                }
                notificationRequests.addAll(generateNotifications(notificationRequest, actionRequest.getChildrenRequests(), user, delegator, notificationRequestCode, actionTakenCode, notifyExclusionWorkgroup));
            }
        }
        return notificationRequests;
    }

    private ActionRequestValue createNotificationRequest(ActionRequestValue actionRequest, WorkflowUser reasonUser, String notificationRequestCode, String actionTakenCode) throws EdenUserNotFoundException {

    	String annotation = generateNotificationAnnotation(reasonUser, notificationRequestCode, actionTakenCode, actionRequest);
        ActionRequestValue request = createActionRequest(notificationRequestCode, actionRequest.getPriority(), actionRequest.getRecipient(), actionRequest.getResponsibilityDesc(), EdenConstants.MACHINE_GENERATED_RESPONSIBILITY_ID, Boolean.TRUE, annotation);

        request.setDocVersion(actionRequest.getDocVersion());
        request.setApprovePolicy(actionRequest.getApprovePolicy());
        request.setRoleName(actionRequest.getRoleName());
        request.setQualifiedRoleName(actionRequest.getQualifiedRoleName());
        request.setQualifiedRoleNameLabel(actionRequest.getQualifiedRoleNameLabel());
        request.setDelegationType(actionRequest.getDelegationType());
        return request;
    }

    private void setDefaultProperties(ActionRequestValue actionRequest) {
    	if (actionRequest.getApprovePolicy() == null) {
    		actionRequest.setApprovePolicy(EdenConstants.APPROVE_POLICY_FIRST_APPROVE);
    	}
        actionRequest.setCreateDate(new Timestamp(System.currentTimeMillis()));
        actionRequest.setCurrentIndicator(Boolean.TRUE);
        if (actionRequest.getIgnorePrevAction() == null) {
        	actionRequest.setIgnorePrevAction(Boolean.FALSE);
        }
        if (routeNode != null) {
        	actionRequest.setNodeInstance(routeNode);
        }
        actionRequest.setJrfVerNbr(new Integer(0));
        actionRequest.setStatus(EdenConstants.ACTION_REQUEST_INITIALIZED);
        actionRequest.setRouteHeader(document);
    }

    private static void resolveRecipient(ActionRequestValue actionRequest, Recipient recipient) {
    	if (recipient instanceof WorkflowUser) {
    		actionRequest.setRecipientTypeCd(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
    		actionRequest.setWorkflowId(((WorkflowUser)recipient).getWorkflowId());
    	} else if (recipient instanceof Workgroup){
    		actionRequest.setRecipientTypeCd(EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD);
    		actionRequest.setWorkgroupId(((Workgroup)recipient).getWorkflowGroupId().getGroupId());
    	} else {
    		RoleRecipient role = (RoleRecipient)recipient;
    		actionRequest.setRecipientTypeCd(EdenConstants.ACTION_REQUEST_ROLE_RECIPIENT_CD);
    		actionRequest.setRoleName(role.getRoleName());
    		actionRequest.setQualifiedRoleName(role.getQualifiedRoleName());
    		ResolvedQualifiedRole qualifiedRole = role.getResolvedQualifiedRole();
    		if (qualifiedRole != null) {
    			actionRequest.setAnnotation(qualifiedRole.getAnnotation() == null ? "" : qualifiedRole.getAnnotation());
    			actionRequest.setQualifiedRoleNameLabel(qualifiedRole.getQualifiedRoleLabel());
    		}
    		Recipient targetRecipient = role.getTarget();
    		if (role.getTarget() != null) {
    			if (targetRecipient instanceof RoleRecipient) {
    				throw new WorkflowRuntimeException("Role Cannot Target a role problem activating request for document " + actionRequest.getRouteHeader().getRouteHeaderId());
    			}
    			resolveRecipient(actionRequest, role.getTarget());
    		}
    	}
    }

//    public ActionRequestValue createControlRequest(ActionRequestValue parentRequest, Recipient recipient, String delegationType, String actionRequested, String approvePolicy, Integer priority, Long responsibilityId, Boolean ignorePrevious, String description) {
//    	ActionRequestValue actionRequest = createActionRequest(actionRequested, priority, recipient, description, responsibilityId, ignorePrevious);
//    	if (parentRequest != null) {
//    		actionRequest.setActionRequested(parentRequest.getActionRequested());
//            actionRequest.setPriority(parentRequest.getPriority());
//            actionRequest.setDelegationType(delegationType);
//    	}
//    	return actionRequest;
//    }

    /**
     * Creates a root Role Request
     * @param role
     * @param actionRequested
     * @param approvePolicy
     * @param priority
     * @param responsibilityId
     * @param ignorePrevious
     * @param description
     * @return the created root role request
     * @throws EdenUserNotFoundException
     */
    public ActionRequestValue addRoleRequest(RoleRecipient role, String actionRequested, String approvePolicy, Integer priority, Long responsibilityId, Boolean ignorePrevious, String description, Long ruleId) throws EdenUserNotFoundException {

    	ActionRequestValue requestGraph = createActionRequest(actionRequested, priority, role, description, responsibilityId, ignorePrevious, approvePolicy, ruleId, null);
    	if (role != null && role.getResolvedQualifiedRole() != null && role.getResolvedQualifiedRole().getRecipients() != null) {
    	for (Iterator iter = role.getResolvedQualifiedRole().getRecipients().iterator(); iter.hasNext();) {
			Id recipientId = (Id) iter.next();
			if (recipientId.isEmpty()) {
				throw new WorkflowRuntimeException("Failed to resolve id of type " + recipientId.getClass().getName() + " returned from role '" + role.getRoleName() + "'.  Id returned contained a null or empty value.");
			}
			if (recipientId instanceof UserId) {
				role.setTarget(KEWServiceLocator.getUserService().getWorkflowUser((UserId) recipientId));
			} else {
				role.setTarget(KEWServiceLocator.getWorkgroupService().getWorkgroup((GroupId) recipientId));
			}
			ActionRequestValue request = createActionRequest(actionRequested, priority, role, description, responsibilityId, ignorePrevious, null, ruleId, null);
			request.setParentActionRequest(requestGraph);
			requestGraph.getChildrenRequests().add(request);
	     }
    	} else {
    		LOG.warn("Didn't create action requests for action request description '" + description + "' because of null role or null part of role object graph.");
    	}
    	requestGraphs.add(requestGraph);
    	return requestGraph;
    }

    public ActionRequestValue addDelegationRoleRequest(ActionRequestValue parentRequest, String approvePolicy, RoleRecipient role, Long responsibilityId, Boolean ignorePrevious, String delegationType, String description, Long ruleId) throws EdenUserNotFoundException {
    	Recipient parentRecipient = parentRequest.getRecipient();
    	if (parentRecipient instanceof RoleRecipient) {
    		throw new WorkflowRuntimeException("Cannot delegate on Role Request.  It must be a request to a person or workgroup, although that request may be in a role");
    	}
    	if (! relatedToRoot(parentRequest)) {
    		throw new WorkflowRuntimeException("The parent request is not related to any request managed by this factory");
    	}
    	ActionRequestValue delegationRoleRequest = createActionRequest(parentRequest.getActionRequested(), parentRequest.getPriority(), role, description, responsibilityId, ignorePrevious, approvePolicy, ruleId, null);
    	delegationRoleRequest.setDelegationType(delegationType);
    	int count = 0;
    	for (Iterator iter = role.getResolvedQualifiedRole().getRecipients().iterator(); iter.hasNext(); count++) {
    		//repeat of createRoleRequest code
    		Id recipientId = (Id) iter.next();
    		if (recipientId.isEmpty()) {
				throw new WorkflowRuntimeException("Failed to resolve id of type " + recipientId.getClass().getName() + " returned from role '" + role.getRoleName() + "'.  Id returned contained a null or empty value.");
			}
			if (recipientId instanceof UserId) {
				role.setTarget(KEWServiceLocator.getUserService().getWorkflowUser((UserId) recipientId));
			} else {
				role.setTarget(KEWServiceLocator.getWorkgroupService().getWorkgroup((GroupId) recipientId));
			}
			ActionRequestValue request = createActionRequest(parentRequest.getActionRequested(), parentRequest.getPriority(), role, description, responsibilityId, ignorePrevious, null, ruleId, null);
			request.setDelegationType(delegationType);
			//end repeat
			request.setParentActionRequest(delegationRoleRequest);
			delegationRoleRequest.getChildrenRequests().add(request);
    	}

    	//put this mini graph in the larger graph
    	if (count > 0) {
    		parentRequest.getChildrenRequests().add(delegationRoleRequest);
    		delegationRoleRequest.setParentActionRequest(parentRequest);
    	}

    	return delegationRoleRequest;
    }

    public ActionRequestValue addDelegationRequest(ActionRequestValue parentRequest, Recipient recipient, Long responsibilityId, Boolean ignorePrevious, String delegationType, String description, Long ruleId) throws EdenUserNotFoundException {
    	if (! relatedToRoot(parentRequest)) {
    		throw new WorkflowRuntimeException("The parent request is not related to any request managed by this factory");
    	}
    	ActionRequestValue delegationRequest = createActionRequest(parentRequest.getActionRequested(), parentRequest.getPriority(), recipient, description, responsibilityId, ignorePrevious, null, ruleId, null);
    	delegationRequest.setDelegationType(delegationType);
    	parentRequest.getChildrenRequests().add(delegationRequest);
    	delegationRequest.setParentActionRequest(parentRequest);

    	return delegationRequest;
    }

    //could probably base behavior off of recipient type
    public ActionRequestValue addRootActionRequest(String actionRequested, Integer priority, Recipient recipient, String description, Long responsibilityId, Boolean ignorePrevious, String approvePolicy, Long ruleId) {
    	ActionRequestValue requestGraph = createActionRequest(actionRequested, priority, recipient, description, responsibilityId, ignorePrevious, approvePolicy, ruleId, null);
    	requestGraphs.add(requestGraph);
    	return requestGraph;
    }



    //return true if requestGraph (root) is in this requests' parents
    public boolean relatedToRoot(ActionRequestValue request) {
    	int i = 0;
    	while(i < 3) {
    		if (requestGraphs.contains(request)) {
    			return true;
    		} else if (request == null) {
    			return false;
    		}
    		i++;
    		request = request.getParentActionRequest();
    	}
    	return false;
    }

	public List getRequestGraphs() {
		//clean up all the trailing role requests with no children -
		requestGraphs.removeAll(cleanUpChildren(requestGraphs));
		return requestGraphs;
	}

	private Collection cleanUpChildren(Collection children) {
		Collection requestsToRemove = new ArrayList();
		for (Iterator iter = children.iterator(); iter.hasNext();) {

			ActionRequestValue request = (ActionRequestValue)iter.next();
			if (request.isRoleRequest()) {
				if (request.getChildrenRequests().isEmpty()) {
					requestsToRemove.add(request);
				} else {
					Collection childRequestsToRemove = cleanUpChildren(request.getChildrenRequests());
					request.getChildrenRequests().removeAll(childRequestsToRemove);
				}
			}
		}
		return requestsToRemove;
	}

    private String generateNotificationAnnotation(WorkflowUser user, String notificationRequestCode, String actionTakenCode, ActionRequestValue request) {
    	String notification = "Action " + CodeTranslator.getActionRequestLabel(notificationRequestCode) + " generated by Workflow because " + user.getDisplayName() + " took action "
				+ CodeTranslator.getActionTakenLabel(actionTakenCode);
    	if (request.getResponsibilityId() != null && request.getResponsibilityId().longValue() != 0) {
    		notification += " Responsibility " + request.getResponsibilityId();
    	}
    	if (request.getRuleBaseValuesId() != null) {
    		notification += " Rule Id " + request.getRuleBaseValuesId();
    	}
    	return notification;
	}

    public ActionRequestService getActionRequestService() {
        return (ActionRequestService) KEWServiceLocator.getService(KEWServiceLocator.ACTION_REQUEST_SRV);
    }
    
    public WorkgroupService getWorkgroupService() {
        return (WorkgroupService) KEWServiceLocator.getService(KEWServiceLocator.WORKGROUP_SRV);
    }
}