/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package edu.iu.uis.eden.actions.asyncservices;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.definition.DataDefinition;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actions.ActionTakenEvent;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Service for doing the actual work of a mass action in the action list. Represents a single action on a single document.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionInvocationProcessor implements ActionInvocationService { // implements RouteQueueProcessor {

    private static final Logger LOG = Logger.getLogger(ActionInvocationProcessor.class);

    public void invokeAction(WorkflowUser user, Long documentId, ActionInvocation invocation) {

	KEWServiceLocator.getRouteHeaderService().lockRouteHeader(documentId, true);
	DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);

	List<Object> parameters = new ArrayList<Object>();
	parameters.add(new DataDefinition(document));
	parameters.add(new DataDefinition(user));
	parameters.add(new DataDefinition(""));
	ActionTakenEvent action;
	try {
	    action = KEWServiceLocator.getActionRegistry().createAction(invocation.getActionCode(), parameters);
	    if (!document.isValidActionToTake(invocation.getActionCode())) {
		LOG.warn("Action " + action.getActionTakenCode() + " is not a valid action to take against document " + document.getRouteHeaderId() + " by user " + user);
		return;
	    } else if (!KEWServiceLocator.getActionRegistry().getValidActions(user, document).getActionTakenCodes().contains(action.getActionTakenCode())) {
		LOG.warn("Action " + action.getActionTakenCode() + " is not valid for document " + document.getRouteHeaderId() + " by user " + user);
		return;
	    }
	    action.recordAction();
	    action.queueDocument();
	} catch (Exception e) {
	    throw new WorkflowRuntimeException(e);
	}

    }

}
