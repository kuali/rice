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
package edu.iu.uis.eden.routemanager;

import edu.iu.uis.eden.ActionTakenEvent;
import edu.iu.uis.eden.DocumentRouteLevelChange;
import edu.iu.uis.eden.DocumentRouteStatusChange;
import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.DeleteEvent;
import edu.iu.uis.eden.postprocessor.PostProcessor;
import edu.iu.uis.eden.postprocessor.ProcessDocReport;

public class ExceptionRoutingTestPostProcessor implements PostProcessor {
	
	public static boolean THROW_ROUTE_STATUS_CHANGE_EXCEPTION;
	public static boolean THROW_ROUTE_STATUS_LEVEL_EXCEPTION;
	public static boolean THROW_ROUTE_DELETE_ROUTE_HEADER_EXCEPTION;
	public static boolean THROW_DO_ACTION_TAKEN_EXCEPTION;
	public static boolean TRANSITIONED_OUT_OF_EXCEPTION_ROUTING = false;
	
	public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception {
	        // defend against re-entrancy by only throwing the route status change exception if the status change we are undergoing is not a transition into exception state!
	        // if we don't do this, this postprocessor will blow up when it is subsequently notified about the transition into exception state that it previously caused
	        // which will result in the document never actually transitioning into exception state
	        boolean transitioningIntoException = !EdenConstants.ROUTE_HEADER_EXCEPTION_CD.equals(statusChangeEvent.getOldRouteStatus()) &&
                                                      EdenConstants.ROUTE_HEADER_EXCEPTION_CD.equals(statusChangeEvent.getNewRouteStatus()); 
		if (THROW_ROUTE_STATUS_CHANGE_EXCEPTION && !transitioningIntoException) {
			throw new RuntimeException("I am the doRouteStatusChange exploder");
		}
		if (EdenConstants.ROUTE_HEADER_EXCEPTION_CD.equals(statusChangeEvent.getOldRouteStatus())) {
			TRANSITIONED_OUT_OF_EXCEPTION_ROUTING = true;
		}
		return new ProcessDocReport(true, "");
	}

	public ProcessDocReport doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) throws Exception {
		if (THROW_ROUTE_STATUS_LEVEL_EXCEPTION) {
			throw new RuntimeException("I am the doRouteLevelChange exploder");
		}
		return new ProcessDocReport(true, "");
	}

	public ProcessDocReport doDeleteRouteHeader(DeleteEvent event) throws Exception {
		if (THROW_ROUTE_DELETE_ROUTE_HEADER_EXCEPTION) {
			throw new RuntimeException("I am the doDeleteRouteHeader exploder");
		}
		return new ProcessDocReport(true, "");
	}

	public ProcessDocReport doActionTaken(ActionTakenEvent event) throws Exception {
		if (THROW_DO_ACTION_TAKEN_EXCEPTION) {
			throw new RuntimeException("I am the doActionTaken exploder");
		}
		return new ProcessDocReport(true, "");
	}

}
