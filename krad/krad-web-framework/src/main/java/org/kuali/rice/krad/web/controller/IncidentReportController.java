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
package org.kuali.rice.krad.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiExceptionIncidentService;
import org.kuali.rice.krad.web.form.IncidentReportForm;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handler for incident reports
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/incidentReport")
public class IncidentReportController extends UifControllerBase {

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected IncidentReportForm createInitialForm(HttpServletRequest request) {
        return new IncidentReportForm();
    }

    /**
     * Emails the report and closes the incident report screen
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=submitReport")
    public ModelAndView submitReport(@ModelAttribute("KualiForm") UifFormBase uifForm, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        // get the exception incident service and use it to mail the report
        KualiExceptionIncidentService reporterService = KRADServiceLocatorWeb.getKualiExceptionIncidentService();
        reporterService.emailReport(((IncidentReportForm) uifForm).createEmailSubject(),
                ((IncidentReportForm) uifForm).createEmailMessage());

        // return the close redirect
        return close(uifForm, result, request, response);
    }

}
