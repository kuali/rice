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

import edu.iu.uis.eden.engine.simulation.SimulationCriteria;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;

/**
 * A service for executing routing reports from {@link SimulationCriteria}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface RoutingReportService {

	//public DocumentRouteHeaderValue report(Long routeHeaderId, Integer startRouteLevel, Integer endRouteLevel, List usersToFilterIn) throws WorkflowException;
	
	//public DocumentRouteHeaderValue report(String documentTypeName, DocumentContent documentContent, Integer startRouteLevel, Integer endRouteLevel, List usersToFilterIn) throws WorkflowException;
	
	//public DocumentRouteHeaderValue simulationReport(DocumentRouteHeaderValue document, List actionsToTake) throws WorkflowException;
    
    public DocumentRouteHeaderValue report(SimulationCriteria criteria) throws WorkflowException;
	
}
