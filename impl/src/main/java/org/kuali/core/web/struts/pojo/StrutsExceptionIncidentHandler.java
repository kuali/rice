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
package org.kuali.core.web.struts.pojo;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ExceptionHandler;
import org.apache.struts.config.ExceptionConfig;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.KualiExceptionIncident;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This class is the exception handler for the base exception class java.lang.Throwable
 * and is defined as global exception in the struts-config.xml. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class StrutsExceptionIncidentHandler extends ExceptionHandler {
    private static final Logger LOG=
        Logger.getLogger(StrutsExceptionIncidentHandler.class);
    /**
     * Key to define the attribute stores exception properties such as
     * user email, user name, component name, etc.
     * <p>Value is exceptionProperties
     */
    public static final String EXCEPTION_PROPERTIES="exceptionProperties";
    /**
     * This is defined in struts-config.xml for forwarding this exception to a specified
     * exception handler.
     * <p>Value is exceptionIncidentHandler
     */
    public static final String EXCEPTION_INCIDENT_HANDLER="exceptionIncidentHandler";
    
    /**
     * This overridden method extract exception information such as component name,
     * user name and email, etc.
     * 
     * @see org.apache.struts.action.ExceptionHandler#execute(
     * java.lang.Exception,
     *  org.apache.struts.config.ExceptionConfig,
     *   org.apache.struts.action.ActionMapping,
     *    org.apache.struts.action.ActionForm,
     *     javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(Exception exception,
            ExceptionConfig exceptionConfig,
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        if (LOG.isTraceEnabled()) {
            String message=String.format("ENTRY %s", exception.getMessage());
            LOG.trace(message);
        }
        
        LOG.error("Exception being handled by Exception Handler", exception);

        // Create properties of form and user for additional information
        // to be displayed or passing through JSP
        Map<String, String> properties=new HashMap<String, String>();
        String documentId="";
        if (form instanceof KualiDocumentFormBase) {
            KualiDocumentFormBase docForm=(KualiDocumentFormBase)form;
            if (docForm.getDocument() != null) {
            	documentId=docForm.getDocument().getDocumentNumber();
            }
        }
        properties.put(KualiExceptionIncident.DOCUMENT_ID, documentId);
        String userEmail="";
        String userName="";
        // No specific forward for the caught exception, use default logic
        // Get user information
        UserSession userSession = (UserSession)
        request.getSession().getAttribute(KNSConstants.USER_SESSION_KEY);
        UniversalUser sessionUser=null;
        if (userSession != null) {
            sessionUser=userSession.getUniversalUser();
        }
        if (sessionUser != null) {
            userEmail=sessionUser.getPersonEmailAddress();
            userName=sessionUser.getPersonName();
        }
        properties.put(KualiExceptionIncident.USER_EMAIL, userEmail);
        properties.put(KualiExceptionIncident.USER_NAME, userName);
        properties.put(KualiExceptionIncident.COMPONENT_NAME,
                form.getClass().getSimpleName());
        properties.put(KualiExceptionIncident.CUSTOM_CONTEXTUAL_INFO, "?");

        // Reset the exception so the forward action can read it
        request.setAttribute(Globals.EXCEPTION_KEY, exception);
        // Set exception current information
        request.setAttribute(EXCEPTION_PROPERTIES, properties);
        
        ActionForward forward=mapping.findForward(EXCEPTION_INCIDENT_HANDLER);
        
        if (LOG.isTraceEnabled()) {
            String message=String.format("ENTRY %s%n%s%n%s",
                    exception.getMessage(),
                    properties.toString(),
                    (forward==null)?"null":forward.getPath());
            LOG.trace(message);
        }

        return forward;
    }

}
