/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kew.postprocessor;

import java.util.Arrays;
import java.util.List;

import org.kuali.rice.kew.dto.DTOConverter;


/**
 * Adapts a {@link PostProcessorRemote} implementation to the {@link PostProcessor} interface.
 *
 * @see PostProcessorRemote
 * @see PostProcessor
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PostProcessorRemoteAdapter implements PostProcessor {

    public PostProcessorRemote postProcessor;
    
    public PostProcessorRemoteAdapter(PostProcessorRemote postProcessor) {
        this.postProcessor = postProcessor;
    }
    
    public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception {
        return new ProcessDocReport(postProcessor.doRouteStatusChange(DTOConverter.convertDocumentRouteStatusChange(statusChangeEvent)));
    }

    public ProcessDocReport doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) throws Exception {
        return new ProcessDocReport(postProcessor.doRouteLevelChange(DTOConverter.convertDocumentRouteLevelChange(levelChangeEvent)));
    }

    public ProcessDocReport doDeleteRouteHeader(DeleteEvent deleteEvent) throws Exception {
        return new ProcessDocReport(postProcessor.doDeleteRouteHeader(DTOConverter.convertDeleteEvent(deleteEvent)));
    }
    
    public ProcessDocReport doActionTaken(ActionTakenEvent actionTakenEvent) throws Exception {
        return new ProcessDocReport(postProcessor.doActionTaken(DTOConverter.convertActionTakenEvent(actionTakenEvent)));
    }
    
    public ProcessDocReport beforeProcess(BeforeProcessEvent event) throws Exception {
        return new ProcessDocReport(postProcessor.beforeProcess(DTOConverter.convertBeforeProcessEvent(event)));
    }

    public ProcessDocReport afterProcess(AfterProcessEvent event) throws Exception {
        return new ProcessDocReport(postProcessor.afterProcess(DTOConverter.convertAfterProcessEvent(event)));
    }
    
    public List<Long> getDocumentIdsToLock(DocumentLockingEvent event) throws Exception {
    	Long[] documentIdsToLock = postProcessor.getDocumentIdsToLock(DTOConverter.convertDocumentLockingEvent(event));
    	if (documentIdsToLock == null) {
    		return null;
    	}
    	return Arrays.asList(documentIdsToLock);
    }
    
    public PostProcessorRemote getPostProcessorRemote() {
    	return postProcessor;
    }
}
