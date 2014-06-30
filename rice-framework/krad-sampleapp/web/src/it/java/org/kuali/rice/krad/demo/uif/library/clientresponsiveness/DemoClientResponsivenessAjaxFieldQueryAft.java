/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.demo.uif.library.clientresponsiveness;

import org.junit.Ignore;
import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.Keys;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoClientResponsivenessAjaxFieldQueryAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-AjaxFieldQueryView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-AjaxFieldQueryView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Client Responsiveness");
        waitAndClickByLinkText("AJAX Field Query");
    }

    protected void testClientResponsivenessAjaxFieldQuery() throws Exception {
    	waitAndClickByLinkText("Ajax Field Query");
    	waitForElementPresentByXpath("//input[@name='inputField3' and @value='a1']");
        clickAndTabByName("inputField3");
        assertTextPresent(new String[] {"Travel Account 1", "fred"});
    }
    
    protected void testClientResponsivenessAjaxFieldQueryCustomMethod() throws Exception {
        waitAndClickByLinkText("Ajax Field Query Custom Method");
    	waitForElementPresentByXpath("//input[@name='inputField6' and @value='a2']");
        clickAndTabByName("inputField6");
        assertTextPresent(new String[] {"Travel Account 2", "fran"});
    }
    
    protected void testClientResponsivenessAjaxFieldQueryCustomMethodAndService() throws Exception {
        waitAndClickByLinkText("Ajax Field Query Custom Method and Service");
    	waitForElementPresentByXpath("//input[@name='inputField9' and @value='a3']");
        clickAndTabByName("inputField9");
        assertTextPresent(new String[]{"Travel Account 3", "frank"});
    }

    /**
     * focus, blur seem real flaky on xvfb, maybe click and tab will be better
     * @param name
     * @throws InterruptedException
     */
    private void clickAndTabByName(String name) throws InterruptedException {
        waitAndClickByName(name);
        driver.switchTo().activeElement().sendKeys(Keys.TAB); // update to call typeTab() in 2.5+
    }

    @Test
    @Ignore // https://jira.kuali.org/browse/RICEQA-434 AFT Failures in CI that pass locally
    public void testClientResponsivenessAjaxFieldQueryBookmark() throws Exception {
        testClientResponsivenessAjaxFieldQuery();
        testClientResponsivenessAjaxFieldQueryCustomMethod();
        passed();
    }

    @Test
    @Ignore // https://jira.kuali.org/browse/RICEQA-434 AFT Failures in CI that pass locally
    public void testClientResponsivenessAjaxFieldQueryNav() throws Exception {
        testClientResponsivenessAjaxFieldQuery();
        testClientResponsivenessAjaxFieldQueryCustomMethod();
        passed();
    }

    @Test
    @Ignore // https://jira.kuali.org/browse/RICEQA-434 AFT Failures in CI that pass locally
    public void testClientResponsivenessAjaxFieldQueryCustomMethodAndServiceBookmark() throws Exception {
        testClientResponsivenessAjaxFieldQueryCustomMethodAndService();
        passed();
    }

    @Test
    @Ignore // https://jira.kuali.org/browse/RICEQA-434 AFT Failures in CI that pass locally
    public void testClientResponsivenessAjaxFieldQueryCustomMethodAndServiceNav() throws Exception {
        testClientResponsivenessAjaxFieldQueryCustomMethodAndService();
        passed();
    }
}
