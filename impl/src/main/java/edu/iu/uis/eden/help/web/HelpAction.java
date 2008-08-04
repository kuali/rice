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
package edu.iu.uis.eden.help.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.export.ExportFormat;
import edu.iu.uis.eden.help.HelpEntry;
import edu.iu.uis.eden.help.HelpService;
import edu.iu.uis.eden.web.WorkflowAction;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * Struts action for interfacing with the Help system.
 * 
 * @see HelpService
 * @see HelpEntry
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class HelpAction extends WorkflowAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(HelpAction.class);

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	return mapping.findForward("basic");
    }
    
    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        return null;
    }
    
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HelpForm helpForm = (HelpForm) form;
        HelpEntry helpEntry = helpForm.getHelpEntry();
        getHelpService().save(helpEntry);
        ActionMessages messages = new ActionMessages();
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("helpentry.saved"));
        saveMessages(request, messages);
        return mapping.findForward("summary");
    }
    
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception{
    	HelpForm helpForm=(HelpForm)form;
    	HelpEntry helpEntry=helpForm.getHelpEntry();
    	LOG.info(helpEntry.getHelpName());
    	getHelpService().delete(helpEntry);
        helpForm.setShowDelete("no");
    	ActionMessages messages=new ActionMessages();
    	messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("helpentry.deleted"));
    	saveMessages(request,messages);
    	return mapping.findForward("delete");
    }
    
    public ActionForward getSearch (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	LOG.debug("getSearch");
    	UserSession uSession = getUserSession(request);
        HelpForm helpForm = (HelpForm) form;
        helpForm.setIsAdmin(uSession.isAdmin());
       	return mapping.findForward("getSearch");
    }
    
    public ActionForward search (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HelpForm helpForm = (HelpForm) form;
        HelpEntry helpEntry = helpForm.getHelpEntry();
        List searchResults = getHelpService().search(helpEntry);
        
        if(searchResults != null && searchResults.size() > 0){
            request.setAttribute("reqSearchResults", searchResults);
        }
        UserSession uSession = getUserSession(request);
        helpForm.setIsAdmin(uSession.isAdmin());
        return mapping.findForward("getSearch");
    }
    
    public ActionForward clearSearch (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HelpForm helpForm = (HelpForm) form;
        helpForm.getHelpEntry().setHelpId(null);
        helpForm.getHelpEntry().setHelpName(null);
        helpForm.getHelpEntry().setHelpText(null);
        request.setAttribute("reqSearchResults", null);
        UserSession uSession = getUserSession(request);
        helpForm.setIsAdmin(uSession.isAdmin());      
        return mapping.findForward("getSearch");
    }
    
    public ActionForward report(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HelpForm helpForm = (HelpForm) form;
        helpForm.setHelpEntry(getHelpService().findById(new Long(request.getParameter("helpId"))));
        return mapping.findForward("report");
    }
    
    public ActionForward showEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HelpForm helpForm = (HelpForm) form;
        if(helpForm.getHelpEntry().getHelpId() == null){
            Long helpId = new Long(request.getParameter("helpId"));
            helpForm.setHelpEntry(getHelpService().findById(helpId));
        } 
        return mapping.findForward("basic");
    }
    
    public ActionForward showDelete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception{
    	HelpForm helpForm=(HelpForm)form;
    	if(helpForm.getHelpEntry().getHelpId()==null){
    		Long helpId=new Long(request.getParameter("helpId"));
    		helpForm.setHelpEntry(getHelpService().findById(helpId));
    	}
    	UserSession uSession = getUserSession(request);
        helpForm.setIsAdmin(uSession.isAdmin());   
    	return mapping.findForward("delete");
    }
    
    private HelpService getHelpService(){
        return  (HelpService) KEWServiceLocator.getService(KEWServiceLocator.HELP_SERVICE);
    }
    
    public ActionForward getHelpEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HelpForm helpForm = (HelpForm) form;
        String helpKey = request.getParameter("helpKey");
        helpForm.setHelpEntry(getHelpService().findByKey(helpKey));
        helpForm.setShowEdit(EdenConstants.NO_LABEL);
        return mapping.findForward("popHelp");
    }
    
    /**
     * TODO implement the help search as a lookupable, rendering this code redundant 
     */
    public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HelpForm helpForm = (HelpForm) form;
        HelpEntry helpEntry = helpForm.getHelpEntry();
        List searchResults = getHelpService().search(helpEntry);
        if(searchResults == null) {
        	searchResults = new ArrayList();
        }
        ExportDataSet dataSet = new ExportDataSet(ExportFormat.XML);
        dataSet.getHelp().addAll(searchResults);
        return exportDataSet(request, dataSet);
    }
    
   
}
