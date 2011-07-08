/*
 * Copyright 2005-2007 The Kuali Foundation
 *
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.routemodule;

import java.util.List;

import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.ResponsibleParty;


/**
 * A RouteModule is responsible for generating Action Requests for a given Route Header document.
 * Implementations of this class are potentially remotable, so this interface uses value objects.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface RouteModule {

    /**
     * Generate action requests for the given RouteContext.
     *
     * @return A List of the generated ActionRequestValue objects.
     */
    public List<ActionRequestValue> findActionRequests(RouteContext context) throws Exception;

    /**
     * The route module will resolve the given responsibilityId and return an object that contains the key to
     * either a user or a workgroup.
     * @param rId ResponsibiliyId that we need resolved.
     * @return The ResponsibleParty containing a key to a user or workgroup.
     */
    public ResponsibleParty resolveResponsibilityId(String responsibilityId) throws WorkflowException;

}
