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
package edu.iu.uis.eden.stats.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.stats.Stats;
import edu.iu.uis.eden.stats.StatsService;
import edu.iu.uis.eden.web.WorkflowAction;

/**
 * A Struts Action for compiling and displaying statistics about the KEW application.
 *
 * @see Stats
 * @see StatsService
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class StatsAction extends WorkflowAction {

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

            StatsForm statForm = (StatsForm) form;

            statForm.determineBeginDate();
            statForm.determineEndDate();
            
            this.getStatsService().NumUsersReport(statForm.getStats());
            this.getStatsService().NumActiveItemsReport(statForm.getStats());
            this.getStatsService().DocumentsRoutedReport(statForm.getStats(), statForm.getBeginningDate(), statForm.getEndingDate());
            this.getStatsService().NumberOfDocTypesReport(statForm.getStats());
            this.getStatsService().ActionsTakenPerUnitOfTimeReport(statForm.getStats(), statForm.getBeginningDate(), statForm.getEndingDate(), statForm.getAvgActionsPerTimeUnit());
            this.getStatsService().NumInitiatedDocsByDocTypeReport(statForm.getStats());
            
            return mapping.findForward("basic");

    }
        
    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) {
        StatsForm statForm = (StatsForm) form;
       
        Map dropDownMap = statForm.makePerUnitOfTimeDropDownMap();
        request.setAttribute("timeUnitDropDown", dropDownMap);    
        return null;
    }

    public StatsService getStatsService() {
        return (StatsService) KEWServiceLocator.getService(KEWServiceLocator.STATS_SERVICE);
    }

}