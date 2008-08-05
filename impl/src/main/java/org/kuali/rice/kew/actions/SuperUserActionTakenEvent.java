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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.rice.kew.WorkflowServiceErrorException;
import org.kuali.rice.kew.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.actionrequests.ActionRequestValue;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.doctype.DocumentType;
import org.kuali.rice.kew.exception.EdenUserNotFoundException;
import org.kuali.rice.kew.exception.InvalidActionTakenException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;

import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;

/**
 * Super class for all super user action takens.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class SuperUserActionTakenEvent extends ActionTakenEvent {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SuperUserActionTakenEvent.class);

    protected String superUserAction;
    //protected DocumentRouteStatusChange event;
    private ActionRequestValue actionRequest;
    public static String AUTHORIZATION = "general.routing.superuser.notAuthorized";

    public SuperUserActionTakenEvent(String actionTakenCode, DocumentRouteHeaderValue routeHeader, WorkflowUser user) {
        super(actionTakenCode, routeHeader, user);
    }

    public SuperUserActionTakenEvent(String actionTakenCode, DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation, boolean runPostProcessor) {
        super(actionTakenCode, routeHeader, user, annotation, runPostProcessor);
    }

    /* (non-Javadoc)
     * @see org.kuali.rice.kew.actions.ActionTakenEvent#validateActionRules()
     */
    @Override
    public String validateActionRules() throws EdenUserNotFoundException {
        DocumentType docType = getRouteHeader().getDocumentType();
        if (!docType.isSuperUser(getUser())) {
            return "User not authorized for super user action";
        }
        return "";
    }

    public void recordAction() throws InvalidActionTakenException, EdenUserNotFoundException {
   //     checkLocking();

        String errorMessage = validateActionRules();
        if (!Utilities.isEmpty(errorMessage)) {
            LOG.info("User not authorized");
            List errors = new ArrayList();
            errors.add(new WorkflowServiceErrorImpl(errorMessage, AUTHORIZATION));
            throw new WorkflowServiceErrorException(errorMessage, errors);
        }

//        DocumentType docType = getRouteHeader().getDocumentType();
//        if (!docType.isSuperUser(getUser())) {
//            LOG.info("User not authorized");
//            List errors = new ArrayList();
//            errors.add(new WorkflowServiceErrorImpl("User not authorized for super user action", AUTHORIZATION));
//            throw new WorkflowServiceErrorException("Super User Authorization Error", errors);
//        }

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

    protected void processActionRequests() throws InvalidActionTakenException, EdenUserNotFoundException {
        LOG.debug("Processing pending action requests");

        ActionTakenValue actionTaken = saveActionTaken();

        List actionRequests = getActionRequestService().findPendingByDoc(getRouteHeaderId());

        for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
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