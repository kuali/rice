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

import java.util.List;
import java.util.Map;

import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.ResourceUnavailableException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Maintains the registry of Workflow Actions.  Actions are (currently) identified by a one-letter
 * action code and map to a Class which should extend the edu.iu.uis.eden.actions.ActionTakenEvent class.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface ActionRegistry {
	
	/**
	 * Registers the given Action.
	 * 
	 * @param actionCode Should be a one-letter unique code 
	 * @param actionClass the fully-qualified Java classname of the ActionTakenEvent implementation
	 * 
	 * @throws IllegalArgumentException if the actionCode is not one character or already in use,
	 * 	also throws this exception if the actionClass is null
	 */
	public void registerAction(String actionCode, String actionClass);
	
	/**
	 * Unregisters the Action with the given code.
	 */
	public void unregisterAction(String actionCode);
	
	/**
	 * Returns an immutable map of the Action Code to Action Class mappings in this registry.
	 */
	public Map getActionMap();

	/**
	 * Constructs and returns the ActionTakenEvent implementation which can be used to invoke the
	 * Action with the given parameters.

	 * @throws ResourceUnavailableException if the action class cannot be constructed
	 * @throws IllegalArgumentException if the given actionCode has not been registered
	 */
	public ActionTakenEvent createAction(String actionCode, List parameters) throws ResourceUnavailableException;
	
    /**
     * Returns a List of valid action codes for the given user on the document.
     *
     * @throws ResourceUnavailableException if an action class cannot be constructed
     * @throws EdenUserNotFoundException if the given user is invalid
     */
    public ValidActions getValidActions(WorkflowUser user, DocumentRouteHeaderValue document) throws ResourceUnavailableException, EdenUserNotFoundException;
}
