/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kew;

import java.io.InputStream;
import java.net.URL;

import org.kuali.rice.kew.batch.KEWXmlDataLoader;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.preferences.Preferences;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.test.web.HtmlUnitUtil;
import org.kuali.rice.web.test.ServerTestBase;

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
public class KEWHtmlUnitTestCase extends ServerTestBase {

    public static final String ADMIN_USER_NETWORK_ID = "admin";

    private WebClient webClient;
    private KimPrincipal adminPrincipal;

    
    
    @Override
	protected void setUpInternal() throws Exception {
		super.setUpInternal();
		setUpAfterDataLoad();
	}

	
    protected void setUpAfterDataLoad() throws Exception {
        webClient = new WebClient();

        // Set the user preference refresh rate to 0 to prevent a <META HTTP-EQUIV="Refresh" .../> tag from being rendered.
        // If it is rendered than HtmlUnit will immediately redirect, causing an error to be thrown.
        this.adminPrincipal = KEWServiceLocator.getIdentityHelperService().getPrincipalByPrincipalName(ADMIN_USER_NETWORK_ID);
        Preferences preferences = KEWServiceLocator.getPreferencesService().getPreferences(adminPrincipal.getPrincipalId());
        preferences.setRefreshRate("0");
        KEWServiceLocator.getPreferencesService().savePreferences(adminPrincipal.getPrincipalId(), preferences);
    }
    
	protected void loadXmlFile(String fileName) {
		try {
			KEWXmlDataLoader.loadXmlClassLoaderResource(getClass(), fileName);
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
	}

	protected void loadXmlFile(Class clazz, String fileName) {
		try {
			KEWXmlDataLoader.loadXmlClassLoaderResource(clazz, fileName);
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
	}

	protected void loadXmlFileFromFileSystem(String fileName) {
		try {
			KEWXmlDataLoader.loadXmlFile(fileName);
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
	}

	protected void loadXmlStream(InputStream xmlStream) {
		try {
			KEWXmlDataLoader.loadXmlStream(xmlStream);
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
	}

	protected String getPrincipalIdForName(String principalName) {
		return KEWServiceLocator.getIdentityHelperService()
				.getIdForPrincipalName(principalName);
	}

	protected String getPrincipalNameForId(String principalId) {
		return KEWServiceLocator.getIdentityHelperService().getPrincipal(
				principalId).getPrincipalName();
	}

	protected String getGroupIdForName(String namespace, String groupName) {
		return KEWServiceLocator.getIdentityHelperService().getIdForGroupName(
				namespace, groupName);
	}

    protected HtmlPage performLogin(String loginUserNetworkId, String urlActionSuffix) throws Exception {
        URL url = new URL (HtmlUnitUtil.BASE_URL + urlActionSuffix);
        HtmlPage loginPage = (HtmlPage)getWebClient().getPage(url);

        // On the first access, we should end up on the backdoor and login as quickstart
        HtmlForm loginForm = (HtmlForm) loginPage.getForms().get(0);
        HtmlTextInput textInput = (HtmlTextInput)loginForm.getInputByName("__login_user");
        textInput.setValueAttribute(loginUserNetworkId);
        return (HtmlPage)loginForm.submit();
    }

    protected HtmlPage getPage(String urlActionSuffix) throws Exception {
        URL url = new URL (HtmlUnitUtil.BASE_URL + urlActionSuffix);
        return (HtmlPage)getWebClient().getPage(url);
    }

    public WebClient getWebClient() {
        return this.webClient;
    }

    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public KimPrincipal getAdminPrincipal() {
        return this.adminPrincipal;
    }

}
