/*
 * Copyright 2005-2008 The Kuali Foundation
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
package mocks;

import java.util.List;

import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.exception.ResourceUnavailableException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routemodule.RouteModule;
import org.kuali.rice.kew.util.ResponsibleParty;


public class MockRouteModule implements RouteModule {
    
    List nonPrimaryDelegationResponsibleParties;

    public List findActionRequests(RouteContext context) throws ResourceUnavailableException, WorkflowException {
        return null;
    }
    
    public List findActionRequests(DocumentRouteHeaderValue routeHeader) throws ResourceUnavailableException, WorkflowException {
        return null;
    }

    public ResponsibleParty resolveResponsibilityId(String rId) throws ResourceUnavailableException, WorkflowException {
        return null;
    }

}
