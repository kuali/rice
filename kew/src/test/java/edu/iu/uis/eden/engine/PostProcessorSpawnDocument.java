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
package edu.iu.uis.eden.engine;

import org.apache.commons.lang.StringUtils;

import edu.iu.uis.eden.ActionTakenEvent;
import edu.iu.uis.eden.DocumentRouteLevelChange;
import edu.iu.uis.eden.DocumentRouteStatusChange;
import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.DeleteEvent;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.postprocessor.PostProcessor;
import edu.iu.uis.eden.postprocessor.ProcessDocReport;

/**
 * Tests a new document being spawned from the post processing of an existing document
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PostProcessorSpawnDocument implements PostProcessor {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostProcessorSpawnDocument.class);

    public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception {
    	LOG.info("Moving document " + statusChangeEvent.getRouteHeaderId() + " from status '" + statusChangeEvent.getOldRouteStatus() + "' to status '" + statusChangeEvent.getNewRouteStatus() + "'");
    	if (StringUtils.equals(EdenConstants.ROUTE_HEADER_PROCESSED_CD, statusChangeEvent.getNewRouteStatus())) {
    		// spawn and route a new document
        	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "SpawnedDocumentType");
        	document.routeDocument("");
    	}
        return new ProcessDocReport(true, "");
    }

    public ProcessDocReport doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) throws Exception {
        return new ProcessDocReport(true, "");
    }

    public ProcessDocReport doDeleteRouteHeader(DeleteEvent event) throws Exception {
        return new ProcessDocReport(false, "");
    }

    public ProcessDocReport doActionTaken(ActionTakenEvent event) throws Exception {
        return new ProcessDocReport(true, "");
    }

}
