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
package edu.iu.uis.eden.preferences.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.preferences.Preferences;
import edu.iu.uis.eden.preferences.PreferencesService;
import edu.iu.uis.eden.web.KeyValue;
import edu.iu.uis.eden.web.WorkflowAction;

/**
 * A Struts Action for interfaces with {@link Preferences}.
 * 
 * @see PreferencesService
 * @see Preferences
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PreferencesAction extends WorkflowAction {

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PreferencesService preferencesService = (PreferencesService) KEWServiceLocator.getService(KEWServiceLocator.PREFERENCES_SERVICE);
        PreferencesForm preferencesForm = (PreferencesForm) form;
        preferencesForm.setPreferences(preferencesService.getPreferences(getUserSession(request).getWorkflowUser()));
        return mapping.findForward("viewPreferences");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PreferencesService prefSrv = (PreferencesService) KEWServiceLocator.getService(KEWServiceLocator.PREFERENCES_SERVICE);
        PreferencesForm prefForm = (PreferencesForm) form;
        prefSrv.savePreferences(getUserSession(request).getWorkflowUser(), prefForm.getPreferences());
        getUserSession(request).setPreferences(prefForm.getPreferences());
        if (! isEmpty(prefForm.getReturnMapping())) {
            return mapping.findForward(prefForm.getReturnMapping());
        }
        return mapping.findForward("viewPreferences");
    }

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        request.setAttribute("actionListContent", EdenConstants.ACTION_LIST_CONTENT);
        getDelegatorFilterChoices(request);
        return null;
    }

    public void getDelegatorFilterChoices(HttpServletRequest request) {
        List delegatorFilterChoices = new ArrayList();
        delegatorFilterChoices.add(new KeyValue(EdenConstants.DELEGATORS_ON_FILTER_PAGE, EdenConstants.DELEGATORS_ON_FILTER_PAGE));
        delegatorFilterChoices.add(new KeyValue(EdenConstants.DELEGATORS_ON_ACTION_LIST_PAGE, EdenConstants.DELEGATORS_ON_ACTION_LIST_PAGE));
        request.setAttribute("delegatorFilter", delegatorFilterChoices);
    }
    
}