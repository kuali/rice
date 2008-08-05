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
import java.util.List;

import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.WorkflowServiceErrorException;
import org.kuali.rice.kew.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.doctype.DocumentType;
import org.kuali.rice.kew.engine.BlanketApproveEngine;
import org.kuali.rice.kew.engine.OrchestrationConfig;
import org.kuali.rice.kew.exception.EdenUserNotFoundException;
import org.kuali.rice.kew.exception.InvalidActionTakenException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.util.KEWConstants;

import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;

/**
 * Does a node level super user approve action.  All approve/complete requests outstanding for
 * this node are satisfied by this action.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class SuperUserNodeApproveEvent extends SuperUserActionTakenEvent {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SuperUserNodeApproveEvent.class);
    private String nodeName;

    public SuperUserNodeApproveEvent(DocumentRouteHeaderValue routeHeader, WorkflowUser user) {
        super(KEWConstants.ACTION_TAKEN_SU_ROUTE_LEVEL_APPROVED_CD, routeHeader, user);
        this.superUserAction = KEWConstants.SUPER_USER_ROUTE_LEVEL_APPROVE;
    }

    public SuperUserNodeApproveEvent(DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation, boolean runPostProcessor, String nodeName) {
        super(KEWConstants.ACTION_TAKEN_SU_ROUTE_LEVEL_APPROVED_CD, routeHeader, user, annotation, runPostProcessor);
        this.superUserAction = KEWConstants.SUPER_USER_ROUTE_LEVEL_APPROVE;
        this.nodeName = nodeName;
    }

    public void recordAction() throws InvalidActionTakenException, EdenUserNotFoundException {
    //    checkLocking();

        if (Utilities.isEmpty(nodeName)) {
            throw new InvalidActionTakenException("No approval node name set");
        }

        DocumentType docType = getRouteHeader().getDocumentType();

        String errorMessage = super.validateActionRules();
        if (!Utilities.isEmpty(errorMessage)) {
            LOG.info("User not authorized");
            List errors = new ArrayList();
            errors.add(new WorkflowServiceErrorImpl(errorMessage, SuperUserActionTakenEvent.AUTHORIZATION));
            throw new WorkflowServiceErrorException(errorMessage, errors);
        }
//        if (!docType.isSuperUser(getUser())) {
//            LOG.info("User not authorized");
//            List errors = new ArrayList();
//            errors.add(new WorkflowServiceErrorImpl("User not authorized for super user action", AUTHORIZATION));
//            throw new WorkflowServiceErrorException("Super User Authorization Error", errors);
//        }

        ActionTakenValue actionTaken = saveActionTaken();

        notifyActionTaken(actionTaken);

            if (getRouteHeader().isInException()) {
                LOG.debug("Moving document back to Enroute from Exception");
                String oldStatus = getRouteHeader().getDocRouteStatus();
                getRouteHeader().markDocumentEnroute();
                String newStatus = getRouteHeader().getDocRouteStatus();
                notifyStatusChange(newStatus, oldStatus);
                KEWServiceLocator.getRouteHeaderService().saveRouteHeader(getRouteHeader());
            }

            OrchestrationConfig config = new OrchestrationConfig();
            config.setCause(actionTaken);
            config.setDestinationNodeNames(Utilities.asSet(nodeName));
            config.setSendNotifications(docType.getSuperUserApproveNotificationPolicy().getPolicyValue().booleanValue());
            try {
                new BlanketApproveEngine(config, isRunPostProcessorLogic()).process(getRouteHeader().getRouteHeaderId(), null);
            } catch (Exception e) {
            	if (e instanceof RuntimeException) {
        		throw (RuntimeException)e;
        	} else {
        		throw new WorkflowRuntimeException(e.toString(), e);
        	}
            }

        //queueDocument();
    }

    protected void markDocument() throws WorkflowException {
        // do nothing since we are overriding the entire behavior
    }





}