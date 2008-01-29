/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.workflow.actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.exception.ResourceUnavailableException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;

/**
 * Implementation of ActionHandlerRegistry, initialized with standard workflow action handlers
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionHandlerRegistryImpl implements ActionHandlerRegistry {
    private static final Logger LOG = Logger.getLogger(ActionHandlerRegistryImpl.class);

    private final Map<String, String> actionHandlers = new HashMap<String, String>();

    public ActionHandlerRegistryImpl() {
        // register all standard workflow action handlers
        register("approve", ApproveActionHandler.class.getName());
        register("disapprove", DisapproveActionHandler.class.getName());
        register("acknowledge", AcknowledgeActionHandler.class.getName());
        register("complete", CompleteActionHandler.class.getName());
        register("cancel", CancelActionHandler.class.getName());
        register("clearfyi", ClearFYIActionHandler.class.getName());
        register("route", RouteActionHandler.class.getName());
        register("log", LogActionHandler.class.getName());
        register("save", SaveActionHandler.class.getName());
        register("takeworkgroupauthority", TakeWorkgroupAuthorityActionHandler.class.getName());
        register("releaseworkgroupauthority", ReleaseWorkgroupAuthorityActionHandler.class.getName());
        register("adhoc", AdhocActionHandler.class.getName());
        register("blanketapprove", BlanketApproveActionHandler.class.getName());
        register("returntopreviousnode", ReturnToPreviousNodeActionHandler.class.getName());
        register("movedocument", MoveDocumentActionHandler.class.getName());
        register("revokeadhoc", RevokeAdHocActionHandler.class.getName());
        register("suactionrequestapprove", SUActionRequestApproveActionHandler.class.getName());
        register("suapprove", SUApproveActionHandler.class.getName());
        register("sudisapprove", SUDisapproveActionHandler.class.getName());
        register("sucancel", SUCancelActionHandler.class.getName());
        register("sureturntopreviousnode", SUReturnToPreviousNodeActionHandler.class.getName());
        register("sunodeapprove", SUNodeApproveActionHandler.class.getName());
    }
    
    
    /**
     * This overridden method ...
     * 
     * @see org.kuali.workflow.actions.ActionHandlerRegistry#register(java.lang.String, java.lang.String)
     */
    public synchronized void register(String action, String classname) {
        if (classname == null) {
            throw new IllegalArgumentException("Action '" + action + "' cannot be registered with a null action handler class.");
        }
        if (actionHandlers.containsKey(action)) {
            throw new WorkflowRuntimeException("Action Code is already in use.  [" + action + ", " + classname + "].  Please unregister the existing implementation first.");
        }
        actionHandlers.put(action, classname);
    }

    /**
     * @see org.kuali.workflow.actions.ActionHandlerRegistry#unregister(java.lang.String)
     */
    public synchronized String unregister(String action) {
        return actionHandlers.remove(action);
    }

    /**
     * @see org.kuali.workflow.actions.ActionHandlerRegistry#getActionHandler(java.lang.String)
     */
    public ActionHandler getActionHandler(String action) throws ResourceUnavailableException {
        String classname = actionHandlers.get(action);
        if (classname == null) {
            // TODO: implement some sort of default action handler that can service unknown actions (e.g. just requeue the doc and delegate to flow constructs)
            throw new IllegalArgumentException("No action handler has been registered for the given action of '" + action + "'.");
        }
        ObjectDefinition actionDefinition = new ObjectDefinition(classname);
        ActionHandler actionHandler = null;
        try {
            //ActionTakenEvent actionTaken = (ActionTakenEvent)GlobalResourceLoader.getResourceLoader().getObject(actionDefinition);
            // TODO ActionTakenEvent is not an interface so we can't fetch them through the GlobalResourceLoader, for now, just use
            // the ObjectDefinitionResolver
            actionHandler = (ActionHandler) GlobalResourceLoader.getObject(actionDefinition);
        } catch (Exception e) {
            LOG.debug("getActionHandler() Exception thrown while working with action handler class name '" + classname + "'");
            if (e instanceof ResourceUnavailableException) {
                throw (ResourceUnavailableException) e;
            }
            throw new ResourceUnavailableException(e);
        }
        if (actionHandler == null) {
            throw new ResourceUnavailableException("Could not locate action handler class '" + classname + "'");
        }
        return actionHandler;
    }
}