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
package mocks;

import org.kuali.rice.kew.ActionTakenEvent;
import org.kuali.rice.kew.AfterProcessEvent;
import org.kuali.rice.kew.BeforeProcessEvent;
import org.kuali.rice.kew.DocumentRouteLevelChange;
import org.kuali.rice.kew.DocumentRouteStatusChange;
import org.kuali.rice.kew.clientapp.DeleteEvent;

import edu.iu.uis.eden.postprocessor.PostProcessor;
import edu.iu.uis.eden.postprocessor.ProcessDocReport;

public class MockPostProcessor implements PostProcessor {

    private boolean processDocReportResult = true;
    private boolean actionTakenResult = true;
    private boolean processMethodsDocReportResult = true;
    
    public MockPostProcessor() {
        this(true);
    }
    
    public MockPostProcessor(boolean processDocReportResult) {
    	this(processDocReportResult, true);
    }
    
    public MockPostProcessor(boolean processDocReportResult, boolean actionTakenResult) {
        this(processDocReportResult, actionTakenResult, true);
    }
        
    public MockPostProcessor(boolean processDocReportResult, boolean actionTakenResult, boolean processMethodsDocReportResult) {
        this.processDocReportResult = processDocReportResult;
        this.actionTakenResult = actionTakenResult;
        this.processMethodsDocReportResult = processMethodsDocReportResult;
    }
        
    public ProcessDocReport doDeleteRouteHeader(DeleteEvent event) throws Exception {
        return new ProcessDocReport(processDocReportResult, "testing");
    }
    public ProcessDocReport doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) throws Exception {
        return new ProcessDocReport(processDocReportResult, "testing");
    }
    public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception {
        return new ProcessDocReport(processDocReportResult, "testing");
    }
    
    public ProcessDocReport doActionTaken(ActionTakenEvent event) throws Exception {
    	return new ProcessDocReport(actionTakenResult, "testing");
	}

    public ProcessDocReport beforeProcess(BeforeProcessEvent event) throws Exception {
        return new ProcessDocReport(processMethodsDocReportResult, "testing");
    }

    public ProcessDocReport afterProcess(AfterProcessEvent event) throws Exception {
        return new ProcessDocReport(processMethodsDocReportResult, "testing");
    }

    public void setProcessDocReportResult(boolean processDocReportResult) {
        this.processDocReportResult = processDocReportResult;
    }

	public void setActionTakenResult(boolean actionTakenResult) {
		this.actionTakenResult = actionTakenResult;
	}

    public void setProcessMethodsDocReportResult(boolean processMethodsDocReportResult) {
        this.processMethodsDocReportResult = processMethodsDocReportResult;
    }

}