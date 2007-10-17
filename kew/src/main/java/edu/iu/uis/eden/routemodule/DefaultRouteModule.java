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
package edu.iu.uis.eden.routemodule;

import java.util.ArrayList;
import java.util.List;

import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.util.ResponsibleParty;

/**
 * A default implementation of a {@link RouteModule}.  This implementation
 * does not generate requests or resolve responsibility IDs.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DefaultRouteModule implements RouteModule {

	public List findActionRequests(RouteContext context) throws Exception {
		return new ArrayList();
	}
	
	public List findActionRequests(DocumentRouteHeaderValue routeHeader)
    throws WorkflowException {
		return new ArrayList();
	}

	public ResponsibleParty resolveResponsibilityId(Long responsibilityId) throws WorkflowException {
		return null;
	}

}
