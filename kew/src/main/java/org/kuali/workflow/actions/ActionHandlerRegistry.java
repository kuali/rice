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

import edu.iu.uis.eden.exception.ResourceUnavailableException;

/**
 * A registry of ActionHandlers 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface ActionHandlerRegistry {
    /**
     * Registers an ActionHandler by abstract action name
     * @param action the abstract action name
     * @param classname the ActionHandler class name
     * @throws WorkflowRuntimeException if an ActionHandler is already registered for the action
     */
    public void register(String action, String classname);
    /**
     * Unregisters any handler associated with the action returning the existing registration if any
     * @param action the action to unregister
     * @return the class name that was previously registered, or null
     */
    public String unregister(String action);
    /**
     * Constructs and returns the ActionHandler associated with the action,
     * or null if there is no ActionHandler associated with the action
     * @param action the action whose handler to return
     * @return the ActionHandler associated with the action or null if there is no ActionHandler associated with the action
     * @throws ResourceUnavailableException if the action handler cannot be loaded
     */
    public ActionHandler getActionHandler(String action) throws ResourceUnavailableException;
}
