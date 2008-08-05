/*
 * Copyright 2007 The Kuali Foundation
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
package edu.iu.uis.eden.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;

import edu.iu.uis.eden.ActionTakenEvent;
import edu.iu.uis.eden.AfterProcessEvent;
import edu.iu.uis.eden.BeforeProcessEvent;
import edu.iu.uis.eden.DocumentRouteLevelChange;
import edu.iu.uis.eden.DocumentRouteStatusChange;
import edu.iu.uis.eden.clientapp.DeleteEvent;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.postprocessor.PostProcessor;
import edu.iu.uis.eden.postprocessor.ProcessDocReport;

/**
 * This is a post processor class used for a Super User Test 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class SuperUserActionInvalidPostProcessor implements PostProcessor {

    private static final String USER_AUTH_ID = "rkirkend";
    
    /**
     * THIS METHOD WILL THROW AN EXCEPTION IF OLD ROUTE NODE IS 'WorkflowTemplate'
     */
    public ProcessDocReport doActionTaken(ActionTakenEvent event) throws Exception {
        if (isDocumentPostProcessable(new WorkflowDocument(new NetworkIdDTO(USER_AUTH_ID), event.getRouteHeaderId()))) {
            return new ProcessDocReport(true, "");
        }
        throw new WorkflowRuntimeException("Post Processor should never be called in this instance");
    }

    /**
     * THIS METHOD WILL THROW AN EXCEPTION IF OLD ROUTE NODE IS 'WorkflowTemplate'
     */
    public ProcessDocReport doDeleteRouteHeader(DeleteEvent event) throws Exception {
        if (isDocumentPostProcessable(new WorkflowDocument(new NetworkIdDTO(USER_AUTH_ID), event.getRouteHeaderId()))) {
            return new ProcessDocReport(true, "");
        }
        throw new WorkflowRuntimeException("Post Processor should never be called in this instance");
    }

    /**
     * THIS METHOD WILL THROW AN EXCEPTION IF OLD ROUTE NODE IS 'WorkflowTemplate'
     */
    public ProcessDocReport doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) throws Exception {
        if (isDocumentPostProcessable(new WorkflowDocument(new NetworkIdDTO(USER_AUTH_ID), levelChangeEvent.getRouteHeaderId()))) {
            return new ProcessDocReport(true, "");
        }
        if ("WorkflowDocument2".equals(levelChangeEvent.getNewNodeName())) {
            return new ProcessDocReport(true, "");
        }
        throw new WorkflowRuntimeException("Post Processor should never be called in this instance");
    }

    /**
     * THIS METHOD WILL THROW AN EXCEPTION IF OLD ROUTE NODE IS 'WorkflowTemplate'
     */
    public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception {
        if (isDocumentPostProcessable(new WorkflowDocument(new NetworkIdDTO(USER_AUTH_ID), statusChangeEvent.getRouteHeaderId()))) {
            return new ProcessDocReport(true, "");
        }
        throw new WorkflowRuntimeException("Post Processor should never be called in this instance");
    }
    
    /**
     * THIS METHOD WILL THROW AN EXCEPTION IF OLD ROUTE NODE IS 'WorkflowTemplate'
     */
    public ProcessDocReport beforeProcess(BeforeProcessEvent beforeProcessEvent) throws Exception {
        if (isDocumentPostProcessable(new WorkflowDocument(new NetworkIdDTO(USER_AUTH_ID), beforeProcessEvent.getRouteHeaderId()))) {
            return new ProcessDocReport(true, "");
        }
        throw new WorkflowRuntimeException("Post Processor should never be called in this instance");
    }
    
    /**
     * THIS METHOD WILL THROW AN EXCEPTION IF OLD ROUTE NODE IS 'WorkflowTemplate'
     */
    public ProcessDocReport afterProcess(AfterProcessEvent afterProcessEvent) throws Exception {
        if (isDocumentPostProcessable(new WorkflowDocument(new NetworkIdDTO(USER_AUTH_ID), afterProcessEvent.getRouteHeaderId()), Arrays.asList(new String[]{"WorkflowDocument2"}))) {
            return new ProcessDocReport(true, "");
        }
        throw new WorkflowRuntimeException("Post Processor should never be called in this instance");
    }
    
    private boolean isDocumentPostProcessable(WorkflowDocument doc) throws WorkflowException {
    	return isDocumentPostProcessable(doc, new ArrayList<String>());
    }
    
    private boolean isDocumentPostProcessable(WorkflowDocument doc, List<String> validNodeNames) throws WorkflowException {
        String[] nodeNames = doc.getNodeNames();
        if (nodeNames != null && nodeNames.length > 0) {
        	return validNodeNames.contains(doc.getNodeNames()[0]) || (doc.getNodeNames()[0].equals("AdHoc")) || (doc.getNodeNames()[0].equals("WorkflowDocument"));
        }
        return false;
    }

}
