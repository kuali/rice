/*
 * Copyright 2005-2006 The Kuali Foundation.
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

package org.kuali.core.web.struts.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.core.exceptions.KualiExceptionIncident;
import org.kuali.core.service.KualiExceptionIncidentService;
import org.kuali.core.util.WebUtils;
import org.kuali.core.web.struts.form.KualiExceptionIncidentForm;
import org.kuali.core.web.struts.pojo.StrutsExceptionIncidentHandler;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.util.RiceConstants;


/**
 * This is the struts action class for handling the exception for Kuali applications.
 * 
 */
public class KualiExceptionHandlerAction extends KualiAction {
    private static final String module=KualiExceptionHandlerAction.class.getName();
    private static final Logger LOG = Logger.getLogger(KualiExceptionHandlerAction.class);
    /**
     * A definition internal to this class for a correlation with the JSP page for
     * retrieving message content.
     * <p>Value of this is ACTION_FORWARD
    */
    public static final String ACTION_FORWARD="ACTION_FORWARD";
    /**
     * A defined default forwarding for this struts action class.
     * <p>Value of this is "next"
     */
    public static final String FORWARD_NEXT=RiceConstants.MAPPING_BASIC;
    /**
     * A defined default forwarding for this struts action class.
     * <p>Value of this is "close"
     */
    public static final String FORWARD_RETURN=KNSConstants.MAPPING_CLOSE;
    /**
     * A defined default forwarding for this struts action class.
     * <p>Value of this is "cancel"
     */
    public static final String FORWARD_CANCEL=KNSConstants.MAPPING_CANCEL;
    /**
     * A defined error forwarding for this struts action class.
     * <p>Value of this is "error"
     */
    public static final String FORWARD_ERR=KNSConstants.MAPPING_ERROR;
    /**
     * This is the property key and must be specified in the configuration for incident
     * reporting service. The reporting service is located by the KNSServiceLocator.
     * <p>Note: If not specified, no action is taken and no exception thrown.
     * <p>Value of this is KualiExceptionHandlerAction.EXCEPTION_INCIDENT_REPORT_SERVICE
     */
    public static final String EXCEPTION_INCIDENT_REPORT_SERVICE="knsExceptionIncidentService";
    
//    String.format(
//            "%s.EXCEPTION_INCIDENT_REPORT_SERVICE",
//            KualiExceptionHandlerAction.class.getSimpleName());
    
    /**
     * This overridden method dispatches action to be taken based on "methodToCall"
     * parameter. The exception is processed when there is no "methodToCall" specified.
     * 
     * @see org.apache.struts.action.Action#execute(
     * org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm,
     * javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {    
        
        if (LOG.isTraceEnabled()) {
            String lm=String.format("ENTRY %s%n%s", form.getClass().getSimpleName(),
                    request.getRequestURI());
            LOG.trace(lm);
        }
                        
        String methodToCall=WebUtils.parseMethodToCall(request);
        
        ActionForward forward;
        if (methodToCall == null) {
            // Process the exception
            forward=executeException(mapping, form, request, response);
        } else {
            String next=(String)request.getAttribute(FORWARD_NEXT);
            // Enable Extended TextArea implementation by checking the methodToCall, "notify"
            if (next == null &&
               !methodToCall.equalsIgnoreCase(KualiAction.FORWARD_TEXT_AREA_UPDATE) &&
               !methodToCall.equalsIgnoreCase(KualiAction.POST_TEXT_AREA_TO_PARENT)) {
                // Process the exception
                forward=executeException(mapping, form, request, response);
            } else {
                //  Delegate to parent
                request.setAttribute(FORWARD_NEXT, FORWARD_NEXT);
                forward=super.execute(mapping, form, request, response);
            }
        }
        
        if (LOG.isTraceEnabled()) {
            String lm=String.format("EXIT %s", (forward==null)?"null":forward.getPath());
            LOG.trace(lm);
        }
                        
        return forward;
    }

    /**
     * This overridden method processes the exception and post exception (when user
     * either submit/cancel the exception JSP page).
     * <ul>
     * <li>Process application Exception - Exception is stored in Http Request</li>
     * <li>Process exception incident reporting - No exception, only form data</li>
     * </ul>
     * 
     * @see org.apache.struts.action.Action#execute(
     * org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm,
     * javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    public ActionForward executeException(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        if (LOG.isTraceEnabled()) {
            String lm=String.format("ENTRY %s%n%s", form.getClass().getSimpleName(),
                    request.getRequestURI());
            LOG.trace(lm);
        }
                        
        // Get exception thrown
        Exception e = (Exception) request.getAttribute(Globals.EXCEPTION_KEY);

        // Initialize defined action mapping from struts-config
        ActionForward returnForward=null;

        // In case there is no exception, either a post back after page was filled in
        // or just an error from directly accessing this struts action
        if (e == null) {
            if (form instanceof KualiExceptionIncidentForm) {
                KualiExceptionIncidentForm formObject=(KualiExceptionIncidentForm)form;
                // Manage conditions: submit or cancel
                if (!formObject.isCancel()) {
                    // Locate the post exception handler service. The service id is
                    // defined in the application properties
                    String serviceName=EXCEPTION_INCIDENT_REPORT_SERVICE;
                    if (serviceName != null) {
                        // Only process the post exception handling when the service
                        // is specified
                        KualiExceptionIncidentService reporterService=
                            (KualiExceptionIncidentService)
                                        KNSServiceLocator.getService(serviceName);
                        // An instance of the ExceptionIncident is created by the
                        // ExceptionIncidentService
                        KualiExceptionIncident exceptionIncident=
                            reporterService.getExceptionIncident(formObject.toMap());
                        // Report the incident
                        reporterService.report(exceptionIncident);
                    } else {
                        // Log information
                        LOG.info(EXCEPTION_INCIDENT_REPORT_SERVICE+"?");
                    }
                } else {
                    // Set return after canceling
                    ActionForward cancelForward=mapping.findForward(FORWARD_CANCEL);
                    if (cancelForward == null) {
                        cancelForward=returnForward;
                    } else {
                        returnForward=cancelForward;
                    }
                }
            }
        } else {
            // Process the received exception from HTTP request
            returnForward=processException(mapping, form, request, e);
        }

        // Not specified, return
        if (returnForward == null) {
            returnForward=mapping.findForward(FORWARD_RETURN);
        }
        
        if (LOG.isTraceEnabled()) {
            String lm=String.format("EXIT %s",
                    (returnForward==null)?"null":returnForward.getPath());
            LOG.trace(lm);
        }
                        
        return returnForward;
    }
    
    /**
     * This method process the caught exception by creating an exception information
     * properties list and forward these properties to the exception incident handler
     * JSP.
     * 
     * @param exception
     * @param mapping
     * @param request
     * @param documentId Id of the document that Struts threw exception during its
     * processing. null if not the document processing that caused the exception
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected ActionForward processException(
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            Exception exception) throws Exception {
        
        if (LOG.isTraceEnabled()) {
            String lm=String.format("ENTRY %s%n%s%n%s", form.getClass().getSimpleName(),
                    request.getRequestURI(),
                    (exception==null)?"null":exception.getMessage());
            LOG.trace(lm);
        }
                        
        ActionForward returnForward=null;
        try {
            String exceptionClassName=exception.getClass().getSimpleName();
            // Lookup jsp forwarding. The logic is matching the forwarding id defined
            // in struts-config.xml for this action with the exception class name
            returnForward=mapping.findForward(exceptionClassName);
            if (returnForward == null) {
                // Locate the exception handler service. The service id is
                // defined in the application properties
                String serviceName=EXCEPTION_INCIDENT_REPORT_SERVICE;
                if (serviceName != null) {
                    // Only process the exception handling when the service
                    // is specified
                    KualiExceptionIncidentService reporterService=(KualiExceptionIncidentService)
                    KNSServiceLocator.getService(serviceName);
                    // Get exception properties from the Http Request
                    Map<String, String> properties=(Map<String, String>)
                        request.getAttribute(StrutsExceptionIncidentHandler.EXCEPTION_PROPERTIES);
                    // Construct the exception incident object
                    KualiExceptionIncident ei=
                        reporterService.getExceptionIncident(exception, properties);
                    // Set full exception properties in Http Request and forward to JSP
                    request.setAttribute(module, ei.toProperties());
                }
                // Default to local forwarding
                returnForward = mapping.findForward(FORWARD_NEXT);

            }

        } catch (Exception e) {
            returnForward=processInternalError(e.getMessage(), mapping, request);
        }

        if (LOG.isTraceEnabled()) {
            String lm=String.format("EXIT %s",
                    (returnForward==null)?"null":returnForward.getPath());
            LOG.trace(lm);
        }
                        
        return returnForward;
    }
    
    /**
     * This method processes a Code-Bug error.
     * 
     * @param error
     * @param mapping
     * @param request
     * @return
     * @throws Exception
     */
    protected ActionForward processInternalError(String error,
            ActionMapping mapping,
            HttpServletRequest request) throws Exception {
        
        if (LOG.isTraceEnabled()) {
            String lm=String.format("ENTRY %s%n%s", error,
                    request.getRequestURI());
            LOG.trace(lm);
        }
                        
        ActionMessage errorMessage=new ActionMessage(error);
        ActionMessages errors = new ActionMessages();
        errors.add(ActionMessages.GLOBAL_MESSAGE, errorMessage);
        saveErrors(request, errors);
        ActionForward returnForward=mapping.findForward(FORWARD_ERR);
        
        if (LOG.isTraceEnabled()) {
            String lm=String.format("EXIT %s",
                    (returnForward==null)?"null":returnForward.getPath());
            LOG.trace(lm);
        }
                        
        return returnForward;
    }
    
}