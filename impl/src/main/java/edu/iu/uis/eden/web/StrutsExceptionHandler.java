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
package edu.iu.uis.eden.web;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ExceptionHandler;
import org.apache.struts.config.ExceptionConfig;

import edu.iu.uis.eden.WorkflowServiceError;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.routetemplate.WorkflowAttributeValidationError;
import edu.iu.uis.eden.validation.ValidationResult;
import edu.iu.uis.eden.validation.ValidationResults;

/**
 * Catches exceptions throw from Workflow Actions If exception is of type 
 * WorkflowServiceErrorException any WorkflowServiceErrors saved on the exception 
 * are stripped off of the exception put into ActionMessages in the Error que and 
 * the request is directed back to the original ActionMapping input page. Other 
 * exceptions are logged and directed to the mapped jsp according to the
 * exception handler mapping.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class StrutsExceptionHandler extends ExceptionHandler {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StrutsExceptionHandler.class);

    public ActionForward execute(Exception e, ExceptionConfig exceptionConfig, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException {

        if (e instanceof WorkflowServiceErrorException || e.getCause() instanceof WorkflowServiceErrorException) {
        	WorkflowServiceErrorException serviceException = null;
        	if (! (e instanceof WorkflowServiceErrorException)) {
        		serviceException = (WorkflowServiceErrorException) e.getCause();
        	} else {
        		serviceException = (WorkflowServiceErrorException) e;	
        	}
        	
            Collection serviceErrors = serviceException.getServiceErrors();
            saveServiceErrors(serviceErrors, request);
        } else {
            LOG.error("Mapping " + mapping.getPath() + " threw error: ", e);
            saveServiceErrors("general.workflow.error", e, request);
        }

        if (mapping.getInputForward() != null && mapping.getInputForward().getPath() != null) {
            return mapping.getInputForward();
        } else if (request.getParameter("inputPage") != null && request.getParameter("inputPage").length() > 0) {
            mapping.setInput(request.getParameter("inputPage"));
            return mapping.getInputForward();
        } else if (mapping.getPath() != null) {
            return new ActionForward(mapping.getPath());
        } else {
            request.setAttribute("WORKFLOW_ERROR", e.getMessage());
            return mapping.findForward("WorkflowError");
        }
    }

    protected void saveServiceErrors(Collection srvErrors, HttpServletRequest request) {
        ActionMessages messages = new ActionMessages();
        Iterator errors = srvErrors.iterator();
        while (errors.hasNext()) {
        	Object errorObject = errors.next();
        	if (errorObject instanceof WorkflowServiceError) {
        		WorkflowServiceError err = (WorkflowServiceError)errorObject;
        		if (err.getArg1() == null && err.getArg2() == null) {
        			messages.add(Globals.ERROR_KEY, new ActionMessage(err.getKey()));
        		} else if (err.getArg1() != null && err.getArg2() == null) {
        			messages.add(Globals.ERROR_KEY, new ActionMessage(err.getKey(), err.getArg1())); 
        		} else {
        			messages.add(Globals.ERROR_KEY, new ActionMessage(err.getKey(), err.getArg1(), err.getArg2()));
        		}
        	} else if (errorObject instanceof ValidationResults) {
        		ValidationResults results = (ValidationResults)errorObject;
        		for (Iterator iterator = results.getValidationResults().iterator(); iterator.hasNext(); ) {
					ValidationResult result = (ValidationResult) iterator.next();
					messages.add(Globals.ERROR_KEY, new ActionMessage("general.message", result.getErrorMessage()));
				}
        	} else if (errorObject instanceof WorkflowAttributeValidationError) {
        		WorkflowAttributeValidationError error = (WorkflowAttributeValidationError)errorObject;
        		messages.add(Globals.ERROR_KEY, new ActionMessage(error.getKey(), error.getMessage()));
        	}
        }
        request.setAttribute("workflowServiceError", messages);
    }

    protected void saveServiceErrors(String key, Exception e, HttpServletRequest request) {
        ActionMessages messages = new ActionMessages();
        messages.add(Globals.ERROR_KEY, new ActionMessage(key, e.getMessage()));
        request.setAttribute("exceptionError", messages);
    }

}