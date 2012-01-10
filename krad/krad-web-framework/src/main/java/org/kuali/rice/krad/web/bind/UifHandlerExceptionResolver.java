/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.web.bind;

import org.apache.log4j.Logger;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.SessionDocumentService;
import org.kuali.rice.krad.uif.view.History;
import org.kuali.rice.krad.uif.view.HistoryEntry;
import org.kuali.rice.krad.uif.service.ViewService;
import org.kuali.rice.krad.uif.util.UifWebUtils;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.DocumentFormBase;
import org.kuali.rice.krad.web.form.IncidentReportForm;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Spring Exception intercepter
 *
 * <p>
 * Gets the data needed for the incident report from the request and builds the
 * model and view for the incident report. This resolver intercepts any unhandled 
 * exception.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifHandlerExceptionResolver implements org.springframework.web.servlet.HandlerExceptionResolver {
    private static final Logger LOG = Logger.getLogger(UifHandlerExceptionResolver.class);

    /**
     * Builds the incident report model and view from the request that threw the exception.
     * 
     * @param request -
     *            the request
     * @param response -
     *            the response
     * @param handler -
     *            the current handler when the exception occurred
     * @param ex -
     *            the exception
     * @return the incident report model and view
     * @see org.springframework.web.servlet.HandlerExceptionResolver#resolveException(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, java.lang.Object,
     *      java.lang.Exception)
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {    
        LOG.error("The following error was caught by the UifHandlerExceptionResolver : ", ex);        
        String incidentDocId = request.getParameter(KRADConstants.DOCUMENT_DOCUMENT_NUMBER);
        String incidentViewId = "";

        // log exception
        LOG.error(ex.getMessage(), ex);

        UifFormBase form = UifWebUtils.getFormFromRequest(request);
        if (form instanceof DocumentFormBase) {
            if (((DocumentFormBase) form).getDocument() != null) {
                incidentDocId = ((DocumentFormBase) form).getDocument().getDocumentNumber();
            }
            incidentViewId = ((DocumentFormBase) form).getViewId();
        }

        UserSession userSession = (UserSession) request.getSession().getAttribute(KRADConstants.USER_SESSION_KEY);
        IncidentReportForm incidentReportForm = new IncidentReportForm();

        // Set the post url map to the incident report controller and not 
        // the one the exception occurred on
        String postUrl = request.getRequestURL().toString();        
        postUrl = postUrl.substring(0, postUrl.lastIndexOf("/")) + "/incidentReport";
        incidentReportForm.setFormPostUrl(postUrl);
        incidentReportForm.setException(ex);
        incidentReportForm.setIncidentDocId(incidentDocId);
        incidentReportForm.setIncidentViewId(incidentViewId);
        incidentReportForm.setController(handler.getClass().toString());
        incidentReportForm.setUserId(userSession.getPrincipalId());
        incidentReportForm.setUserName(userSession.getPrincipalName());
        incidentReportForm.setUserEmail(userSession.getPerson().getEmailAddress());
        incidentReportForm.setDevMode(!KRADUtils.isProductionEnvironment());
        incidentReportForm.setViewId("Uif-IncidentReportView");
        // Set the view object
        incidentReportForm.setView(getViewService().getViewById("Uif-IncidentReportView"));

        // Add a new History entry to avoid errors in the postHandle
        History history = new History();
        HistoryEntry entry = new HistoryEntry("", "", "Incident Report", "", "");
        history.setCurrent(entry);
        incidentReportForm.setFormHistory(history);

        // Set render full view to force full render
        incidentReportForm.setRenderFullView(true);

        ModelAndView modelAndView = UifWebUtils.getUIFModelAndView(incidentReportForm, "");
        try {
            UifWebUtils.postControllerHandle(request, response, handler, modelAndView);
        } catch (Exception e) {
            LOG.error("An error stopped the incident form from loading", e);
        }
        request.getSession().setAttribute(incidentReportForm.getFormKey(), incidentReportForm);
        return modelAndView;
    }

    protected ViewService getViewService() {
        return KRADServiceLocatorWeb.getViewService();
    }

    protected SessionDocumentService getSessionDocumentService() {
        return KRADServiceLocatorWeb.getSessionDocumentService();
    }

}
