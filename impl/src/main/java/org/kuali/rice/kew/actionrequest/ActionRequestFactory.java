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
package org.kuali.rice.kew.actionrequest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.identity.Id;
import org.kuali.rice.kew.identity.service.IdentityHelperService;
import org.kuali.rice.kew.role.KimRoleRecipient;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.rule.ResolvedQualifiedRole;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.RoleRecipient;
import org.kuali.rice.kew.user.UserId;
import org.kuali.rice.kew.util.CodeTranslator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.workgroup.GroupId;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.dto.KimPrincipalInfo;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.role.KimRole;
import org.kuali.rice.kim.bo.role.dto.DelegateInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.dto.ResponsibilityActionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kns.util.KNSConstants;


/**
 * A factory to aid in creating the ever-so-gnarly ActionRequestValue object.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionRequestFactory {

	private static final Logger LOG = Logger.getLogger(ActionRequestFactory.class);

	private static RoleManagementService roleManagementService;
	private static IdentityHelperService identityHelperService;
	private static IdentityManagementService identityManagementService;
	private static ActionRequestService actionRequestService;
	
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
		return createActionRequest(actionRequested, new Integer(0), recipient, description, KEWConstants.MACHINE_GENERATED_RESPONSIBILITY_ID, ignorePrevious, annotation);
	}

	public ActionRequestValue createActionRequest(String actionRequested, Integer priority, Recipient recipient, String description, Long responsibilityId, Boolean ignorePrevious, String annotation) {
    	return createActionRequest(actionRequested, priority, recipient, description, responsibilityId, ignorePrevious, null, null, annotation);
    }

	public ActionRequestValue createActionRequest(String actionRequested, Integer priority, Recipient recipient, String description, Long responsibilityId, Boolean ignorePrevious, String approvePolicy, Long ruleId, String annotation) {
		return createActionRequest(actionRequested, priority, recipient, description, responsibilityId, ignorePrevious, null, null, annotation, null);
	}

	public ActionRequestValue createActionRequest(String actionRequested, Integer priority, Recipient recipient, String description, Long responsibilityId, Boolean ignorePrevious, String approvePolicy, Long ruleId, String annotation, String requestLabel) {
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
    	actionRequest.setRequestLabel(requestLabel);
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


    public ActionRequestValue createNotificationRequest(String actionRequestCode, KimPrincipal principal, String reasonActionCode, KimPrincipal reasonActionUser, String responsibilityDesc) {
    	ActionRequestValue request = createActionRequest(actionRequestCode, new KimPrincipalRecipient(principal), responsibilityDesc, Boolean.TRUE, null);
    	String annotation = generateNotificationAnnotation(reasonActionUser, actionRequestCode, reasonActionCode, request);
    	request.setAnnotation(annotation);
    	return request;
    }

    //unify these 2 methods if possible
    public List generateNotifications(List requests, KimPrincipal principal, Recipient delegator,
            String notificationRequestCode, String actionTakenCode)
    {
        String groupName =  Utilities.getKNSParameterValue(KEWConstants.KEW_NAMESPACE,
		        KNSConstants.DetailTypes.WORKGROUP_DETAIL_TYPE,
		        KEWConstants.NOTIFICATION_EXCLUDED_USERS_WORKGROUP_NAME_IND);

        KimGroup notifyExclusionWorkgroup = getIdentityManagementService().getGroupByName(Utilities.parseGroupNamespaceCode(groupName), Utilities.parseGroupName(groupName));
        return generateNotifications(null, getActionRequestService().getRootRequests(requests), principal, delegator, notificationRequestCode, actionTakenCode, notifyExclusionWorkgroup);
    }

    private List<ActionRequestValue> generateNotifications(ActionRequestValue parentRequest,
            List requests, KimPrincipal principal, Recipient delegator, String notificationRequestCode,
            String actionTakenCode, KimGroup notifyExclusionWorkgroup)
    {
        List<ActionRequestValue> notificationRequests = new ArrayList<ActionRequestValue>();
        for (Iterator iter = requests.iterator(); iter.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
            if (!(actionRequest.isRecipientRoutedRequest(principal.getPrincipalId()) || actionRequest.isRecipientRoutedRequest(delegator))) {
                // skip user requests to system users
                if( (notifyExclusionWorkgroup != null) &&
                        (isRecipientInGroup(notifyExclusionWorkgroup, actionRequest.getRecipient())))
                {
                    continue;
                }
                ActionRequestValue notificationRequest = createNotificationRequest(actionRequest, principal, notificationRequestCode, actionTakenCode);
                notificationRequests.add(notificationRequest);
                if (parentRequest != null) {
                    notificationRequest.setParentActionRequest(parentRequest);
                    parentRequest.getChildrenRequests().add(notificationRequest);
                }
                notificationRequests.addAll(generateNotifications(notificationRequest, actionRequest.getChildrenRequests(), principal, delegator, notificationRequestCode, actionTakenCode, notifyExclusionWorkgroup));
            }
        }
        return notificationRequests;
    }

    private boolean isRecipientInGroup(KimGroup group, Recipient recipient)
    {
        boolean isMember = false;

        if(recipient instanceof KimPrincipalRecipient)
        {
            String principalId = ((KimPrincipalRecipient) recipient).getPrincipalId();
            String groupId = group.getGroupId();
            isMember = getIdentityManagementService().isMemberOfGroup(principalId, groupId);
        }
        else if (recipient instanceof KimGroupRecipient)
        {
            String kimRecipientId = ((KimGroupRecipient) recipient).getGroup().getGroupId();
            isMember = getIdentityManagementService().isGroupMemberOfGroup(kimRecipientId, group.getGroupId() );
        }
        return isMember;
    }

    private ActionRequestValue createNotificationRequest(ActionRequestValue actionRequest, KimPrincipal reasonPrincipal, String notificationRequestCode, String actionTakenCode) {

    	String annotation = generateNotificationAnnotation(reasonPrincipal, notificationRequestCode, actionTakenCode, actionRequest);
        ActionRequestValue request = createActionRequest(notificationRequestCode, actionRequest.getPriority(), actionRequest.getRecipient(), actionRequest.getResponsibilityDesc(), KEWConstants.MACHINE_GENERATED_RESPONSIBILITY_ID, Boolean.TRUE, annotation);

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
    		actionRequest.setApprovePolicy(KEWConstants.APPROVE_POLICY_FIRST_APPROVE);
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
        actionRequest.setStatus(KEWConstants.ACTION_REQUEST_INITIALIZED);
        actionRequest.setRouteHeader(document);
    }

    private static void resolveRecipient(ActionRequestValue actionRequest, Recipient recipient) {
    	if (recipient instanceof KimPrincipalRecipient) {
    		actionRequest.setRecipientTypeCd(KEWConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
    		actionRequest.setPrincipalId(((KimPrincipalRecipient)recipient).getPrincipal().getPrincipalId());
    	}  else if (recipient instanceof KimGroupRecipient) {
    		KimGroupRecipient kimGroupRecipient = (KimGroupRecipient)recipient;
    		actionRequest.setRecipientTypeCd(KEWConstants.ACTION_REQUEST_GROUP_RECIPIENT_CD);
    		actionRequest.setGroupId(kimGroupRecipient.getGroup().getGroupId());
    	} else if (recipient instanceof RoleRecipient){
    		RoleRecipient role = (RoleRecipient)recipient;
    		actionRequest.setRecipientTypeCd(KEWConstants.ACTION_REQUEST_ROLE_RECIPIENT_CD);
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
    	} else if (recipient instanceof KimRoleRecipient) {
    		KimRoleRecipient roleRecipient = (KimRoleRecipient)recipient;
    		actionRequest.setRecipientTypeCd(KEWConstants.ACTION_REQUEST_ROLE_RECIPIENT_CD);
    		actionRequest.setRoleName(roleRecipient.getResponsibilities().get(0).getRoleId());
    		actionRequest.setQualifiedRoleName(roleRecipient.getResponsibilities().get(0).getResponsibilityName());
    		// what about qualified role name label?
//    		actionRequest.setAnnotation(roleRecipient.getResponsibilities().get(0).getResponsibilityName());
    		Recipient targetRecipient = roleRecipient.getTarget();
    		if (targetRecipient != null) {
    			if (targetRecipient instanceof RoleRecipient) {
    				throw new WorkflowRuntimeException("Role Cannot Target a role problem activating request for document " + actionRequest.getRouteHeader().getRouteHeaderId());
    			}
    			resolveRecipient(actionRequest, roleRecipient.getTarget());
    		}
    	}
    }

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
     */
    public ActionRequestValue addRoleRequest(RoleRecipient role, String actionRequested, String approvePolicy, Integer priority, Long responsibilityId, Boolean ignorePrevious, String description, Long ruleId) {

    	ActionRequestValue requestGraph = createActionRequest(actionRequested, priority, role, description, responsibilityId, ignorePrevious, approvePolicy, ruleId, null);
    	if (role != null && role.getResolvedQualifiedRole() != null && role.getResolvedQualifiedRole().getRecipients() != null) {
    	    int legitimateTargets = 0;
    	for (Iterator iter = role.getResolvedQualifiedRole().getRecipients().iterator(); iter.hasNext();) {
			Id recipientId = (Id) iter.next();
			if (recipientId.isEmpty()) {
				throw new WorkflowRuntimeException("Failed to resolve id of type " + recipientId.getClass().getName() + " returned from role '" + role.getRoleName() + "'.  Id returned contained a null or empty value.");
			}
			if (recipientId instanceof UserId) {
				KimPrincipal principal = getIdentityHelperService().getPrincipal((UserId)recipientId);
				role.setTarget(new KimPrincipalRecipient(principal));
			} else if (recipientId instanceof GroupId){
				role.setTarget(new KimGroupRecipient(getIdentityHelperService().getGroup((GroupId) recipientId)));
			} else {
				throw new WorkflowRuntimeException("Could not process the given type of id: " + recipientId.getClass());
			}
			if (role.getTarget() != null) {
                legitimateTargets++;
                ActionRequestValue request = createActionRequest(actionRequested, priority, role, description, responsibilityId, ignorePrevious, null, ruleId, null);
                request.setParentActionRequest(requestGraph);
                requestGraph.getChildrenRequests().add(request);
			}
	     }
    	if (legitimateTargets == 0) {
            LOG.warn("Role did not yield any legitimate recipients");
        }
    	} else {
    		LOG.warn("Didn't create action requests for action request description '" + description + "' because of null role or null part of role object graph.");
    	}
    	requestGraphs.add(requestGraph);
    	return requestGraph;
    }

    /**
     * Generates an ActionRequest graph for the given KIM Responsibilities.  This graph includes any associated delegations.
     */
    public void addRoleResponsibilityRequest(List<ResponsibilityActionInfo> responsibilities, String approvePolicy) {
    	if (responsibilities == null || responsibilities.isEmpty()) {
    		LOG.warn("Didn't create action requests for action request description because no responsibilities were defined.");
    		return;
    	}
    	// it's assumed the that all in the list have the same action type code, priority number, etc.
    	String actionTypeCode = responsibilities.get(0).getActionTypeCode();
    	Integer priority = responsibilities.get(0).getPriorityNumber();
    	boolean ignorePrevious = responsibilities.get(0).isIgnorePrevious();
    	KimRoleRecipient roleRecipient = new KimRoleRecipient(responsibilities);

    	// Creation of a parent graph entry for ????
    	ActionRequestValue requestGraph = null;
    	StringBuffer parentAnnotation = null;
    	// set to allow for suppression of duplicate annotations on the parent action request
    	Set<String> uniqueChildAnnotations = null;
    	if ( responsibilities.size() > 1 ) {
	    	requestGraph = createActionRequest(
	    	        actionTypeCode, 
	    	        priority, 
	    	        roleRecipient, 
	    	        "", // description 
	    	        KEWConstants.MACHINE_GENERATED_RESPONSIBILITY_ID, 
	    	        ignorePrevious, 
	    	        approvePolicy, 
	    	        null, // ruleId
	    	        null );// annotation
	    	requestGraphs.add(requestGraph);
	    	parentAnnotation = new StringBuffer();
	    	uniqueChildAnnotations = new HashSet<String>( responsibilities.size() );
    	}
    	StringBuffer annotation = new StringBuffer();
    	for (ResponsibilityActionInfo responsibility : responsibilities) {
    		if ( LOG.isDebugEnabled() ) {
    			LOG.debug( "Processing Responsibility for action request: " + responsibility );
    		}
        	// KFSMI-2381 - pull information from KIM to populate annotation
    		annotation.setLength( 0 );
    		KimRoleInfo role = getRoleManagementService().getRole(responsibility.getRoleId());
    		annotation.append( role.getNamespaceCode() ).append( ' ' ).append( role.getRoleName() ).append( ' ' );
    		AttributeSet qualifier = responsibility.getQualifier();
    		if ( qualifier != null ) {
	    		for ( String key : qualifier.keySet() ) {	    		    
	        		annotation.append( qualifier.get( key ) ).append( ' ' );
	    		}
    		}
			if (responsibility.getPrincipalId() != null) {
				roleRecipient.setTarget(new KimPrincipalRecipient(responsibility.getPrincipalId()));
			} else if (responsibility.getGroupId() != null) {
				roleRecipient.setTarget(new KimGroupRecipient(responsibility.getGroupId()));
			} else {
				throw new RiceRuntimeException("Failed to identify a group or principal on the given ResponsibilityResolutionInfo:" + responsibility);
			}
			String annotationStr = annotation.toString();
			ActionRequestValue request = createActionRequest(
			        responsibility.getActionTypeCode(), 
			        responsibility.getPriorityNumber(), 
			        roleRecipient, 
			        responsibility.getParallelRoutingGroupingCode(), // description
			        new Long(responsibility.getResponsibilityId()), 
			        responsibility.isIgnorePrevious(), 
			        approvePolicy, 
			        null, // ruleId
			        annotationStr);
			// if there is only a single request, don't create the nesting structure
			if ( responsibilities.size() > 1 ) {
				request.setParentActionRequest(requestGraph);
				requestGraph.getChildrenRequests().add(request);
				generateRoleResponsibilityDelegationRequests(responsibility, request);
				if ( !uniqueChildAnnotations.contains(annotationStr) ) {
					parentAnnotation.append( annotationStr ).append( " -- " );
					uniqueChildAnnotations.add(annotationStr);
				}
			} else {
				requestGraphs.add(request);
				generateRoleResponsibilityDelegationRequests(responsibility, request);
			}
	    }
    	if ( responsibilities.size() > 1 ) {
	    	requestGraph.setAnnotation( StringUtils.chomp( parentAnnotation.toString(), " -- " ) );
    	}
    }

    private void generateRoleResponsibilityDelegationRequests(ResponsibilityActionInfo responsibility, ActionRequestValue parentRequest) {
    	List<DelegateInfo> delegates = responsibility.getDelegates();
    	for (DelegateInfo delegate : delegates) {
    		Recipient recipient = null;
    		boolean isPrincipal = delegate.getMemberTypeCode().equals(KimRole.PRINCIPAL_MEMBER_TYPE);
            boolean isGroup = delegate.getMemberTypeCode().equals(KimRole.GROUP_MEMBER_TYPE);
    		if (isPrincipal) {
    			recipient = new KimPrincipalRecipient(delegate.getMemberId());
    		} else if (isGroup) {
    			recipient = new KimGroupRecipient(delegate.getMemberId());
    		} else {
    			throw new RiceRuntimeException("Invalid DelegateInfo memberTypeCode encountered, was '" + delegate.getMemberTypeCode() + "'");
    		}
    		String delegationAnnotation = generateRoleResponsibilityDelegateAnnotation(delegate, isPrincipal, isGroup, parentRequest);
    		addDelegationRequest(parentRequest, recipient, new Long(delegate.getDelegationId()), parentRequest.getIgnorePrevAction(), delegate.getDelegationTypeCode(), delegationAnnotation, null);
    	}
    }

    private String generateRoleResponsibilityDelegateAnnotation(DelegateInfo delegate, boolean isPrincipal, boolean isGroup, ActionRequestValue parentRequest) {
    	StringBuffer annotation = new StringBuffer( "Delegation of: " );
    	annotation.append( parentRequest.getAnnotation() );
    	annotation.append( " to " );
    	if (isPrincipal) {
    		annotation.append( "principal " );
    		KimPrincipalInfo principal = getIdentityManagementService().getPrincipal( delegate.getMemberId() );
    		if ( principal != null ) {
    			annotation.append( principal.getPrincipalName() );
    		} else {
    			annotation.append( delegate.getMemberId() );
    		}
    	} else if (isGroup) {
    		annotation.append( "group " );
    		GroupInfo group = getIdentityManagementService().getGroup( delegate.getMemberId() );
    		if ( group != null ) {
    			annotation.append( group.getNamespaceCode() ).append( '/' ).append( group.getGroupName() );
    		} else {
    			annotation.append( delegate.getMemberId() );
    		}
    	} else {
    		annotation.append( "?????? '" );
			annotation.append( delegate.getMemberId() );
    		annotation.append( "'" );
    	}
    	return annotation.toString();
    }

    public ActionRequestValue addDelegationRoleRequest(ActionRequestValue parentRequest, String approvePolicy, RoleRecipient role, Long responsibilityId, Boolean ignorePrevious, String delegationType, String description, Long ruleId) {
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
				role.setTarget(new KimPrincipalRecipient(getIdentityHelperService().getPrincipal((UserId) recipientId)));
			} else if (recipientId instanceof GroupId) {
			    role.setTarget(new KimGroupRecipient(getIdentityHelperService().getGroup((GroupId) recipientId)));
			} else {
				throw new WorkflowRuntimeException("Could not process the given type of id: " + recipientId.getClass());
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

    public ActionRequestValue addDelegationRequest(ActionRequestValue parentRequest, Recipient recipient, Long responsibilityId, Boolean ignorePrevious, String delegationType, String annotation, Long ruleId) {
    	if (! relatedToRoot(parentRequest)) {
    		throw new WorkflowRuntimeException("The parent request is not related to any request managed by this factory");
    	}
    	ActionRequestValue delegationRequest = createActionRequest(parentRequest.getActionRequested(), parentRequest.getPriority(), recipient, parentRequest.getResponsibilityDesc(), responsibilityId, ignorePrevious, null, ruleId, annotation);
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

    private String generateNotificationAnnotation(KimPrincipal principal, String notificationRequestCode, String actionTakenCode, ActionRequestValue request) {
    	String notification = "Action " + CodeTranslator.getActionRequestLabel(notificationRequestCode) + " generated by Workflow because " + principal.getPrincipalName() + " took action "
				+ CodeTranslator.getActionTakenLabel(actionTakenCode);
    	if (request.getResponsibilityId() != null && request.getResponsibilityId().longValue() != 0) {
    		notification += " Responsibility " + request.getResponsibilityId();
    	}
    	if (request.getRuleBaseValuesId() != null) {
    		notification += " Rule Id " + request.getRuleBaseValuesId();
    	}
    	return notification;
	}

    protected static ActionRequestService getActionRequestService() {
		if ( actionRequestService == null ) {
			actionRequestService = KEWServiceLocator.getActionRequestService();
		}
		return actionRequestService;
    }

	/**
	 * @return the roleManagementService
	 */
    protected static RoleManagementService getRoleManagementService() {
		if ( roleManagementService == null ) {
			roleManagementService = KIMServiceLocator.getRoleManagementService();
		}
		return roleManagementService;
	}

	/**
	 * @return the identityHelperService
	 */
    protected static IdentityHelperService getIdentityHelperService() {
		if ( identityHelperService == null ) {
			identityHelperService = KEWServiceLocator.getIdentityHelperService();
		}
		return identityHelperService;
	}

	/**
	 * @return the identityManagementService
	 */
    protected static IdentityManagementService getIdentityManagementService() {
		if ( identityManagementService == null ) {
			identityManagementService = KIMServiceLocator.getIdentityManagementService();
		}
		return identityManagementService;
	}

}