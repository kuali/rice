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
package org.kuali.rice.kew.actions;

import java.util.Iterator;
import java.util.List;

import org.kuali.rice.kew.actionrequest.ActionRequestFactory;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.KimGroupRecipient;
import org.kuali.rice.kew.actionrequest.KimPrincipalRecipient;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.exception.InvalidActionTakenException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.KimGroup;


/**
 * Responsible for creating adhoc requests that are requested from the client.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class AdHocAction extends ActionTakenEvent {
    /**
     * AdHoc actions don't actually result in an action being taken...it's a special case that generates other action requests
     */
    private static final String NO_ACTION_TAKEN_CODE = null;

	private String actionRequested;
	private String nodeName;
	private String responsibilityDesc;
	private Boolean forceAction;
	private Recipient recipient;
	private String annotation;
	private String requestLabel;

    public AdHocAction(DocumentRouteHeaderValue routeHeader, KimPrincipal principal) {
        super(NO_ACTION_TAKEN_CODE, routeHeader, principal);
    }

	public AdHocAction(DocumentRouteHeaderValue routeHeader, KimPrincipal principal, String annotation, String actionRequested, String nodeName, Recipient recipient, String responsibilityDesc, Boolean forceAction, String requestLabel) {
		super(NO_ACTION_TAKEN_CODE, routeHeader, principal, annotation);
		this.actionRequested = actionRequested;
		this.nodeName = nodeName;
		this.responsibilityDesc = responsibilityDesc;
		this.forceAction = forceAction;
		this.recipient = recipient;
		this.annotation = annotation;
		this.requestLabel = requestLabel;
	}

	public void recordAction() throws InvalidActionTakenException {
		String errorMessage = validateActionRules();
        if (!Utilities.isEmpty(errorMessage)) {
            throw new InvalidActionTakenException(errorMessage);
        }
		List targetNodes = KEWServiceLocator.getRouteNodeService().getCurrentNodeInstances(getRouteHeaderId());
        String error = adhocRouteAction(targetNodes, false);
        if (!Utilities.isEmpty(error)) {
            throw new InvalidActionTakenException(error);
        }
    }

    /* (non-Javadoc)
     * @see org.kuali.rice.kew.actions.ActionTakenEvent#validateActionRules()
     */
    @Override
    public String validateActionRules() {
        List targetNodes = KEWServiceLocator.getRouteNodeService().getCurrentNodeInstances(getRouteHeaderId());
        return validateActionRules(targetNodes);
    }

    private String validateActionRules(List targetNodes) {
    	// recipient will be null when this is invoked from ActionRegistry.getValidActions
    	if (recipient != null) {
    		if (recipient instanceof KimPrincipalRecipient) {
    			KimPrincipalRecipient principalRecipient = (KimPrincipalRecipient)recipient;
    			if (!KEWServiceLocator.getDocumentTypePermissionService().canReceiveAdHocRequest(principalRecipient.getPrincipalId(), getRouteHeader().getDocumentType(), actionRequested)) {
    				return "The principal '" + principalRecipient.getPrincipal().getPrincipalName() + "' does not have permission to recieve ad hoc requests on DocumentType '" + getRouteHeader().getDocumentType().getName() + "'";
    			}
    		} else if (recipient instanceof KimGroupRecipient) {
    			KimGroup group = ((KimGroupRecipient)recipient).getGroup();
    			if (!KEWServiceLocator.getDocumentTypePermissionService().canGroupReceiveAdHocRequest("" + group.getGroupId(), getRouteHeader().getDocumentType(), actionRequested)) {
    				return "The group '" + group.getGroupName() + "' does not have permission to recieve ad hoc requests on DocumentType '" + getRouteHeader().getDocumentType().getName() + "'";
    			}
    		} else {
    			return "Invalid Recipient type encountered: " + recipient.getClass();
    		}
    	}
        return adhocRouteAction(targetNodes, true);
    }

    private String adhocRouteAction(List targetNodes, boolean forValidationOnly) {
        if (targetNodes.isEmpty()) {
            return "Could not locate an node instance on the document with the name '" + nodeName + "'";
        }
        boolean requestCreated = false;
        for (Iterator iter = targetNodes.iterator(); iter.hasNext();) {
            RouteNodeInstance routeNode = (RouteNodeInstance) iter.next();
            // if the node name is null, then adhoc it to the first available node
            if (nodeName == null || routeNode.getName().equals(nodeName)) {
                ActionRequestValue adhocRequest = new ActionRequestValue();
                if (!forValidationOnly) {
                    ActionRequestFactory arFactory = new ActionRequestFactory(routeHeader, routeNode);
                    adhocRequest = arFactory.createActionRequest(actionRequested, recipient, responsibilityDesc, forceAction, annotation);
                    adhocRequest.setResponsibilityId(KEWConstants.ADHOC_REQUEST_RESPONSIBILITY_ID);
                    adhocRequest.setRequestLabel(requestLabel);
                } else {
                    adhocRequest.setActionRequested(actionRequested);
                }
                if (adhocRequest.isApproveOrCompleteRequest() && ! (routeHeader.isEnroute() || routeHeader.isStateInitiated() ||
                        routeHeader.isStateSaved())) {
                    return "Cannot AdHoc a Complete or Approve request when document is in state '" + routeHeader.getDocRouteStatusLabel() + "'.";
                }
                if (!forValidationOnly) {
                    if (routeHeader.isDisaproved() || routeHeader.isFinal() || routeHeader.isProcessed()) {
                        getActionRequestService().activateRequest(adhocRequest);
                    } else {
                        KEWServiceLocator.getActionRequestService().saveActionRequest(adhocRequest);
                    }
                }
                requestCreated = true;
                if (nodeName == null) {
                    break;
                }
            }
        }
        if (!requestCreated) {
            return "Didn't create request.  The node name " + nodeName + " given is probably invalid ";
        }
        return "";
    }
}