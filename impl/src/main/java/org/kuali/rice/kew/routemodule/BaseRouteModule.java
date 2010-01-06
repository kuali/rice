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
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class BaseRouteModule implements RouteModule {

	public abstract List<ActionRequestValue> findActionRequests(RouteContext context) throws Exception;

	/**
	 * Default behavior is to return null which results in the responsibility staying the same.
	 */
	public ResponsibleParty resolveResponsibilityId(Long responsibilityId) throws WorkflowException {
		return null;
	}

}
