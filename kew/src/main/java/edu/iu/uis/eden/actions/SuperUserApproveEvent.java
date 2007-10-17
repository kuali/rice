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
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.actionrequests.ActionRequestFactory;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.engine.BlanketApproveEngine;
import edu.iu.uis.eden.engine.OrchestrationConfig;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.node.RequestsNode;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;

/**
 * Does a super user approve action.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class SuperUserApproveEvent extends SuperUserActionTakenEvent {

	private static final Logger LOG = Logger.getLogger(SuperUserApproveEvent.class);

    public SuperUserApproveEvent(DocumentRouteHeaderValue routeHeader, WorkflowUser user) {
        super(routeHeader, user);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_SU_APPROVED_CD);
        this.superUserAction = EdenConstants.SUPER_USER_APPROVE;
    }

    public SuperUserApproveEvent(DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation) {
        super(routeHeader, user, annotation);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_SU_APPROVED_CD);
        this.superUserAction = EdenConstants.SUPER_USER_APPROVE;
    }

	public void recordAction() throws InvalidActionTakenException, EdenUserNotFoundException {
		// TODO: this is used because calling this code from SuperUserAction without
        // it causes an optimistic lock
		this.routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(getRouteHeaderId(), true);

		checkLocking();

		DocumentType docType = getRouteHeader().getDocumentType();

        String errorMessage = super.validateActionRules();
        if (!Utilities.isEmpty(errorMessage)) {
            LOG.info("User not authorized");
            List errors = new ArrayList();
            errors.add(new WorkflowServiceErrorImpl(errorMessage, AUTHORIZATION));
            throw new WorkflowServiceErrorException(errorMessage, errors);
        }

//		if (!docType.isSuperUser(getUser())) {
//			LOG.info("User not authorized");
//			List<WorkflowServiceError> errors = new ArrayList<WorkflowServiceError>();
//			errors.add(new WorkflowServiceErrorImpl("User not authorized for super user action", AUTHORIZATION));
//			throw new WorkflowServiceErrorException("Super User Authorization Error", errors);
//		}

		saveActionTaken();

	        notifyActionTaken(this.actionTaken);

		if (getRouteHeader().isInException() || getRouteHeader().isStateInitiated()) {
			LOG.debug("Moving document back to Enroute");
			String oldStatus = getRouteHeader().getDocRouteStatus();
			getRouteHeader().markDocumentEnroute();
			String newStatus = getRouteHeader().getDocRouteStatus();
			notifyStatusChange(newStatus, oldStatus);
			getRouteHeaderService().saveRouteHeader(getRouteHeader());
		}

		OrchestrationConfig config = new OrchestrationConfig();
		config.setCause(actionTaken);
		config.setDestinationNodeNames(new HashSet());
		config.setSendNotifications(docType.getSuperUserApproveNotificationPolicy().getPolicyValue().booleanValue());
		RequestsNode.setSupressPolicyErrors(RouteContext.getCurrentRouteContext());
		try {
			completeAnyOutstandingCompleteApproveRequets(docType.getSuperUserApproveNotificationPolicy().getPolicyValue().booleanValue());
			new BlanketApproveEngine(config).process(getRouteHeader().getRouteHeaderId(), null);
		} catch (Exception e) {
			LOG.error("Failed to orchestrate the document to SuperUserApproved.", e);
			throw new InvalidActionTakenException("Failed to orchestrate the document to SuperUserApproved.", e);
		}

	}

	@SuppressWarnings("unchecked")
	protected void completeAnyOutstandingCompleteApproveRequets(boolean sendNotifications) throws Exception {
		List<ActionRequestValue> actionRequests = KEWServiceLocator.getActionRequestService().findPendingByActionRequestedAndDocId(EdenConstants.ACTION_REQUEST_APPROVE_REQ, routeHeaderId);
		actionRequests.addAll(KEWServiceLocator.getActionRequestService().findPendingByActionRequestedAndDocId(EdenConstants.ACTION_REQUEST_COMPLETE_REQ, routeHeaderId));
		for (ActionRequestValue actionRequest : actionRequests) {
			KEWServiceLocator.getActionRequestService().deactivateRequest(this.getActionTaken(), actionRequest);
		}
		if (sendNotifications) {
			new ActionRequestFactory(this.getRouteHeader()).generateNotifications(actionRequests, this.getUser(), this.findDelegatorForActionRequests(actionRequests), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, EdenConstants.ACTION_TAKEN_SU_APPROVED_CD);
		}
	}

	protected void markDocument() throws WorkflowException {
		// do nothing since we are overriding the entire behavior
	}
}