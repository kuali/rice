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

import org.kuali.rice.kew.ActionTakenEvent;
import org.kuali.rice.kew.AfterProcessEvent;
import org.kuali.rice.kew.BeforeProcessEvent;
import org.kuali.rice.kew.DocumentRouteLevelChange;
import org.kuali.rice.kew.DocumentRouteStatusChange;
import org.kuali.rice.kew.clientapp.DeleteEvent;


/**
 * Hook for applications to perform logic due to workflow events from the engine.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface PostProcessor {

    public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception;
    public ProcessDocReport doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) throws Exception;
    public ProcessDocReport doDeleteRouteHeader(DeleteEvent event) throws Exception;
    public ProcessDocReport doActionTaken(ActionTakenEvent event) throws Exception;
    public ProcessDocReport beforeProcess(BeforeProcessEvent processEvent) throws Exception;
    public ProcessDocReport afterProcess(AfterProcessEvent processEvent) throws Exception;
    
}
