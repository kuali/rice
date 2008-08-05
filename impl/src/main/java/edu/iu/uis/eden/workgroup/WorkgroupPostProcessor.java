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
package edu.iu.uis.eden.workgroup;

import org.kuali.rice.kew.DocumentRouteLevelChange;
import org.kuali.rice.kew.DocumentRouteStatusChange;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.clientapp.DeleteEvent;
import org.kuali.rice.kew.exception.ResourceUnavailableException;
import org.kuali.rice.kew.util.KEWConstants;

import edu.iu.uis.eden.postprocessor.DefaultPostProcessor;
import edu.iu.uis.eden.postprocessor.PostProcessor;
import edu.iu.uis.eden.postprocessor.ProcessDocReport;

/**
 * The {@link PostProcessor} for Workgroup documents.  Simply delegates to
 * {@link WorkgroupRoutingService#activateRoutedWorkgroup(Long)}.
 *
 * @see PostProcessor
 * @see WorkgroupRoutingService#activateRoutedWorkgroup(Long)
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupPostProcessor extends DefaultPostProcessor {

    public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception, ResourceUnavailableException {
        if (KEWConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
            KEWServiceLocator.getWorkgroupRoutingService().activateRoutedWorkgroup(statusChangeEvent.getRouteHeaderId());
        }
        
        return new ProcessDocReport(true, "");
    }

    public ProcessDocReport doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) throws Exception, ResourceUnavailableException {
        return new ProcessDocReport(true, "");
    }

    public ProcessDocReport doDeleteRouteHeader(DeleteEvent event) throws Exception, ResourceUnavailableException {
        return new ProcessDocReport(true, "");
    }

}