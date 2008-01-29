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

import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * A class that handles actions taken/submitted by the client 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface ActionHandler {
    /**
     * Invokes the action on the document
     * @param routeHeader the document the action applies to
     * @param user the user taking the action
     * @param annotation the note supplied with the action
     * @param args supplemental arguments used by the handler
     * @throws InvalidActionTakenException if the action is not valid at this point in time
     * @throws EdenUserNotFoundException if there is an error looking up the user
     */
    public void invoke(DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation, Object... args) throws InvalidActionTakenException, EdenUserNotFoundException;
}