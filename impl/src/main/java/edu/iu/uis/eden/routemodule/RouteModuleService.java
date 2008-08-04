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

import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.exception.ResourceUnavailableException;


/**
 * A service for locating Route Modules.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface RouteModuleService {
    
    public RouteModule findRouteModule(RouteNode node) throws ResourceUnavailableException;
    
    public RouteModule findRouteModule(ActionRequestValue actionRequest) throws ResourceUnavailableException;
    
}
