/*
 * Copyright 2005-2007 The Kuali Foundation.
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
import edu.iu.uis.eden.engine.simulation.SimulationEngine;
import edu.iu.uis.eden.engine.simulation.SimulationResults;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;

public class RoutingReportServiceImpl implements RoutingReportService {

    public DocumentRouteHeaderValue report(SimulationCriteria criteria) throws WorkflowException {
        try {
            SimulationEngine simulationEngine = new SimulationEngine();
            SimulationResults results = simulationEngine.runSimulation(criteria);
            return materializeDocument(results);
        } catch (Exception e) {
            throw new WorkflowException("Problem running report: " + e.getMessage(), e);
        }
    }

    /**
     * The document returned does not have any of the simulated action requests set on it, we'll want to set them.
     */
    private DocumentRouteHeaderValue materializeDocument(SimulationResults results) {
    	DocumentRouteHeaderValue document = results.getDocument();
		document.getActionRequests().addAll(results.getSimulatedActionRequests());
        return document;

    }
}
