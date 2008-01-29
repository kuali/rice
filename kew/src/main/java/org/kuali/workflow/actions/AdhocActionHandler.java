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

import edu.iu.uis.eden.actions.AdHocAction;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Implements the "adhoc" action 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class AdhocActionHandler implements ActionHandler {
    /**
     * This overridden method ...
     * 
     * @see org.kuali.workflow.actions.ActionHandler#invoke(edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue, edu.iu.uis.eden.user.WorkflowUser, java.lang.String, java.lang.Object[])
     */
    public void invoke(DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation, Object... args) throws InvalidActionTakenException, EdenUserNotFoundException {
        AdHocAction action = new AdHocAction(routeHeader, user, annotation, (String) args[0], (String) args[1], (Recipient) args[2], (String) args[3], (Boolean) args[4]);
        action.performAction();
    }
}