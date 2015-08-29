/**
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.rice.kns.web.struts.action;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kns.util.IncidentReportUtils;
import org.kuali.rice.kns.web.struts.form.KualiExceptionIncidentForm;
import org.kuali.rice.krad.exception.KualiExceptionIncident;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiExceptionIncidentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the struts action class for handling the exception for Kuali
 * applications.
 *
 * @deprecated KNS Struts deprecated, use KRAD and the Spring MVC framework.
 */
@Deprecated
public class KualiExceptionHandlerAction extends Action {
    private static final Logger LOG = Logger
            .getLogger(KualiExceptionHandlerAction.class);

    /**
     * This overridden method dispatches action to be taken based on
     * "methodToCall" parameter. The exception is processed when there is no
     * "methodToCall" specified.
     *
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return executeException(mapping, form, request, response);
    }

    /**
     * This overridden method processes the exception and post exception (when
     * user either submit/cancel the exception JSP page).
     * <ul>
     * <li>ProcessDefinition application Exception - Exception is stored in Http Request</li>
     * <li>ProcessDefinition exception incident reporting - No exception, only form data</li>
     * </ul>
     *
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward executeException(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        if (LOG.isDebugEnabled()) {
            String lm = String.format("ENTRY %s%n%s", form.getClass()
                    .getSimpleName(), request.getRequestURI());
            LOG.debug(lm);
        }

        // Get exception thrown
        Exception e = (Exception) request.getAttribute(Globals.EXCEPTION_KEY);

        // Initialize defined action mapping from struts-config
        ActionForward returnForward = null;

        // In case there is no exception, either a post back after page was
        // filled in
        // or just an error from directly accessing this struts action
        if (e == null) {
            if (form instanceof KualiExceptionIncidentForm) {
                KualiExceptionIncidentForm formObject = (KualiExceptionIncidentForm) form;
                // Manage conditions: submit or cancel
                if (!formObject.isCancel()) {
                    // Locate the post exception handler service. The service id
                    // is
                    // defined in the application properties
                    // Only process the post exception handling when the
                    // service
                    // is specified
                    KualiExceptionIncidentService reporterService = KRADServiceLocatorWeb
                            .getKualiExceptionIncidentService();
                    // An instance of the ExceptionIncident is created by
                    // the
                    // ExceptionIncidentService
                    Map reducedMap = new HashMap();
                    Enumeration<String> names = request.getParameterNames();
                    while (names.hasMoreElements()) {
                        String name = names.nextElement();
                        reducedMap.put(name, request.getParameter(name));
                    }

                    // Sensitive data stored in user session
                    JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(GlobalVariables.
                            getUserSession().getObjectMap().get("exception").toString().getBytes()));
                    JsonObject jsonException = jsonReader.readObject();
                    jsonReader.close();

                    // Only display if this is the right exception
                    if(jsonException.getString("displayMessage").equals(reducedMap.get("displayMessage"))) {
                        reducedMap.put("documentId", jsonException.getString("documentId"));
                        reducedMap.put("userEmail", jsonException.getString("userEmail"));
                        reducedMap.put("principalName", jsonException.getString("principalName"));
                        reducedMap.put("stackTrace", jsonException.getString("stackTrace"));
                        reducedMap.put("userName", jsonException.getString("userName"));
                        reducedMap.put("exceptionMessage", jsonException.getString("exceptionMessage"));
                    }

                    KualiExceptionIncident exceptionIncident = reporterService.getExceptionIncident(reducedMap);
                    // Report the incident
                    reporterService.report(exceptionIncident);
                } else {
                    // Set return after canceling
                    ActionForward cancelForward = mapping
                            .findForward(KRADConstants.MAPPING_CANCEL);
                    if (cancelForward == null) {
                        cancelForward = returnForward;
                    } else {
                        returnForward = cancelForward;
                    }
                }
            }
        } else {
            // ProcessDefinition the received exception from HTTP request
            returnForward = processException(mapping, form, request, e);
        }

        // Not specified, return
        if (returnForward == null) {
            returnForward = mapping.findForward(KRADConstants.MAPPING_CLOSE);
        }

        if (LOG.isDebugEnabled()) {
            String lm = String.format("EXIT %s",
                    (returnForward == null) ? "null" : returnForward.getPath());
            LOG.debug(lm);
        }

        return returnForward;
    }

    /**
     * This method process the caught exception by creating an exception
     * information properties list and forward these properties to the exception
     * incident handler JSP.
     *
     * @param exception
     * @param mapping
     * @param request
     * @param documentId
     *            Id of the document that Struts threw exception during its
     *            processing. null if not the document processing that caused
     *            the exception
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected ActionForward processException(ActionMapping mapping,
            ActionForm form, HttpServletRequest request, Exception exception)
            throws Exception {
        // Only process the exception handling when the service
        // is specified
        KualiExceptionIncidentService reporterService = KRADServiceLocatorWeb
                .getKualiExceptionIncidentService();
        // Get exception properties from the Http Request
        Map<String, String> properties = (Map<String, String>) request
                .getAttribute(IncidentReportUtils.EXCEPTION_PROPERTIES);

        // Construct the exception incident object
        KualiExceptionIncident ei = reporterService.getExceptionIncident(
                exception, properties);

        // Add sensitive data to user session
        Map<String, String> prodProperties = ei.toProperties();
        JsonObjectBuilder exceptionBuilder = Json.createObjectBuilder();
        exceptionBuilder.add("displayMessage", ei.getProperty("displayMessage"));
        exceptionBuilder.add("documentId", ei.getProperty("documentId"));
        exceptionBuilder.add("userEmail", ei.getProperty("userEmail"));
        exceptionBuilder.add("principalName", ei.getProperty("principalName"));
        exceptionBuilder.add("stackTrace", ei.getProperty("stackTrace"));
        exceptionBuilder.add("userName", ei.getProperty("userName"));
        exceptionBuilder.add("exceptionMessage", ei.getProperty("exceptionMessage"));
        JsonObject jsonException = exceptionBuilder.build();
        GlobalVariables.getUserSession().addObject("exception", jsonException.toString());

        // Hide sensitive data from form in production only
        if(ConfigContext.getCurrentContextConfig().isProductionEnvironment()) {
            prodProperties.put("documentId", "");
            prodProperties.put("userEmail", "");
            prodProperties.put("principalName", "");
            prodProperties.put("stackTrace", "");
            prodProperties.put("userName", "");
            prodProperties.put("exceptionMessage", "");
            ei = reporterService.getExceptionIncident(
                    null, prodProperties);
        }

        // Set full exception properties in Http Request and forward to JSP
        request.setAttribute(KualiExceptionHandlerAction.class
                .getSimpleName(), ei.toProperties());
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
}
