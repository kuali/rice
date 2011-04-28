/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kew.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.postprocessor.ActionTakenEvent;
import org.kuali.rice.kew.postprocessor.AfterProcessEvent;
import org.kuali.rice.kew.postprocessor.BeforeProcessEvent;
import org.kuali.rice.kew.postprocessor.DeleteEvent;
import org.kuali.rice.kew.postprocessor.DocumentLockingEvent;
import org.kuali.rice.kew.postprocessor.DocumentRouteLevelChange;
import org.kuali.rice.kew.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kew.postprocessor.PostProcessor;
import org.kuali.rice.kew.postprocessor.ProcessDocReport;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;


/**
 * This is a post processor class used for a Super User Test 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class SuperUserActionInvalidPostProcessor implements PostProcessor {

    private static final String USER_AUTH_ID = "rkirkend";
    
    /**
     * THIS METHOD WILL THROW AN EXCEPTION IF OLD ROUTE NODE IS 'WorkflowTemplate'
     */
    public ProcessDocReport doActionTaken(ActionTakenEvent event) throws Exception {
        if (isDocumentPostProcessable(new WorkflowDocument(getPrincipalId(USER_AUTH_ID), event.getRouteHeaderId()))) {
            return new ProcessDocReport(true, "");
        }
        throw new WorkflowRuntimeException("Post Processor should never be called in this instance");
    }

    /**
     * THIS METHOD WILL THROW AN EXCEPTION IF OLD ROUTE NODE IS 'WorkflowTemplate'
     */
    public ProcessDocReport doDeleteRouteHeader(DeleteEvent event) throws Exception {
        if (isDocumentPostProcessable(new WorkflowDocument(getPrincipalId(USER_AUTH_ID), event.getRouteHeaderId()))) {
            return new ProcessDocReport(true, "");
        }
        throw new WorkflowRuntimeException("Post Processor should never be called in this instance");
    }

    /**
     * THIS METHOD WILL THROW AN EXCEPTION IF OLD ROUTE NODE IS 'WorkflowTemplate'
     */
    public ProcessDocReport doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) throws Exception {
        if (isDocumentPostProcessable(new WorkflowDocument(getPrincipalId(USER_AUTH_ID), levelChangeEvent.getRouteHeaderId()))) {
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
        if (isDocumentPostProcessable(new WorkflowDocument(getPrincipalId(USER_AUTH_ID), statusChangeEvent.getRouteHeaderId()))) {
            return new ProcessDocReport(true, "");
        }
        throw new WorkflowRuntimeException("Post Processor should never be called in this instance");
    }
    
    /**
     * THIS METHOD WILL THROW AN EXCEPTION IF OLD ROUTE NODE IS 'WorkflowTemplate'
     */
    public ProcessDocReport beforeProcess(BeforeProcessEvent beforeProcessEvent) throws Exception {
        if (isDocumentPostProcessable(new WorkflowDocument(getPrincipalId(USER_AUTH_ID), beforeProcessEvent.getRouteHeaderId()))) {
            return new ProcessDocReport(true, "");
        }
        throw new WorkflowRuntimeException("Post Processor should never be called in this instance");
    }
    
    /**
     * THIS METHOD WILL THROW AN EXCEPTION IF OLD ROUTE NODE IS 'WorkflowTemplate'
     */
    public ProcessDocReport afterProcess(AfterProcessEvent afterProcessEvent) throws Exception {
        if (isDocumentPostProcessable(new WorkflowDocument(getPrincipalId(USER_AUTH_ID), afterProcessEvent.getRouteHeaderId()), Arrays.asList(new String[]{"WorkflowDocument2"}))) {
            return new ProcessDocReport(true, "");
        }
        throw new WorkflowRuntimeException("Post Processor should never be called in this instance");
    }
    
    /**
     * @see org.kuali.rice.kew.postprocessor.PostProcessor#getDocumentIdsToLock(org.kuali.rice.kew.postprocessor.DocumentLockingEvent)
     */
    public List<Long> getDocumentIdsToLock(DocumentLockingEvent lockingEvent) throws Exception {
		return null;
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

    private String getPrincipalId(String principalName) {
        return KimApiServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(principalName).getPrincipalId();
    }
}
