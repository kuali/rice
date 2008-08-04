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
package edu.iu.uis.eden.quicklinks.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.quicklinks.QuickLinksService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.web.KeyValue;
import edu.iu.uis.eden.web.WorkflowAction;

/**
 * A Struts Action for interfacing with the Quick Links system
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class QuickLinksAction extends WorkflowAction {
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(QuickLinksAction.class);
    
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("basic");
    }
    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        QuickLinksForm quickLinksForm = (QuickLinksForm)form;
        WorkflowUser user = getUserSession(request).getWorkflowUser();
        LOG.debug("getting Action List Stats");
        quickLinksForm.setActionListStats(getQuickLinksService().getActionListStats(user));
        LOG.debug("finished getting Action List Stats");
        
        LOG.debug("getting Initiated Document Types");
        quickLinksForm.setInitiatedDocumentTypes(getQuickLinksService().getInitiatedDocumentTypesList(user));
        LOG.debug("finished getting Initiated Document Types");
        
        LOG.debug("getting Named Searches");
        List namedSearches = new ArrayList();
        namedSearches.add(new KeyValue("", "Named Searches"));
        namedSearches.addAll(getQuickLinksService().getNamedSearches(user));
        quickLinksForm.setNamedSearches(namedSearches);
        request.setAttribute("namedSearches",namedSearches);
        LOG.debug("finished getting Named Searches");
        
        LOG.debug("getting Recent Searches");
        quickLinksForm.setRecentSearches(getQuickLinksService().getRecentSearches(user));
        LOG.debug("finished getting Recent Searches");
        
        LOG.debug("getting Watched Documents");
        quickLinksForm.setWatchedDocuments(getQuickLinksService().getWatchedDocuments(user));
        LOG.debug("finished getting Watched Documents");
        return null;
    }
    private QuickLinksService getQuickLinksService() {
        return ((QuickLinksService)KEWServiceLocator.getService(KEWServiceLocator.QUICK_LINKS_SERVICE));
    }
}
