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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
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

    public SuperUserActionTakenEvent(DocumentRouteHeaderValue routeHeader, WorkflowUser user) {
        super(routeHeader, user);
    }

    public SuperUserActionTakenEvent(DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation) {
        super(routeHeader, user, annotation);
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.actions.ActionTakenEvent#validateActionRules()
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
        checkLocking();

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

        queueDocument();
    }

    protected abstract void markDocument() throws WorkflowException;

    protected void processActionRequests() throws InvalidActionTakenException, EdenUserNotFoundException {
        LOG.debug("Processing pending action requests");

        saveActionTaken();

        List actionRequests = getActionRequestService().findPendingByDoc(getRouteHeaderId());

        for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
            getActionRequestService().deactivateRequest(actionTaken, actionRequest);
        }

        notifyActionTaken(this.actionTaken);
    }

    public ActionRequestValue getActionRequest() {
        return actionRequest;
    }

    public void setActionRequest(ActionRequestValue actionRequest) {
        this.actionRequest = actionRequest;
    }
}