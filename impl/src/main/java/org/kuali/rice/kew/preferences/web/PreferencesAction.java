/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kew.preferences.web;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.preferences.Preferences;
import org.kuali.rice.kew.preferences.service.PreferencesService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.web.KewKualiAction;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


/**
 * A Struts Action for interfaces with {@link Preferences}.
 *
 * @see PreferencesService
 * @see Preferences
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PreferencesAction extends KewKualiAction {

    @Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        initForm(request, form);
        request.setAttribute("Constants", getServlet().getServletContext().getAttribute("KewApiConstants"));
        return super.execute(mapping, form, request, response);
    }

    @Override
	public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PreferencesService preferencesService = (PreferencesService) KEWServiceLocator.getService(KEWServiceLocator.PREFERENCES_SERVICE);
        PreferencesForm preferencesForm = (PreferencesForm) form;
        preferencesForm.setPreferences(preferencesService.getPreferences(getUserSession().getPrincipalId()));
        return mapping.findForward("basic");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PreferencesService prefSrv = (PreferencesService) KEWServiceLocator.getService(KEWServiceLocator.PREFERENCES_SERVICE);
        PreferencesForm prefForm = (PreferencesForm) form;

        prefForm.validatePreferences();
        if (GlobalVariables.getMessageMap().hasNoErrors()) {
            prefSrv.savePreferences(getUserSession().getPrincipalId(), prefForm.getPreferences());
        }
        
        GlobalVariables.getUserSession().addObject(KewApiConstants.UPDATE_ACTION_LIST_ATTR_NAME, Boolean.TRUE);
        GlobalVariables.getUserSession().removeObject(KewApiConstants.PREFERENCES);
        
        if (! StringUtils.isEmpty(prefForm.getReturnMapping())) {
            return mapping.findForward(prefForm.getReturnMapping());
        }
        return mapping.findForward("basic");
    }

    public ActionMessages initForm(HttpServletRequest request, ActionForm form) throws Exception {
        request.setAttribute("actionListContent", KewApiConstants.ACTION_LIST_CONTENT);
        getDelegatorFilterChoices(request);
        getPrimaryDelegateFilterChoices(request);
        PreferencesForm prefForm = (PreferencesForm)form;
        prefForm.setShowOutbox(ConfigContext.getCurrentContextConfig().getOutBoxOn());
        return null;
    }

    public void getDelegatorFilterChoices(HttpServletRequest request) {
        List<KeyValue> delegatorFilterChoices = new ArrayList<KeyValue>();
        delegatorFilterChoices.add(new ConcreteKeyValue(KewApiConstants.DELEGATORS_ON_FILTER_PAGE, KewApiConstants.DELEGATORS_ON_FILTER_PAGE));
        delegatorFilterChoices.add(new ConcreteKeyValue(KewApiConstants.DELEGATORS_ON_ACTION_LIST_PAGE, KewApiConstants.DELEGATORS_ON_ACTION_LIST_PAGE));
        request.setAttribute("delegatorFilter", delegatorFilterChoices);
    }
    
    public void getPrimaryDelegateFilterChoices(HttpServletRequest request) {
    	List<KeyValue> primaryDelegateFilterChoices = new ArrayList<KeyValue>();
    	primaryDelegateFilterChoices.add(new ConcreteKeyValue(KewApiConstants.PRIMARY_DELEGATES_ON_FILTER_PAGE, KewApiConstants.PRIMARY_DELEGATES_ON_FILTER_PAGE));
        primaryDelegateFilterChoices.add(new ConcreteKeyValue(KewApiConstants.PRIMARY_DELEGATES_ON_ACTION_LIST_PAGE, KewApiConstants.PRIMARY_DELEGATES_ON_ACTION_LIST_PAGE));
        request.setAttribute("primaryDelegateFilter", primaryDelegateFilterChoices);
    }

    private static UserSession getUserSession() {
        return GlobalVariables.getUserSession();
    }
}
