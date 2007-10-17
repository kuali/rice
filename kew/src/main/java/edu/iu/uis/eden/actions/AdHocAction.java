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
package edu.iu.uis.eden.actions;

import java.util.Iterator;
import java.util.List;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestFactory;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;

/**
 * Responsible for creating adhoc requests that are requested from the client.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class AdHocAction extends ActionTakenEvent {

	private String actionRequested;
	private String nodeName;
	private String responsibilityDesc;
	private Boolean ignorePrevious;
	private Recipient recipient;
	private String annotation;
	
    public AdHocAction(DocumentRouteHeaderValue routeHeader, WorkflowUser user) {
        super(routeHeader, user);
    }

	public AdHocAction(DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation, String actionRequested, String nodeName, Recipient recipient, String responsibilityDesc, Boolean ignorePrevActions) {
		super(routeHeader, user, annotation);
		this.actionRequested = actionRequested;
		this.nodeName = nodeName;
		this.responsibilityDesc = responsibilityDesc;
		this.ignorePrevious = ignorePrevActions;
		this.recipient = recipient;
		this.annotation = annotation;
	}

	public void recordAction() throws InvalidActionTakenException, EdenUserNotFoundException {
		List targetNodes = KEWServiceLocator.getRouteNodeService().getCurrentNodeInstances(getRouteHeaderId());
        String error = adhocRouteAction(targetNodes, false);
        if (!Utilities.isEmpty(error)) {
            throw new InvalidActionTakenException(error);
        }
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.actions.ActionTakenEvent#validateActionRules()
     */
    @Override
    public String validateActionRules() throws EdenUserNotFoundException {
        List targetNodes = KEWServiceLocator.getRouteNodeService().getCurrentNodeInstances(getRouteHeaderId());
        return validateActionRules(targetNodes);
    }
    
    private String validateActionRules(List targetNodes) throws EdenUserNotFoundException {
        return adhocRouteAction(targetNodes, true);
    }
    
    private String adhocRouteAction(List targetNodes, boolean forValidationOnly) throws EdenUserNotFoundException {
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
                    adhocRequest = arFactory.createActionRequest(actionRequested, recipient, responsibilityDesc, ignorePrevious, annotation);
                    adhocRequest.setResponsibilityId(EdenConstants.ADHOC_REQUEST_RESPONSIBILITY_ID);
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