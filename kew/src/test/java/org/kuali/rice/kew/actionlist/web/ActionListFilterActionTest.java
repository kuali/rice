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
package org.kuali.rice.kew.actionlist.web;

import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMessages;
import org.junit.Test;
import org.kuali.rice.kew.actionlist.service.ActionListService;
import org.kuali.rice.kew.preferences.Preferences;
import org.kuali.rice.kew.preferences.service.PreferencesService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * This is a description of what this class does - chb  don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ActionListFilterActionTest extends KEWTestCase
{
    ActionListFilterAction alfa = new ActionListFilterAction();

    protected void loadTestData() throws Exception 
    {
        loadXmlFile("ActionsConfig.xml");
    }
    
    /**
     * Test method for {@link org.kuali.rice.kew.actionlist.web.ActionListFilterAction#establishRequiredState(javax.servlet.http.HttpServletRequest, org.apache.struts.action.ActionForm)}.
     */
    @Test
    public final void testEstablishRequiredState()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
       
        ActionListFilterForm form = new ActionListFilterForm();
        
        try
        {
            alfa.establishRequiredState(request, form);
        } 
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
        
        /*public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
            ActionListFilterForm filterForm = (ActionListFilterForm) form;
            filterForm.setUserWorkgroups(getUserWorkgroupsDropDownList(null));
            PreferencesService prefSrv = (PreferencesService) KEWServiceLocator.getPreferencesService();
            Preferences preferences = prefSrv.getPreferences(getUserSession(request).getWorkflowUser());
            request.setAttribute("preferences", preferences);
            ActionListService actionListSrv = (ActionListService) KEWServiceLocator.getActionListService();
            request.setAttribute("delegators", getWebFriendlyRecipients(actionListSrv.findUserSecondaryDelegators(getUserSession(request).getWorkflowUser())));
            request.setAttribute("primaryDelegates", getWebFriendlyRecipients(actionListSrv.findUserPrimaryDelegations(getUserSession(request).getWorkflowUser())));
            if (! filterForm.getMethodToCall().equalsIgnoreCase("clear")) {
                filterForm.validateDates();
            }
            return null;
        }*/
    }

}
