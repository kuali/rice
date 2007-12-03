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
package edu.iu.uis.eden.docsearch.xml;

import edu.iu.uis.eden.ActionTakenEvent;
import edu.iu.uis.eden.DocumentRouteLevelChange;
import edu.iu.uis.eden.DocumentRouteStatusChange;
import edu.iu.uis.eden.clientapp.DeleteEvent;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.postprocessor.PostProcessor;
import edu.iu.uis.eden.postprocessor.ProcessDocReport;

public class StandardGenericXMLSearchableAttributePostProcessor implements PostProcessor {

	public ProcessDocReport doActionTaken(ActionTakenEvent event) throws Exception {
		return new ProcessDocReport(true);
	}

	public ProcessDocReport doDeleteRouteHeader(DeleteEvent event) throws Exception {
		return new ProcessDocReport(true);
	}

	public ProcessDocReport doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) throws Exception {
		return new ProcessDocReport(true);
	}

	public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception {
		WorkflowDocument doc = new WorkflowDocument(new NetworkIdVO("rkirkend"), statusChangeEvent.getRouteHeaderId());
		doc.setTitle("I'm a title - I should increment the lockVersion Number of this document");
		doc.saveRoutingData();
		return new ProcessDocReport(true);
	}



}
