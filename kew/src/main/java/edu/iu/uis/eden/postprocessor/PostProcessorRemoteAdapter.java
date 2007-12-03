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
package edu.iu.uis.eden.postprocessor;

import edu.iu.uis.eden.ActionTakenEvent;
import edu.iu.uis.eden.DocumentRouteLevelChange;
import edu.iu.uis.eden.DocumentRouteStatusChange;
import edu.iu.uis.eden.clientapp.DeleteEvent;
import edu.iu.uis.eden.clientapp.PostProcessorRemote;
import edu.iu.uis.eden.server.BeanConverter;

/**
 * Adapts a {@link PostProcessorRemote} implementation to the {@link PostProcessor} interface.
 *
 * @see PostProcessorRemote
 * @see PostProcessor
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PostProcessorRemoteAdapter implements PostProcessor {

    public PostProcessorRemote postProcessor;
    
    public PostProcessorRemoteAdapter(PostProcessorRemote postProcessor) {
        this.postProcessor = postProcessor;
    }
    
    public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception {
        return new ProcessDocReport(postProcessor.doRouteStatusChange(BeanConverter.convertDocumentRouteStatusChange(statusChangeEvent)));
    }

    public ProcessDocReport doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) throws Exception {
        return new ProcessDocReport(postProcessor.doRouteLevelChange(BeanConverter.convertDocumentRouteLevelChange(levelChangeEvent)));
    }

    public ProcessDocReport doDeleteRouteHeader(DeleteEvent deleteEvent) throws Exception {
        return new ProcessDocReport(postProcessor.doDeleteRouteHeader(BeanConverter.convertDeleteEvent(deleteEvent)));
    }
    
    public ProcessDocReport doActionTaken(ActionTakenEvent actionTakenEvent) throws Exception {
        return new ProcessDocReport(postProcessor.doActionTaken(BeanConverter.convertActionTakenEvent(actionTakenEvent)));
    }
    
}
