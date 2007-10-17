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
import java.util.List;

import org.apache.log4j.MDC;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.actionrequests.ActionRequestFactory;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;

/**
 * Super user Approves a single action request.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SuperUserActionRequestApproveEvent extends SuperUserActionTakenEvent {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SuperUserActionRequestApproveEvent.class);
    private Long actionRequestId;

    public SuperUserActionRequestApproveEvent(DocumentRouteHeaderValue routeHeader, WorkflowUser user) {
        super(routeHeader, user);
        this.superUserAction = EdenConstants.SUPER_USER_ACTION_REQUEST_APPROVE;
    }

    public SuperUserActionRequestApproveEvent(DocumentRouteHeaderValue routeHeader, WorkflowUser user, Long actionRequestId, String annotation) {
        super(routeHeader, user, annotation);
        this.superUserAction = EdenConstants.SUPER_USER_ACTION_REQUEST_APPROVE;
        this.actionRequestId = actionRequestId;
    }

    public void setActionTaken() {
        String actionRequestCode = "";

        ActionRequestValue actionRequest = getActionRequestService().findByActionRequestId(actionRequestId);

        setActionRequest(actionRequest);

        actionRequestCode = actionRequest.getActionRequested();
        //This has been set up for all of the actions, but this class only does approvals
        if (EdenConstants.ACTION_REQUEST_APPROVE_REQ.equals(actionRequestCode)) {
            this.setActionTakenCode(EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_APPROVED_CD);
        } else if (EdenConstants.ACTION_REQUEST_COMPLETE_REQ.equals(actionRequestCode)) {
            this.setActionTakenCode(EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_COMPLETED_CD);
        } else if (EdenConstants.ACTION_REQUEST_FYI_REQ.equals(actionRequestCode)) {
            this.setActionTakenCode(EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_FYI_CD);
        } else if (EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ.equals(actionRequestCode)) {
            this.setActionTakenCode(EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_ACKNOWLEDGED_CD);
        } else {
            //TODO this should be checked
            LOG.error("Invalid SU delegation action request code: " + actionRequestCode);
            throw new RuntimeException("Invalid SU delegation action request code: " + actionRequestCode);
        }
    }

    protected void processActionRequests() throws InvalidActionTakenException, EdenUserNotFoundException {
        //this method has been written to process all of the actions though only approvals are currently processed

        DocumentType docType = getRouteHeader().getDocumentType();
//        boolean userAuthorized = getDocumentTypeService().verifySUAuthority(docType, getUser());

        String errorMessage = super.validateActionRules();
        if (!Utilities.isEmpty(errorMessage)) {
            LOG.info("User not authorized");
            List errors = new ArrayList();
            errors.add(new WorkflowServiceErrorImpl(errorMessage, SuperUserActionTakenEvent.AUTHORIZATION));
            throw new WorkflowServiceErrorException(errorMessage, errors);
        }
//        if (!docType.isSuperUser(getUser())) {
//            List errors = new ArrayList();
//            errors.add(new WorkflowServiceErrorImpl("User not authorized for super user action", SuperUserActionTakenEvent.AUTHORIZATION));
//            throw new WorkflowServiceErrorException("Super User Authorization Error", errors);
//        }

        this.setActionTaken();

        MDC.put("docId", getRouteHeader().getRouteHeaderId());

        if (annotation == null) {
            annotation = "";
        }

        LOG.debug("Super User Delegation Action on action request: " + annotation);
        this.saveActionTaken(getActionRequest().getWorkflowUser());

        LOG.debug("Deactivate this action request");

        ActionRequestValue request = getActionRequest();
        getActionRequestService().deactivateRequest(actionTaken, request);
        if (docType.getSuperUserApproveNotificationPolicy().getPolicyValue().booleanValue() && request.isApproveOrCompleteRequest()) {
        	KEWServiceLocator.getActionRequestService().activateRequest(
        	new ActionRequestFactory(this.getRouteHeader()).createNotificationRequest(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, request.getWorkflowUser(), this.getActionTakenCode(), this.getUser(), null));
        }
        notifyActionTaken(this.actionTaken);

        if (!(EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_FYI_CD.equals(this.getActionTakenCode()) && EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_ACKNOWLEDGED_CD.equals(this.getActionTakenCode()))) {
            if (getRouteHeader().isInException()) {
                LOG.debug("Moving document back to Enroute from Exception");

                String oldStatus = getRouteHeader().getDocRouteStatus();
                this.getRouteHeader().markDocumentEnroute();

                String newStatus = getRouteHeader().getDocRouteStatus();
                this.notifyStatusChange(newStatus, oldStatus);
                getRouteHeaderService().saveRouteHeader(getRouteHeader());
            }
            else if (getRouteHeader().isStateSaved()) {
        	if (EdenConstants.SAVED_REQUEST_RESPONSIBILITY_ID.equals(request.getResponsibilityId())) {
                    LOG.debug("Moving document to Enroute from Saved because action request was request generated by save action");
            	
                    String oldStatus = getRouteHeader().getDocRouteStatus();
                    this.getRouteHeader().markDocumentEnroute();
                    String newStatus = getRouteHeader().getDocRouteStatus();
                    this.notifyStatusChange(newStatus, oldStatus);
                    getRouteHeaderService().saveRouteHeader(getRouteHeader());
        	}
            }
        }
    }

    public void recordAction() throws InvalidActionTakenException, EdenUserNotFoundException {
        checkLocking();
        this.processActionRequests();
        this.queueDocument();
    }

    protected void markDocument() throws WorkflowException {
    }
}