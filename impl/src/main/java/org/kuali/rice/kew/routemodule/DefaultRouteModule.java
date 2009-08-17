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

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.util.ResponsibleParty;


/**
 * A default implementation of a {@link RouteModule}.  This implementation
 * does not generate requests or resolve responsibility IDs.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
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
