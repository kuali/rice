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
package edu.iu.uis.eden.applicationconstants.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.applicationconstants.ApplicationConstant;
import edu.iu.uis.eden.applicationconstants.ApplicationConstantsService;
import edu.iu.uis.eden.web.WorkflowAction;

/**
 * Stuts Action for interaction with application constants.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ApplicationConstantsAction extends WorkflowAction {

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        return mapping.findForward("start");
    }
    
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        return mapping.findForward("restart");
    }
    
    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ApplicationConstantsForm applicationConstantsForm = (ApplicationConstantsForm) form;
        clearFields(applicationConstantsForm);
        return mapping.findForward("restart");
    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ApplicationConstantsForm applicationConstantsForm = (ApplicationConstantsForm) form;
        try{
            getService().save(applicationConstantsForm.getConstant());
        } catch (WorkflowServiceErrorException e){   
            applicationConstantsForm.setMethodToCall("create");
            throw e;
        }        
        return mapping.findForward("restart");
    }
    
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ApplicationConstantsForm applicationConstantsForm = (ApplicationConstantsForm) form;
        ApplicationConstant constant = getService().findByName(request.getParameter("applicationConstantName"));
        applicationConstantsForm.setApplicationConstantName(constant.getApplicationConstantName());
        applicationConstantsForm.setApplicationConstantValue(constant.getApplicationConstantValue());       
        return mapping.findForward("edit");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ApplicationConstantsForm applicationConstantsForm = (ApplicationConstantsForm) form;
        ApplicationConstant constant = getService().findByName(applicationConstantsForm.getConstant().getApplicationConstantName());
        constant.setApplicationConstantValue(applicationConstantsForm.getConstant().getApplicationConstantValue());
        
        try{
            getService().save(constant);
        } catch (WorkflowServiceErrorException e){   
            applicationConstantsForm.setMethodToCall("edit");
            throw e;
        }
        return mapping.findForward("restart");
    }
    
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ApplicationConstantsForm applicationConstantsForm = (ApplicationConstantsForm) form;
        getService().delete(applicationConstantsForm.getConstant());
        return mapping.findForward("restart");
    }
    
    public ActionForward confirmDelete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ApplicationConstantsForm applicationConstantsForm = (ApplicationConstantsForm) form;
        ApplicationConstant constant = getService().findByName(request.getParameter("applicationConstantName"));
        applicationConstantsForm.setConstant(constant);
        return mapping.findForward("confirmDelete");
    }
    
    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        ApplicationConstantsService service = (ApplicationConstantsService) KEWServiceLocator.getService(KEWServiceLocator.APPLICATION_CONSTANTS_SRV);
	    ApplicationConstantsForm applicationConstantsForm = (ApplicationConstantsForm) form;
	    applicationConstantsForm.setApplicationConstants(service.findAll());
	    return null;
    }
    
    private ApplicationConstantsService getService(){
        return  (ApplicationConstantsService) KEWServiceLocator.getService(KEWServiceLocator.APPLICATION_CONSTANTS_SRV);
    }

    public ActionMessages establishFinalState(HttpServletRequest request, ActionForm form){
        return null;
    }
    
    private void clearFields(ApplicationConstantsForm applicationConstantsForm){
        applicationConstantsForm.getConstant().setApplicationConstantName(null);
        applicationConstantsForm.getConstant().setApplicationConstantValue(null);
    }
}