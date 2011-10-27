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
package org.kuali.rice.kew.actions;

import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.api.exception.InvalidActionTakenException;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.api.identity.principal.PrincipalContract;


import java.util.ArrayList;
import java.util.List;


/**
 * Super class for all super user action takens.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class SuperUserActionTakenEvent extends ActionTakenEvent {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SuperUserActionTakenEvent.class);

    protected String superUserAction;
    //protected DocumentRouteStatusChange event;
    private ActionRequestValue actionRequest;
    public static String AUTHORIZATION = "general.routing.superuser.notAuthorized";

    public SuperUserActionTakenEvent(String actionTakenCode, DocumentRouteHeaderValue routeHeader, PrincipalContract principal) {
        super(actionTakenCode, routeHeader, principal);
    }

    public SuperUserActionTakenEvent(String actionTakenCode, DocumentRouteHeaderValue routeHeader, PrincipalContract principal, String annotation, boolean runPostProcessor) {
        super(actionTakenCode, routeHeader, principal, annotation, runPostProcessor);
    }

    /* (non-Javadoc)
     * @see org.kuali.rice.kew.actions.ActionTakenEvent#validateActionRules()
     */
    @Override
    public String validateActionRules() {
        DocumentType docType = getRouteHeader().getDocumentType();
        if (!KEWServiceLocator.getDocumentTypePermissionService().canAdministerRouting(getPrincipal().getPrincipalId(), docType)) {
            return "User not authorized for super user action";
        }
        return "";
    }

    @Override
    public String validateActionRules(List<ActionRequestValue> actionRequests) {
    	return validateActionRules();
    }

    public void recordAction() throws InvalidActionTakenException {

        String errorMessage = validateActionRules();
        if (!org.apache.commons.lang.StringUtils.isEmpty(errorMessage)) {
            LOG.info("User not authorized");
            List<WorkflowServiceErrorImpl> errors = new ArrayList<WorkflowServiceErrorImpl>();
            errors.add(new WorkflowServiceErrorImpl(errorMessage, AUTHORIZATION));
            throw new WorkflowServiceErrorException(errorMessage, errors);
        }

        processActionRequests();

        try {
        	String oldStatus = getRouteHeader().getDocRouteStatus();
        	//if the document is initiated then set it enroute so we can transition to any other status
        	if (getRouteHeader().isStateInitiated()) {
        		getRouteHeader().markDocumentEnroute();
        		notifyStatusChange(getRouteHeader().getDocRouteStatus(), oldStatus);
        	}
            markDocument();
            String newStatus = getRouteHeader().getDocRouteStatus();
            notifyStatusChange(newStatus, oldStatus);
        } catch (Exception ex) {
            LOG.error("Caught Exception talking to post processor", ex);
            throw new RuntimeException(ex.getMessage());
        }

    }

    protected abstract void markDocument() throws WorkflowException;

    protected void processActionRequests() throws InvalidActionTakenException {
        LOG.debug("Processing pending action requests");

        ActionTakenValue actionTaken = saveActionTaken();

        List<ActionRequestValue> actionRequests = getActionRequestService().findPendingByDoc(getDocumentId());

        for (ActionRequestValue actionRequest : actionRequests)
        {
            getActionRequestService().deactivateRequest(actionTaken, actionRequest);
        }

        notifyActionTaken(actionTaken);
    }

    public ActionRequestValue getActionRequest() {
        return actionRequest;
    }

    public void setActionRequest(ActionRequestValue actionRequest) {
        this.actionRequest = actionRequest;
    }

}
