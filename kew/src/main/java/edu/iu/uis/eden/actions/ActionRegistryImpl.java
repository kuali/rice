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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.rice.definition.DataDefinition;
import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.resourceloader.ObjectDefinitionResolver;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.ResourceUnavailableException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.ClassLoaderUtils;

/**
 * A simple implementation of an ActionRegistry which includes all of the default Workflow Actions.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionRegistryImpl implements ActionRegistry {
    private static final Logger LOG = Logger.getLogger(ActionRegistryImpl.class);

	private static Map actionMap = new HashMap();
	static {
		actionMap.put(EdenConstants.ACTION_TAKEN_ACKNOWLEDGED_CD, AcknowledgeAction.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_ADHOC_CD, AdHocAction.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_ADHOC_REVOKED_CD, RevokeAdHocAction.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_APPROVED_CD, ApproveAction.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD, BlanketApproveAction.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_CANCELED_CD, CancelAction.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_COMPLETED_CD, CompleteAction.class.getName());
        actionMap.put(EdenConstants.ACTION_TAKEN_ROUTED_CD, RouteDocumentAction.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_DENIED_CD, DisapproveAction.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_FYI_CD, ClearFYIAction.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_LOG_DOCUMENT_ACTION_CD, LogDocumentActionAction.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_MOVE_CD, MoveDocumentAction.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_TAKE_WORKGROUP_AUTHORITY_CD, TakeWorkgroupAuthority.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_RELEASE_WORKGROUP_AUTHORITY_CD, ReleaseWorkgroupAuthority.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_RETURNED_TO_PREVIOUS_CD, ReturnToPreviousNodeAction.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_SAVED_CD, SaveActionEvent.class.getName());
		//actionMap.put(EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_ACKNOWLEDGED_CD, SuperUserActionRequestAcknowledgeEvent.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_APPROVED_CD, SuperUserActionRequestApproveEvent.class.getName());
		//actionMap.put(EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_COMPLETED_CD, SuperUserActionRequestCompleteEvent.class.getName());
		//actionMap.put(EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_FYI_CD, SuperUserActionRequestFYIEvent.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_SU_APPROVED_CD, SuperUserApproveEvent.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_SU_CANCELED_CD, SuperUserCancelEvent.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_SU_DISAPPROVED_CD, SuperUserDisapproveEvent.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_SU_RETURNED_TO_PREVIOUS_CD, SuperUserReturnToPreviousNodeAction.class.getName());
		actionMap.put(EdenConstants.ACTION_TAKEN_SU_ROUTE_LEVEL_APPROVED_CD, SuperUserNodeApproveEvent.class.getName());
	}

	public void registerAction(String actionCode, String actionClass) {
		if (actionClass == null) {
			throw new IllegalArgumentException("Action Code '" + actionCode + "' cannot be registered with a null action class.");
		}
		if (actionMap.containsKey(actionCode)) {
			throw new WorkflowRuntimeException("Action Code is already in use.  [" +
					actionCode + ", " + actionClass + "].  "+
					"Please unregister the existing implementation first.");
		}
		actionMap.put(actionCode, actionClass);
	}

	public void unregisterAction(String actionCode) {
		actionMap.remove(actionCode);
	}

	public Map getActionMap() {
		return Collections.unmodifiableMap(actionMap);
	}

	/* (non-Javadoc)
	 * @see edu.iu.uis.eden.actions.ActionRegistry#createAction(java.lang.String, java.util.List)
	 */
	public ActionTakenEvent createAction(String actionCode, List parameters) throws ResourceUnavailableException {
		String actionClassName = (String)actionMap.get(actionCode);
		if (actionClassName == null) {
			throw new IllegalArgumentException("No action has been registered for the given action code of '" + actionCode + "'.");
		}
		ObjectDefinition actionDefinition = new ObjectDefinition(actionClassName);
		if (parameters != null && !parameters.isEmpty()) {
			actionDefinition.setConstructorParameters(parameters);
		}
		try {
			//ActionTakenEvent actionTaken = (ActionTakenEvent)GlobalResourceLoader.getResourceLoader().getObject(actionDefinition);
			// TODO ActionTakenEvent is not an interface so we can't fetch them through the GlobalResourceLoader, for now, just use
			// the ObjectDefinitionResolver
			ActionTakenEvent actionTaken = (ActionTakenEvent)ObjectDefinitionResolver.createObject(actionDefinition, ClassLoaderUtils.getDefaultClassLoader(), false);
			if (actionTaken == null) {
				// TODO the exception handling here is a bit wonky
				throw new ResourceUnavailableException("Could not locate action taken class '" + actionClassName + "'");
			}
			return actionTaken;
		} catch (Exception e) {
            LOG.debug("createAction() Exception thrown while working with action class name '" + actionClassName + "'");
			if (e instanceof ResourceUnavailableException) {
				throw (ResourceUnavailableException)e;
			}
			throw new ResourceUnavailableException(e);
		}
	}

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.actions.ActionValidationService#getValidActions(edu.iu.uis.eden.user.WorkflowUser, edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue)
     */
    public ValidActions getValidActions(WorkflowUser user, DocumentRouteHeaderValue document) throws ResourceUnavailableException, EdenUserNotFoundException {
        ValidActions validActions = new ValidActions();
        for (Iterator iter = actionMap.keySet().iterator(); iter.hasNext();) {
            String actionTakenCode = (String) iter.next();
            List<Object> parameters = new ArrayList<Object>();
            parameters.add(new DataDefinition(document));
            parameters.add(new DataDefinition(user));
            ActionTakenEvent actionEvent = createAction(actionTakenCode, parameters);
            if (actionEvent.isActionValid()) {
                validActions.addActionTakenCode(actionTakenCode);
}
        }
        return validActions;
    }
}
