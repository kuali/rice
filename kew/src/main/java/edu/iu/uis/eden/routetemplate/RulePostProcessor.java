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
package edu.iu.uis.eden.routetemplate;

import edu.iu.uis.eden.DocumentRouteLevelChange;
import edu.iu.uis.eden.DocumentRouteStatusChange;
import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.DeleteEvent;
import edu.iu.uis.eden.postprocessor.DefaultPostProcessor;
import edu.iu.uis.eden.postprocessor.PostProcessor;
import edu.iu.uis.eden.postprocessor.ProcessDocReport;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.RouteHeaderService;

/**
 * The {@link PostProcessor} implementation for rules.  Delegates to the
 * {@link RuleService#makeCurrent(Long)} method.
 *
 * @see RuleService#makeCurrent(Long)
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RulePostProcessor extends DefaultPostProcessor {

    public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception {
        DocumentRouteHeaderValue routeHeader = getRouteHeaderService().getRouteHeader(statusChangeEvent.getRouteHeaderId());
        
        if (EdenConstants.ROUTE_HEADER_PROCESSED_CD.equals(routeHeader.getDocRouteStatus())) {
            getRuleService().makeCurrent(routeHeader.getRouteHeaderId());
        }
        return new ProcessDocReport(true, "");
    }    
    
    public ProcessDocReport doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) throws Exception {
        return new ProcessDocReport(true, "");
    }

    public ProcessDocReport doDeleteRouteHeader(DeleteEvent event) throws Exception {
        return new ProcessDocReport(true, "");
    }

    public String getVersion() throws Exception {
        return "";
    }

    private RouteHeaderService getRouteHeaderService() {
        return (RouteHeaderService) KEWServiceLocator.getService(KEWServiceLocator.DOC_ROUTE_HEADER_SRV);
    }

    private RuleService getRuleService() {
        return (RuleService) KEWServiceLocator.getService(KEWServiceLocator.RULE_SERVICE);
    }

}