/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.kew.help.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.kew.export.ExportDataSet;
import org.kuali.rice.kew.export.web.ExportServlet;
import org.kuali.rice.kew.help.HelpEntry;
import org.kuali.rice.kew.help.service.HelpService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.web.KewKualiAction;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;


/**
 * Struts action for interfacing with the Help system.
 *
 * @see HelpService
 * @see HelpEntry
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class HelpAction extends KewKualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(HelpAction.class);
    private static final String HELP_ID_KEY = "helpId";
    private static final String ID_INVALID = "helpentry.id.invalid";


    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HelpForm helpForm = (HelpForm) form;
        HelpEntry helpEntry = helpForm.getHelpEntry();
        getHelpService().save(helpEntry);
        GlobalVariables.getMessageMap().putInfo(KNSConstants.GLOBAL_MESSAGES, "helpentry.saved");
        return mapping.findForward("summary");
    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception{
    	HelpForm helpForm=(HelpForm)form;
    	HelpEntry helpEntry=helpForm.getHelpEntry();
    	LOG.info(helpEntry.getHelpName());
    	getHelpService().delete(helpEntry);
        helpForm.setShowDelete("no");
        GlobalVariables.getMessageMap().putInfo(KNSConstants.GLOBAL_MESSAGES, "helpentry.deleted");
    	return mapping.findForward("delete");
    }

    public ActionForward getSearch (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	LOG.debug("getSearch");
        HelpForm helpForm = (HelpForm) form;
        // TODO hook up KIM permissions to this
        helpForm.setIsAdmin(false);
       	return mapping.findForward("getSearch");
    }

    public ActionForward search (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HelpForm helpForm = (HelpForm) form;
        HelpEntry helpEntry = helpForm.getHelpEntry();
        if(helpForm.getHelpId() != null && !StringUtils.isNumeric(helpForm.getHelpId())){
            GlobalVariables.getMessageMap().putError(HELP_ID_KEY, ID_INVALID);
        } else {
            if (helpForm.getHelpId() != null) {
                helpEntry.setHelpId(new Long(helpForm.getHelpId()));
            }
        }

        List searchResults = getHelpService().search(helpEntry);

        if(searchResults != null && searchResults.size() > 0){
            request.setAttribute("reqSearchResults", searchResults);
        }
        // TODO hook up KIM permissions to this
        helpForm.setIsAdmin(false);

        return mapping.findForward("getSearch");
    }

    public ActionForward clearSearch (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HelpForm helpForm = (HelpForm) form;
        helpForm.getHelpEntry().setHelpId(null);
        helpForm.getHelpEntry().setHelpName(null);
        helpForm.getHelpEntry().setHelpText(null);
        request.setAttribute("reqSearchResults", null);
        // TODO hook up KIM permissions to this
        helpForm.setIsAdmin(false);
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
        // TOOD hook up KIM permissions to this
        helpForm.setIsAdmin(false);
    	return mapping.findForward("delete");
    }

    private HelpService getHelpService(){
        return  (HelpService) KEWServiceLocator.getService(KEWServiceLocator.HELP_SERVICE);
    }

    public ActionForward getHelpEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HelpForm helpForm = (HelpForm) form;
        String helpKey = request.getParameter("helpKey");
        helpForm.setHelpEntry(getHelpService().findByKey(helpKey));
        helpForm.setShowEdit(KEWConstants.NO_LABEL);
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
        ExportDataSet dataSet = new ExportDataSet();
        dataSet.getHelp().addAll(searchResults);
        request.getSession().setAttribute(ExportServlet.EXPORT_DATA_SET_KEY, dataSet);
        return new ActionForward(ExportServlet.generateExportPath(request, dataSet), true);
    }

}
