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
package org.kuali.rice.kew.test;

import java.net.URL;

import org.kuali.rice.kew.preferences.Preferences;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.AuthenticationUserId;
import org.kuali.rice.kew.user.WorkflowUser;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;


/**
 * This is a helper class for writing html unit tests for KEW
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KEWHtmlUnitTestCase extends KEWTestCase {

    public static final String URL_PREFIX = "http://localhost:9952/en-test/";
    public static final String QUICKSTART_USER_NETWORK_ID = "quickstart";
    public static final String QUICKSTART_USER_EMPLOYEE_ID = "1005";

    private WebClient webClient;
    private WorkflowUser quickstartUser;

    @Override
    protected void setUpTransaction() throws Exception {
        super.setUpTransaction();
        webClient = new WebClient();

        // Set the user preference refresh rate to 0 to prevent a <META HTTP-EQUIV="Refresh" .../> tag from being rendered.
        // If it is rendered than HtmlUnit will immediately redirect, causing an error to be thrown.
        this.quickstartUser = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("quickstart"));
        Preferences preferences = KEWServiceLocator.getPreferencesService().getPreferences(quickstartUser.getWorkflowUserId().getId());
        preferences.setRefreshRate("0");
        KEWServiceLocator.getPreferencesService().savePreferences(quickstartUser.getWorkflowUserId().getId(), preferences);
    }

    protected HtmlPage performLogin(String loginUserNetworkId, String urlActionSuffix) throws Exception {
        URL url = new URL (URL_PREFIX + urlActionSuffix);
        HtmlPage loginPage = (HtmlPage)getWebClient().getPage(url);

        // On the first access, we should end up on the backdoor and login as quickstart
        HtmlForm loginForm = (HtmlForm) loginPage.getForms().get(0);
        HtmlTextInput textInput = (HtmlTextInput)loginForm.getInputByName("__login_user");
        textInput.setValueAttribute(loginUserNetworkId);
        return (HtmlPage)loginForm.submit();
    }

    protected HtmlPage getPage(String urlActionSuffix) throws Exception {
        URL url = new URL (URL_PREFIX + urlActionSuffix);
        return (HtmlPage)getWebClient().getPage(url);
    }

    public WebClient getWebClient() {
        return this.webClient;
    }

    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public WorkflowUser getQuickstartUser() {
        return this.quickstartUser;
    }

    public void setQuickstartUser(WorkflowUser quickstartUser) {
        this.quickstartUser = quickstartUser;
    }

}
